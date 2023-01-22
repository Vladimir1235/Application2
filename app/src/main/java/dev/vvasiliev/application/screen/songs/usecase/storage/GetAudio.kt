package dev.vvasiliev.application.screen.songs.usecase.storage

import dev.vvasiliev.application.screen.songs.mapper.MapMusicCardData
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.structures.android.UriCollection
import javax.inject.Inject

class GetAudio @Inject constructor(
    private val storage: UriCollection<AudioFileCollection.Audio>,
    private val mapper: MapMusicCardData,
) {
    operator fun invoke() =
        storage.getAllContent().map(mapper::mapAudioData).toMutableList()
}