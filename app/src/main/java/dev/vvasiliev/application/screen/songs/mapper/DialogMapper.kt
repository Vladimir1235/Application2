package dev.vvasiliev.application.screen.songs.mapper

import android.content.Context
import androidx.annotation.IdRes
import dev.vvasiliev.application.R
import dev.vvasiliev.view.composable.modular.dialog.DialogData
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import javax.inject.Inject

class DialogMapper @Inject constructor(private val context: Context) {

    fun mapInfoDialog(
        cardData: MusicCardData,
        onDismiss: () -> Unit
    ): DialogData.InformationDialog =
        DialogData.InformationDialog(
            title = string(R.string.info_dialog_title),
            info = "${
                string(
                    R.string.info_dialog_title,
                    cardData.title
                )
            }\n${
                string(
                    R.string.info_dialog_track_title,
                    cardData.author
                )
            }\n${
                string(
                    R.string.info_dialog_track_location,
                    cardData.uri
                )
            }\n${
                string(
                    R.string.info_dialog_track_duration,
                    cardData.duration
                )
            }",
            confirmText = string(R.string.info_dialog_confirm_btn_title),
            confirm = onDismiss,
            onCancel = onDismiss
        )

    fun mapRenameAudioDialog(
        cardData: MusicCardData,
        onComplete: (title: String, album: String, author: String, cardData: MusicCardData) -> Unit,
        onDismiss: () -> Unit
    ): DialogData.MusicEditTextDialogData =
        DialogData.MusicEditTextDialogData(
            title = "Edit Info",
            confirmText = string(R.string.edit_dialog_edit_confirm_btn),
            confirmData = { title, album, author ->
                //Update content values in external storage
                onComplete(title, album, author, cardData)
                onDismiss()
            },
            onCancel = onDismiss,
            songTitle = cardData.title,
            songAlbum = cardData.album,
            songAuthor = cardData.author,
            albumLabel = string(R.string.edit_dialog_edit_album_label),
            authorLabel = string(R.string.edit_dialog_edit_author_label),
            titleLabel = string(R.string.edit_dialog_edit_title_label)
        )

    private fun <T : Any> string(id: Int, arg: T) =
        context.getString(id, arg)

    private fun string(id: Int) =
        context.getString(id)
}