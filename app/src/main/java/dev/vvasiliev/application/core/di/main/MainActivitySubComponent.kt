package dev.vvasiliev.application.core.di.main

import dagger.Subcomponent
import dev.vvasiliev.application.MainActivity
import dev.vvasiliev.application.core.di.core.CoreModule
import dev.vvasiliev.application.core.di.scope.MainActivityScope

@Subcomponent(modules = [CoreModule::class, MainActivityModule::class])
@MainActivityScope
interface MainActivitySubComponent {
    fun injectMainActivity(mainActivity: MainActivity)
}