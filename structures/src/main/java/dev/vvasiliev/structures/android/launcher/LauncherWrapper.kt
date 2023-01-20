package dev.vvasiliev.structures.android.launcher

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import kotlin.coroutines.resumeWithException

interface LauncherWrapper<InputType, ResultType> :
    ActivityResultCallback<ResultType> {

    var continuation: CancellableContinuation<Boolean>?

    var launcher: WeakReference<ActivityResultLauncher<InputType>?>

    fun perform(input: InputType)
    fun buildResult(result: ResultType): Result<Boolean>

    suspend fun launch(
        context: Context,
        input: InputType
    ): Boolean = suspendCancellableCoroutine {
        this.continuation = it
        perform(input)
    }

    fun create(
        activity: ComponentActivity,
        contract: ActivityResultContract<InputType, ResultType>
    ) {
        launcher = WeakReference(
            activity.registerForActivityResult(
                contract,
                this
            )
        )
    }

    fun done(result: ResultType) {
        result?.let {
            continuation?.resumeWith(buildResult(result))
        } ?: continuation?.resumeWithException(NullPointerException("No result"))
        continuation = null
    }

    override fun onActivityResult(result: ResultType) {
        done(result)
    }
}