package dev.vvasiliev.view.composable.modular.music

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties

@Composable
fun MusicDropDownMenu(items: MusicDropDownItems, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box (modifier = modifier){
        IconButton(onClick = { expanded = !expanded }) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "menuIcon")
        }
        if (expanded)
            Box {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = !expanded },
                    properties = PopupProperties()
                ) {
                    items.DropDownItems { expanded = !expanded }
                }
            }
    }
}

sealed class MusicDropDownItems {
    class MusicCardDropDownItems(
        private val onShareItemClick: () -> Unit = {},
        private val onDeleteItemClick: () -> Unit = {},
        private val onInfoItemClick: () -> Unit = {},
        private val onRenameItemClicked: () -> Unit = {}
    ) : MusicDropDownItems() {
        @Composable
        override fun DropDownItems(onItemClicked: () -> Unit) {
            DropdownMenuItem(
                text = { Text("Rename") },
                onClick = { onRenameItemClicked(); onItemClicked() },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "rename")
                })
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = { onDeleteItemClick(); onItemClicked() },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
                })
            DropdownMenuItem(
                text = { Text("Share") },
                onClick = { onShareItemClick(); onItemClicked() },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "share")
                }
            )
            Divider()
            DropdownMenuItem(
                text = { Text("Info") },
                onClick = { onInfoItemClick(); onItemClicked() },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "info")
                })
        }
    }

    @Composable
    abstract fun DropDownItems(onItemClicked: () -> Unit)
}

@Composable
@Preview
fun MusicDropDownPreview() {
    MusicDropDownMenu(MusicDropDownItems.MusicCardDropDownItems())
}