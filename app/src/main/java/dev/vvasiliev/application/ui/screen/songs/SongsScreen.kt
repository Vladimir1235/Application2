package dev.vvasiliev.application.ui.screen.songs

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.data.EventListener
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
    fun getService() = service?.get()!!

    LaunchedEffect(true) {
        CoroutineScope(Dispatchers.IO).launch {
            service = SoftReference(AudioServiceConnector(context).getService())
            service?.get()?.run {
                ReadStoragePermissionLauncher.requestExternalStorage()
            }
        }
    }

    val musicData = AudioFileCollection(context).getAllContent().map {
        MusicCardData(
            false,
            title = it.name,
            album = it.album,
            author = it.artist,
            duration = it.duration,
            id = it.id,
            uri = it.uri
        )
    }

    LazyColumn {
        items(count = musicData.size) { index ->
            with(musicData[index]) {
                MusicCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    data = this,
                    onStateChanged = { status ->
                        getService().run {
                            when (status) {
                                true -> {
                                    val startingPosition = (position.value * duration).toLong()
                                    play(
                                        uri, id,
                                        EventListener(
                                            onChange = { position -> setPlayingPosition((position.toFloat() / duration)) },
                                            onPlaybackStopped = { setPlayingStatus(false) }
                                        ), startingPosition
                                    )
                                }
                                false -> {
                                    if (isCurrent(id))
                                        stopCurrent()
                                }
                            }
                        }
                    },
                    onPositionChanged = { position ->
                        if (getService().isCurrent(id)) {
                            val positionMs = (position * duration).toLong()
                            getService().seekTo(positionMs)
                        }
                    }
                )
            }
        }
    }
}