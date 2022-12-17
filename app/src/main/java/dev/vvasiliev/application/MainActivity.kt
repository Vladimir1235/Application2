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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import dev.vvasiliev.application.core.app.MyPlayerApp
import dev.vvasiliev.application.screen.navigation.Navigation
import dev.vvasiliev.audio.service.AudioPlaybackService
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher.create
import dev.vvasiliev.view.composable.splash.screen.SplashScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    val launcher = create(this@MainActivity)

    @Inject
    lateinit var navHost: NavHostController

    @Inject
    lateinit var serviceConnector: AudioServiceConnector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inject This activity into dependency graph
        MyPlayerApp.dagger.mainActivityModule.injectMainActivity(this)

        startForegroundService(
            Intent(this.application.applicationContext, AudioPlaybackService::class.java)
        )
        CoroutineScope(Dispatchers.Main).launch {
            Timber.d("${serviceConnector.getService().state}")
        }

        setContent {
            AppTheme {
                navHost
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navigation = rememberNavController()
                    Navigation(controller = navigation)
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