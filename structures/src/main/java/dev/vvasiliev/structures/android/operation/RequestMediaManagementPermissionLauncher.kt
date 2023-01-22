package dev.vvasiliev.structures.android.operation

import androidx.activity.result.ActivityResultLauncher
import dev.vvasiliev.structures.android.launcher.LauncherWrapper
import kotlinx.coroutines.CancellableContinuation

object RequestMediaManagementPermissionLauncher : LauncherWrapper<Unit, Boolean> {
    override var continuation: CancellableContinuation<Boolean>? = null
    override var launcher: ActivityResultLauncher<Unit>? = null

    override fun perform(input: Unit) {
        launcher?.launch(input)
    }

    override fun buildResult(result: Boolean): Result<Boolean> =
        Result.success(result)
}