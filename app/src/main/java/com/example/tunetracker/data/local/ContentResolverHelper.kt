package com.example.tunetracker.data.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.example.tunetracker.data.local.model.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContentResolverHelper @Inject
constructor(@ApplicationContext val context: Context) {
    private var mCursor: Cursor? = null

    private val projection: Array<String> = arrayOf(
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.ALBUM_ID
    )

    private var selectionClause: String? =
        "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"

    private var selectionArg = arrayOf("1")

    private val sortOder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    @WorkerThread
    fun getAudioData(): List<Audio> {
        return getCursorData()
    }

    private fun getCursorData(): MutableList<Audio> {
        val audioList = mutableListOf<Audio>()

        mCursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionClause,
            selectionArg,
            sortOder
        )

        mCursor?.use { cursor ->
            val idColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val artistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val dataColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val albumIdColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)

            if (cursor.moveToFirst()) { // Move cursor to the first row
                do {
                    val displayName = cursor.getString(displayNameColumn)
                    val id = cursor.getLong(idColumn)
                    val artist = cursor.getString(artistColumn)
                    val data = cursor.getString(dataColumn)
                    val duration = cursor.getInt(durationColumn)
                    val title = cursor.getString(titleColumn)
                    val albumId = cursor.getLong(albumIdColumn)
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )
                    audioList += Audio(
                        uri, displayName, id.toString(), artist, data, duration, title, albumArtUri
                    )
                } while (cursor.moveToNext()) // Move cursor to the next row until there are no more rows
            }
        }
        return audioList
    }
}