package dev.vvasiliev.audio.service.util.player

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Util class to specify behaviour of execution operations at concrete looper specified as [looper] parameter
 */
class ServiceSpecificThreadExecutor
@Inject constructor(
    private val looper: Looper
) {
    private val handler = Handler(looper).asCoroutineDispatcher()
    val looperId = looper.thread.id
    /**
     * Runs [task] asynchronously
     */
    fun <R : Any> execute(task: () -> R) {
        Handler(looper).post {
            task()
        }
    }

    /**
     * Runs [task] synchronously
     */
    fun <R : Any?> executeBlocking(task: () -> R) =
        if(Thread.currentThread() != looper.thread)
        runBlocking(context = handler) {
            return@runBlocking task()
        }else task()
}