package dev.vvasiliev.application.core.di.config

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.vvasiliev.application.core.config.AppConfiguration
import dev.vvasiliev.application.core.config.AppConfigurator
import dev.vvasiliev.application.core.config.ApplicationConfiguration
import dev.vvasiliev.application.core.di.core.CoreModule
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module(includes = [CoreModule::class])
class ConfigModule {

    @Provides
    @Singleton
    fun provideConfiguration(): AppConfiguration =
        ApplicationConfiguration(
            MutableStateFlow(AudioServiceState.UNKNOWN),
            MutableStateFlow(null)
        )

    @Provides
    @Singleton
    fun provideConfigurator(
        context: Context,
        configuration: AppConfiguration,
        serviceConnector: AudioServiceConnector
    ): AppConfigurator =
        AppConfigurator(context, configuration, serviceConnector, Dispatchers.Main)
}