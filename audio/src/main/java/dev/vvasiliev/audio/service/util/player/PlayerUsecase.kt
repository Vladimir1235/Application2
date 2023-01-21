package dev.vvasiliev.audio.service.util.player

import android.media.session.PlaybackState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dev.vvasiliev.audio.AudioEventListener
import dev.vvasiliev.audio.service.broadcast.NotificationBroadcastReceiver
import dev.vvasiliev.audio.service.data.EndOfPlayListException
import dev.vvasiliev.audio.service.data.Playlist
import dev.vvasiliev.audio.service.data.StartOfPlayListException
import dev.vvasiliev.audio.service.notifications.audio.AudioNotificationManager
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.audio.service.state.holder.ServiceEventPipeline
import dev.vvasiliev.audio.service.util.player.updater.PlayerProgressUpdate
import dev.vvasiliev.audio.service.util.player.updater.PlayerProgressUpdateFactory
import javax.inject.Inject

interface PlayerUsecase {
    //Current media item
    fun setCurrent(mediaItem: MediaItem)
    fun isCurrent(mediaItem: MediaItem): Boolean
    fun isCurrent(id: Long): Boolean
    fun hasCurrent(): Boolean
    fun getCurrentSongId(): Long?

    //State
    fun isPlaying(): Boolean
    fun getState(): Int
    fun subscribeOnPositionChange(eventListener: AudioEventListener)
    fun unsubscribeOnPositionChange(eventListener: AudioEventListener)
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
    private val executor: ServiceSpecificThreadExecutor,
    private val updaterFactory: PlayerProgressUpdateFactory,
    private val state: ServiceEventPipeline,
    private val notifications: AudioNotificationManager
) : PlayerUsecase {

    private val listeners: MutableMap<MediaItem, PlayerProgressUpdate> = mutableMapOf()

    private val playList: Playlist.AudioPlaylist? = null

    private val notificationBroadcastReceiver = NotificationBroadcastReceiver(this);

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

    override fun hasCurrent(): Boolean =
        executor.executeBlocking { player.currentMediaItem != null }

    override fun getCurrentSongId(): Long? =
        executor.executeBlocking { player.currentMediaItem?.mediaId?.toLong() }

    override fun isPlaying(): Boolean =
        executor.executeBlocking { player.playbackState == PlaybackState.STATE_PLAYING }

    override fun getState(): Int = executor.executeBlocking { player.playbackState }

    override fun subscribeOnPositionChange(eventListener: AudioEventListener) {
        val updater = updaterFactory.create(getCurrentMedia(), ::onCompositionEnd)

        if (listeners[getCurrentMedia()]?.hasSubscribers() == true) {
            listeners[getCurrentMedia()]?.subscribeOnUpdates(eventListener)
        } else {
            listeners[getCurrentMedia()] = updater
        }

        updater.subscribeOnUpdates(eventListener)
    }

    override fun unsubscribeOnPositionChange(eventListener: AudioEventListener) {
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
        executor.executeBlocking {
            player.play()
        }

        state.onServiceStateChanged(AudioServiceState.PLAYING)
        getCurrentSongId()?.let(state::onPlaybackStarted)
        notifications.showPlayNotification(getCurrentMedia().mediaMetadata)
    }

    override fun resume() {
        executor.executeBlocking {
            player.prepare()
            play()
        }
    }

    /**
     * Stop [player]
     */
    override fun stop() {
        executor.executeBlocking {
            player.stop()
            cancelPositionUpdates()

            state.onServiceStateChanged(AudioServiceState.STOPPED)
            getCurrentSongId()?.let(state::onPlaybackStopped)

            notifications.showStoppedNotification(getCurrentMedia().mediaMetadata)
        }
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

    private fun onCompositionEnd() {
        player.seekTo(0)
        cancelPositionUpdates()
        stop()
    }

    private fun getCurrentMedia() =
        executor.executeBlocking {
            player.currentMediaItem
        } ?: throw EmptyPlaylistException("standard query")
}

open class PlayerUsecaseException(override val message: String) : Exception(message)

class EmptyPlaylistException(playlistTitle: String) :
    PlayerUsecaseException("Playlist $playlistTitle is empty")