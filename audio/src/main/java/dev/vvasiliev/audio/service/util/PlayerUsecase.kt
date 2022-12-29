package dev.vvasiliev.audio.service.util

import android.media.session.PlaybackState
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dev.vvasiliev.audio.AudioEventListener
import dev.vvasiliev.audio.service.data.EndOfPlayListException
import dev.vvasiliev.audio.service.data.Playlist
import dev.vvasiliev.audio.service.data.StartOfPlayListException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
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
    fun requestPositionUpdates()
    fun cancelPositionUpdates()

    //Actions
    fun play()
    fun stop()
    fun switchTo(position: Long)
    fun playNext()
    fun playPrevious()
}

private const val UPDATE_INTERVAL = 150L

class PlayerUsage @Inject constructor(
    private val player: ExoPlayer,
    private val executor: ServiceSpecificThreadExecutor
) : PlayerUsecase {
    var currentItem: MediaItem? = null
    private val playList: Playlist.AudioPlaylist? = null
    private val positionUpdateChannel = Channel<Long>()
    private var updatesRequired: Boolean = false

    override fun setCurrent(mediaItem: MediaItem) {
        currentItem = mediaItem
        player.setMediaItem(currentItem!!)
        player.prepare()
    }

    override fun isCurrent(mediaItem: MediaItem): Boolean =
        isCurrent(mediaItem.mediaId.toLong())


    /**
     * @return true if [id] is id of [currentItem] and false if not or if [currentItem] is null
     */
    override fun isCurrent(id: Long): Boolean {
        return currentItem?.let { currentItem ->
            currentItem.mediaId.toLong() == id
        } ?: false
    }

    override fun hasCurrent(): Boolean = currentItem != null

    override fun isPlaying(): Boolean = player.playbackState == PlaybackState.STATE_PLAYING

    override fun getState(): Int = player.playbackState

    /**
     * Subscribe on [positionUpdateChannel] to get actual playback position values
     */
    override suspend fun subscribeOnPositionChange(eventListener: AudioEventListener) {
        positionUpdateChannel.receiveAsFlow().collect(eventListener::onPositionChange)
//        eventListener.onPositionChange(positionUpdateChannel.receive())
    }

    override fun requestPositionUpdates() {
        updatesRequired = true
        CoroutineScope(Dispatchers.IO).launch {
            while (updatesRequired) {
                positionUpdateChannel.send(executor.executeBlocking { player.currentPosition })
                delay(UPDATE_INTERVAL)
            }
        }
    }

    override fun cancelPositionUpdates() {
        updatesRequired = false
    }


    /**
     * Plays [currentItem]
     */
    override fun play() {
        player.play()
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
            currentItem?.pickNext()
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
            currentItem?.pickPrevious()
        } ?: throw EmptyPlaylistException(playList?.title ?: "isn't initialized and")
        setCurrent(previous)
    }
}

open class PlayerUsecaseException(override val message: String) : Exception(message)

class EmptyPlaylistException(playlistTitle: String) :
    PlayerUsecaseException("Playlist $playlistTitle is empty")