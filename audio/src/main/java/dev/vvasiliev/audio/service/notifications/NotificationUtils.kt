package dev.vvasiliev.audio.service.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context

object NotificationUtils {
    private const val CHANNEL_ID = "MyAppAudioPlaybackServiceChannel"
    const val FOREGROUND_CHANNEL_ID = 3311

    fun buildInitialNotification(context: Context) =
        Notification.Builder(context, CHANNEL_ID)
            .setContentTitle("Service is running")
            .setContentText("Service was started now")
            .build()


    fun Service.createNotificationChannel() {
        val name = "Notification"
        val descriptionText = "Audio-playback Notification"
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