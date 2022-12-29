package dev.vvasiliev.audio.service

import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.*
import dev.vvasiliev.audio.AudioEventListener
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.data.EventListener
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.audio.service.util.AudioUtils.isEnd
import dev.vvasiliev.audio.service.util.PlayerUsecase
import dev.vvasiliev.audio.service.util.ServiceSpecificThreadExecutor
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class AudioPlaybackServiceImpl @Inject constructor(
    private val player: PlayerUsecase,
    private val executor: ServiceSpecificThreadExecutor
) : IAudioPlaybackService.Stub() {

    private var localScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var listener: WeakReference<AudioEventListener>? = null

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
            player.play()
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
        executor.execute {
            player.cancelPositionUpdates()
            localScope.cancel()
            localScope = CoroutineScope(Dispatchers.IO)
            listener?.get()?.onPlaybackStopped()
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
            player.requestPositionUpdates()
            player.subscribeOnPositionChange(listener)
        }
    }
}