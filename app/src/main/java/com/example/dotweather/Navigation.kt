package com.example.dotweather

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.dotweather.ui.main.MainScreen

// Author Attribution: Developed by S. M. Mahmud Iqbal

@Composable
fun MainNavigation(
    initialTab: Int = -1
) {
    MainScreen(
        initialTab = initialTab,
        modifier = Modifier.safeDrawingPadding()
    )
}
