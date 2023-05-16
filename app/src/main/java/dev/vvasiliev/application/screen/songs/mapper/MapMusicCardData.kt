package dev.vvasiliev.application.screen.songs.mapper

import android.content.Context
import dev.vvasiliev.application.R
import dev.vvasiliev.structures.android.AudioFileCollection
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import javax.inject.Inject

class MapMusicCardData @Inject constructor(val context: Context) {
    fun mapAudioData(data: AudioFileCollection.Audio) =
        MusicCardData(
            false,
            title = data.name,
            album = data.album,
            author = data.artist,
            duration = data.duration,
            id = data.id,
            uri = data.uri,
            playTitle = context.getString(R.string.play_btn_title),
            stopTitle = context.getString(R.string.stop_btn_title)
        )
}