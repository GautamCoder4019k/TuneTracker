package com.example.tunetracker.data.local.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Audio(
    var uri: Uri,
    val displayName: String,
    val id: String,
    val artist: String,
    val data: String,
    val duration: Int,
    val title: String,
    val albumArtUri:Uri
) : Parcelable