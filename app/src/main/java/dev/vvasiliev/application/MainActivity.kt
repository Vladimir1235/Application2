package dev.vvasiliev.application

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.AppTheme
import dev.vvasiliev.application.ui.screen.songs.SongsScreen
import dev.vvasiliev.audio.service.AudioPlaybackService
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher.create
import dev.vvasiliev.view.composable.splash.screen.SplashScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val launcher = create(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startForegroundService(
            Intent(this.application.applicationContext, AudioPlaybackService::class.java)
        )

        setContent {
            var loading by remember { mutableStateOf(true) }

            LaunchedEffect(true) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000)
                    loading = false
                }
            }
            AppTheme {
                // A surface container using the 'background' color from the theme
                if (loading) SplashScreen() else SongsScreen()
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Column {

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        Greeting("Android")
    }
}