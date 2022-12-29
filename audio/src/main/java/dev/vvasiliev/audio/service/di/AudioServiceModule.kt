package dev.vvasiliev.audio.service.di

import android.content.Context
import android.os.HandlerThread
import android.os.Looper
import com.google.android.exoplayer2.ExoPlayer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.AudioPlaybackServiceImpl
import dev.vvasiliev.audio.service.util.PlayerUsage
import dev.vvasiliev.audio.service.util.PlayerUsecase
import dev.vvasiliev.audio.service.util.ServiceSpecificThreadExecutor
import java.util.concurrent.atomic.AtomicReference

@Module
class AudioServiceModule {

    @Provides
    @AudioServiceScope
    fun provideAudioService(service: AudioPlaybackServiceImpl): IAudioPlaybackService = service

    @Provides
    @AudioServiceScope
    fun provideAudioControlThread(): Looper = HandlerThread("AudioControlThread").also {
        it.start()
    }.looper

    @Provides
    @AudioServiceScope
    fun provideServiceSpecificThreadExecutor(looper: Looper) = ServiceSpecificThreadExecutor(looper)

    @Provides
    @AudioServiceScope
    fun provideExoPlayer(context: Context, looper: Looper) =
        ExoPlayer.Builder(context)
            .setLooper(looper)
            .build()
}