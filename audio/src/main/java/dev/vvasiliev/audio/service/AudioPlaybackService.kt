package dev.vvasiliev.audio.service

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import android.util.Log
import dev.vvasiliev.audio.BuildConfig
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.di.AudioServiceComponent
import dev.vvasiliev.audio.service.di.DaggerAudioServiceComponent
import dev.vvasiliev.audio.service.notifications.NotificationUtils.FOREGROUND_CHANNEL_ID
import dev.vvasiliev.audio.service.notifications.NotificationUtils.buildInitialNotification
import dev.vvasiliev.audio.service.notifications.NotificationUtils.createNotificationChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject


/**
 * PLAY, STOP, RESUME - is the only things this service can do with music, for now =)
 *
 * It's simply represents an interface that's allows to perform commands described above
 *
 * As services does simple bunch of things as play,stop, resume, it'll not use DI for now
 */
class AudioPlaybackService : Service() {

    private val serviceComponent: AudioServiceComponent by lazy {
        DaggerAudioServiceComponent.builder().bindContext(this).build()
    }

    @Inject
    lateinit var service: IAudioPlaybackService

    override fun onCreate() {
        super.onCreate()
        initTimber()
        serviceComponent.injectService(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return service.asBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotificationChannel()

        startForeground(
            FOREGROUND_CHANNEL_ID,
            buildInitialNotification(this)
        )
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initTimber() {
        if (BuildConfig.LOG_ENABLED) {
            if(Timber.treeCount < 1){
                Timber.plant(Timber.DebugTree())
            }
        }
    }
}