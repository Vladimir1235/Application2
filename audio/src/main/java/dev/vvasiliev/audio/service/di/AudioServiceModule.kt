package dev.vvasiliev.audio.service.di

import android.content.Context
import android.os.Looper
import com.google.android.exoplayer2.ExoPlayer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.AudioPlaybackServiceImpl

@Module
class AudioServiceModule {

    @Provides
    @AudioServiceScope
    fun provideAudioService(service: AudioPlaybackServiceImpl): IAudioPlaybackService = service

    @Provides
    @AudioServiceScope
    fun provideExoPlayer(context: Context) =
        ExoPlayer.Builder(context)
            .setLooper(Looper.myLooper()!!)
            .build()
}