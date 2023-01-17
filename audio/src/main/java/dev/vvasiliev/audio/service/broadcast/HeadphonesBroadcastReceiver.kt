package dev.vvasiliev.audio.service.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.vvasiliev.audio.service.util.player.PlayerUsecase
import javax.inject.Inject

private const val CONNECTED = 1
private const val DISCONNECTED = 0


class HeadphonesBroadcastReceiver @Inject constructor(private val player: PlayerUsecase) :
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action?.let { action ->
            if(action.equals(Intent.ACTION_HEADSET_PLUG)) {
                intent.extras?.getInt("state")?.let { state ->
                    when (state) {
                        CONNECTED -> {}
                        DISCONNECTED -> {
                            if(player.hasCurrent())
                            player.stop()
                        }
                    }
                }
            }
        }
    }
}