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
import java.lang.ref.WeakReference

/**
 * Audio service connector, which helps you to easily obtain [IAudioPlaybackService] object in synchronous way
 */
class AudioServiceConnector constructor(private val context: Context) : ServiceConnection {
    var connected: Boolean = false
    private var serviceRef: WeakReference<IAudioPlaybackService?> = WeakReference(null)

    /**
     * [CancellableContinuation] which falls current thread into a sleep till [IAudioPlaybackService] will be available
     */
    private var continuation: CancellableContinuation<IAudioPlaybackService>? = null

    override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
        connected = true
        val service = IAudioPlaybackService.Stub.asInterface(binder)
        continuation?.resumeWith(Result.success(service))
        serviceRef = WeakReference(service)
        this.continuation = null
    }

    /**
     * Unbind service if disconnected
     */
    override fun onServiceDisconnected(componentName: ComponentName?) {
        connected = false
        context.unbindService(this)
    }

    /**
     * Clear cached [serviceRef]
     */
    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        connected = false
        serviceRef.clear()
    }

    /**
     * Clear cached [serviceRef]
     */
    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
        connected = false
        serviceRef.clear()
    }

    /**
     * Asynchronously get audio playback service
     * @param context used to build an intent which specifies desired service
     */
    suspend fun getService() =
        if (serviceRef.get() != null) serviceRef.get()!! else
            suspendCancellableCoroutine { continuation ->
                this.continuation = continuation
                context.bindService(
                    Intent(context, AudioPlaybackService::class.java),
                    this,
                    Context.BIND_AUTO_CREATE
                )
            }
}