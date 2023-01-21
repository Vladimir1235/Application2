package dev.vvasiliev.application.screen.songs.usecase

import android.database.ContentObserver
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import javax.inject.Inject

class RegisterObserver @Inject constructor(
    private val storage: UriCollection<AudioFileCollection.Audio>
) {
    operator fun invoke(contentObserver: ContentObserver) {
        storage.registerCollectionUpdateCallback(contentObserver)
    }
}