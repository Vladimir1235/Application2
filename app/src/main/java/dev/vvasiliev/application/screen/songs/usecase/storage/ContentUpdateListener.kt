package dev.vvasiliev.application.screen.songs.usecase.storage

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper

fun interface CollectionUpdateListener {
    fun onUpdate()
}

class ContentUpdateListener constructor(private val listener: CollectionUpdateListener) :
    ContentObserver(Handler(Looper.myLooper()!!)) {
    override fun onChange(selfChange: Boolean) {
        listener.onUpdate()
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        listener.onUpdate()
    }

    override fun onChange(selfChange: Boolean, uri: Uri?, flags: Int) {
        listener.onUpdate()
    }

    override fun onChange(selfChange: Boolean, uris: MutableCollection<Uri>, flags: Int) {
        listener.onUpdate()
    }
}