package dev.vvasiliev.application.screen.songs

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dev.vvasiliev.application.exception.ServiceException
import dev.vvasiliev.application.exception.ServiceNotBoundException
import dev.vvasiliev.application.screen.songs.usecase.ContentUpdateListener
import dev.vvasiliev.application.screen.songs.usecase.Audio
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.data.EventListener
import dev.vvasiliev.audio.service.data.SongMetadata
import dev.vvasiliev.audio.service.event.PlaybackStarted
import dev.vvasiliev.audio.service.event.PlaybackStopped
import dev.vvasiliev.audio.service.event.ServiceStateEvent
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.operation.ContentDeletionLauncher
import dev.vvasiliev.structures.android.permission.NotificationPermissionLauncher
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher
import dev.vvasiliev.view.composable.modular.music.MusicCardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SongsViewModel
@Inject constructor(
    private val context: Context,
    private val serviceConnector: AudioServiceConnector,
    private val audio: Audio,
    private val navHostController: NavHostController,
    _serviceStatus: MutableStateFlow<AudioServiceState>,
    _playbackStatus: MutableStateFlow<ServiceStateEvent>
) : ViewModel() {

    private val contentObserver: ContentUpdateListener = ContentUpdateListener {
        fetchSongsAsync()
    }

    private val _musicList: MutableStateFlow<MutableList<MusicCardData>> =
        MutableStateFlow(mutableListOf())

    val musicList: StateFlow<List<MusicCardData>> = _musicList
    val playbackStatus: StateFlow<ServiceStateEvent> = _playbackStatus
    val serviceStatus: StateFlow<AudioServiceState> = _serviceStatus

    private var service: IAudioPlaybackService? = null

    suspend fun onCreate() {
        viewModelScope.launch {
            if (ReadStoragePermissionLauncher.requestPermission(context)) {
                service = serviceConnector.getService()
                fetchSongs()
            }
            NotificationPermissionLauncher.requestPermission(context)
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
        _musicList.emit(audio(contentObserver = contentObserver))
    }

    fun onEvent(event: SongScreenEvent) {
        try {
            when (event) {
                is SongScreenEvent.PlayEvent -> with(event.musicCardData) {
                    val startingPosition = calculateSeekToValue(position.value)
                    service?.play(
                        SongMetadata(id, uri, title, author),
                        startingPosition
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
                    audio.createDeletionRequest(event.uri)?.let { request ->
                        viewModelScope.launch { ContentDeletionLauncher.launch(context, request) }
                    }
                }
                is SongScreenEvent.CardClickEvent -> {
//                    navHostController.navigate(
//                        Destination.MusicEditScreen(uri = event.uri).applyUri()
//                    )
                }
            }
        } catch (exception: ServiceException) {
            Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun MusicCardData.createListener() =
        EventListener(onChange = { position ->
            updatePosition(
                position
            )
        },
            onPlaybackStopped = { setPlayingStatus(false) }
        )

    private fun MusicCardData.calculateSeekToValue(position: Float) = (position * duration).toLong()
}

sealed class SongScreenEvent {
    class PlayEvent(val musicCardData: MusicCardData) : SongScreenEvent()
    class StopEvent : SongScreenEvent()
    class CardClickEvent(val uri: Uri) : SongScreenEvent()
    class PositionChanged(val musicCardData: MusicCardData, val position: Float) : SongScreenEvent()
    class DeleteAudioItem(val uri: Uri) : SongScreenEvent()
}