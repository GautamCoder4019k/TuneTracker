package com.example.tunetracker.ui.search

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.tunetracker.data.local.model.Audio
import com.example.tunetracker.data.remote.model.AudioResponse
import com.example.tunetracker.data.remote.model.BearerTokenResponse
import com.example.tunetracker.data.remote.model.SpotifyResponse
import com.example.tunetracker.data.remote.model.TrackItem
import com.example.tunetracker.data.remote.repository.SpotifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

const val TAG = "Response"

@HiltViewModel
class SearchViewModel @Inject constructor(private val spotifyRepository: SpotifyRepository) :
    ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private var bearerToken: String = ""

    init {
        getBearerToken {
            bearerToken =it.toString()
        }
    }


    fun searchTracks() {
        _isSearching.value = true
        val call = spotifyRepository.searchTracks(searchText.value, bearerToken)
        call.enqueue(object : Callback<SpotifyResponse> {
            override fun onResponse(
                call: Call<SpotifyResponse>,
                response: Response<SpotifyResponse>
            ) {
                if (response.isSuccessful) {
                    _songs.value = response.body()?.tracks?.items?.toAudioList() ?: listOf()

                } else {
                    // Handle error
                    if(response.code() == 401){
                        getBearerToken {
                            if (it != null) {
                                bearerToken = it
                                searchTracks()
                            }
                        }
                    }

                }
                _isSearching.value = false
            }

            override fun onFailure(call: Call<SpotifyResponse>, t: Throwable) {
                // Handle failure

            }
        })

    }

    fun getSongUrl(trackId: String, callback: (String) -> Unit) {
        val call = spotifyRepository.getTrack(trackId)
        call.enqueue(object : Callback<AudioResponse> {
            override fun onResponse(call: Call<AudioResponse>, response: Response<AudioResponse>) {
                if (response.isSuccessful) {
                    val audioResponse = response.body()
                    val url = audioResponse?.youtubeVideo?.audio?.firstOrNull()?.url ?: ""
                    // Pass the preview URL to the callback function
                    callback(url)
                } else {
                    // Handle error

                }
            }

            override fun onFailure(call: Call<AudioResponse>, t: Throwable) {
                // Handle failure

            }
        })
    }

    fun getBearerToken(callback: (String?) -> Unit) {
        spotifyRepository.getBearerToken()
            .enqueue(object : Callback<BearerTokenResponse> {
                override fun onResponse(
                    call: Call<BearerTokenResponse>,
                    response: Response<BearerTokenResponse>
                ) {
                    if (response.isSuccessful) {
                        callback(response.body()?.access_token)
                    } else {
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<BearerTokenResponse>, t: Throwable) {
                    callback(null)
                }
            })
    }


    private val _songs = MutableStateFlow(listOf<Audio>())
    val songs = searchText.combine(_songs) { text, songs ->
        if (text.isBlank()) {
            songs
        } else {
            songs
        }

    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun clearSearchText() {
        _searchText.value = ""
    }

    fun List<TrackItem>.toAudioList(): List<Audio> {
        return this.map { trackItem ->
            Audio(
                uri = trackItem.uri.toUri(),
                displayName = trackItem.name,
                id = trackItem.id,
                artist = trackItem.artists.joinToString { it.name },
                data = "",
                duration = trackItem.duration,
                title = trackItem.name,
                albumArtUri = Uri.parse(trackItem.album.images.firstOrNull()?.url ?: "")
            )

        }
    }
}

