package com.example.tunetracker

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.tunetracker.player.service.AudioService
import com.example.tunetracker.ui.audio.AudioViewModel
import com.example.tunetracker.ui.audio.HomeScreen
import com.example.tunetracker.ui.audio.UIEvents
import com.example.tunetracker.ui.theme.TuneTrackerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: AudioViewModel by viewModels()
    private var isServiceRunning = false

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TuneTrackerTheme {
                // A surface container using the 'background' color from the theme
                val permissionState =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_AUDIO)
                    } else {
                        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                Log.d("Permission", "onCreate:${permissionState.status} ")
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(key1 = lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME)
                            permissionState.launchPermissionRequest()
                        Log.d("Permission", "onCreate:${permissionState.status} ")
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        progress = viewModel.progress,
                        onProgress = { viewModel.onUiEvents(UIEvents.SeekTo(it)) },
                        isAudioPlaying = viewModel.isPlaying,
                        currentPlayingAudio = viewModel.currentSelectedAudio,
                        audioList = viewModel.audioList,
                        onStart = { viewModel.onUiEvents(UIEvents.PlayPause) },
                        onItemClick = {
                            viewModel.onUiEvents(UIEvents.SelectedAudioChange(it))
                            startService()
                        },
                        onNext = {
                            viewModel.onUiEvents(UIEvents.SeekToNext)
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        println("on destroy is called")
        super.onDestroy()
        stopService(Intent(this, AudioService::class.java))
        isServiceRunning=false
    }

    private fun startService() {
        if (!isServiceRunning) {
            val intent = Intent(this, AudioService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else
                startService(intent)
            isServiceRunning = true
        }
    }
}
