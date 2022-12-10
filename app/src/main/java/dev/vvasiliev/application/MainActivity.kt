package dev.vvasiliev.application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.vvasiliev.application.ui.theme.ApplicationTheme
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.AudioPlaybackService
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.view.composable.modular.ImageCard
import dev.vvasiliev.view.composable.modular.ImageCardModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var service: IAudioPlaybackService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForegroundService(
            Intent(this, AudioPlaybackService::class.java)
        )
        CoroutineScope(Dispatchers.IO).launch {
            service = AudioServiceConnector.getService(this@MainActivity)
        }
        setContent {
            ApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Column {
        Text(text = "Hello $name!")
        Column(
            Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                item { ImageCard(data = ImageCardModel.mock(), actionModel = {}) }
                item { Spacer(modifier = Modifier.width(12.dp)) }
                item { ImageCard(data = ImageCardModel.mock(), actionModel = {}) }
                item { Spacer(modifier = Modifier.width(12.dp)) }
                item { ImageCard(data = ImageCardModel.mock(), actionModel = {}) }
                item { Spacer(modifier = Modifier.width(12.dp)) }
                item { ImageCard(data = ImageCardModel.mock(), actionModel = {}) }
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                item { ImageCard(data = ImageCardModel.mock(), actionModel = {}) }
                item { Spacer(modifier = Modifier.width(12.dp)) }
                item { ImageCard(data = ImageCardModel.mock(), actionModel = {}) }
                item { Spacer(modifier = Modifier.width(12.dp)) }
                item { ImageCard(data = ImageCardModel.mock(), actionModel = {}) }
                item { Spacer(modifier = Modifier.width(12.dp)) }
                item { ImageCard(data = ImageCardModel.mock(), actionModel = {}) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ApplicationTheme {
        Greeting("Android")
    }
}