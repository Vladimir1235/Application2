package dev.vvasiliev.audio.service.util.player.updater

import com.google.android.exoplayer2.MediaItem
import dagger.assisted.AssistedFactory

@AssistedFactory
interface PlayerProgressUpdateFactory {
    fun create(mediaItem: MediaItem): PlayerProgressUpdate
}