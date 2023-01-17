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
    PermissionRequestHelper(mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) add("android.permission.READ_MEDIA_AUDIO")
    }.toTypedArray()) {

    override fun buildResult(result: Map<String, Boolean>): Result<Boolean> =
        Result.success(result.values.any { it })
}