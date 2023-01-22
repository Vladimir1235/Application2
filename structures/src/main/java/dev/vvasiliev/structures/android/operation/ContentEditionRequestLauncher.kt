package dev.vvasiliev.structures.android.operation

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import dev.vvasiliev.structures.android.launcher.LauncherWrapper
import kotlinx.coroutines.CancellableContinuation

object ContentEditionRequestLauncher : LauncherWrapper<IntentSenderRequest, ActivityResult> {
    override var continuation: CancellableContinuation<Boolean>? = null
    override var launcher: ActivityResultLauncher<IntentSenderRequest>? = null

    override fun perform(input: IntentSenderRequest) {
        launcher?.launch(input)
    }

    override fun buildResult(result: ActivityResult): Result<Boolean> =
        Result.success(result.resultCode == Activity.RESULT_OK)

}