package dev.vvasiliev.application.screen.songs.usecase

import android.database.ContentObserver
import android.net.Uri
import androidx.activity.result.IntentSenderRequest
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import dev.vvasiliev.view.composable.modular.music.MusicCardData
import timber.log.Timber
import javax.inject.Inject

class Audio @Inject constructor(
    private val storage: UriCollection<AudioFileCollection.Audio>
) {

    operator fun invoke(contentObserver: ContentObserver): MutableList<MusicCardData> {
        storage.registerCollectionUpdateCallback(contentObserver)
        return get()
    }

    fun get() =
        storage.getAllContent().map {
            MusicCardData(
                false,
                title = it.name,
                album = it.album,
                author = it.artist,
                duration = it.duration,
                id = it.id,
                uri = it.uri
            )
        }.toMutableList()

    fun createDeletionRequest(uri: Uri) =
        storage.requestDeleteContent(uri)?.let { deletionIntent ->
            IntentSenderRequest.Builder(deletionIntent.intentSender).build()
        }

}