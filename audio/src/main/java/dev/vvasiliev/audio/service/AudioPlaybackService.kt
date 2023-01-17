package dev.vvasiliev.audio.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import dev.vvasiliev.audio.BuildConfig
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.broadcast.NotificationBroadcastReceiver
import dev.vvasiliev.audio.service.broadcast.command.PlayPauseCommand
import dev.vvasiliev.audio.service.di.AudioServiceComponent
import dev.vvasiliev.audio.service.di.DaggerAudioServiceComponent
import dev.vvasiliev.audio.service.notifications.NotificationUtils.FOREGROUND_CHANNEL_ID
import dev.vvasiliev.audio.service.notifications.NotificationUtils.buildInitialNotification
import dev.vvasiliev.audio.service.notifications.NotificationUtils.buildNotificationActivityUnBind
import dev.vvasiliev.audio.service.notifications.NotificationUtils.createNotificationChannel
import dev.vvasiliev.audio.service.notifications.NotificationUtils.getNotificationManager
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

    @Inject
    lateinit var receiver: NotificationBroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        initTimber()
        serviceComponent.injectService(this)
        registerBroadcastReceiver()
    }

    override fun onBind(intent: Intent?): IBinder {
        return service.asBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotificationChannel()

        startForeground(
            FOREGROUND_CHANNEL_ID + 1,
            buildInitialNotification(this)
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("Client Disconnected")
        unregisterBroadcastReceiver()
        getNotificationManager(this).notify(
            FOREGROUND_CHANNEL_ID + 1,
            buildNotificationActivityUnBind(this)
        )
        return true
    }

    private fun registerBroadcastReceiver() {
        registerReceiver(receiver, IntentFilter(PlayPauseCommand.getName(this)))
    }

    private fun unregisterBroadcastReceiver() {
        unregisterReceiver(receiver)
    }

    private fun initTimber() {
        if (BuildConfig.LOG_ENABLED) {
            if (Timber.treeCount < 1) {
                Timber.plant(Timber.DebugTree())
            }
        }
    }
}