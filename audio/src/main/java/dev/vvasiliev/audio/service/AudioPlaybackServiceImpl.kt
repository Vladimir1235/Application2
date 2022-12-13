package dev.vvasiliev.audio.service

import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.*
import dev.vvasiliev.audio.AudioEventListener
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.state.AudioServiceState
import kotlinx.coroutines.*
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

class AudioPlaybackServiceImpl(private val exoplayer: SoftReference<ExoPlayer>) :
    IAudioPlaybackService.Stub() {

    private var localScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var listener: WeakReference<AudioEventListener>? = null

    override fun play(uri: Uri, id: Long, listener: AudioEventListener, startPosition: Long) {
        exoplayer.get()?.run {

            val song = buildSong(uri, id)

            val isCurrent = song.isCurrent(this)

            if (!isCurrent) {
                stopCurrent()
                clearMediaItems()
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
        exoplayer.get()?.playbackState?.let {
            when (it) {
                STATE_IDLE -> AudioServiceState.STOPPED
                STATE_READY -> AudioServiceState.STOPPED
                STATE_BUFFERING -> AudioServiceState.PLAYING
                STATE_ENDED -> AudioServiceState.STOPPED
                else -> AudioServiceState.NOT_CREATED
            }
        } ?: AudioServiceState.NOT_CREATED

    override fun seekTo(position: Long) {
        exoplayer.get()?.run {
            this.seekTo(position)
        }
    }

    override fun stopCurrent() {
        localScope.cancel()
        listener?.get()?.onPlaybackStopped()
        exoplayer.get()?.stop()
    }

    override fun resumeCurrent() {
        exoplayer.get()?.play()
    }

    override fun isCurrent(id: Long): Boolean =
        exoplayer.get()?.currentMediaItem?.mediaId == id.toString()

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
                    var position: Long?
                    var isPlaying = false
                    withContext(Dispatchers.Main) {
                        position = exoplayer.get()?.currentPosition
                        isPlaying = exoplayer.get()?.isPlaying == true
                    }
                    if (isPlaying)
                        position?.let {
                            delay(100)
                            this?.onPositionChange(it)
                        }
                }
                cancel()
            }
        }
    }
}