package dev.vvasiliev.audio.service

import android.media.session.PlaybackState
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import dev.vvasiliev.audio.AudioEventListener
import dev.vvasiliev.audio.AudioServiceStateListener
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.data.SongMetadata
import dev.vvasiliev.audio.service.event.PlaybackStarted
import dev.vvasiliev.audio.service.event.PlaybackStopped
import dev.vvasiliev.audio.service.event.ServiceStateChanged
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.audio.service.state.holder.AudioServiceStateHolder
import dev.vvasiliev.audio.service.util.player.PlayerUsecase
import dev.vvasiliev.audio.service.util.player.ServiceSpecificThreadExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ID_UNSPECIFIED: Long = -1

class AudioPlaybackServiceImpl @Inject constructor(
    private val player: PlayerUsecase,
    private val executor: ServiceSpecificThreadExecutor,
    private val state: AudioServiceStateHolder
) : IAudioPlaybackService.Stub() {

    private var listener: AudioServiceStateListener? = null

    override fun play(metadata: SongMetadata, startPosition: Long) {
        executor.execute {

            val song = buildSong(metadata)

            val isCurrent = player.isCurrent(song)

            if (!isCurrent) {
                if (player.isPlaying()) {
                    stopCurrent()
                }
                player.setCurrent(song)
                player.switchTo(startPosition)
            }

            player.resume()
        }
    }

    override fun getState(): AudioServiceState =
        when (player.getState()) {
            PlaybackState.STATE_BUFFERING,
            PlaybackState.STATE_PLAYING -> AudioServiceState.PLAYING
            PlaybackState.STATE_STOPPED,
            PlaybackState.STATE_PAUSED,
            PlaybackState.STATE_ERROR -> AudioServiceState.STOPPED
            else -> AudioServiceState.UNKNOWN
        }


    override fun seekTo(position: Long) {
        executor.execute {
            player.switchTo(position)
        }
    }

    override fun stopCurrent() {
        executor.executeBlocking {
            player.cancelPositionUpdates()
            player.stop()
        }
    }

    override fun getCurrentSongId(): Long = player.getCurrentSongId() ?: ID_UNSPECIFIED

    override fun registerAudioEventListener(listener: AudioEventListener) {
        updateCurrentPosition(listener)
    }

    override fun unregisterAudioEventListener(listener: AudioEventListener) {
        player.unsubscribeOnPositionChange(listener)
    }

    override fun registerStateListener(listener: AudioServiceStateListener?) {
        this.listener = listener
        val aListener = this.listener
        CoroutineScope(Dispatchers.IO).launch {
            state.getFlow().collect { serviceStateEvent ->
                when (serviceStateEvent) {
                    is ServiceStateChanged -> {
                        aListener?.onAudioServiceStateChanged(serviceStateEvent.serviceState)
                    }
                    is PlaybackStarted -> {
                        aListener?.onPlaybackStarted(serviceStateEvent.songId)
                    }
                    is PlaybackStopped -> {
                        aListener?.onPlaybackStopped(serviceStateEvent.songId)
                    }
                }
            }
        }
    }

    override fun unregisterStateListener() {
        listener = null
    }

    private fun buildSong(metadata: SongMetadata) = MediaItem.Builder()
        .setUri(metadata.uri)
        .setMediaId(metadata.id.toString())
        .setMediaMetadata(
            MediaMetadata.Builder().setTitle(metadata.title).setArtist(metadata.artist).build()
        )
        .build()

    private fun updateCurrentPosition(listener: AudioEventListener) {
        CoroutineScope(Dispatchers.IO).launch {
            player.subscribeOnPositionChange(listener)
            player.requestPositionUpdates()
        }
    }
}