package dev.vvasiliev.application.screen.songs.mapper

import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import javax.inject.Inject

class MapMusicCardData @Inject constructor() {
    fun mapAudioData(data: AudioFileCollection.Audio) =
        MusicCardData(
            false,
            title = data.name,
            album = data.album,
            author = data.artist,
            duration = data.duration,
            id = data.id,
            uri = data.uri
        )
}