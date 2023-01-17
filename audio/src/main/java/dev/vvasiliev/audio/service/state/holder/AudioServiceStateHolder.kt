package dev.vvasiliev.audio.service.state.holder

import dev.vvasiliev.audio.service.event.PlaybackStarted
import dev.vvasiliev.audio.service.event.PlaybackStopped
import dev.vvasiliev.audio.service.event.ServiceStateChanged
import dev.vvasiliev.audio.service.event.ServiceStateEvent
import dev.vvasiliev.audio.service.state.AudioServiceState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AudioServiceStateHolder(
    private val state: MutableSharedFlow<ServiceStateEvent> = MutableSharedFlow(
        replay = 3,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
) : StateHolder<ServiceStateEvent>(state), ServiceEventPipeline {
    override fun onPlaybackStopped(songId: Long) {
        set(PlaybackStopped(songId))
    }

    override fun onPlaybackStarted(songId: Long) {
        set(PlaybackStarted(songId))
    }

    override fun onServiceStateChanged(state: AudioServiceState) {
        set(ServiceStateChanged(state))
    }

    fun getFlow(): SharedFlow<ServiceStateEvent> = state
}