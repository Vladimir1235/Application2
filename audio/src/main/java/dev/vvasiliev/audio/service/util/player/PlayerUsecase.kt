package dev.vvasiliev.audio.service.util.player

import android.media.session.PlaybackState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dev.vvasiliev.audio.AudioEventListener
import dev.vvasiliev.audio.service.data.EndOfPlayListException
import dev.vvasiliev.audio.service.data.Playlist
import dev.vvasiliev.audio.service.data.StartOfPlayListException
import dev.vvasiliev.audio.service.util.player.updater.PlayerProgressUpdate
import dev.vvasiliev.audio.service.util.player.updater.PlayerProgressUpdateFactory
import javax.inject.Inject

interface PlayerUsecase {
    //Current media item
    fun setCurrent(mediaItem: MediaItem)
    fun isCurrent(mediaItem: MediaItem): Boolean
    fun isCurrent(id: Long): Boolean
    fun hasCurrent(): Boolean

    //State
    fun isPlaying(): Boolean
    fun getState(): Int
    suspend fun subscribeOnPositionChange(eventListener: AudioEventListener)
    suspend fun unsubscribeOnPositionChange(eventListener: AudioEventListener)
    fun requestPositionUpdates()
    fun cancelPositionUpdates()

    //Actions
    fun play()
    fun resume()
    fun stop()
    fun switchTo(position: Long)
    fun playNext()
    fun playPrevious()
}


class PlayerUsage @Inject constructor(
    private val player: ExoPlayer,
    private val specificThreadExecutor: ServiceSpecificThreadExecutor,
    private val updaterFactory: PlayerProgressUpdateFactory
) : PlayerUsecase {

    private val listeners: MutableMap<MediaItem, PlayerProgressUpdate> = mutableMapOf()

    private val playList: Playlist.AudioPlaylist? = null

    override fun setCurrent(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    override fun isCurrent(mediaItem: MediaItem): Boolean =
        isCurrent(mediaItem.mediaId.toLong())

    /**
     * @return true if [id] is id of [currentItem] and false if not or if [currentItem] is null
     */
    override fun isCurrent(id: Long): Boolean {
        return player.currentMediaItem?.let { currentItem ->
            currentItem.mediaId.toLong() == id
        } ?: false
    }

    override fun hasCurrent(): Boolean = player.currentMediaItem != null

    override fun isPlaying(): Boolean = player.playbackState == PlaybackState.STATE_PLAYING

    override fun getState(): Int = player.playbackState

    override suspend fun subscribeOnPositionChange(eventListener: AudioEventListener) {
        val updater = updaterFactory.create(getCurrentMedia())
        listeners[getCurrentMedia()] = updater
        updater.subscribeOnUpdates(eventListener)
    }

    override suspend fun unsubscribeOnPositionChange(eventListener: AudioEventListener) {
        listeners[getCurrentMedia()]?.unsubscribeOnUpdates(eventListener)
    }

    override fun requestPositionUpdates() {
        listeners[getCurrentMedia()]?.requestUpdates()
    }

    override fun cancelPositionUpdates() {
        listeners[getCurrentMedia()]?.cancelUpdates()
    }

    /**
     * Plays [currentItem]
     */
    override fun play() {
        player.play()
    }

    override fun resume() {
        player.prepare()
        play()
    }

    /**
     * Stop [player]
     */
    override fun stop() {
        player.stop()
    }

    /**
     * Seek playback to [position]
     */
    override fun switchTo(position: Long) {
        player.seekTo(position)
    }

    /**
     * Play next song in [playList]
     *
     * @throws EndOfPlayListException if [currentItem] is last one in current playlist
     * @throws EmptyPlaylistException if [playList] is not initialized
     */
    @Throws(EndOfPlayListException::class, EmptyPlaylistException::class)
    override fun playNext() {
        val next = playList?.run {
            getCurrentMedia().pickNext()
        } ?: throw EmptyPlaylistException(playList?.title ?: "isn't initialized and")
        setCurrent(next)
    }

    /**
     * Play previous song in [playList]
     *
     * @throws StartOfPlayListException if [currentItem] is first one in current playlist
     * @throws EmptyPlaylistException if [playList] is not initialized
     */
    @Throws(StartOfPlayListException::class, EmptyPlaylistException::class)
    override fun playPrevious() {
        val previous = playList?.run {
            getCurrentMedia().pickPrevious()
        } ?: throw EmptyPlaylistException(playList?.title ?: "isn't initialized and")
        setCurrent(previous)
    }

    private fun getCurrentMedia() =
        specificThreadExecutor.executeBlocking {
            player.currentMediaItem
        } ?: throw EmptyPlaylistException("standard query")
}

open class PlayerUsecaseException(override val message: String) : Exception(message)

class EmptyPlaylistException(playlistTitle: String) :
    PlayerUsecaseException("Playlist $playlistTitle is empty")