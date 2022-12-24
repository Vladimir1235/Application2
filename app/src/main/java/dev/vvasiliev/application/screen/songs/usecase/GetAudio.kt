package dev.vvasiliev.application.screen.songs.usecase

import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import dev.vvasiliev.view.composable.modular.music.MusicCardData
import javax.inject.Inject

class GetAudio @Inject constructor(
    private val storage: UriCollection<AudioFileCollection.Audio>
) {
    operator fun invoke() =
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
}