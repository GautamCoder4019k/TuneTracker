package com.example.tunetracker.data.remote.api

import com.example.tunetracker.data.remote.model.BearerTokenResponse
import com.example.tunetracker.data.remote.model.SpotifyResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SpotifyApiService {

    @GET("search")
    fun searchTracks(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 20,
    ): Call<SpotifyResponse>




}