package dev.vvasiliev.application.screen.songs.mapper

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.vvasiliev.view.composable.modular.music.MusicCard
import dev.vvasiliev.view.composable.modular.music.MusicDropDownItems
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import javax.inject.Inject

class MusicCardMapper @Inject constructor() {
    fun map(
        musicCardData: MusicCardData,
        dropDownItems: MusicDropDownItems.MusicCardDropDownItems,
        onStateChanged: (Boolean) -> Unit,
        onPositionChanged: (Float) -> Unit
    ): @Composable () -> Unit {
        val card =
            @Composable {
                MusicCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    data = musicCardData,
                    onStateChanged = onStateChanged,
                    onPositionChanged = onPositionChanged,
                    menuItems = dropDownItems
                )
            }
        return card
    }
}