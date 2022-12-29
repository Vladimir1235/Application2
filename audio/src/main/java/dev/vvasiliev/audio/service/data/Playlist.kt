package dev.vvasiliev.audio.service.data

import com.google.android.exoplayer2.MediaItem

sealed class Playlist<ItemType>(val title: String, open val items: List<ItemType>) {
    class AudioPlaylist(title: String, override val items: List<MediaItem>) :
        Playlist<MediaItem>(title, items)

    fun pickNext(current: ItemType): ItemType {
        val currentIndex = items.indexOf(current)
        return if (current != items.lastIndex)
            items[currentIndex + 1]
        else throw EndOfPlayListException()
    }

    fun pickPrevious(current: ItemType): ItemType {
        val currentIndex = items.indexOf(current)
        return if (currentIndex > 0)
            items[currentIndex - 1]
        else throw StartOfPlayListException()
    }

    @JvmName("pickNextSugar")
    fun ItemType.pickNext() =
        pickNext(this)

    @JvmName("pickPreviousSugar")
    fun ItemType.pickPrevious() =
        pickPrevious(this)

}

open class PlayListException(message: String) : Exception(message)

class EndOfPlayListException : PlayListException("There is no next song in playlist")
class StartOfPlayListException : PlayListException("You are at the start of playlist")
