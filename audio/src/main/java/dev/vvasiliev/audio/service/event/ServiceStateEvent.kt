package dev.vvasiliev.audio.service.event

import dev.vvasiliev.audio.service.state.AudioServiceState

interface ServiceStateEvent
data class PlaybackStopped(val songId: Long) : ServiceStateEvent
data class PlaybackStarted(val songId: Long) : ServiceStateEvent
data class ServiceStateChanged(val serviceState: AudioServiceState) : ServiceStateEvent