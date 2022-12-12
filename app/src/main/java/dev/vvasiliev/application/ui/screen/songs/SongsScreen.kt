package dev.vvasiliev.application.ui.screen.songs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher
import dev.vvasiliev.view.composable.modular.MusicCard
import dev.vvasiliev.view.composable.modular.MusicCardData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference

@Composable
fun SongsScreen() {
    val context = LocalContext.current
    var service: SoftReference<IAudioPlaybackService>? = null

    LaunchedEffect(true) {
        CoroutineScope(Dispatchers.IO).launch {
            service = SoftReference(AudioServiceConnector(context).getService())
            service?.get()?.run {
                ReadStoragePermissionLauncher.requestExternalStorage()
            }
        }
    }

    val songs = AudioFileCollection(context).getAllContent()

    Column {
        songs.forEach { audio ->
            MusicCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                data = MusicCardData(
                    false,
                    title = audio.name,
                    album = audio.album,
                    author = audio.artist,
                    duration = ((audio.duration / 1000).toFloat() / 60).toString()
                ), onStateChanged = { status ->
                    service?.get()?.run {
                        if (status) play(audio.uri) else stopCurrent()
                    }
                }
            )
        }
    }
}