package dev.vvasiliev.application.ui.screen.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.data.EventListener
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher
import dev.vvasiliev.view.composable.modular.MusicCardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel
@Inject constructor(
    private val serviceConnector: AudioServiceConnector,
    private val storage: UriCollection<AudioFileCollection.Audio>
) : ViewModel() {

    private val _musicList: MutableStateFlow<MutableList<MusicCardData>> =
        MutableStateFlow(mutableListOf())

    val musicList: StateFlow<List<MusicCardData>> = _musicList

    private var service: IAudioPlaybackService? = null

    suspend fun onCreate() {
        viewModelScope.launch {
            ReadStoragePermissionLauncher.requestExternalStorage()
            service = serviceConnector.getService()
            fetchSongs()
        }
    }

    private suspend fun fetchSongs() {
        _musicList.emit(storage.getAllContent().map {
            MusicCardData(
                false,
                title = it.name,
                album = it.album,
                author = it.artist,
                duration = it.duration,
                id = it.id,
                uri = it.uri
            )
        }.toMutableList())
    }

    fun onEvent(event: SongScreenEvent) {
        when (event) {
            is SongScreenEvent.StateChanged -> with(event.musicCardData) {
                when (event.isPlaying) {
                    true -> {
                        val startingPosition = calculateSeekToValue(position.value)

                        service?.play(
                            uri, id,
                            EventListener(
                                onChange = { position -> updatePosition(position) },
                                onPlaybackStopped = { setPlayingStatus(false) }
                            ), startingPosition
                        )
                    }
                    false -> {
                        if (service?.isCurrent(id) == true)
                            service?.stopCurrent() else Unit
                    }
                }
            }
            is SongScreenEvent.PositionChanged -> with(event.musicCardData) {
                if (service?.isCurrent(id) == true) {
                    val positionMs = calculateSeekToValue(event.position)
                    service?.seekTo(positionMs)
                }
            }
        }
    }

    private fun MusicCardData.calculateSeekToValue(position: Float) = (position * duration).toLong()
}

sealed class SongScreenEvent {
    class StateChanged(val musicCardData: MusicCardData, val isPlaying: Boolean) :
        SongScreenEvent()

    class PositionChanged(val musicCardData: MusicCardData, val position: Float) :
        SongScreenEvent()
}