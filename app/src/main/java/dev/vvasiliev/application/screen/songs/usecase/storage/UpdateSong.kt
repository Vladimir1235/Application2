package dev.vvasiliev.application.screen.songs.usecase.storage

import androidx.activity.result.IntentSenderRequest
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import dev.vvasiliev.structures.android.operation.ContentEditionRequestLauncher
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import javax.inject.Inject

class UpdateSong @Inject constructor(
    private val storage: UriCollection<AudioFileCollection.Audio>
) {
    suspend operator fun invoke(cardData: MusicCardData) =

        storage.updateContent(
            input = cardData.toAudioContent()
        )?.let { pendingIntent ->
            ContentEditionRequestLauncher.launch(
                IntentSenderRequest.Builder(pendingIntent.intentSender).build()
            )
            storage.updateContent(input = cardData.toAudioContent())
        }

    private fun MusicCardData.toAudioContent() =
        AudioFileCollection.Audio(
            artist = author,
            album = album,
            uri = uri,
            id = id,
            duration = duration,
            name = title
        )

}
