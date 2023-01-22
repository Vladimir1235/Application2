package dev.vvasiliev.application.screen.songs.usecase.storage

import android.database.ContentObserver
import android.net.Uri
import dev.vvasiliev.structures.android.operation.ContentDeletionLauncher
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class Audio @Inject constructor(
    private val getAudio: GetAudio,
    private val deleteAudio: DeleteAudio,
    private val shareAudio: ShareAudio,
    private val registerObserver: RegisterObserver,
    private val updateSong: UpdateSong
) {
    fun getAll() = getAudio()

    fun remove(uri: Uri, coroutineScope: CoroutineScope) {
        deleteAudio.createDeletionRequest(uri)?.let { sender ->
            coroutineScope.launch { ContentDeletionLauncher.launch(sender) }
        }
    }

    fun share(uri: Uri, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            shareAudio(uri)
        }
    }

    fun update(cardData: MusicCardData, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            updateSong(cardData)
        }
    }

    fun registerContentObserver(contentObserver: ContentObserver) {
        registerObserver(contentObserver)
    }

}