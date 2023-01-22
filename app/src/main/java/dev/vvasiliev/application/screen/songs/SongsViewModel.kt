package dev.vvasiliev.application.screen.songs

import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.invalidateGroupsWithKey
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dev.vvasiliev.application.exception.ServiceException
import dev.vvasiliev.application.exception.ServiceNotBoundException
import dev.vvasiliev.application.screen.songs.usecase.Audio
import dev.vvasiliev.application.screen.songs.usecase.ContentUpdateListener
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.data.EventListener
import dev.vvasiliev.audio.service.data.SongMetadata
import dev.vvasiliev.audio.service.event.PlaybackStarted
import dev.vvasiliev.audio.service.event.PlaybackStopped
import dev.vvasiliev.audio.service.event.ServiceStateEvent
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.operation.RequestMediaManagementPermissionLauncher
import dev.vvasiliev.structures.android.permission.NotificationPermissionLauncher
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher
import dev.vvasiliev.structures.android.permission.WriteStoragePermission
import dev.vvasiliev.view.composable.modular.dialog.DialogData
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SongsViewModel
@Inject constructor(
    private val serviceConnector: AudioServiceConnector,
    private val audio: Audio,
    private val navHostController: NavHostController,
    _serviceStatus: MutableStateFlow<AudioServiceState>,
    _playbackStatus: MutableStateFlow<ServiceStateEvent>
) : ViewModel() {

    private val contentObserver: ContentUpdateListener = ContentUpdateListener {
        fetchSongsAsync()
    }

    private val _musicList: MutableStateFlow<List<MusicCardData>> =
        MutableStateFlow(mutableListOf())

    val musicList: StateFlow<List<MusicCardData>> = _musicList
    val playbackStatus: StateFlow<ServiceStateEvent> = _playbackStatus
    val serviceStatus: StateFlow<AudioServiceState> = _serviceStatus
    val dialogState: MutableState<DialogData?> = mutableStateOf(null)

    private var service: IAudioPlaybackService? = null

    fun onCreate() {

        viewModelScope.launch {
            if (ReadStoragePermissionLauncher.requestPermission(navHostController.context)) {
                service = serviceConnector.getService()
                fetchSongs()
                audio.registerContentObserver(contentObserver)
            }
            NotificationPermissionLauncher.requestPermission(navHostController.context)
            getPlaybackUpdates()
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
                        service?.registerAudioEventListener(data.createListener())
                    }
                }
            }
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
                is SongScreenEvent.PlayEvent -> with(event.musicCardData) {
                    val startingPosition = calculateSeekToValue(position.value)
                    service?.play(
                        SongMetadata(id, uri, title, author), startingPosition
                    )
                    service?.registerAudioEventListener(createListener())
                        ?: throw ServiceNotBoundException(serviceClass = IAudioPlaybackService::class.java)
                }

                is SongScreenEvent.StopEvent -> {
                    service?.stopCurrent()
                        ?: throw ServiceNotBoundException(serviceClass = IAudioPlaybackService::class.java)
                }

                is SongScreenEvent.PositionChanged -> with(event.musicCardData) {
                    val isCurrentSong = event.musicCardData.id == service?.currentSongId

                    if (isCurrentSong) {
                        val positionMs = calculateSeekToValue(event.position)
                        service?.seekTo(positionMs)
                            ?: throw ServiceNotBoundException(serviceClass = IAudioPlaybackService::class.java)
                    }
                }
                is SongScreenEvent.DeleteAudioItem -> {
                    service?.stopCurrent()
                    audio.remove(event.uri, viewModelScope)
                }

                is SongScreenEvent.CardClickEvent -> {
//                    navHostController.navigate(
//                        Destination.MusicEditScreen(uri = event.uri).applyUri()
//                    )
                }

                is SongScreenEvent.ShareAudioItem -> {
                    audio.share(event.uri, viewModelScope)
                }
                is SongScreenEvent.InfoAudioItem -> {
                    dialogState.value = DialogData.InformationDialog(
                        title = "Track Info",
                        info = "Title: ${event.cardData.title}\nTrack Author: ${event.cardData.author}\nTrack Location: ${event.cardData.uri}\nTrack duration:${event.cardData.duration} ms",
                        confirmText = "Ok",
                        confirm = {
                            dialogState.value = null
                        },
                        onCancel = {
                            dialogState.value = null
                        }
                    )
                }
                is SongScreenEvent.RenameAudioItem -> {
                    dialogState.value = DialogData.MusicEditTextDialogData(
                        title = "Edit Info",
                        confirmText = "Ok",
                        confirmData = { title, album, author ->
                            //Update content values in external storage
                            updateSong(title, album, author, event.cardData)
                            dialogState.value = null
                        },
                        onCancel = {
                            dialogState.value = null
                        },
                        songTitle = event.cardData.title,
                        songAlbum = event.cardData.album,
                        songAuthor = event.cardData.author,
                    )
                }
            }
        } catch (exception: ServiceException) {
            Toast.makeText(navHostController.context, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSong(title: String, album: String, author: String, cardData: MusicCardData) {
        viewModelScope.launch {
            if (WriteStoragePermission.requestPermission(context = navHostController.context)) {
                val writeInfo = cardData.copy(
                    title = title,
                    album = album,
                    author = author
                )
                audio.update(
                    writeInfo,
                    viewModelScope
                )
            }
        }
    }

    private fun MusicCardData.createListener() = EventListener(
        onChange = { position ->
            updatePosition(
                position
            )
        },
        onPlaybackStopped = { setPlayingStatus(false) })

    private fun MusicCardData.calculateSeekToValue(position: Float) = (position * duration).toLong()
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