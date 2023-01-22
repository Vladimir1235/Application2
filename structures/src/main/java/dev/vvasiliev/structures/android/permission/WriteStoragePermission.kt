package dev.vvasiliev.structures.android.permission

import android.Manifest
import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Build

object WriteStoragePermission :
    PermissionRequestHelper(
        permissions = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) arrayOf(
            MANAGE_EXTERNAL_STORAGE
        ) else arrayOf(
            WRITE_EXTERNAL_STORAGE
        )
    ) {
    override fun buildResult(result: Map<String, Boolean>): Result<Boolean> =
        Result.success(result.any())
}