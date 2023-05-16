package dev.vvasiliev.view.composable.modular.music

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import dev.vvasiliev.view.composable.primitive.InteractableProgress

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
                        modifier = Modifier.weight(0.8f),
                        text = data.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    menuItems?.let {
                        MusicDropDownMenu(
                            modifier = Modifier
                                .size(52.dp)
                                .weight(0.1f),
                            items = it
                        )
                    }
                }
                Text(
                    text = data.author,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = data.album,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = {
                                data.setPlayingStatus(!isPlaying)
                                onStateChanged(isPlaying)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(text = if (isPlaying) data.stopTitle else data.playTitle)
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
        menuItems = MusicDropDownItems.MusicCardDropDownItems("", "", "", "")
    )
}