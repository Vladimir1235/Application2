package dev.vvasiliev.application.screen.songs.usecase

import android.content.Context
import android.net.Uri
import dev.vvasiliev.structures.android.operation.ShareContentLauncher
import javax.inject.Inject

class ShareAudio @Inject constructor() {
    suspend operator fun invoke(context: Context, uri: Uri) =
        ShareContentLauncher.launch(context, uri)

}