package com.example.tunetracker.data.remote.api

import com.example.tunetracker.data.remote.model.BearerTokenResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SpotifyAuthService {

    @FormUrlEncoded
    @POST("token")
    fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String
    ): Call<BearerTokenResponse>
}