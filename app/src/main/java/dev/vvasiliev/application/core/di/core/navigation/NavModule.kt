package dev.vvasiliev.application.core.di.core.navigation

import android.content.Context
import androidx.navigation.NavHostController
import dagger.Module
import dagger.Provides
import dev.vvasiliev.application.core.di.main.MainBindsModule
import dev.vvasiliev.application.core.di.scope.MainActivityScope
import dev.vvasiliev.application.core.di.viewmodel.ViewModelInjection
import dev.vvasiliev.application.screen.navigation.Navigator

@Module(includes = [MainBindsModule::class])
class NavModule {
    @Provides
    @MainActivityScope
    fun provideNavigationController(context: Context) =
        NavHostController(context)

    @Provides
    @MainActivityScope
    fun provideNavigator(
        navHostController: NavHostController,
        viewModelFactory: ViewModelInjection
    ) =
        Navigator(navHostController = navHostController, viewModelFactory = viewModelFactory)
}