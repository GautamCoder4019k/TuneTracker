package com.example.tunetracker.ui.player

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tunetracker.R
import com.example.tunetracker.ui.audio.AudioViewModel
import com.example.tunetracker.ui.audio.UIEvents
import com.example.tunetracker.ui.components.LockScreenOrientation
import com.example.tunetracker.ui.theme.Green
import com.example.tunetracker.ui.theme.White


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    audioViewModel: AudioViewModel,
    isAudioPlaying: Boolean,
    onStart: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onRepeatClicked: () -> Unit = {},
    onShuffleClicked: () -> Unit = {},
    onDownIconClicked: () -> Unit = {}
) {
    LockScreenOrientation()
    Log.d("Audio", "PlayerScreen: ${audioViewModel.currentSelectedAudio} ")
    val context = LocalContext.current
    var bitmap: Bitmap? by remember {
        mutableStateOf(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.musicbackground
            )
        )
    }
    var lightVibrantColor by remember {
        mutableStateOf(Color.White)
    }
    var darkVibrantColor by remember {
        mutableStateOf(Color.Black)
    }
    audioViewModel.getBitmapFromUri(context, audioViewModel.currentSelectedAudio.albumArtUri) {
        bitmap = it
    }
    Log.d("Audio", "PlayerScreen: ${bitmap} ")
    lightVibrantColor = Color(parseColor(audioViewModel.extractLightColorsFromBitmap(bitmap!!)))
    darkVibrantColor = Color(parseColor(audioViewModel.extractDarkColorsFromBitmap(bitmap!!)))
    Log.d("Audio", "PlayerScreen: ${lightVibrantColor},${darkVibrantColor}} ")


    Scaffold(topBar = { PlayerTopAppBar(onDownIconClicked = onDownIconClicked) }) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lightVibrantColor,
                            darkVibrantColor,
                        )
                    )
                )
                .padding(bottom = 16.dp)

        ) {
            Spacer(modifier = Modifier.weight(3f))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(audioViewModel.currentSelectedAudio.albumArtUri)
                    .error(R.drawable.musicbackground)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(16.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .weight(11f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            SongDescription(
                songName = audioViewModel.currentSelectedAudio.displayName,
                artistName = audioViewModel.currentSelectedAudio.artist
            )
            Column(modifier = Modifier.weight(5f)) {
                PlayerSlider(
                    progressString = audioViewModel.progressString,
                    totalDuration = audioViewModel.formatDuration(audioViewModel.duration),
                    progress = audioViewModel.progress,
                    onProgress = {
                        audioViewModel.onUiEvents(
                            UIEvents.SeekTo(it)
                        )
                    })
                PlayerButtons(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    isAudioPlaying = isAudioPlaying,
                    onStart = onStart,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    onRepeatClicked = onRepeatClicked,
                    isRepeating = audioViewModel.isRepeating,
                    isShuffleOn = audioViewModel.isShuffleOn,
                    onShuffleClicked = onShuffleClicked
                )

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTopAppBar(onDownIconClicked: () -> Unit) {
    TopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "PLAYING FROM PLAYLIST",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Text(
                    text = "Your Song",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Song options",
                    modifier = Modifier.fillMaxSize(0.8f),
                    tint = Color.White
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onDownIconClicked) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Go back",
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongDescription(songName: String, artistName: String) {
    Text(
        text = songName,
        style = MaterialTheme.typography.headlineSmall,
        color = Color.White,
        maxLines = 1,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .basicMarquee()
    )
    Text(
        text = artistName,
        style = MaterialTheme.typography.headlineSmall,
        color = Color.Gray,
        maxLines = 1,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    )
}

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSlider(
    modifier: Modifier = Modifier,
    progressString: String,
    totalDuration: String,
    progress: Float,
    onProgress: (Float) -> Unit,
) {
    val interactionSource = MutableInteractionSource()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Slider(
                value = progress,
                onValueChange = { onProgress(it) },
                valueRange = 0f..100f,
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        thumbSize = DpSize(8.dp, 8.dp),
                        modifier = Modifier.padding(6.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White
                        )
                    )
                },
                colors = SliderDefaults.colors(activeTrackColor = Color.White),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = progressString, color = Color.White)
            Text(text = totalDuration, color = Color.White)

        }

    }
}


@Composable
fun PlayerButtons(
    modifier: Modifier = Modifier,
    isAudioPlaying: Boolean,
    isRepeating: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onRepeatClicked: () -> Unit,
    isShuffleOn: Boolean,
    onShuffleClicked: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(id = R.drawable.shuffle),
            contentDescription = "Shuffle songs",
            tint = if (isShuffleOn) Green else Color.White,
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable {
                    onShuffleClicked()
                }
        )
        Icon(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = "Previous",
            tint = Color.White,
            modifier = Modifier
                .size(48.dp)
                .noRippleClickable { onPrevious() }
        )
        Button(
            onClick = onStart,
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White),
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = if (isAudioPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(48.dp)
            )
        }
        Icon(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = "Next",
            tint = Color.White,
            modifier = Modifier
                .size(48.dp)
                .noRippleClickable { onNext() }
        )
        Icon(
            imageVector = Icons.Default.Repeat,
            contentDescription = null,
            tint = if (isRepeating) Green else Color.White,
            modifier = Modifier
                .size(30.dp)
                .noRippleClickable {
                    onRepeatClicked()
                }
        )
    }
}

@Composable
fun Modifier.noRippleClickable(
    onClick: () -> Unit
): Modifier = this then
        clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }) {
            onClick()
        }


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PlayerScreenPreview() {
    PlayerScreen(
        modifier = Modifier.fillMaxSize(),
        viewModel(),
        isAudioPlaying = false,
    )
}
