package com.example.tunetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

@Composable
fun CustomIconButton(imageVector: ImageVector, contentDescription: String, onclick: () -> Unit,size: Dp) {
    Icon(
        imageVector = imageVector,
        contentDescription = "Previous",
        tint = Color.White,
        modifier = Modifier
            .size(size)
            .clickable { onclick() }
    )
}