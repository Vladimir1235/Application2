package dev.vvasiliev.audio.service

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.STATE_IDLE
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.Player.STATE_BUFFERING
import com.google.android.exoplayer2.Player.STATE_ENDED
import dev.vvasiliev.audio.AudioEventListener
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.audio.service.util.player.PlayerUsecase
import dev.vvasiliev.audio.service.util.player.ServiceSpecificThreadExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioPlaybackServiceImpl @Inject constructor(
    private val player: PlayerUsecase,
    private val executor: ServiceSpecificThreadExecutor
) : IAudioPlaybackService.Stub() {

    override fun play(uri: Uri, id: Long, listener: AudioEventListener, startPosition: Long) {
        executor.execute {
            val song = buildSong(uri, id)

            val isCurrent = player.isCurrent(song)

            if (!isCurrent) {
                if (player.isPlaying()) {
                    stopCurrent()
                }
                player.setCurrent(song)
                player.switchTo(startPosition)
            }

            updateCurrentPosition(listener)
            player.resume()
        }
    }

    override fun getState(): AudioServiceState =
        player.getState().let {
            when (it) {
                STATE_IDLE -> AudioServiceState.STOPPED
                STATE_READY -> AudioServiceState.STOPPED
                STATE_BUFFERING -> AudioServiceState.PLAYING
                STATE_ENDED -> AudioServiceState.STOPPED
                else -> AudioServiceState.NOT_CREATED
            }
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

    override fun resumeCurrent() {
        player.play()
    }

    override fun isCurrent(id: Long): Boolean =
        executor.executeBlocking { player.isCurrent(id) }

    private fun buildSong(uri: Uri, id: Long) = MediaItem.Builder()
        .setUri(uri)
        .setMediaId(id.toString())
        .build()

    private fun updateCurrentPosition(listener: AudioEventListener) {
        CoroutineScope(Dispatchers.IO).launch {
            player.subscribeOnPositionChange(listener)
            player.requestPositionUpdates()
        }
    }
}