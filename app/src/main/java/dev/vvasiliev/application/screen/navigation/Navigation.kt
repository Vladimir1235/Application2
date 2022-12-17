package dev.vvasiliev.application.screen.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.vvasiliev.application.screen.songs.SongsScreen
import dev.vvasiliev.view.composable.splash.screen.SplashScreen

@Composable
fun Navigation(controller: NavHostController) {
    NavHost(
        startDestination = Destination.SplashScreen.toString(),
        navController = controller
    ) {
        composable(Destination.SplashScreen.toString()) {
            SplashScreen()
        }
        composable(Destination.MusicScreen.toString()) {
            SongsScreen()
        }
        composable(
            Destination.MusicDetailedScreen.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            it.arguments?.getString("id")?.let { _ ->}
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

        fun routeReal(): String {
            return route.replace("id", id.toString())
        }
    }

    override fun toString(): String {
        return route
    }
}