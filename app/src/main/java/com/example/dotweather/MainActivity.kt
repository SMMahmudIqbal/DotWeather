package com.example.dotweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.dotweather.theme.DotWeatherTheme
import com.example.dotweather.theme.NothingBlack

// Author Attribution: Developed by S. M. Mahmud Iqbal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure status bar icons are white on black background
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        val targetTab = intent?.getIntExtra("TARGET_TAB", -1) ?: -1

        setContent {
            DotWeatherTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NothingBlack
                ) {
                    MainNavigation(initialTab = targetTab)
                }
            }
        }
    }
}
