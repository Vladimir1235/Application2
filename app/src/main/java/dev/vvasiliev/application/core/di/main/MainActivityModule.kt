package dev.vvasiliev.application.core.di.main

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.vvasiliev.application.core.config.AppConfiguration
import dev.vvasiliev.application.core.di.scope.MainActivityScope
import dev.vvasiliev.application.screen.songs.usecase.Audio
import dev.vvasiliev.application.screen.songs.usecase.storage.*
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection

@Module
class MainActivityModule {

    @Provides
    @MainActivityScope
    fun provideServiceState(configuration: AppConfiguration) =
        configuration.serviceStatus

    @Provides
    @MainActivityScope
    fun provideServicePlaybackState(configuration: AppConfiguration) =
        configuration.servicePlaybackStatus

    @Provides
    @MainActivityScope
    fun provideGetAudioUseCase(
        getAudio: GetAudio,
        shareAudio: ShareAudio,
        registerObserver: RegisterObserver,
        deleteAudio: DeleteAudio,
        updateSong: UpdateSong,
        serviceAssistFactory: ServiceAssistFactory,
        serviceConnector: AudioServiceConnector
    ) =
        Audio(
            getAudio,
            deleteAudio,
            shareAudio,
            registerObserver,
            updateSong,
            serviceAssistFactory,
            serviceConnector
        )

    @Provides
    @MainActivityScope
    fun provideAudioStorageAccessor(context: Context): UriCollection<AudioFileCollection.Audio> =
        AudioFileCollection(context)
}