package com.example.tunetracker.data.remote.repository

import com.example.tunetracker.data.remote.api.SpotifyApiService
import com.example.tunetracker.data.remote.api.SpotifyAuthService
import com.example.tunetracker.data.remote.api.SpotifyScraperApiService
import com.example.tunetracker.data.remote.model.AudioResponse
import com.example.tunetracker.data.remote.model.BearerTokenResponse
import com.example.tunetracker.data.remote.model.SpotifyResponse
import retrofit2.Call
import javax.inject.Inject

private const val CLIENT_ID = "6f71f4cd863449e5a0bef625e7d422e2"
private const val CLIENT_SECRET = "83ae755bc2734d06bc0f6a092bf21213"
private const val GRANT_TYPE = "client_credentials"

class SpotifyRepository @Inject constructor(
    private val spotifyApiService: SpotifyApiService,
    private val spotifyScraperApiService: SpotifyScraperApiService,
    private val spotifyAuthService: SpotifyAuthService
) {

    fun searchTracks(query: String, bearerToken: String): Call<SpotifyResponse> {
        return spotifyApiService.searchTracks(
            "Bearer $bearerToken",
            query
        )
    }

    fun getTrack(trackId: String): Call<AudioResponse> {
        return spotifyScraperApiService.getTrack(trackId)
    }

    fun getBearerToken(): Call<BearerTokenResponse> {
        return spotifyAuthService.getAccessToken(CLIENT_ID, CLIENT_SECRET, GRANT_TYPE)
    }
}