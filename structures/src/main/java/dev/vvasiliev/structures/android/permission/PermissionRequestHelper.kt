package dev.vvasiliev.structures.android.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference

abstract class PermissionRequestHelper constructor(private val permissions: Array<String>) :
    ActivityResultCallback<Map<String, Boolean>> {

    var continuation: CancellableContinuation<Boolean>? = null

    var launcher: ActivityResultLauncher<Array<String>>? =
        null


    suspend fun requestPermission(
        context: Context
    ): Boolean = suspendCancellableCoroutine {
        this.continuation = it
        if (!context.checkPermission())
            launcher.let {
                it?.launch(permissions)
            }
        else continuation?.resumeWith(Result.success(true))
    }

    fun Context.checkPermission(): Boolean =
        permissions.any { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }

    fun create(
        activity: ComponentActivity
    ) {
        launcher =
            activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
                this@PermissionRequestHelper
            )
    }

    override fun onActivityResult(result: Map<String, Boolean>?) {
        result?.let {
            continuation?.resumeWith(buildResult(result))
        }
        continuation = null
    }

    abstract fun buildResult(result: Map<String, Boolean>): Result<Boolean>
}