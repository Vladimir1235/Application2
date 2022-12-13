package dev.vvasiliev.audio.service.data

import dev.vvasiliev.audio.AudioEventListener

class EventListener(private val onChange: (Long) -> Unit, private val onPlaybackStopped:()->Unit) : AudioEventListener.Stub() {
    override fun onPositionChange(position: Long) =
        onChange(position)


    override fun onPlaybackStopped() = onPlaybackStopped.invoke()
}