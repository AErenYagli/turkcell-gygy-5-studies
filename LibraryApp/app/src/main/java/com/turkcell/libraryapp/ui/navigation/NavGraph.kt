package com.turkcell.libraryapp.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Login.route)
    {
        composable(Screen.Login.route) { Text("Giriş Sayfası") }
        composable(Screen.Register.route) { Text("Kayıt Ol Sayfası")  }
    }
}