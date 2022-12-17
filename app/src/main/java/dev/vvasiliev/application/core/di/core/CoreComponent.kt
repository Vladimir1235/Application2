package dev.vvasiliev.application.core.di.core

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.vvasiliev.application.core.di.main.MainActivitySubComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [CoreModule::class])
interface CoreComponent {

    val mainActivityModule: MainActivitySubComponent

    @Component.Builder
    interface CoreComponentBuilder {
        @BindsInstance
        fun includeContext(context: Context): CoreComponentBuilder
        fun build(): CoreComponent
    }
}