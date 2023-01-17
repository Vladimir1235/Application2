package dev.vvasiliev.structures.android.permission

object NotificationPermissionLauncher :
    PermissionRequestHelper(permissions = arrayOf("android.permission.POST_NOTIFICATIONS")) {
    override fun buildResult(result: Map<String, Boolean>): Result<Boolean> =
        Result.success(result.any())
}