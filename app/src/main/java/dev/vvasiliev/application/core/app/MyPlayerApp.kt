package dev.vvasiliev.application.core.app

import android.app.Application
import dev.vvasiliev.application.core.di.core.CoreComponent
import dev.vvasiliev.application.core.di.core.DaggerCoreComponent
import timber.log.Timber

class MyPlayerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        dagger = DaggerCoreComponent.builder()
            .includeContext(this).build()

        initLogger()
    }

    private fun initLogger(){
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var dagger: CoreComponent
    }
}