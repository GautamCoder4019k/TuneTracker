package com.example.tunetracker.ui.navigation

sealed class Screens(val route: String) {
    data object Home : Screens("HomeRoute")
    data object Search : Screens("SearchRoute")
    data object Profile : Screens("ProfileRoute")
    data object Player : Screens("PlayerRoute")
}