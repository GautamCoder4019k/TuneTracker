package com.example.tunetracker.ui.audio

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.palette.graphics.Palette
import com.example.tunetracker.data.local.model.Audio
import com.example.tunetracker.data.repository.AudioRepository
import com.example.tunetracker.player.service.AudioServiceHandler
import com.example.tunetracker.player.service.AudioState
import com.example.tunetracker.player.service.PlayerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

val audioDummy = Audio(
    "".toUri(), "", 0L, "", "", 0, ""
)

@HiltViewModel
@OptIn(SavedStateHandleSaveableApi::class)
class AudioViewModel @Inject constructor(
    private val audioServiceHandler: AudioServiceHandler,
    private val repository: AudioRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var duration by savedStateHandle.saveable { mutableLongStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableFloatStateOf(0f) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var isRepeating by savedStateHandle.saveable { mutableStateOf(false) }
    var isShuffleOn by savedStateHandle.saveable { mutableStateOf(false) }
    var currentSelectedAudio by savedStateHandle.saveable { mutableStateOf(audioDummy) }
    var audioList by savedStateHandle.saveable { mutableStateOf(listOf<Audio>()) }

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)

    val uiState: StateFlow<UIState> = _uiState.asStateFlow()


    init {
        loadAudioData()
    }

    //handled uiState
    init {
        viewModelScope.launch {
            audioServiceHandler.audioState.collectLatest { mediaState ->
                when (mediaState) {
                    AudioState.Initial -> _uiState.value = UIState.Initial
                    is AudioState.Buffering -> calculateProgressValue(mediaState.progress)
                    is AudioState.CurrentPlaying -> {
                        currentSelectedAudio = audioList[mediaState.mediaItemIndex]
                    }

                    is AudioState.Playing -> isPlaying = mediaState.isPlaying
                    is AudioState.Progress -> calculateProgressValue(mediaState.progress)
                    is AudioState.Ready -> {
                        duration = mediaState.duration
                        _uiState.value = UIState.Ready
                    }

                    is AudioState.Idle -> {
                        _uiState.value = UIState.Ready
                    }
                }
            }
        }
    }

    private fun loadAudioData() {
        viewModelScope.launch {
            val audio = repository.getAudioData()
            audioList = audio
            setMediaItems()
            _uiState.value = UIState.Ready
        }
    }

    private fun setMediaItems() {
        audioList.map { audio ->
            MediaItem.Builder().setUri(audio.uri).setMediaMetadata(
                MediaMetadata.Builder().setAlbumArtist(audio.artist)
                    .setDisplayTitle(audio.title).setSubtitle(audio.displayName).build()
            ).build()
        }.also {
            audioServiceHandler.setMediaItemList(it)
        }
    }

    private fun calculateProgressValue(currentProgress: Long) {
        progress =
            if (currentProgress > 0) ((currentProgress.toFloat() / duration.toFloat()) * 100f)
            else 0f
        progressString = formatDuration(currentProgress)
    }

    fun onUiEvents(uiEvents: UIEvents) = viewModelScope.launch {
        when (uiEvents) {
            UIEvents.Backward -> audioServiceHandler.onPlayerEvents(PlayerEvent.Backward)
            UIEvents.Forward -> audioServiceHandler.onPlayerEvents((PlayerEvent.Forward))
            UIEvents.PlayPause -> audioServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
            UIEvents.SeekToNext -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext)
            UIEvents.SeekToPrevious -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToPrevious)
            UIEvents.Repeat -> audioServiceHandler.onPlayerEvents(PlayerEvent.Repeat)
            UIEvents.Shuffle -> audioServiceHandler.onPlayerEvents(PlayerEvent.Shuffle)
            is UIEvents.SeekTo -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.SeekTo,
                    seekPosition = ((duration * uiEvents.position) / 100f).toLong()
                )
            }

            is UIEvents.SelectedAudioChange -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.SelectedAudioChange,
                    selectedAudioIndex = uiEvents.index
                )
            }

            is UIEvents.UpdateProgress -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.UpdateProgress(
                        uiEvents.newProgress
                    )
                )
                progress = uiEvents.newProgress
            }
        }
    }

    fun formatDuration(duration: Long): String {
        val minute = TimeUnit.MILLISECONDS.toMinutes(duration)
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minute, seconds)
    }

    override fun onCleared() {
        viewModelScope.launch {
            audioServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        }

    }

    suspend fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("Range")
    fun getAlbumArtUri(context: Context, audioUri: Uri): Uri? {
        val projection = arrayOf(MediaStore.Audio.Media.ALBUM_ID)
        context.contentResolver.query(audioUri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                return ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
            }
        }
        return null
    }

    fun extractDarkColorsFromBitmap(bitmap: Bitmap): String {
        return parseColorSwatch(
            color = Palette.from(bitmap).generate().darkMutedSwatch
        )
    }

    fun extractLightColorsFromBitmap(bitmap: Bitmap): String {
        return parseColorSwatch(
            color = Palette.from(bitmap).generate().lightVibrantSwatch
        )
    }

    private fun parseColorSwatch(color: Palette.Swatch?): String {
        return if (color != null) {
            val parsedColor = Integer.toHexString(color.rgb)
            return "#$parsedColor"
        } else {
            "#000000"
        }
    }

}

sealed class UIEvents {
    data object PlayPause : UIEvents()
    data class SelectedAudioChange(val index: Int) : UIEvents()
    data class SeekTo(val position: Float) : UIEvents()
    data object SeekToNext : UIEvents()
    data object SeekToPrevious : UIEvents()
    data object Forward : UIEvents()
    data object Backward : UIEvents()
    data object Repeat : UIEvents()
    data object Shuffle : UIEvents()
    data class UpdateProgress(val newProgress: Float) : UIEvents()
}

sealed class UIState {
    data object Initial : UIState()
    data object Ready : UIState()
}