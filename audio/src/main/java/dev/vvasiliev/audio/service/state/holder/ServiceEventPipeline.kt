package dev.vvasiliev.audio.service.state.holder

import dev.vvasiliev.audio.service.state.AudioServiceState

interface ServiceEventPipeline {
    fun onPlaybackStopped(songId: Long)
    fun onPlaybackStarted(songId: Long)
    fun onServiceStateChanged(state: AudioServiceState)
}