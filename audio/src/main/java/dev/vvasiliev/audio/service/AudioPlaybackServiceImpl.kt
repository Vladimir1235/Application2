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
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import javax.inject.Inject

class AudioPlaybackServiceImpl @Inject constructor(private val exoplayer: ExoPlayer) :
    IAudioPlaybackService.Stub() {

    private var localScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var listener: WeakReference<AudioEventListener>? = null

    override fun play(uri: Uri, id: Long, listener: AudioEventListener, startPosition: Long) {
        exoplayer.run {

            val song = buildSong(uri, id)

            val isCurrent = song.isCurrent(this)

            if (!isCurrent) {
                if (mediaItemCount > 0) {
                    stopCurrent()
                    clearMediaItems()
                }
                addMediaItem(song)
                seekTo(startPosition)
            }

            prepare()

            if (isCurrent) {
                seekTo(currentPosition)
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
        exoplayer.run {
            this.seekTo(position)
        }
    }

    override fun stopCurrent() {
        localScope.cancel()
        localScope = CoroutineScope(Dispatchers.IO)
        listener?.get()?.onPlaybackStopped()
        exoplayer.stop()
    }

    override fun resumeCurrent() {
        exoplayer.play()
    }

    override fun isCurrent(id: Long): Boolean =
        exoplayer.currentMediaItem?.mediaId == id.toString()

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
                    var position: Long
                    var isPlaying: Boolean
                    var duration: Long
                    withContext(Dispatchers.Main) {
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
                    delay(100)
                }
                cancel()
            }
        }
    }

    private suspend fun stopAndSwitchToStart() {
        withContext(Dispatchers.Main) {
            exoplayer.clearMediaItems()
            listener?.get()?.onPositionChange(0)
            stopCurrent()
        }
    }
}