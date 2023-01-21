package dev.vvasiliev.structures.android.launcher.contract

import androidx.activity.result.ActivityResult
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContract

class ShareFileContract : ActivityResultContract<Uri, ActivityResult>() {
    override fun createIntent(context: Context, input: Uri): Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, input)
        type = context.contentResolver.getType(input)
        addFlags(FLAG_GRANT_READ_URI_PERMISSION)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult =
        ActivityResult(resultCode, intent)
}