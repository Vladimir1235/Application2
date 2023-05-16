package dev.vvasiliev.application.screen.songs

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dev.vvasiliev.application.exception.ServiceException
import dev.vvasiliev.application.screen.songs.mapper.DialogMapper
import dev.vvasiliev.application.screen.songs.mapper.DropDownMenuMapper
import dev.vvasiliev.application.screen.songs.mapper.MusicCardMapper
import dev.vvasiliev.application.screen.songs.usecase.Audio
import dev.vvasiliev.application.screen.songs.usecase.storage.ContentUpdateListener
import dev.vvasiliev.audio.service.event.PlaybackStarted
import dev.vvasiliev.audio.service.event.PlaybackStopped
import dev.vvasiliev.audio.service.event.ServiceStateEvent
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.structures.android.permission.NotificationPermissionLauncher
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher
import dev.vvasiliev.structures.android.permission.WriteStoragePermission
import dev.vvasiliev.view.composable.modular.dialog.DialogData
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongsViewModel
@Inject constructor(
    private val audio: Audio,
    private val navHostController: NavHostController,
    private val dialogMapper: DialogMapper,
    private val cardMapper: MusicCardMapper,
    private val dropDownMenuMapper: DropDownMenuMapper,
    _serviceStatus: MutableStateFlow<AudioServiceState>,
    _playbackStatus: MutableStateFlow<ServiceStateEvent>
) : ViewModel() {

    private val _musicList: MutableStateFlow<List<MusicCardData>> =
        MutableStateFlow(mutableListOf())
    val musicList: StateFlow<List<MusicCardData>> = _musicList
    val playbackStatus: StateFlow<ServiceStateEvent> = _playbackStatus
    val serviceStatus: StateFlow<AudioServiceState> = _serviceStatus
    val dialogState: MutableState<DialogData?> = mutableStateOf(null)

    fun onCreate() {
        viewModelScope.launch {
            if (ReadStoragePermissionLauncher.requestPermission(navHostController.context)) {
                fetchSongs()
                audio.registerContentObserver(ContentUpdateListener {
                    fetchSongsAsync()
                })
            }
            NotificationPermissionLauncher.requestPermission(navHostController.context)
            getPlaybackUpdates()
        }
    }

    private fun fetchSongsAsync() {
        viewModelScope.launch { fetchSongs() }
    }

    private suspend fun fetchSongs() {
        _musicList.emit(audio.getAll())
    }

    fun onEvent(event: SongScreenEvent) {
        try {
            when (event) {
                is SongScreenEvent.PlayEvent -> {
                    audio.play(event.musicCardData)
                }

                is SongScreenEvent.StopEvent -> {
                    audio.stop()
                }

                is SongScreenEvent.PositionChanged -> {
                    audio.onChangePosition(event.musicCardData, position = event.position)
                }

                is SongScreenEvent.DeleteAudioItem -> {
                    audio.stop()
                    audio.remove(event.uri, viewModelScope)
                }

                is SongScreenEvent.CardClickEvent -> {}

                is SongScreenEvent.ShareAudioItem -> {
                    audio.share(event.uri, viewModelScope)
                }
                is SongScreenEvent.InfoAudioItem -> {
                    dialogState.value =
                        dialogMapper.mapInfoDialog(cardData = event.cardData, onDismiss = {
                            dialogState.value = null
                        })
                }
                is SongScreenEvent.RenameAudioItem -> {
                    dialogState.value = dialogMapper.mapRenameAudioDialog(cardData = event.cardData,
                        onComplete = ::updateSong,
                        onDismiss = {
                            dialogState.value = null
                        })
                }
            }
        } catch (exception: ServiceException) {
            Toast.makeText(navHostController.context, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getCardComposable(index: Int) =
        musicList.value.getOrNull(index)?.let { data ->
            cardMapper.map(
                musicCardData = data,
                onStateChanged = { status ->
                    onEvent(
                        if (status) SongScreenEvent.PlayEvent(data)
                        else SongScreenEvent.StopEvent()
                    )
                },
                onPositionChanged = { position ->
                    onEvent(SongScreenEvent.PositionChanged(data, position))
                },
                dropDownItems = dropDownMenuMapper.map(onEvent = ::onEvent, data)
            )
        }


    private fun updateSong(title: String, album: String, author: String, cardData: MusicCardData) {
        viewModelScope.launch {
            if (WriteStoragePermission.requestPermission(context = navHostController.context)) {
                val writeInfo = cardData.copy(
                    title = title, album = album, author = author
                )
                audio.update(
                    writeInfo, viewModelScope
                )
            }
        }
    }

    private suspend fun getPlaybackUpdates() {
        playbackStatus.collectLatest { event ->
            when (event) {
                is PlaybackStopped -> {
                    musicList.value.find { cardData ->
                        cardData.id == event.songId
                    }?.setPlayingStatus(false)
                }
                is PlaybackStarted -> {
                    musicList.value.find { cardData ->
                        cardData.id == event.songId
                    }?.let { data ->
                        data.setPlayingStatus(true)
                        audio.registerAudioEventListener(data)
                    }
                }
            }
        }
    }

}

sealed class SongScreenEvent {
    class PlayEvent(val musicCardData: MusicCardData) : SongScreenEvent()
    class StopEvent : SongScreenEvent()
    class CardClickEvent(val uri: Uri) : SongScreenEvent()
    class PositionChanged(val musicCardData: MusicCardData, val position: Float) : SongScreenEvent()
    class DeleteAudioItem(val uri: Uri) : SongScreenEvent()
    class ShareAudioItem(val uri: Uri) : SongScreenEvent()
    class InfoAudioItem(val cardData: MusicCardData) : SongScreenEvent()
    class RenameAudioItem(val cardData: MusicCardData) : SongScreenEvent()
}