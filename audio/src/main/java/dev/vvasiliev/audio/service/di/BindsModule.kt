package dev.vvasiliev.audio.service.di

import dagger.Binds
import dagger.Module
import dev.vvasiliev.audio.service.util.PlayerUsage
import dev.vvasiliev.audio.service.util.PlayerUsecase

@Module(includes = [AudioServiceModule::class])
interface BindsModule {

    @Binds
    @AudioServiceScope
    abstract fun bindsPlayerUsecase(usecase: PlayerUsage): PlayerUsecase
}