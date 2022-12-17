package dev.vvasiliev.application.screen.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vvasiliev.application.screen.songs.usecase.GetAudio
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.data.EventListener
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher
import dev.vvasiliev.view.composable.modular.MusicCardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongsViewModel
@Inject constructor(
    private val serviceConnector: AudioServiceConnector,
    private val getAudio: GetAudio,
) : ViewModel() {

    private val _musicList: MutableStateFlow<MutableList<MusicCardData>> =
        MutableStateFlow(mutableListOf())
    val musicList: StateFlow<List<MusicCardData>> = _musicList

    private var service: IAudioPlaybackService? = null

    suspend fun onCreate() {
        viewModelScope.launch {
            if (ReadStoragePermissionLauncher.requestExternalStorage()) {
                service = serviceConnector.getService()
                fetchSongs()
            }
        }
    }

    private suspend fun fetchSongs() {
        _musicList.emit(getAudio())
    }

    fun onEvent(event: SongScreenEvent) {
        when (event) {
            is SongScreenEvent.PlayEvent -> with(event.musicCardData) {
                val startingPosition = calculateSeekToValue(position.value)
                service?.play(
                    uri, id,
                    EventListener(
                        onChange = { position -> updatePosition(position) },
                        onPlaybackStopped = { setPlayingStatus(false) }
                    ), startingPosition
                )
            }

            is SongScreenEvent.StopEvent -> {
                service?.stopCurrent()
            }

            is SongScreenEvent.PositionChanged -> with(event.musicCardData) {
                if (isCurrent()) {
                    val positionMs = calculateSeekToValue(event.position)
                    service?.seekTo(positionMs)
                }
            }
        }
    }

    private fun MusicCardData.calculateSeekToValue(position: Float) = (position * duration).toLong()
    private fun MusicCardData.isCurrent() = service?.isCurrent(id) == true
}

sealed class SongScreenEvent {
    class PlayEvent(val musicCardData: MusicCardData) : SongScreenEvent()
    class StopEvent : SongScreenEvent()
    class PositionChanged(val musicCardData: MusicCardData, val position: Float) :
        SongScreenEvent()
}