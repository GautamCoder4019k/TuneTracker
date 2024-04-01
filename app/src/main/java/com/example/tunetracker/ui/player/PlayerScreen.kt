package com.example.tunetracker.ui.player

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.PlayCircleOutline
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tunetracker.R
import com.example.tunetracker.ui.theme.Green
import com.example.tunetracker.ui.theme.White

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlayerScreen(modifier: Modifier = Modifier, playerViewModel: PlayerViewModel = viewModel()) {
    Scaffold(topBar = { PlayerTopAppBar() }) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            playerViewModel.getRandomColor(),
                            playerViewModel.getRandomColor()
                        )
                    )
                )
                .padding(bottom = 16.dp)

        ) {
            Spacer(modifier = Modifier.weight(2f))
            Image(
                painter = painterResource(R.drawable.songimage),
                contentDescription = "Song Cover Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(16.dp)
                    .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .weight(10f)
            )
            SongDescription(songName = "Song name", artistName = "artist")
            Column(modifier = Modifier.weight(5f)) {
                PlayerSlider()
                PlayerButtons(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTopAppBar() {
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
            IconButton(onClick = { /*TODO*/ }) {
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

@Composable
fun SongDescription(songName: String, artistName: String) {
    Text(
        text = songName,
        style = MaterialTheme.typography.headlineSmall,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    )
    Text(
        text = artistName,
        style = MaterialTheme.typography.headlineSmall,
        color = Color.Gray,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    )
}

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PlayerSlider(modifier: Modifier = Modifier) {
    val interactionSource = MutableInteractionSource()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Slider(
                value = 0.3f,
                onValueChange = {},
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
            Text(text = "0.00", color = Color.White)
            Text(text = "0.00", color = Color.White)

        }

    }
}


@Composable
fun PlayerButtons(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var iconColor by remember {
            mutableStateOf(Color.White)
        }
        var isPlaying by remember {
            mutableStateOf(false)
        }
        Icon(
            painter = painterResource(id = R.drawable.shuffle),
            contentDescription = "Shuffle songs",
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    iconColor = if (iconColor == Color.White) Green else Color.White
                }
        )
        Icon(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = "Previous",
            tint = Color.White,
            modifier = Modifier
                .size(48.dp)
                .clickable { }
        )
        Button(
            onClick = { isPlaying=!isPlaying },
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White),
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = if(isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(48.dp)
            )
        }
        Icon(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = "Previous",
            tint = Color.White,
            modifier = Modifier
                .size(48.dp)
                .clickable { }
        )
        Icon(
            imageVector = Icons.Default.ChecklistRtl,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PlayerScreenPreview() {
    PlayerScreen(modifier = Modifier.fillMaxSize())
}
