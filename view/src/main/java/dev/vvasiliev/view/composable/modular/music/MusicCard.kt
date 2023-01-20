package dev.vvasiliev.view.composable.modular.music

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.vvasiliev.view.R
import dev.vvasiliev.view.composable.primitive.InteractableProgress
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MusicCard(
    modifier: Modifier = Modifier,
    data: MusicCardData,
    onStateChanged: (Boolean) -> Unit,
    onPositionChanged: (Float) -> Unit,
    onClick: () -> Unit = {},
    menuItems: MusicDropDownItems? = null
) {
    val progressState by remember { data.position }
    val isPlaying by remember { data.playing }

    Card(modifier = modifier) {
        Box {
            Column(
                Modifier
                    .clickable { onClick() }
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = data.title,
                        style = MaterialTheme.typography.titleMedium
                    ); menuItems?.let { MusicDropDownMenu(items = it) }
                }
                Text(text = data.author, style = MaterialTheme.typography.bodyMedium)
                Text(text = data.album, style = MaterialTheme.typography.bodySmall)

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
                        Box {
                            Text(
                                text = data.getDurationTime(),
                                modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 16.dp, top = 24.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                            InteractableProgress(
                                Modifier
                                    .height(10.dp)
                                    .padding(horizontal = 8.dp)
                                    .align(Alignment.Center),
                                progressState = progressState,
                                onStateChanged = { position ->
                                    data.setPlayingPosition(position)
                                    onPositionChanged(position)
                                })
                        }
                    }
                }
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
        onPositionChanged = data::setPlayingPosition,
        menuItems = MusicDropDownItems.MusicCardDropDownItems()
    )
}

@Stable
class MusicCardData(
    isPlaying: Boolean = false,
    private val _status: MutableState<Boolean> = mutableStateOf(isPlaying),
    private val _position: MutableState<Float> = mutableStateOf(0f),
    val playing: State<Boolean> = _status,
    val position: State<Float> = _position,
    @Stable
    val title: String,
    @Stable
    val author: String,
    @Stable
    val album: String,
    @Stable
    val duration: Long,
    @Stable
    val uri: Uri,
    @Stable
    val id: Long
) {
    fun setPlayingStatus(state: Boolean) {
        _status.value = state
    }

    fun setPlayingPosition(position: Float) {
        _position.value = position
    }

    fun updatePosition(position: Long) {
        setPlayingPosition((position.toFloat() / duration))
    }

    @Composable
    fun getDurationTime() = duration.milliseconds.toComponents { minutes, seconds, nanoseconds ->
        val inprogressString = stringResource(id = R.string.time_in_progress)
        val pendingString = stringResource(id = R.string.time_pending)

        val progress = (position.value * duration).toLong().milliseconds

        return@toComponents if (playing.value)
            progress.toComponents { pminutes, pseconds, pnanoseconds ->
                String.format(
                    inprogressString,
                    pminutes,
                    if (pseconds < 10) "0$pseconds" else pseconds,
                    minutes,
                    if (seconds < 10) "0$seconds" else seconds
                )
            } else String.format(
            pendingString,
            minutes,
            if (seconds < 10) "0$seconds" else seconds
        )
    }

    companion object {
        fun mock() = MusicCardData(
            false,
            title = "SongTitle",
            author = "Author Name",
            album = "Album title",
            duration = 14880,
            uri = Uri.EMPTY,
            id = 0
        )
    }
}