package dev.vvasiliev.application.core.di.main

import dagger.assisted.AssistedFactory
import dev.vvasiliev.application.screen.songs.usecase.service.Service
import dev.vvasiliev.audio.IAudioPlaybackService

@AssistedFactory
interface ServiceAssistFactory {
    fun createServiceUsecase(service: IAudioPlaybackService): Service
}