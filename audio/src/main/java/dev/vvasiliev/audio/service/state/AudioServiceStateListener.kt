package dev.vvasiliev.audio.service.state

import dev.vvasiliev.audio.service.state.holder.ServiceEventPipeline

class AudioServiceStateListener(private val pipeline: ServiceEventPipeline) :
    dev.vvasiliev.audio.AudioServiceStateListener.Stub() {

    override fun onAudioServiceStateChanged(state: AudioServiceState?): Unit =
        state?.let(pipeline::onServiceStateChanged) ?: Unit

    override fun onPlaybackStarted(songId: Long) {
        pipeline.onPlaybackStarted(songId)
    }

    override fun onPlaybackStopped(songId: Long) {
        pipeline.onPlaybackStopped(songId)
    }
}