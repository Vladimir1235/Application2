package dev.vvasiliev.application

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import dev.vvasiliev.application.core.app.MyPlayerApp
import dev.vvasiliev.application.screen.navigation.Destination
import dev.vvasiliev.application.screen.navigation.Navigator
import dev.vvasiliev.audio.service.AudioPlaybackService
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher.create
import dev.vvasiliev.view.composable.splash.screen.SplashScreen
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    val launcher = create(this@MainActivity)

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inject This activity into dependency graph
        MyPlayerApp.dagger.mainActivityModule.injectMainActivity(this)

        startForegroundService(
            Intent(this.application.applicationContext, AudioPlaybackService::class.java)
        )

        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    navigator.Navigation(controller = rememberNavController())
                    navigator.navHostController.navigate(Destination.MusicScreen.toString())
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    SplashScreen()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        Greeting()
    }
}