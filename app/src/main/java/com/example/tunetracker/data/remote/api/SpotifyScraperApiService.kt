package com.example.tunetracker.data.remote.api

import com.example.tunetracker.data.remote.model.AudioResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SpotifyScraperApiService {
    @Headers(
        "X-RapidAPI-Key: a6db294fd3msh74325e5e9c88651p1e1f2fjsnaf2739f54e24",
        "X-RapidAPI-Host: spotify-scraper.p.rapidapi.com"
    )
    @GET("/v1/track/download")
    fun getTrack(@Query("track") track: String): Call<AudioResponse>
}