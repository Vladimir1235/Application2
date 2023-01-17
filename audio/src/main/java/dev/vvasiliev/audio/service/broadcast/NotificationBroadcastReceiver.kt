package dev.vvasiliev.audio.service.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.vvasiliev.audio.service.broadcast.command.NotificationCommand
import dev.vvasiliev.audio.service.broadcast.command.PlayPauseCommand
import dev.vvasiliev.audio.service.util.player.PlayerUsecase
import timber.log.Timber
import javax.inject.Inject

class NotificationBroadcastReceiver @Inject constructor(private val player: PlayerUsecase) :
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("Received notification")
        intent?.extras?.getParcelable<NotificationCommand>("command")?.let { command ->
            when (command) {
                is PlayPauseCommand -> {
                    Timber.d("Play Pause clicked")
                    if (player.isPlaying()) player.stop() else player.resume()
                }
            }
        }
    }
}