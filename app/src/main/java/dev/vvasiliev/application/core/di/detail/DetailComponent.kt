package dev.vvasiliev.application.core.di.detail

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import dagger.BindsInstance
import dagger.Component

@Component(modules = [DetailBindModule::class])
@DetailsScope
interface DetailComponent {
    val detailsViewModelFactory: ViewModelProvider.Factory

    @dagger.Component.Builder
    interface Builder {
        @BindsInstance
        fun bindsNavigationController(navHostController: NavHostController): Builder
        fun build(): DetailComponent
    }
}