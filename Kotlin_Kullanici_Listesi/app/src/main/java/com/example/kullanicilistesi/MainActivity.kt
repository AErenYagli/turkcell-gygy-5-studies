package com.example.kullanicilistesi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kullanicilistesi.ui.theme.KullaniciListesiTheme
import com.example.kullanicilistesi.ui.screen.UserListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KullaniciListesiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    UserListScreen()
                }
            }
        }
    }
}


