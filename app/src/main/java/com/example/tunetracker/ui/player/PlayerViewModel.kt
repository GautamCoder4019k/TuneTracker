package com.example.tunetracker.ui.player

import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.tunetracker.data.local.model.Audio
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration

@HiltViewModel
class PlayerViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {




    fun getRandomColor(): Color {
        val random = Random.Default
        val color = android.graphics.Color.argb(
            255,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
        return Color(color)
    }
}

data class PlayerUIState(
    val duration: Long,
    var progress: Float,
    var progressString: String,
    var isPlaying: Boolean,
    var currentSelectedAudio: Audio
)