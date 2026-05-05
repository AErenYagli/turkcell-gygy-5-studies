package com.turkcell.libraryapp.ui.screen

import android.window.SplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel
import com.turkcell.libraryapp.ui.viewmodel.SessionState

@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onAuthenticated: (String) -> Unit,
    onUnauthenticated: () -> Unit
) {
    // hesaplama sonucunu oku..
    val sessionState by authViewModel.sessionState.collectAsState();

    // hesaplama sonucu her değiştiğinde..
    LaunchedEffect(sessionState) {
        when (val s = sessionState)
        {
            is SessionState.Authenticated -> onAuthenticated(s.role)
            SessionState.Unauthenticated -> onUnauthenticated()
            SessionState.Initializing -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}