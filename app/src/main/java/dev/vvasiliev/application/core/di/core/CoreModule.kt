package dev.vvasiliev.application.core.di.core

import android.content.Context
import androidx.navigation.NavHostController
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import javax.inject.Singleton

@Module
class CoreModule {
    @Provides
    @Singleton
    fun provideServiceConnector(context: Context) =
        AudioServiceConnector(context)
}