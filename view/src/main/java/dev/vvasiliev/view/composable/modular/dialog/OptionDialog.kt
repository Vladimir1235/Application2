package dev.vvasiliev.view.composable.modular.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData

@Composable
fun OptionDialog(dialogData: DialogData.OptionDialogData) {

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            dialogData.reject()
        },
        title = {
            Text(text = dialogData.title)
        },
        text = {
            Text(text = dialogData.info)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    dialogData.confirm()
                }
            ) {
                Text(dialogData.confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    dialogData.reject()
                }
            ) {
                Text(dialogData.rejectText)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicEditTextDialog(dialogData: DialogData.MusicEditTextDialogData) {
    var title by remember { mutableStateOf(dialogData.songTitle) }
    var album by remember { mutableStateOf(dialogData.songAlbum) }
    var author by remember { mutableStateOf(dialogData.songAuthor) }

    Dialog(onDismissRequest = { dialogData.onCancel() }) {
        Card {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(value = title, onValueChange = {
                    title = it
                }, label = {
                    Text(
                        text = "Song Title"
                    )
                })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = author, onValueChange = {
                    author = it
                }, label = {
                    Text(
                        text = "Song Author"
                    )
                })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = album, onValueChange = {
                    album = it
                }, label = {
                    Text(
                        text = "Song Album"
                    )
                })
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        dialogData.confirmData(
                            title,
                            album,
                            author
                        )
                    },
                    Modifier.fillMaxWidth()
                ) {
                    Text(text = dialogData.confirmText)
                }
            }
        }
    }
}

@Composable
fun InformationDialog(dialogData: DialogData.InformationDialog) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            dialogData.onCancel()
        },
        title = {
            Text(text = dialogData.title)
        },
        text = {
            Text(text = dialogData.info)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    dialogData.confirm()
                }
            ) {
                Text(dialogData.confirmText)
            }
        }
    )
}

sealed class DialogData(
    @Stable
    val title: String,
    @Stable
    val info: String,
    @Stable
    val confirmText: String,
    @Stable
    val confirm: () -> Unit,
    @Stable
    val onCancel: () -> Unit
) {
    @StableMarker
    class OptionDialogData(
        title: String,
        info: String,
        confirmText: String,
        confirm: () -> Unit,
        onCancel: () -> Unit,
        @Stable
        val rejectText: String,
        @Stable
        val reject: () -> Unit
    ) :
        DialogData(title, info, confirmText, confirm, onCancel) {
        companion object {
            fun mock() = OptionDialogData(
                title = "title",
                info = "info",
                confirmText = "OK",
                rejectText = "NO",
                confirm = {},
                reject = {},
                onCancel = {}
            )
        }
    }

    @StableMarker
    class MusicEditTextDialogData(
        title: String,
        info: String = "",
        confirmText: String,
        val confirmData: (title: String, album: String, author: String) -> Unit,
        onCancel: () -> Unit,
        @Stable
        val songTitle: String,
        @Stable
        val songAuthor: String,
        @Stable
        val songAlbum: String
    ) : DialogData(title, info, confirmText, confirm = {}, onCancel)

    @StableMarker
    class InformationDialog(
        title: String,
        info: String,
        confirmText: String,
        confirm: () -> Unit,
        onCancel: () -> Unit
    ) :
        DialogData(title, info, confirmText, confirm, onCancel)
}

@Composable
@Preview
fun DialogPreview() {
}