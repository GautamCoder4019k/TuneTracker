package com.example.tunetracker.di

import android.app.Application
import com.example.tunetracker.data.remote.api.SpotifyApiService
import com.example.tunetracker.data.remote.api.SpotifyAuthService
import com.example.tunetracker.data.remote.api.SpotifyScraperApiService
import com.example.tunetracker.data.remote.repository.SpotifyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Provides
    fun provideSpotifyService(): SpotifyApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApiService::class.java)
    }

    @Provides
    fun provideSpotifyAuthService(): SpotifyAuthService {
        return Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyAuthService::class.java)
    }

    @Provides
    fun provideSpotifyScraperService(): SpotifyScraperApiService {
        return Retrofit.Builder()
            .baseUrl("https://spotify-scraper.p.rapidapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyScraperApiService::class.java)
    }

    @Provides
    fun provideSpotifyRepository(
        spotifyApiService: SpotifyApiService,
        spotifyScraperApiService: SpotifyScraperApiService,
        spotifyAuthService: SpotifyAuthService
    ): SpotifyRepository {
        return SpotifyRepository(spotifyApiService, spotifyScraperApiService,spotifyAuthService)
    }
}