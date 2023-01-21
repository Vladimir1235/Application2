package dev.vvasiliev.view.composable.modular.music.data

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import dev.vvasiliev.view.R
import dev.vvasiliev.view.composable.modular.music.mapper.CardElementsMapper


@Immutable
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
    fun getDurationTime() = CardElementsMapper(
        timeString = stringResource(id = R.string.time_pending),
        progressString = stringResource(id = R.string.time_in_progress)
    ).run {
        if (playing.value) mapTimeStringWithProgress(
            time = duration,
            progress = position.value
        ) else mapTimeString(duration)
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