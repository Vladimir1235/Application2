package dev.vvasiliev.application.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import dev.vvasiliev.view.composable.modular.MusicCardData
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {

    @Provides
    @Singleton
    fun provideServiceConnector(@ApplicationContext context: Context) =
        AudioServiceConnector(context)

    @Provides
    @Singleton
    fun provideAudioStorageAccessor(@ApplicationContext context: Context): UriCollection<AudioFileCollection.Audio> =
        AudioFileCollection(context)
}