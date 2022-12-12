package dev.vvasiliev.view.composable.modular

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.vvasiliev.view.composable.primitive.InteractableProgress

@Composable
fun MusicCard(data: MusicCardData, onStateChanged: (Boolean) -> Unit) {

    var progressState by remember {
        mutableStateOf(0.4f)
    }

    val isPlaying by remember { data.playing }
    Card {
        Column(Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = data.title, style = MaterialTheme.typography.titleMedium)
                Text(text = data.duration, style = MaterialTheme.typography.labelSmall)

            }
            Text(text = data.author, style = MaterialTheme.typography.bodyMedium)
            Text(text = data.album, style = MaterialTheme.typography.bodySmall)
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = { onStateChanged(!isPlaying); data.setPlayingStatus(!isPlaying); },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = if (isPlaying) "Stop" else "Play")
                }
                InteractableProgress(
                    Modifier
                        .height(8.dp)
                        .padding(horizontal = 8.dp),
                    progressState = progressState,
                    onStateChanged = {
                        progressState = it
                    })
            }
        }
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun MusicCardPreview() {
    val data = MusicCardData.mock()
    MusicCard(data = data) {
        data.setPlayingStatus(it)
    }
}

class MusicCardData(
    isPlaying: Boolean = false,
    private val _status: MutableState<Boolean> = mutableStateOf(isPlaying),
    val playing: State<Boolean> = _status,
    val title: String,
    val author: String,
    val album: String,
    val duration: String
) {
    fun setPlayingStatus(state: Boolean) {
        _status.value = state
    }

    companion object {
        fun mock() = MusicCardData(
            false,
            title = "SongTitle",
            author = "Author Name",
            album = "Album title",
            duration = "4.12"
        )
    }
}