package dev.vvasiliev.application.screen.songs

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.structures.android.operation.ContentDeletionLauncher
import dev.vvasiliev.view.composable.modular.music.MusicCard
import dev.vvasiliev.view.composable.modular.music.MusicDropDownItems

@Composable
fun SongsScreen(viewModel: SongsViewModel = viewModel()) {

    LaunchedEffect(true) {
        viewModel.onCreate()
    }

    val music by viewModel.musicList.collectAsState()
    val serviceStatus by viewModel.serviceStatus.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxHeight()
    ) {
        item { Text(text = "Service Status: $serviceStatus") }
        items(count = music.size) { index ->
            with(music[index]) {
                MusicCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    data = this,
                    onClick = { viewModel.onEvent(SongScreenEvent.CardClickEvent(uri)) },
                    onStateChanged = { status ->
                        viewModel.onEvent(
                            if (status) SongScreenEvent.PlayEvent(music[index])
                            else SongScreenEvent.StopEvent()
                        )
                    },
                    onPositionChanged = { position ->
                        viewModel.onEvent(SongScreenEvent.PositionChanged(music[index], position))
                    },
                    menuItems = MusicDropDownItems.MusicCardDropDownItems(onDeleteItemClick = {
                        viewModel.onEvent(SongScreenEvent.DeleteAudioItem(uri))
                    }, onShareItemClick = {
                        viewModel.onEvent(SongScreenEvent.ShareAudioItem(uri))
                    })
                )
            }
        }
    }
}