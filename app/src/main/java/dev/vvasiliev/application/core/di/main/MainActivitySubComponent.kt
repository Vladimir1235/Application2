package dev.vvasiliev.application.core.di.main

import dagger.Subcomponent
import dev.vvasiliev.application.MainActivity
import dev.vvasiliev.application.core.di.core.CoreModule
import dev.vvasiliev.application.core.di.core.navigation.NavModule
import dev.vvasiliev.application.core.di.scope.MainActivityScope
import dev.vvasiliev.application.core.di.viewmodel.ViewModelInjection

@Subcomponent(
    modules = [CoreModule::class,
        MainActivityModule::class,
        MainBindsModule::class,
        NavModule::class]
)
@MainActivityScope
interface MainActivitySubComponent {
    fun injectMainActivity(mainActivity: MainActivity)
    val factory: ViewModelInjection
}