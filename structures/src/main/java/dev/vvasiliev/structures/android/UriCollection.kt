package dev.vvasiliev.structures.android

import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import dev.vvasiliev.structures.android.AudioFileCollection.Audio.Companion.fields

interface UriCollection<Content> {
    fun getAllContent(): List<Content>
    fun updateContent(input: Content): PendingIntent?
    fun registerCollectionUpdateCallback(contentObserver: ContentObserver)
    fun requestDeleteContent(uri: Uri): PendingIntent?
}

private val collectionUri =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Media.getContentUri(MediaStore.VOLUME_EXTERNAL) else Media.EXTERNAL_CONTENT_URI

class AudioFileCollection constructor(private val context: Context) :

    UriCollection<AudioFileCollection.Audio> {

    data class Audio constructor(
        val id: Long,
        val name: String,
        val artist: String,
        val album: String,
        val uri: Uri,
        val duration: Long
    ) {
        companion object {
            private val ID = "ID"
            private val ALBUM = "ALBUM"
            private val TITLE = "TITLE"
            private val ARTIST = "ARTIST"
            private val DURATION = "DURATION"
            private val DISPLAY_NAME = "DISPLAY_NAME"

            val fields = mapOf(
                ID to Media._ID,
                ALBUM to Media.ALBUM,
                TITLE to Media.TITLE,
                ARTIST to Media.ARTIST,
                DURATION to Media.DURATION,
                DISPLAY_NAME to Media.DISPLAY_NAME
            )
        }

        fun toContentValues() = ContentValues().apply {
            put(fields[ID], id)
            put(fields[TITLE], name)
            put(fields[ALBUM], album)
            put(fields[ARTIST], artist)
        }

        class Builder(private val cursor: Cursor) {
            val ci_id = cursor.getColumnIndex(fields[ID])
            val ci_album = cursor.getColumnIndex(fields[ALBUM])
            val ci_title = cursor.getColumnIndex(fields[TITLE])
            val ci_artist = cursor.getColumnIndex(fields[ARTIST])
            val ci_duration = cursor.getColumnIndex(fields[DURATION])
            val ci_name = cursor.getColumnIndex(fields[DISPLAY_NAME])

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

    override fun registerCollectionUpdateCallback(contentObserver: ContentObserver) {
        context.contentResolver.registerContentObserver(collectionUri, true, contentObserver)
    }

    private val collectionRows =
        arrayOf(Media._ID, Media.ALBUM, Media.TITLE, Media.ARTIST, Media.DURATION)

    override fun requestDeleteContent(uri: Uri): PendingIntent? =
        when (Build.VERSION.SDK_INT) {
            in IntRange(VERSION_CODES.M, VERSION_CODES.Q) -> {
                context.contentResolver.delete(uri, null, null)
                null
            }
            in IntRange(VERSION_CODES.R, 34) -> {
                val request =
                    MediaStore.createDeleteRequest(context.contentResolver, mutableListOf(uri))
                request
            }
            else -> null
        }

    override fun updateContent(input: Audio): PendingIntent? =
        when (Build.VERSION.SDK_INT) {
            in IntRange(VERSION_CODES.M, VERSION_CODES.Q) -> {
                context.contentResolver.update(
                    input.uri,
                    input.toContentValues(),
                    null,
                    null
                )
                null
            }
            in IntRange(VERSION_CODES.R, 34) -> {
                try {
                    context.contentResolver.update(
                        input.uri,
                        input.toContentValues(),
                        "${Media._ID} = ?",
                        arrayOf(input.id.toString())
                    )
                    null
                } catch (exception: SecurityException) {
                    when (exception) {
                        is RecoverableSecurityException -> {
                            exception.userAction.actionIntent
                        }
                        else -> {
                            MediaStore.createWriteRequest(
                                context.contentResolver,
                                mutableListOf(input.uri)
                            )
                        }
                    }
                }
            }
            else -> null
        }
}