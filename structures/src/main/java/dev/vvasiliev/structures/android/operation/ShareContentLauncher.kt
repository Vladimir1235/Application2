package dev.vvasiliev.structures.android.operation

import android.app.Activity
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import dev.vvasiliev.structures.android.launcher.LauncherWrapper
import kotlinx.coroutines.CancellableContinuation

object ShareContentLauncher : LauncherWrapper<Uri, ActivityResult> {
    override var continuation: CancellableContinuation<Boolean>? = null
    override var launcher: ActivityResultLauncher<Uri>? = null
    override fun perform(input: Uri) {
        launcher?.launch(input)
    }

    override fun buildResult(result: ActivityResult): Result<Boolean> =
        Result.success(result.resultCode == Activity.RESULT_OK)

}