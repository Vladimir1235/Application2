package dev.vvasiliev.application.core.di.main

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.vvasiliev.application.core.di.scope.MainActivityScope
import dev.vvasiliev.application.core.di.viewmodel.ViewModelKey
import dev.vvasiliev.application.screen.songs.SongsViewModel

@Module
interface MainBindsModule {

    @Binds
    @MainActivityScope
    @IntoMap
    @ViewModelKey(SongsViewModel::class)
    fun provideSongViewModel(songsViewModel: SongsViewModel): ViewModel
}