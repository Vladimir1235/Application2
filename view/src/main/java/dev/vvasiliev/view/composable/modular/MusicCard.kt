package dev.vvasiliev.view.composable.modular

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
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
fun MusicCard(
    modifier: Modifier = Modifier,
    data: MusicCardData,
    onStateChanged: (Boolean) -> Unit,
    onPositionChanged: (Float) -> Unit
) {

    val progressState by remember { data.position }
    val isPlaying by remember { data.playing }

    Card(modifier = modifier) {
        Column(Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = data.title, style = MaterialTheme.typography.titleMedium)
                Text(text = data.getDurationTime(), style = MaterialTheme.typography.labelSmall)

            }
            Text(text = data.author, style = MaterialTheme.typography.bodyMedium)
            Text(text = data.album, style = MaterialTheme.typography.bodySmall)
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = {
                        data.setPlayingStatus(!isPlaying)
                        onStateChanged(isPlaying)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = if (isPlaying) "Stop" else "Play")
                }
                InteractableProgress(
                    Modifier
                        .height(8.dp)
                        .padding(horizontal = 8.dp),
                    progressState = progressState,
                    onStateChanged = { position ->
                        data.setPlayingPosition(position)
                        onPositionChanged(position)
                    })
            }
        }
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun MusicCardPreview() {
    val data = MusicCardData.mock()
    MusicCard(
        data = data,
        onStateChanged = data::setPlayingStatus,
        onPositionChanged = data::setPlayingPosition
    )
}

class MusicCardData(
    isPlaying: Boolean = false,
    private val _status: MutableState<Boolean> = mutableStateOf(isPlaying),
    private val _position: MutableState<Float> = mutableStateOf(0f),
    val playing: State<Boolean> = _status,
    val position: State<Float> = _position,
    val title: String,
    val author: String,
    val album: String,
    val duration: Long,
    val uri: Uri,
    val id: Long
) {
    fun setPlayingStatus(state: Boolean) {
        _status.value = state
    }

    fun setPlayingPosition(position: Float) {
        _position.value = position
    }

    fun updatePosition(position: Long){
        setPlayingPosition((position.toFloat() / duration))
    }

    fun getDurationTime() = duration.toString()

    companion object {
        fun mock() = MusicCardData(
            false,
            title = "SongTitle",
            author = "Author Name",
            album = "Album title",
            duration = 0,
            uri = Uri.EMPTY,
            id = 0
        )
    }
}