package dev.vvasiliev.structures.android.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference

object ReadStoragePermissionLauncher :
    ActivityResultCallback<Map<String, Boolean>> {

    private var launcher: WeakReference<ActivityResultLauncher<Array<String>>>? = null
    private var continuation: CancellableContinuation<Boolean>? = null

    private val permissions = mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) add("android.permission.READ_MEDIA_AUDIO")
    }.toTypedArray()

    fun create(activity: ComponentActivity) {
        launcher = WeakReference(
            activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
                this@ReadStoragePermissionLauncher
            )
        )
    }

    private fun Context.checkStoragePermission() =
        permissions.any { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }

    suspend fun requestExternalStorage(context: Context) = suspendCancellableCoroutine {
        this.continuation = it
        if (!context.checkStoragePermission())
            launcher?.let {
                it.get()?.launch(permissions)
            }
        else continuation?.resumeWith(Result.success(true))
    }

    override fun onActivityResult(result: Map<String, Boolean>?) {
        continuation?.resumeWith(Result.success(result?.values?.any { !it } ?: false))
        continuation = null
    }

}