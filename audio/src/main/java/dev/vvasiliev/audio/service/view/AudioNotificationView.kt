package dev.vvasiliev.audio.service.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import android.widget.SeekBar
import android.widget.TextView
import dev.vvasiliev.audio.R

@RemoteView
class AudioNotificationView constructor(
    context: Context
) : RemoteViews(context.packageName, R.layout.audio_notification_layout) {

    fun setTitle(text: String) {
        setCharSequence(R.id.notificationViewTitle, "setText", text)
    }

    fun setSubTitle(text: String) {
        setCharSequence(R.id.notificationViewSubtitle, "setText", text)
    }

    private fun setButtonImage(id: Int) {
    }

    fun setPlaying(isPlaying: Boolean) {
        when (isPlaying) {
            true -> {
                setButtonImage(com.google.android.exoplayer2.R.drawable.exo_controls_play)
            }
            false -> {
                setButtonImage(com.google.android.exoplayer2.R.drawable.exo_controls_pause)
            }
        }
    }
}