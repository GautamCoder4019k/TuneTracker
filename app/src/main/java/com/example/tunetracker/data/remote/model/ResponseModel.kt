package com.example.tunetracker.data.remote.model

import com.google.gson.annotations.SerializedName

data class SpotifyResponse(
    val tracks: Tracks
)

data class Tracks(
    val items: List<TrackItem>
)

data class TrackItem(
    val id:String,
    val name: String,
    val uri: String,
    val artists: List<Artist>,
    val album: Album,
    @SerializedName("duration_ms")
    val duration: Int
)

data class Artist(
    val name: String
)

data class Album(
    val images: List<Image>
)

data class Image(
    val url: String
)



data class AudioResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("youtubeVideo") val youtubeVideo: YoutubeVideo
)

data class YoutubeVideo(
    @SerializedName("audio") val audio: List<AudioItem>
)

data class AudioItem(
    @SerializedName("url") val url: String
)

data class BearerTokenResponse(val access_token: String)