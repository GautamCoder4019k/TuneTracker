package com.example.tunetracker.ui.player

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class PlayerViewModel : ViewModel() {

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