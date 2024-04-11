package com.example.tunetracker.ui.playList

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.tunetracker.data.local.model.Audio
import com.example.tunetracker.ui.audio.AudioItem
import com.example.tunetracker.ui.audio.AudioViewModel
import com.example.tunetracker.ui.audio.BottomBarPlayer
import com.example.tunetracker.ui.audio.UIState

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PlayListScreen(
    viewModel: AudioViewModel,
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: Audio,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onItemClick: (Int) -> Unit,
    onNext: () -> Unit
) {
    Scaffold(bottomBar = {
        BottomBarPlayer(
            progress = progress,
            onProgress = onProgress,
            audio = currentPlayingAudio,
            isAudioPlaying = isAudioPlaying,
            onStart = onStart,
            onNext = onNext,
            onBottomBarClicked = {}
        )
    }) {
        when (viewModel.uiState.value) {
            UIState.Ready -> {
                LazyColumn(contentPadding = it) {
                    itemsIndexed(audioList) { index, audio ->
                        AudioItem(audio = audio) {
                            onItemClick(index)
                        }
                    }
                }
            }

            UIState.Initial -> {
                CircularProgressIndicator()
            }
        }

    }
}