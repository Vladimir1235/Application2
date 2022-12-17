package dev.vvasiliev.audio.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.di.AudioServiceComponent
import dev.vvasiliev.audio.service.di.DaggerAudioServiceComponent
import dev.vvasiliev.audio.service.notifications.NotificationUtils.buildInitialNotification
import dev.vvasiliev.audio.service.notifications.NotificationUtils.createNotificationChannel
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
        serviceComponent.injectService(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return service.asBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotificationChannel()

        startForeground(
            3211,
            buildInitialNotification(this)
        )
        Log.d(this.packageName, "Service started!")
        return super.onStartCommand(intent, flags, startId)
    }
}