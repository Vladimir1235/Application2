package dev.vvasiliev.application.core.config

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import androidx.core.content.OnConfigurationChangedProvider
import androidx.core.util.Consumer
import dev.vvasiliev.audio.service.AudioPlaybackService
import dev.vvasiliev.audio.service.event.PlaybackStarted
import dev.vvasiliev.audio.service.event.PlaybackStopped
import dev.vvasiliev.audio.service.state.AudioServiceState
import dev.vvasiliev.audio.service.state.AudioServiceStateListener
import dev.vvasiliev.audio.service.state.holder.ServiceEventPipeline
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class AppConfigurator @Inject constructor(
    private val context: Context,
    private val configuration: AppConfiguration,
    private val serviceConnector: AudioServiceConnector
) : Consumer<Configuration> {

    fun configure() {
        startMusicService(context, AudioPlaybackService::class.java)
    }

    private val listener = AudioServiceStateListener(object : ServiceEventPipeline {
        override fun onPlaybackStopped(songId: Long) {
            Timber.d("Playback stopped")
            CoroutineScope(Dispatchers.Main).launch {
                configuration.servicePlaybackStatus.emit(
                    PlaybackStopped(songId)
                )
            }
        }

        override fun onPlaybackStarted(songId: Long) {
            Timber.d("Playback started")
            CoroutineScope(Dispatchers.Main).launch {
                configuration.servicePlaybackStatus.emit(
                    PlaybackStarted(songId)
                )
            }
        }

        override fun onServiceStateChanged(state: AudioServiceState) {
            CoroutineScope(Dispatchers.Main).launch {
                configuration.serviceStatus.emit(state)
                configuration.serviceStatus.value = state
            }
        }
    })

    private fun startMusicService(context: Context, serviceClass: Class<out Service>) {
        if (!serviceConnector.connected) {
            context.startForegroundService(Intent(context, serviceClass))
            CoroutineScope(Dispatchers.IO).launch {
                val service = serviceConnector.getService()
                service.registerStateListener(listener)
                configuration.serviceStatus.emit(service.state)
            }
        }
    }

    fun stopMusicService(context: Context) {
        context.stopService(Intent(context, AudioPlaybackService::class.java))
        context.unbindService(serviceConnector)
    }

    override fun accept(newConfiguration: Configuration?) {
        newConfiguration?.let { configuration ->
            configuration.locales.getFirstMatch(arrayOf("EN", "RU", "BY"))
        }
    }

}