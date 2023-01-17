package dev.vvasiliev.application.core.config

import dev.vvasiliev.audio.service.event.ServiceStateEvent
import dev.vvasiliev.audio.service.state.AudioServiceState
import kotlinx.coroutines.flow.MutableStateFlow

interface AppConfiguration {
    val serviceStatus: MutableStateFlow<AudioServiceState>
    val servicePlaybackStatus: MutableStateFlow<ServiceStateEvent?>
}

data class ApplicationConfiguration(
    override val serviceStatus: MutableStateFlow<AudioServiceState>,
    override val servicePlaybackStatus: MutableStateFlow<ServiceStateEvent?>
) : AppConfiguration
