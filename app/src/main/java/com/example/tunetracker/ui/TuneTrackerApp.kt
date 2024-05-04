package com.example.tunetracker.ui

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tunetracker.ui.audio.AudioViewModel
import com.example.tunetracker.ui.audio.BottomBarPlayer
import com.example.tunetracker.ui.audio.HomeScreen
import com.example.tunetracker.ui.audio.UIEvents
import com.example.tunetracker.ui.audio.audioDummy
import com.example.tunetracker.ui.navigation.BottomNavigationItem
import com.example.tunetracker.ui.navigation.Screens
import com.example.tunetracker.ui.player.PlayerScreen
import com.example.tunetracker.ui.search.SearchScreen
import com.example.tunetracker.ui.search.SearchViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.math.log


@Composable
fun TuneTrackerApp(
    searchViewModel: SearchViewModel,
    viewModel: AudioViewModel,
    onItemClick: (Int) -> Unit,
    startService: () -> Unit
) {
    var navigationSelectionItem by remember {
        mutableIntStateOf(0)
    }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (navBackStackEntry?.destination?.route != Screens.Player.route) {
                Column {
                    if (viewModel.currentSelectedAudio != audioDummy)
                        BottomBarPlayer(
                            progress = viewModel.progress,
                            audio = viewModel.currentSelectedAudio,
                            isAudioPlaying = viewModel.isPlaying,
                            onStart = { viewModel.onUiEvents(UIEvents.PlayPause) },
                            onNext = {
                                viewModel.onUiEvents(UIEvents.SeekToNext)
                            },
                            onBottomBarClicked = {
                                navController.navigate(Screens.Player.route)
                            }
                        )
                    NavigationBar(containerColor = Color.Transparent) {
                        BottomNavigationItem().bottomNavigationItems()
                            .forEachIndexed { index, navigationItem ->
                                NavigationBarItem(
                                    selected = index == navigationSelectionItem,
                                    label = { Text(text = navigationItem.label) },
                                    icon = {
                                        Icon(
                                            imageVector = navigationItem.icon,
                                            contentDescription = navigationItem.label
                                        )
                                    },
                                    onClick = {
                                        navigationSelectionItem = index
                                        navController.navigate(navigationItem.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                    }
                }

            }

        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = Screens.Home.route) {
                HomeScreen(
                    progress = viewModel.progress,
                    isAudioPlaying = viewModel.isPlaying,
                    currentPlayingAudio = viewModel.currentSelectedAudio,
                    audioList = viewModel.audioList,
                    onStart = { viewModel.onUiEvents(UIEvents.PlayPause) },
                    onItemClick = onItemClick,
                    onNext = {
                        viewModel.onUiEvents(UIEvents.SeekToNext)
                    },
                    onBottomPlayerClicked = {
                        navController.navigate(Screens.Player.route)
                    },
                    viewModel = viewModel
                )
            }
            composable(route = Screens.Search.route) {
                SearchScreen(
                    viewModel = searchViewModel,
                    onSongClicked = { audio ->
                        searchViewModel.getSongUrl(audio.id) {
                            viewModel.setMediaItem(audio, it)
                            startService()
                            navController.navigate(Screens.Player.route)
                        }
                    }
                )
            }
            composable(route = Screens.Profile.route) {

            }
            composable(route = Screens.Player.route) {
                PlayerScreen(
                    audioViewModel = viewModel,
                    isAudioPlaying = viewModel.isPlaying,
                    onStart = { viewModel.onUiEvents(UIEvents.PlayPause) },
                    onNext = {
                        viewModel.onUiEvents(UIEvents.SeekToNext)
                    },
                    onPrevious = {
                        viewModel.onUiEvents(UIEvents.SeekToPrevious)
                    },
                    onRepeatClicked = {
                        viewModel.isRepeating = !viewModel.isRepeating
                        viewModel.onUiEvents(UIEvents.Repeat)
                    },
                    onShuffleClicked = {
                        viewModel.isShuffleOn = !viewModel.isShuffleOn
                        viewModel.onUiEvents(UIEvents.Shuffle)
                    },
                    onDownIconClicked = {
                        navController.navigateUp()
                    }
                )


            }

        }
    }
}