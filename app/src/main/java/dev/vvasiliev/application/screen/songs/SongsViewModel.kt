package dev.vvasiliev.application.screen.songs

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dev.vvasiliev.application.exception.ServiceException
import dev.vvasiliev.application.exception.ServiceNotBoundException
import dev.vvasiliev.application.screen.navigation.Destination
import dev.vvasiliev.application.screen.songs.usecase.ContentUpdateListener
import dev.vvasiliev.application.screen.songs.usecase.GetAudio
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.data.EventListener
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher
import dev.vvasiliev.view.composable.modular.music.MusicCardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongsViewModel
@Inject constructor(
    private val context: Context,
    private val serviceConnector: AudioServiceConnector,
    private val getAudio: GetAudio,
    private val navHostController: NavHostController
) : ViewModel() {

    private val contentObserver: ContentUpdateListener = ContentUpdateListener {
        fetchSongsAsync()
    }

    private val _musicList: MutableStateFlow<MutableList<MusicCardData>> =
        MutableStateFlow(mutableListOf())
    val musicList: StateFlow<List<MusicCardData>> = _musicList

    private var service: IAudioPlaybackService? = null

    suspend fun onCreate() {
        viewModelScope.launch {
            if (ReadStoragePermissionLauncher.requestExternalStorage(context)) {
                service = serviceConnector.getService()
                fetchSongs()
            }
        }
    }

    private fun fetchSongsAsync() {
        viewModelScope.launch { fetchSongs() }
    }

    private suspend fun fetchSongs() {
        _musicList.emit(getAudio(contentObserver = contentObserver))
    }

    fun onEvent(event: SongScreenEvent) {
        try {
            when (event) {
                is SongScreenEvent.PlayEvent -> with(event.musicCardData) {
                    val startingPosition = calculateSeekToValue(position.value)
                    service?.play(uri,
                        id,
                        EventListener(onChange = { position -> updatePosition(position) },
                            onPlaybackStopped = { setPlayingStatus(false) }),
                        startingPosition)
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

    private fun MusicCardData.calculateSeekToValue(position: Float) = (position * duration).toLong()
}

sealed class SongScreenEvent {
    class PlayEvent(val musicCardData: MusicCardData) : SongScreenEvent()
    class StopEvent : SongScreenEvent()
    class CardClickEvent(val uri: Uri) : SongScreenEvent()
    class PositionChanged(val musicCardData: MusicCardData, val position: Float) : SongScreenEvent()
}