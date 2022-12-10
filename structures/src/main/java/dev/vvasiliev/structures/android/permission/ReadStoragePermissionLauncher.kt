package dev.vvasiliev.structures.android.permission

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

object ReadStoragePermissionLauncher : ActivityResultCallback<Map<String, Boolean>> {

    private var launcher: WeakReference<ActivityResultLauncher<Array<String>>>? = null
    private var continuation: SoftReference<CancellableContinuation<Boolean>>? = null

    fun create(activity: ComponentActivity) {
        launcher = WeakReference(
            activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
                this@ReadStoragePermissionLauncher
            )
        )
    }

   suspend fun requestExternalStorage() = suspendCancellableCoroutine<Boolean>{
        this.continuation = SoftReference(it)
        val permissions = mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) add("android.permission.READ_MEDIA_AUDIO")
        }.toTypedArray()
        launcher?.let {
            it.get()?.launch(permissions)
        }
    }

    override fun onActivityResult(result: Map<String, Boolean>?) {
        continuation?.get()?.resumeWith(Result.success(result?.values?.any{!it}?:false))
        continuation?.clear()
    }

}