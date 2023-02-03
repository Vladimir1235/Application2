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
    @Assisted private val mediaItem: MediaItem,
    @Assisted private val onCompositionEnd: () -> Unit
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

    fun hasSubscribers() = listeners.keys.size > 0

    private fun unsubscribeAll() {
        val array = listeners.keys.toTypedArray()
        /**
         * Why you should not ever use forEach and for(i in variable: Type)
         * cause if you'll try modify variable while iterating it'll fuck up ([ConcurrentModificationException])
         * Use usual for loop instead
         *
         * Don't use List, Set and subtypes as well.
         * You continuously moving through collection deleting elements and it's size decreases,
         * as you getting size of collection at start of loop, it contains initial size for example 10,
         * at 6th iteration size will be 5 (as you already deleted 5 elements)
         * as result you trying to access list[5] and get [IndexOutOfBoundsException]
         * Use array instead
         */
        for (index in 0 until listeners.keys.size) {
            unsubscribeOnUpdates(array[index])
        }
        rootScope.cancel()
    }

    private fun startUpdates(listener: Pair<AudioEventListener, Job>) {
        CoroutineScope(rootScope + listener.second).launch {
            while (true) {
                try {
                    listener.first.onPositionChange(executor.executeBlocking {
                        val position = player.currentPosition;

                        player.ifEnded(position) {
                            onCompositionEnd()
                        }

                        position
                    })
                } catch (illegalState: IllegalStateException) {
                    illegalState.printStackTrace()
                }
                delay(UPDATE_INTERVAL)
            }
        }
    }


    private fun ExoPlayer.ifEnded(position: Long, block: () -> Unit) =
        if (duration in 0..position) block() else Unit
}
