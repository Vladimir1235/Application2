package dev.vvasiliev.audio.service.util.player.updater

import com.google.android.exoplayer2.MediaItem
import dagger.assisted.AssistedFactory
import kotlin.reflect.KFunction

@AssistedFactory
interface PlayerProgressUpdateFactory {
    fun create(mediaItem: MediaItem, onCompositionEnd: () -> Unit): PlayerProgressUpdate
}