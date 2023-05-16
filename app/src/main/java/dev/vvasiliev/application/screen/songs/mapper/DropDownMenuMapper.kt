package dev.vvasiliev.application.screen.songs.mapper

import android.content.Context
import dev.vvasiliev.application.R
import dev.vvasiliev.application.screen.songs.SongScreenEvent
import dev.vvasiliev.view.composable.modular.music.MusicDropDownItems
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import javax.inject.Inject

class DropDownMenuMapper @Inject constructor(private val context: Context) {
    fun map(onEvent: (SongScreenEvent) -> Unit, data: MusicCardData) =
        MusicDropDownItems.MusicCardDropDownItems(
            onDeleteItemClick = {
                onEvent(SongScreenEvent.DeleteAudioItem(data.uri))
            },
            onShareItemClick = {
                onEvent(SongScreenEvent.ShareAudioItem(data.uri))
            },
            onInfoItemClick = {
                onEvent(SongScreenEvent.InfoAudioItem(data))
            },
            onRenameItemClicked = {
                onEvent(SongScreenEvent.RenameAudioItem(data))
            },
            deleteTitle = context.getString(R.string.drop_down_delete_title),
            infoTitle = context.getString(R.string.drop_down_info_title),
            renameTitle = context.getString(R.string.drop_down_edit_title),
            shareTitle = context.getString(R.string.drop_down_share_title)
        )

}