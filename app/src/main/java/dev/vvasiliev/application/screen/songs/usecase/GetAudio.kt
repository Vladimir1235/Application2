package dev.vvasiliev.application.screen.songs.usecase

import android.database.ContentObserver
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import dev.vvasiliev.view.composable.modular.music.MusicCardData
import javax.inject.Inject

class GetAudio @Inject constructor(
    private val storage: UriCollection<AudioFileCollection.Audio>
) {

    operator fun invoke(contentObserver: ContentObserver): MutableList<MusicCardData> {
        storage.registerCollectionUpdateCallback(contentObserver)
        return invoke()
    }

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