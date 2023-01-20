package dev.vvasiliev.structures.android.operation

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import dev.vvasiliev.structures.android.launcher.LauncherWrapper
import kotlinx.coroutines.CancellableContinuation
import java.lang.ref.WeakReference

object ContentDeletionLauncher :
    LauncherWrapper<IntentSenderRequest, ActivityResult> {
    override var continuation: CancellableContinuation<Boolean>? = null
    override var launcher: WeakReference<ActivityResultLauncher<IntentSenderRequest>?> =
        WeakReference(null)

    override fun perform(input: IntentSenderRequest) {
        launcher.get()?.launch(input)
    }

    override fun buildResult(result: ActivityResult): Result<Boolean> =
        Result.success(result.resultCode == Activity.RESULT_OK)

}