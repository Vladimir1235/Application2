package dev.vvasiliev.audio.service

import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.state.AudioServiceState
import java.lang.ref.SoftReference

class AudioPlaybackServiceImpl(private val exoplayer: SoftReference<ExoPlayer>): IAudioPlaybackService.Stub(){
    override fun play(uri: Uri?) {
        uri?.let {
            exoplayer.get()?.addMediaItem(MediaItem.fromUri(uri))
            exoplayer.get()?.prepare()
            resumeCurrent()
        }
    }

    override fun getState(): AudioServiceState {
        exoplayer.get()?.playbackState
        return AudioServiceState.NOT_CREATED
    }

    override fun stopCurrent() {
        exoplayer.get()?.stop()
    }

    override fun resumeCurrent() {
        exoplayer.get()?.play()
    }
}