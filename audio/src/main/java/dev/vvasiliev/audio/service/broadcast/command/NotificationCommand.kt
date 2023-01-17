package dev.vvasiliev.audio.service.broadcast.command

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable

const val NOTIFICATION_COMMAND_KEY = "command"

interface NotificationCommand : Parcelable {
    val name: String
}


data class UnknownCommand(
    override val name: String = "unknown"
) : NotificationCommand {
    constructor(parcel: Parcel) : this(parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UnknownCommand> {
        override fun createFromParcel(parcel: Parcel): UnknownCommand {
            return UnknownCommand(parcel)
        }

        override fun newArray(size: Int): Array<UnknownCommand?> {
            return arrayOfNulls(size)
        }
    }
}


data class PlayPauseCommand(
    override val name: String
) : NotificationCommand {

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayPauseCommand> {

        fun getName(context: Context) =
            context.packageName + "PlayPause"


        override fun createFromParcel(parcel: Parcel): PlayPauseCommand {
            return PlayPauseCommand(parcel)
        }

        override fun newArray(size: Int): Array<PlayPauseCommand?> {
            return arrayOfNulls(size)
        }
    }
}