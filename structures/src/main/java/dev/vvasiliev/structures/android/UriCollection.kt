package dev.vvasiliev.structures.android

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import dev.vvasiliev.structures.android.AudioFileCollection.Audio.Companion.fields

fun interface UriCollection<Content> {
    fun getAllContent(): List<Content>
}

private val collectionUri =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Media.getContentUri(MediaStore.VOLUME_EXTERNAL) else Media.EXTERNAL_CONTENT_URI

class AudioFileCollection constructor(private val context: Context) :

    UriCollection<AudioFileCollection.Audio> {

    data class Audio private constructor(
        val id: Long,
        val name: String,
        val artist: String,
        val album: String,
        val uri: Uri,
        val duration: Long
    ) {
        companion object{
            private val ID = "ID"
            private val ALBUM = "ALBUM"
            private val TITLE = "TITLE"
            private val ARTIST = "ARTIST"
            private val DURATION = "DURATION"

            val fields = mapOf(
                ID to Media._ID,
                ALBUM to Media.ALBUM,
                TITLE to Media.TITLE,
                ARTIST to Media.ARTIST,
                DURATION to Media.DURATION,
            )
        }

        class Builder(private val cursor: Cursor) {
            val ci_id = cursor.getColumnIndex(fields[ID])
            val ci_album = cursor.getColumnIndex(fields[ALBUM])
            val ci_title = cursor.getColumnIndex(fields[TITLE])
            val ci_artist = cursor.getColumnIndex(fields[ARTIST])
            val ci_duration = cursor.getColumnIndex(fields[DURATION])

            fun build() = Audio(
                id = cursor.getLong(ci_id),
                name = cursor.getString(ci_title),
                artist = cursor.getString(ci_artist),
                uri = ContentUris.withAppendedId(collectionUri, cursor.getLong(ci_id)),
                album = cursor.getString(ci_album),
                duration = cursor.getLong(ci_duration)
            )
        }
    }

    override fun getAllContent(): List<Audio> {
        val list = mutableListOf<Audio>()
        context.contentResolver.query(collectionUri, fields.values.toTypedArray(), null, null)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                   list.add(Audio.Builder(cursor).build())
                }
            }
        return list
    }
    private val collectionRows =
        arrayOf(Media._ID, Media.ALBUM, Media.TITLE, Media.ARTIST, Media.DURATION)
}