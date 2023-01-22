package dev.vvasiliev.application.screen.songs.mapper

import android.content.Context
import dev.vvasiliev.view.composable.modular.dialog.DialogData
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import javax.inject.Inject

class DialogMapper @Inject constructor(private val context: Context) {

    fun mapInfoDialog(
        cardData: MusicCardData,
        onDismiss: () -> Unit
    ): DialogData.InformationDialog =
        DialogData.InformationDialog(
            title = "Track Info",
            info = "Title: ${cardData.title}\nTrack Author: ${cardData.author}\nTrack Location: ${cardData.uri}\nTrack duration:${cardData.duration} ms",
            confirmText = "Ok",
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
            confirmText = "Ok",
            confirmData = { title, album, author ->
                //Update content values in external storage
                onComplete(title, album, author, cardData)
                onDismiss()
            },
            onCancel = onDismiss,
            songTitle = cardData.title,
            songAlbum = cardData.album,
            songAuthor = cardData.author,
        )
}