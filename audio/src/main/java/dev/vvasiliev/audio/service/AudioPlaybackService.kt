package dev.vvasiliev.audio.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log

private const val CHANNEL_ID = "MyAppAudioPlaybackServiceChannel"

class AudioPlaybackService: Service() {

    override fun onBind(intent: Intent?): IBinder {
        return AudioPlaybackServiceImpl()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        startForeground(
            startId,
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Service is running")
                .setContentText("Service was started now")
                .build()
        )
        Log.d(this.packageName, "Service started!")
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {
        val name = "Notification"
        val descriptionText = "Audioplayback Notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.createNotificationChannel(channel)
    }

}