package dev.vvasiliev.audio.service.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.vvasiliev.audio.service.AudioPlaybackService

@Component(modules = [AudioServiceModule::class])
@AudioServiceScope
interface AudioServiceComponent {
    fun injectService(audioPlaybackService: AudioPlaybackService)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindContext(context: Context): Builder

        fun build(): AudioServiceComponent
    }
}