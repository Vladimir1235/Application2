package dev.vvasiliev.application.screen.songs.usecase.service

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.data.EventListener
import dev.vvasiliev.audio.service.data.SongMetadata
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData

class Service @AssistedInject constructor(@Assisted private val service: IAudioPlaybackService) {
    fun play(musicCardData: MusicCardData) {
        with(musicCardData) {
            val startingPosition = calculateSeekToValue(position.value)
            service.play(
                SongMetadata(id, uri, title, author), startingPosition
            )
            service.registerAudioEventListener(createListener())
        }
    }

    fun stop() {
        service.stopCurrent()
    }

    fun onChangePosition(musicCardData: MusicCardData, position: Float) {
        val isCurrentSong = musicCardData.id == service.currentSongId

        if (isCurrentSong) {
            val positionMs = musicCardData.calculateSeekToValue(position)
            service.seekTo(positionMs)
        }
    }

    fun registerAudioEventListener(musicCardData: MusicCardData) {
        service.registerAudioEventListener(musicCardData.createListener())
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