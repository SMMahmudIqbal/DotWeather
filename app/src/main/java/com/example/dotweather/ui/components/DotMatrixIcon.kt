package com.example.dotweather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dotweather.theme.NothingRed
import com.example.dotweather.theme.NothingWhite
import com.example.dotweather.theme.NothingWhite20

// Author Attribution: Developed by S. M. Mahmud Iqbal

enum class WeatherCondition {
    SUNNY,
    PARTLY_CLOUDY,
    CLOUDY,
    RAINY,
    THUNDERSTORM,
    WINDY,
    SNOW
}

/**
 * 7x7 Dot-Matrix patterns representing Nothing OS style weather icons.
 * 1 = filled dot, 0 = empty/dim dot, 2 = red accent dot
 */
object DotMatrixPatterns {
    val SUNNY = arrayOf(
        intArrayOf(0, 0, 1, 0, 1, 0, 0),
        intArrayOf(0, 1, 0, 0, 0, 1, 0),
        intArrayOf(1, 0, 1, 1, 1, 0, 1),
        intArrayOf(0, 0, 1, 2, 1, 0, 0),
        intArrayOf(1, 0, 1, 1, 1, 0, 1),
        intArrayOf(0, 1, 0, 0, 0, 1, 0),
        intArrayOf(0, 0, 1, 0, 1, 0, 0),
    )

    val CLOUDY = arrayOf(
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 1, 1, 1, 0, 0),
        intArrayOf(0, 1, 0, 0, 0, 1, 0),
        intArrayOf(1, 0, 0, 0, 0, 0, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
    )

    val PARTLY_CLOUDY = arrayOf(
        intArrayOf(0, 0, 1, 0, 2, 0, 0),
        intArrayOf(0, 1, 0, 1, 1, 1, 0),
        intArrayOf(0, 0, 1, 0, 0, 0, 1),
        intArrayOf(0, 1, 0, 0, 0, 0, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
    )

    val RAINY = arrayOf(
        intArrayOf(0, 0, 1, 1, 1, 0, 0),
        intArrayOf(0, 1, 0, 0, 0, 1, 0),
        intArrayOf(1, 0, 0, 0, 0, 0, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 0, 1, 0, 1, 0),
        intArrayOf(1, 0, 1, 0, 1, 0, 0),
        intArrayOf(0, 1, 0, 1, 0, 0, 0),
    )

    val THUNDERSTORM = arrayOf(
        intArrayOf(0, 0, 1, 1, 1, 0, 0),
        intArrayOf(0, 1, 0, 0, 0, 1, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 0, 2, 1, 0, 0, 0),
        intArrayOf(0, 2, 1, 0, 0, 0, 0),
        intArrayOf(0, 0, 2, 0, 0, 0, 0),
        intArrayOf(0, 0, 2, 0, 0, 0, 0),
    )

    val WINDY = arrayOf(
        intArrayOf(0, 1, 1, 1, 1, 0, 0),
        intArrayOf(1, 0, 0, 0, 0, 1, 0),
        intArrayOf(0, 0, 0, 0, 0, 1, 0),
        intArrayOf(1, 1, 1, 1, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 1, 1, 0),
        intArrayOf(0, 1, 1, 1, 0, 0, 1),
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
    )

    val SNOW = arrayOf(
        intArrayOf(0, 1, 0, 1, 0, 1, 0),
        intArrayOf(1, 0, 1, 1, 1, 0, 1),
        intArrayOf(0, 1, 0, 1, 0, 1, 0),
        intArrayOf(1, 1, 1, 2, 1, 1, 1),
        intArrayOf(0, 1, 0, 1, 0, 1, 0),
        intArrayOf(1, 0, 1, 1, 1, 0, 1),
        intArrayOf(0, 1, 0, 1, 0, 1, 0),
    )

    val MOON_FULL = arrayOf(
        intArrayOf(0, 0, 1, 1, 1, 0, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 0, 1, 1, 1, 0, 0),
    )

    val MOON_CRESCENT = arrayOf(
        intArrayOf(0, 0, 0, 0, 1, 0, 0),
        intArrayOf(0, 0, 0, 1, 1, 1, 0),
        intArrayOf(0, 0, 0, 0, 1, 1, 1),
        intArrayOf(0, 0, 0, 0, 1, 1, 1),
        intArrayOf(0, 0, 0, 0, 1, 1, 1),
        intArrayOf(0, 0, 0, 1, 1, 1, 0),
        intArrayOf(0, 0, 0, 0, 1, 0, 0),
    )

    val MOON_QUARTER = arrayOf(
        intArrayOf(0, 0, 0, 1, 1, 0, 0),
        intArrayOf(0, 0, 0, 1, 1, 1, 0),
        intArrayOf(0, 0, 0, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 1, 1, 1, 0),
        intArrayOf(0, 0, 0, 1, 1, 0, 0),
    )

    val MOON_GIBBOUS = arrayOf(
        intArrayOf(0, 0, 1, 1, 1, 0, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 0, 1, 1, 1, 0, 0),
    )

    val MOON_NEW = arrayOf(
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 2, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0),
    )
}

@Composable
fun DotMatrixPatternIcon(
    pattern: Array<IntArray>,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    activeColor: Color = NothingWhite,
    inactiveColor: Color = NothingWhite20,
    accentColor: Color = NothingRed
) {
    Canvas(modifier = modifier.size(size)) {
        val rows = pattern.size
        val cols = pattern[0].size
        val dotRadius = (this.size.width / (cols * 2.8f))
        val stepX = this.size.width / cols
        val stepY = this.size.height / rows

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val state = pattern[r][c]
                val color = when (state) {
                    2 -> accentColor
                    1 -> activeColor
                    else -> inactiveColor
                }

                val centerX = c * stepX + stepX / 2f
                val centerY = r * stepY + stepY / 2f

                drawCircle(
                    color = color,
                    radius = if (state > 0) dotRadius else dotRadius * 0.45f,
                    center = Offset(centerX, centerY)
                )
            }
        }
    }
}

@Composable
fun DotMatrixIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    activeColor: Color = NothingWhite,
    inactiveColor: Color = NothingWhite20,
    accentColor: Color = NothingRed
) {
    val pattern = when (condition) {
        WeatherCondition.SUNNY -> DotMatrixPatterns.SUNNY
        WeatherCondition.PARTLY_CLOUDY -> DotMatrixPatterns.PARTLY_CLOUDY
        WeatherCondition.CLOUDY -> DotMatrixPatterns.CLOUDY
        WeatherCondition.RAINY -> DotMatrixPatterns.RAINY
        WeatherCondition.THUNDERSTORM -> DotMatrixPatterns.THUNDERSTORM
        WeatherCondition.WINDY -> DotMatrixPatterns.WINDY
        WeatherCondition.SNOW -> DotMatrixPatterns.SNOW
    }

    Canvas(modifier = modifier.size(size)) {
        val rows = pattern.size
        val cols = pattern[0].size
        val dotRadius = (this.size.width / (cols * 2.8f))
        val stepX = this.size.width / cols
        val stepY = this.size.height / rows

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val state = pattern[r][c]
                val color = when (state) {
                    2 -> accentColor
                    1 -> activeColor
                    else -> inactiveColor
                }

                val centerX = c * stepX + stepX / 2f
                val centerY = r * stepY + stepY / 2f

                drawCircle(
                    color = color,
                    radius = if (state > 0) dotRadius else dotRadius * 0.5f,
                    center = Offset(centerX, centerY)
                )
            }
        }
    }
}
