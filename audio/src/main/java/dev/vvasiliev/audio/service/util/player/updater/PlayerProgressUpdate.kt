package dev.vvasiliev.audio.service.util.player.updater

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.vvasiliev.audio.AudioEventListener
import dev.vvasiliev.audio.service.util.player.ServiceSpecificThreadExecutor
import kotlinx.coroutines.*

private const val UPDATE_INTERVAL = 150L


/**
 * An updater for single MediaItem
 *
 * The idea is player may have lot of subscribers accessing its progress state, so you can subscribe as many listeners as you need
 */
class PlayerProgressUpdate @AssistedInject constructor(
    private val executor: ServiceSpecificThreadExecutor,
    private val player: ExoPlayer,
    @Assisted mediaItem: MediaItem
) {
    /**
     * Subscribers are combined into [listeners] where [Pair.first] is [listener][AudioEventListener] and [Pair.second] is [Job]
     *
     * [AudioEventListener] just a listener to get progress state callback
     * [Job] is context for [CoroutineScope] on which updates will be executed
     */
    private val listeners: MutableMap<AudioEventListener, Job> = mutableMapOf()
    private val rootScope = Dispatchers.IO


    fun subscribeOnUpdates(listener: AudioEventListener) {
        listeners[listener] = Job()
    }

    fun unsubscribeOnUpdates(listener: AudioEventListener) {
        listeners[listener]?.cancel()
        listeners.remove(listener)
    }

    fun cancelUpdates() {
        listeners.keys.forEach(AudioEventListener::onPlaybackStopped)
        unsubscribeAll()
    }

    /**
     * Should be called only after listeners were set up
     */
    fun requestUpdates() {
        listeners.toList().forEach { pair: Pair<AudioEventListener, Job> ->
            if (!pair.second.isCancelled && !pair.second.isCompleted) {
                startUpdates(pair)
            }
        }
    }

    private fun unsubscribeAll() {
        listeners.keys.forEach(::unsubscribeOnUpdates)
        rootScope.cancel()
    }

    private fun startUpdates(listener: Pair<AudioEventListener, Job>) {
        CoroutineScope(rootScope + listener.second).launch {
            while (true) {
                listener.first.onPositionChange(executor.executeBlocking {
                    val position = player.currentPosition;
                    position
                })
                delay(UPDATE_INTERVAL)
            }
        }
    }
}
