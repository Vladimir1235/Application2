package dev.vvasiliev.application.core.config

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.core.util.Consumer
import dev.vvasiliev.audio.service.AudioPlaybackService
import dev.vvasiliev.audio.service.event.PlaybackStarted
import dev.vvasiliev.audio.service.event.PlaybackStopped
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.audio.service.state.AudioServiceStateListener
import dev.vvasiliev.audio.service.state.holder.ServiceEventPipeline
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AppConfigurator @Inject constructor(
    private val context: Context,
    private val configuration: AppConfiguration,
    private val serviceConnector: AudioServiceConnector,
    private val coroutineContext: CoroutineContext
) : Consumer<Configuration> {

    fun configure() {
        startMusicService(context, AudioPlaybackService::class.java)
    }

    private val listener = AudioServiceStateListener(object : ServiceEventPipeline {
        override fun onPlaybackStopped(songId: Long) {
            CoroutineScope(coroutineContext).launch {
                configuration.servicePlaybackStatus.emit(
                    PlaybackStopped(songId)
                )
            }
        }

        override fun onPlaybackStarted(songId: Long) {
            CoroutineScope(coroutineContext).launch {
                configuration.servicePlaybackStatus.emit(
                    PlaybackStarted(songId)
                )
            }
        }

        override fun onServiceStateChanged(state: AudioServiceState) {
            CoroutineScope(coroutineContext).launch {
                configuration.serviceStatus.emit(state)
                configuration.serviceStatus.value = state
            }
        }
    })

    private fun startMusicService(context: Context, serviceClass: Class<out Service>) {
        if (!serviceConnector.connected) {
            context.startForegroundService(Intent(context, serviceClass))
            CoroutineScope(coroutineContext).launch {
                val service = serviceConnector.getService()
                service.registerStateListener(listener)
                configuration.serviceStatus.emit(service.state)
            }
        }
    }

    fun stopMusicService() {
        try {
//        context.stopService(Intent(context, AudioPlaybackService::class.java))
            context.unbindService(serviceConnector)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun accept(newConfiguration: Configuration?) {
        newConfiguration?.let { configuration ->
            configuration.locales.getFirstMatch(arrayOf("EN", "RU", "BY"))
        }
    }

}