package dev.vvasiliev.structures.android.launcher.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_REQUEST_MANAGE_MEDIA
import androidx.activity.result.contract.ActivityResultContract

class GetManageMediaPermissionContract : ActivityResultContract<Unit, Boolean>() {
    override fun createIntent(context: Context, input: Unit): Intent =
        Intent().apply {
            action = ACTION_REQUEST_MANAGE_MEDIA
            data = Uri.parse("package:${context.packageName}")
        }


    override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
        resultCode == Activity.RESULT_OK
}

