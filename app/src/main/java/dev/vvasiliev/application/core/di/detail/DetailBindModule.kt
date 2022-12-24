package dev.vvasiliev.application.core.di.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.vvasiliev.application.core.di.viewmodel.ViewModelKey
import dev.vvasiliev.application.screen.detail.DetailViewModel

@Module
interface DetailBindModule {

    @Binds
    @DetailsScope
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    fun bindViewModel(viewModel: DetailViewModel): ViewModel

    @Binds
    @DetailsScope
    fun bindViewModelFactory(viewModelFactory: DetailViewModelFactory): ViewModelProvider.Factory
}