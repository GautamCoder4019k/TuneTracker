package com.example.tunetracker.ui.audio

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tunetracker.data.local.model.Audio
import com.example.tunetracker.ui.player.noRippleClickable
import com.example.tunetracker.ui.theme.White

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    viewModel: AudioViewModel,
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: Audio,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onItemClick: (Int) -> Unit,
    onBottomPlayerClicked: () -> Unit,
    onNext: () -> Unit
) {
    Scaffold(bottomBar = {

        if (viewModel.currentSelectedAudio != audioDummy)
            BottomBarPlayer(
                progress = progress,
                onProgress = onProgress,
                audio = currentPlayingAudio,
                isAudioPlaying = isAudioPlaying,
                onStart = onStart,
                onNext = onNext,
                onBottomBarClicked = onBottomPlayerClicked
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


@Composable
fun AudioItem(
    audio: Audio,
    onItemClick: () -> Unit
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
        .clickable { onItemClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = audio.displayName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Clip,
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = audio.artist,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Clip,
            )
        }
    }
}

@Composable
fun BottomBarPlayer(
    progress: Float,
    onProgress: (Float) -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onBottomBarClicked: () -> Unit
) {

    Card(modifier = Modifier
        .padding(4.dp)
        .noRippleClickable { onBottomBarClicked() }) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            ArtistInfo(
                audio = audio,
                modifier = Modifier.weight(1f)
            )
            MediaPlayerController(
                isPlaying = isAudioPlaying,
                onStart = onStart,
                onNext = onNext
            )

        }
        LinearProgressIndicator(
            progress = { progress / 100f },
            color = White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
    }


}


@Composable
fun MediaPlayerController(
    isPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .padding(4.dp)
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.clickable { onStart() }
        )
        Spacer(modifier = Modifier.size(8.dp))
        Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = null,
            modifier = Modifier.clickable { onNext() }
        )
    }
}

@Composable
fun ArtistInfo(modifier: Modifier = Modifier, audio: Audio) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            modifier = Modifier.clickable { }
        )
        Spacer(modifier = Modifier.size(4.dp))
        Column {
            Text(
                text = audio.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Clip,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = audio.artist,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        progress = 50f,
        onProgress = {},
        isAudioPlaying = true,
        currentPlayingAudio = Audio("".toUri(), "Title One", 0L, "said", "", 0, ""),
        audioList = listOf(
            Audio("".toUri(), "Title One", 0L, "said", "", 0, ""),
            Audio("".toUri(), "Title One", 0L, "said", "", 0, ""),
        ),
        onStart = { /*TODO*/ },
        viewModel = viewModel(),
        onItemClick = {},
        onBottomPlayerClicked = {}
    ) {

    }
}

