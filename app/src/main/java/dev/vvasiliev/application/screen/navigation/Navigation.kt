package dev.vvasiliev.application.screen.navigation

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.vvasiliev.application.core.di.detail.DaggerDetailComponent
import dev.vvasiliev.application.core.di.viewmodel.ViewModelInjection
import dev.vvasiliev.application.screen.detail.DetailScreen
import dev.vvasiliev.application.screen.songs.SongsScreen
import dev.vvasiliev.application.screen.songs.SongsViewModel
import dev.vvasiliev.view.composable.splash.screen.SplashScreen
import javax.inject.Inject

class Navigator @Inject constructor(
    private val viewModelFactory: ViewModelInjection,
    var navHostController: NavHostController
) {

    @Composable
    fun Navigation(controller: NavHostController = this.navHostController) {
        this.navHostController = controller
        NavHost(
            startDestination = Destination.MusicScreen.toString(),
            navController = navHostController
        ) {
            composable(Destination.SplashScreen.toString()) {
                SplashScreen()
            }
            composable(Destination.MusicScreen.toString()) {
                val viewModel =
                    (navHostController.context as ComponentActivity).viewModels<SongsViewModel> { viewModelFactory }
                SongsScreen(viewModel.value)
            }
            composable(
                Destination.MusicDetailedScreen.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                it.arguments?.getString("id")?.let { _ ->
                    DetailScreen(
                        detailViewModel = viewModel(
                            factory = DaggerDetailComponent.builder()
                                .bindsNavigationController(navHostController)
                                .build().detailsViewModelFactory
                        )
                    )
                }
            }
        }
    }
}


sealed class Destination(protected var route: String) {
    object SplashScreen : Destination("splash")
    object MusicScreen : Destination("music")
    class MusicDetailedScreen(private val id: Long) : Destination(route) {
        companion object {
            val route = "music/{id}"
        }

        fun applyId(): String {
            return route.replace("id", id.toString())
        }
    }

    override fun toString(): String {
        return route
    }
}