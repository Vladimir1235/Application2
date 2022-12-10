package dev.vvasiliev.audio.service

import com.google.android.exoplayer2.ExoPlayer
import dev.vvasiliev.audio.IAudioPlaybackService
import java.lang.ref.SoftReference

class AudioPlaybackServiceImpl(private val exoplayer: SoftReference<ExoPlayer>): IAudioPlaybackService.Stub(){

    override fun getServiceState(): Boolean = true

    override fun stopCurrent() {
        exoplayer.get()?.stop()
    }

    override fun resumeCurrent() {
        exoplayer.get()?.play()
    }
}