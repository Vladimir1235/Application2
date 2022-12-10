package dev.vvasiliev.audio.service.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import dev.vvasiliev.audio.IAudioPlaybackService
import dev.vvasiliev.audio.service.AudioPlaybackService
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Audio service connector, which helps you to easily obtain [IAudioPlaybackService] object in synchronous way
 */
class AudioServiceConnector constructor(private val context: Context): ServiceConnection {

    /**
     * [CancellableContinuation] which falls current thread into a sleep till [IAudioPlaybackService] will be available
     */
    private var continuation: CancellableContinuation<IAudioPlaybackService>? = null

    override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
        continuation?.resumeWith(Result.success(IAudioPlaybackService.Stub.asInterface(binder)))
        this.continuation = null
    }

    override fun onServiceDisconnected(componentName: ComponentName?) {
        context.unbindService(this)
    }

    /**
     * Asynchronously get audio playback service
     * @param context used to build an intent which specifies desired service
     */
    suspend fun getService() =
        suspendCancellableCoroutine { continuation ->
            this.continuation = continuation
            context.bindService(
                Intent(context, AudioPlaybackService::class.java),
                this,
                Context.BIND_EXTERNAL_SERVICE
            )
        }
}