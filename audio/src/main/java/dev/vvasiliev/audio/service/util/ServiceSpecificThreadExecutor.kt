package dev.vvasiliev.audio.service.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ServiceSpecificThreadExecutor
@Inject constructor(
    private val looper: Looper
) {
    private val handler = Handler(looper).asCoroutineDispatcher()

    fun <R : Any> execute(task: () -> R) {
        Handler(looper).post {
            task()
        }
    }

    fun <R : Any> executeBlocking(task: () -> R) =
        runBlocking(context = handler) {
            Log.d("Audio service", "${Thread.currentThread()}")
            return@runBlocking task()
        }
}