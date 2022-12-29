package dev.vvasiliev.audio.service

import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.*
import dev.vvasiliev.audio.AudioEventListener
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.audio.service.util.AudioUtils.isEnd
import dev.vvasiliev.audio.service.util.ServiceSpecificThreadExecutor
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject


private const val REFRESH_RATE = 500L

class AudioPlaybackServiceImpl @Inject constructor(
    private val exoplayer: ExoPlayer,
    private val executor: ServiceSpecificThreadExecutor
) : IAudioPlaybackService.Stub() {

    private var localScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var listener: WeakReference<AudioEventListener>? = null

    override fun play(uri: Uri, id: Long, listener: AudioEventListener, startPosition: Long) {
        executor.execute {
            val song = buildSong(uri, id)

            val isCurrent = song.isCurrent(exoplayer)

            if (!isCurrent) {
                if (exoplayer.mediaItemCount > 0) {
                    stopCurrent()
                    exoplayer.clearMediaItems()
                }
                exoplayer.addMediaItem(song)
                seekTo(startPosition)
            }

            exoplayer.prepare()

            if (isCurrent) {
                seekTo(exoplayer.currentPosition)
            }
            updateCurrentPosition(listener)
            resumeCurrent()
        }
    }

    override fun getState(): AudioServiceState =
        exoplayer.playbackState.let {
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
            exoplayer.seekTo(position)
        }
    }

    override fun stopCurrent() {
        executor.execute {
            localScope.cancel()
            localScope = CoroutineScope(Dispatchers.IO)
            listener?.get()?.onPlaybackStopped()
            exoplayer.stop()
        }
    }

    override fun resumeCurrent() {
        exoplayer.play()
    }

    override fun isCurrent(id: Long): Boolean =
        executor.executeBlocking { exoplayer.currentMediaItem?.mediaId == id.toString() }


    private fun buildSong(uri: Uri, id: Long) = MediaItem.Builder()
        .setUri(uri)
        .setMediaId(id.toString())
        .build()

    private fun MediaItem.isCurrent(exoPlayer: ExoPlayer) =
        exoPlayer.currentMediaItem?.mediaId == mediaId


    private fun updateCurrentPosition(listener: AudioEventListener) {
        localScope = CoroutineScope(Dispatchers.IO)
        this.listener = WeakReference(listener)
        localScope.launch {
            listener.run {
                while (listener != null) {
                    var position: Long = 1
                    var isPlaying: Boolean = false
                    var duration: Long = 2
                    executor.executeBlocking {
                        position = exoplayer.currentPosition
                        duration = exoplayer.duration
                        isPlaying = exoplayer.isPlaying
                    }

                    if (isPlaying) {
                        onPositionChange(position)
                        Log.d("Player", "Position changed")
                        if (position.isEnd(duration)) {
                            stopAndSwitchToStart()
                        }
                    }
                    delay(REFRESH_RATE)
                }
                cancel()
            }
        }
    }

    private suspend fun stopAndSwitchToStart() {
        exoplayer.clearMediaItems()
        listener?.get()?.onPositionChange(0)
        stopCurrent()
    }
}