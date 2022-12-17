package dev.vvasiliev.application.core.di.core

import android.content.Context
import androidx.navigation.NavHostController
import dagger.Module
import dagger.Provides
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import javax.inject.Singleton

@Module
class CoreModule {
    @Provides
    fun provideServiceConnector(context: Context) =
        AudioServiceConnector(context)

    @Provides
    fun provideNavigationController(context: Context) =
        NavHostController(context)

    @Provides
    fun provideAudioStorageAccessor(context: Context): UriCollection<AudioFileCollection.Audio> =
        AudioFileCollection(context)
}