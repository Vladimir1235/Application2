package dev.vvasiliev.application.screen.songs.usecase

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import dev.vvasiliev.structures.android.operation.ContentDeletionLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class Audio @Inject constructor(
    private val getAudio: GetAudio,
    private val deleteAudio: DeleteAudio,
    private val shareAudio: ShareAudio,
    private val registerObserver: RegisterObserver
) {
    fun getMusic() = getAudio()
    fun removeSong(context: Context, uri: Uri, coroutineScope: CoroutineScope) {
        deleteAudio.createDeletionRequest(uri)?.let { sender ->
            coroutineScope.launch { ContentDeletionLauncher.launch(context, sender) }
        }
    }

    fun shareSong(context: Context, uri: Uri, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            shareAudio(context, uri)
        }
    }

    fun registerContentObserver(contentObserver: ContentObserver) {
        registerObserver(contentObserver)
    }

}