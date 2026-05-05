package com.turkcell.libraryapp.ui.navigation

// Sayfa routelarımın tanımı.
sealed class Screen(val route: String)
{
    object Login : Screen("login")
    object Register : Screen("register")
    object Homepage : Screen("homepage")
    object Splash : Screen("splash")
}