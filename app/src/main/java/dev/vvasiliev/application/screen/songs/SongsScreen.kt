package dev.vvasiliev.application.screen.songs

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.vvasiliev.view.composable.modular.dialog.DialogData
import dev.vvasiliev.view.composable.modular.dialog.InformationDialog
import dev.vvasiliev.view.composable.modular.dialog.MusicEditTextDialog
import dev.vvasiliev.view.composable.modular.dialog.OptionDialog
import dev.vvasiliev.view.composable.modular.music.MusicCard
import dev.vvasiliev.view.composable.modular.music.MusicDropDownItems

@Composable
fun SongsScreen(viewModel: SongsViewModel) {

    val music by viewModel.musicList.collectAsState()
    val serviceStatus by viewModel.serviceStatus.collectAsState()
    val dialog by remember { viewModel.dialogState }

    when (dialog) {
        is DialogData.MusicEditTextDialogData -> {
            dialog?.let { MusicEditTextDialog(dialogData = it as DialogData.MusicEditTextDialogData) }
        }
        is DialogData.InformationDialog -> {
            dialog?.let { InformationDialog(dialogData = it as DialogData.InformationDialog) }
        }
        is DialogData.OptionDialogData -> {
            dialog?.let { OptionDialog(dialogData = it as DialogData.OptionDialogData) }
        }
        null -> {}
    }

    LazyColumn(
        modifier = Modifier.fillMaxHeight()
    ) {
        item { Text(text = "Service Status: $serviceStatus") }
        items(count = music.size) { index ->
            viewModel.getCardComposable(index)?.invoke()
        }
    }
}