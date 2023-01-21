package dev.vvasiliev.application.screen.songs.usecase

import android.net.Uri
import androidx.activity.result.IntentSenderRequest
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import javax.inject.Inject

class DeleteAudio @Inject constructor(
    private val storage: UriCollection<AudioFileCollection.Audio>
) {
    fun createDeletionRequest(uri: Uri) =
        storage.requestDeleteContent(uri)?.let { deletionIntent ->
            IntentSenderRequest.Builder(deletionIntent.intentSender).build()
        }
}