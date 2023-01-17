package dev.vvasiliev.audio.service.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import dev.vvasiliev.audio.R

object NotificationUtils {
    const val CHANNEL_ID = "MyAppAudioPlaybackServiceChannel"
    const val FOREGROUND_CHANNEL_ID = 3311

    fun buildInitialNotification(context: Context) =
        Notification.Builder(context, CHANNEL_ID).setContentTitle("Service is running")
            .setContentText("Service was started now").build()

    fun buildNotificationActivityUnBind(context: Context) =
        Notification.Builder(context, CHANNEL_ID).setContentTitle("Connection lost").setSmallIcon(
            com.google.android.exoplayer2.R.drawable.exo_notification_small_icon
        )
            .setContentText("Service lost connection with an Activity").build()


    inline fun getNotificationManager(context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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