package com.example.tunetracker.ui.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext


@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun LockScreenOrientation() {
    val context = LocalContext.current
    DisposableEffect(context) {
        // Lock the screen orientation.

        context.requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose {
            // Release the the screen orientation lock.
            context.requireActivity().requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}

/**
 * Find the activity context. If one isn't present, an exception is thrown.
 */
fun Context.requireActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("No activity was present but it is required.")
}