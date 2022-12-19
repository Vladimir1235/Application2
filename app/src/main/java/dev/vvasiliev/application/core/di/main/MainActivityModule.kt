package dev.vvasiliev.application.core.di.main

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dev.vvasiliev.application.core.di.core.CoreModule
import dev.vvasiliev.application.core.di.scope.MainActivityScope
import dev.vvasiliev.application.screen.songs.SongsViewModel
import dev.vvasiliev.application.screen.songs.usecase.GetAudio
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection

@Module
class MainActivityModule {

    @Provides
    @MainActivityScope
    fun provideGetAudioUseCase(collection: UriCollection<AudioFileCollection.Audio>) =
        GetAudio(collection)

    @Provides
    @MainActivityScope
    fun provideAudioStorageAccessor(context: Context): UriCollection<AudioFileCollection.Audio> =
        AudioFileCollection(context)
}