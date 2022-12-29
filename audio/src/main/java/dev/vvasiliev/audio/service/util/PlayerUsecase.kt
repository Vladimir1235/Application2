package dev.vvasiliev.audio.service.util

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dev.vvasiliev.audio.service.data.EndOfPlayListException
import dev.vvasiliev.audio.service.data.Playlist
import dev.vvasiliev.audio.service.data.StartOfPlayListException
import javax.inject.Inject

interface PlayerUsecase {
    fun setCurrent(mediaItem: MediaItem)
    fun play()
    fun stop()
    fun switchTo(position: Long)
    fun playNext()
    fun playPrevious()
}

class PlayerUsage @Inject constructor(private val player: ExoPlayer) : PlayerUsecase {
    var currentItem: MediaItem? = null
    private val playList: Playlist.AudioPlaylist? = null

    override fun setCurrent(mediaItem: MediaItem) {
        currentItem = mediaItem
        player.prepare()
    }

    override fun play() {
        player.play()
    }

    override fun stop() {
        player.stop()
    }

    override fun switchTo(position: Long) {
        player.seekTo(position)
    }

    @Throws(EndOfPlayListException::class, EmptyPlaylistException::class)
    override fun playNext() {
        val next = playList?.run {
            currentItem?.pickNext()
        } ?: throw EmptyPlaylistException(playList?.title ?: "isn't initialized and")
        setCurrent(next)
    }

    @Throws(StartOfPlayListException::class, EmptyPlaylistException::class)
    override fun playPrevious() {
        val previous = playList?.run {
            currentItem?.pickPrevious()
        } ?: throw EmptyPlaylistException(playList?.title ?: "isn't initialized and")
        setCurrent(previous)
    }
}

open class PlayerUsecaseException(override val message: String) : Exception(message)

class EmptyPlaylistException(playlistTitle: String) :
    PlayerUsecaseException("Playlist $playlistTitle is empty")