package com.example.tunetracker.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tunetracker.ui.audio.AudioViewModel
import com.example.tunetracker.ui.audio.HomeScreen
import com.example.tunetracker.ui.audio.UIEvents
import com.example.tunetracker.ui.navigation.BottomNavigationItem
import com.example.tunetracker.ui.navigation.Screens
import com.example.tunetracker.ui.player.PlayerScreen


@Composable
fun TuneTrackerApp(viewModel: AudioViewModel, onItemClick: (Int) -> Unit) {
    var navigationSelectionItem by remember {
        mutableIntStateOf(0)
    }
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
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
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = Screens.Home.route) {
                HomeScreen(
                    progress = viewModel.progress,
                    onProgress = { viewModel.onUiEvents(UIEvents.SeekTo(it)) },
                    isAudioPlaying = viewModel.isPlaying,
                    currentPlayingAudio = viewModel.currentSelectedAudio,
                    audioList = viewModel.audioList,
                    onStart = { viewModel.onUiEvents(UIEvents.PlayPause) },
                    onItemClick = onItemClick,
                    onNext = {
                        viewModel.onUiEvents(UIEvents.SeekToNext)
                    },
                    onBottomPlayerClicked = { navController.navigate(Screens.Player.route) },
                    viewModel = viewModel
                )
            }
            composable(route = Screens.Search.route) {

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
                        viewModel.isShuffleOn=!viewModel.isShuffleOn
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