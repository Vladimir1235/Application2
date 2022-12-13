package dev.vvasiliev.application.ui.screen.songs

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.vvasiliev.view.composable.modular.MusicCard

@Composable
fun SongsScreen(viewModel: SongsViewModel) {

    LaunchedEffect(true) {
        viewModel.onCreate()
    }

    val music by viewModel.musicList.collectAsState()

    LazyColumn {
        items(count = music.size) { index ->
            with(music[index]) {
                MusicCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    data = this,
                    onStateChanged = { status ->
                        viewModel.onEvent(SongScreenEvent.StateChanged(music[index], status))
                    },
                    onPositionChanged = { position ->
                        viewModel.onEvent(SongScreenEvent.PositionChanged(music[index], position))
                    }
                )
            }
        }
    }
}