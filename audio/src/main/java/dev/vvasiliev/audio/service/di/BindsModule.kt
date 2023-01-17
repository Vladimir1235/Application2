package dev.vvasiliev.audio.service.di

import dagger.Binds
import dagger.Module
import dev.vvasiliev.audio.service.state.holder.AudioServiceStateHolder
import dev.vvasiliev.audio.service.state.holder.ServiceEventPipeline
import dev.vvasiliev.audio.service.util.player.PlayerUsage
import dev.vvasiliev.audio.service.util.player.PlayerUsecase

@Module(includes = [AudioServiceModule::class])
interface BindsModule {
    @Binds
    @AudioServiceScope
    abstract fun bindsPlayerUsecase(usecase: PlayerUsage): PlayerUsecase

    @Binds
    @AudioServiceScope
    abstract fun bindsServiceEventPipeline(impl: AudioServiceStateHolder): ServiceEventPipeline
}