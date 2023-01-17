package dev.vvasiliev.audio.service.broadcast

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.vvasiliev.audio.service.broadcast.command.NOTIFICATION_COMMAND_KEY
import dev.vvasiliev.audio.service.broadcast.command.NotificationCommand
import dev.vvasiliev.audio.service.broadcast.command.PlayPauseCommand
import dev.vvasiliev.audio.service.broadcast.command.UnknownCommand

class NotificationIntent(
    private val context: Context,
) : Intent() {
    data class Builder constructor(
        val name: String = "",
        val id: Long = 0,
        val commandName: String = ""
    ) {
        fun buildPlayPause(context: Context) =
            command(PlayPauseCommand.getName(context)).build(
                context
            )

        fun name(name: String) = copy(name = name)
        fun id(id: Long) = copy(id = id)
        fun command(command: String) = copy(commandName = command)

        fun build(
            context: Context
        ): NotificationIntent {
            return NotificationIntent(context).apply {
                action = commandName
                putExtra(
                    NOTIFICATION_COMMAND_KEY,
                    pickCommand(context, commandName)
                )
            }
        }

        private fun pickCommand(context: Context, commandName: String): NotificationCommand =
            when (commandName) {
                PlayPauseCommand.getName(context) -> PlayPauseCommand(commandName)
                else -> {
                    UnknownCommand()
                }
            }
    }

    fun buildPending(): PendingIntent =
        PendingIntent.getBroadcast(context, 0, this, FLAG_MUTABLE)
}