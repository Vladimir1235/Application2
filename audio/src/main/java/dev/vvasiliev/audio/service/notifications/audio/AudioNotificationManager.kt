package dev.vvasiliev.audio.service.notifications.audio

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FLAG_NO_CLEAR
import androidx.core.app.NotificationCompat.FLAG_ONGOING_EVENT
import com.google.android.exoplayer2.MediaMetadata
import dev.vvasiliev.audio.BuildConfig
import dev.vvasiliev.audio.R
import dev.vvasiliev.audio.service.broadcast.NotificationBroadcastReceiver
import dev.vvasiliev.audio.service.broadcast.NotificationIntent
import dev.vvasiliev.audio.service.notifications.NotificationUtils.CHANNEL_ID
import dev.vvasiliev.audio.service.notifications.NotificationUtils.FOREGROUND_CHANNEL_ID
import dev.vvasiliev.audio.service.notifications.NotificationUtils.getNotificationManager
import dev.vvasiliev.audio.service.view.AudioNotificationView
import javax.inject.Inject

class AudioNotificationManager @Inject constructor(
    private val context: Context
) {
    private val notificationManager: NotificationManager = getNotificationManager(
        context
    )
    val remoteView = AudioNotificationView(context)

    private fun buildAudioNotification(
        title: String,
        author: String,
        isPlaying: Boolean
    ): Notification {

        remoteView.apply {
            setTitle(title)
            setSubTitle(author)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(title)
            .setContentInfo(author)
            // Add media control buttons that invoke intents in your media service
            .addAction(
                com.google.android.exoplayer2.ui.R.drawable.exo_controls_pause,
                if (isPlaying) "stop" else "play",
                NotificationIntent.Builder()
                    .buildPlayPause(context)
                    .buildPending()
            ) // #1
            // Apply the media style template
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(com.google.android.exoplayer2.ui.R.drawable.exo_notification_small_icon)
            .setCustomContentView(
                remoteView
            )
            .build().apply {
                flags = FLAG_ONGOING_EVENT
            }
    }

    fun showStoppedNotification(audioMetadata: MediaMetadata) {
        if (notificationManager.activeNotifications.any {
                it.notification.channelId == CHANNEL_ID
            }) {
            remoteView.setPlaying(false)
        }
        val notification = buildAudioNotification(
            audioMetadata.title.toString(),
            audioMetadata.artist.toString(),
            false
        )
        notificationManager.notify(FOREGROUND_CHANNEL_ID, notification)
    }

    fun showPlayNotification(audioMetadata: MediaMetadata) {
        if (notificationManager.activeNotifications.any {
                it.notification.channelId == CHANNEL_ID
            }) {
            remoteView.setPlaying(false)
        }
        val notification = buildAudioNotification(
            audioMetadata.title.toString(),
            audioMetadata.artist.toString(),
            true
        )
        notificationManager.notify(FOREGROUND_CHANNEL_ID, notification)
    }
}