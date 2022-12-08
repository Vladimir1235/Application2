package dev.vvasiliev.audio.service

import dev.vvasiliev.audio.IAudioPlaybackService

class AudioPlaybackServiceImpl: IAudioPlaybackService.Stub(){
    override fun getServiceState(): Boolean = true
}