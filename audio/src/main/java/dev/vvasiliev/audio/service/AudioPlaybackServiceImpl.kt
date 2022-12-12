package dev.vvasiliev.audio.service

import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.*
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.state.AudioServiceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference

class AudioPlaybackServiceImpl(private val exoplayer: SoftReference<ExoPlayer>) :
    IAudioPlaybackService.Stub() {
    override fun play(uri: Uri?) {
        uri?.let {
            CoroutineScope(Dispatchers.Main).launch {
                exoplayer.get()?.addMediaItem(MediaItem.fromUri(uri))
                exoplayer.get()?.prepare()
                resumeCurrent()
            }
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


        override fun stopCurrent() {
            exoplayer.get()?.stop()
        }

        override fun resumeCurrent() {
            exoplayer.get()?.play()
        }
    }