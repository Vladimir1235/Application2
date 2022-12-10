package dev.vvasiliev.audio.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import dev.vvasiliev.audio.service.notifications.NotificationUtils.buildInitialNotification
import dev.vvasiliev.audio.service.notifications.NotificationUtils.createNotificationChannel
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference


/**
 * PLAY, STOP, RESUME - is the only things this service can do with music, for now =)
 *
 * It's simply represents an interface that's allows to perform commands described above
 *
 * As services does simple bunch of things as play,stop, resume, it'll not use DI for now
 */
class AudioPlaybackService : Service() {

    private var exoPlayer: WeakReference<ExoPlayer>? = null
    private var service: WeakReference<AudioPlaybackServiceImpl>? = null

    override fun onBind(intent: Intent?): IBinder {
        exoPlayer = WeakReference(ExoPlayer.Builder(this).build())
        service = WeakReference(AudioPlaybackServiceImpl(SoftReference(exoPlayer!!.get())))
        return service!!.get()!!
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