package dev.vvasiliev.application.core.di.main

import androidx.navigation.NavHostController
import dagger.BindsInstance
import dagger.Subcomponent
import dev.vvasiliev.application.MainActivity
import dev.vvasiliev.application.core.di.scope.MainActivityScope
import dev.vvasiliev.application.core.di.viewmodel.ViewModelInjection

@Subcomponent(
    modules = [MainActivityModule::class,
        MainBindsModule::class]
)
@MainActivityScope
interface MainActivitySubComponent {
    fun injectMainActivity(mainActivity: MainActivity)
    val factory: ViewModelInjection

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun bindNavController(navHostController: NavHostController): Builder
        fun build(): MainActivitySubComponent
    }
}