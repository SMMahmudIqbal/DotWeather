package com.example.dotweather.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.example.dotweather.ui.components.DotMatrixPatterns
import com.example.dotweather.ui.components.WeatherCondition

// Author Attribution: Developed by S. M. Mahmud Iqbal

object DotMatrixWidgetRenderer {

    private const val COLOR_NOTHING_RED = 0xFFD71920.toInt()
    private const val COLOR_NOTHING_WHITE = 0xFFFFFFFF.toInt()
    private const val COLOR_INACTIVE_DOT = 0x22FFFFFF
    private const val COLOR_AQI_WARNING_RED = 0xFFD71920.toInt()

    /**
     * Renders a 7x7 Nothing OS procedural dot-matrix icon for RemoteViews.
     */
    fun renderConditionBitmap(condition: WeatherCondition, sizePx: Int = 180): Bitmap {
        val pattern = when (condition) {
            WeatherCondition.SUNNY -> DotMatrixPatterns.SUNNY
            WeatherCondition.PARTLY_CLOUDY -> DotMatrixPatterns.PARTLY_CLOUDY
            WeatherCondition.CLOUDY -> DotMatrixPatterns.CLOUDY
            WeatherCondition.RAINY -> DotMatrixPatterns.RAINY
            WeatherCondition.THUNDERSTORM -> DotMatrixPatterns.THUNDERSTORM
            WeatherCondition.WINDY -> DotMatrixPatterns.WINDY
            WeatherCondition.SNOW -> DotMatrixPatterns.SNOW
        }

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }

        val rows = pattern.size
        val cols = pattern[0].size
        val dotRadius = sizePx / (cols * 2.85f)
        val stepX = sizePx.toFloat() / cols
        val stepY = sizePx.toFloat() / rows

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val state = pattern[r][c]
                paint.color = when (state) {
                    2 -> COLOR_NOTHING_RED
                    1 -> COLOR_NOTHING_WHITE
                    else -> COLOR_INACTIVE_DOT
                }
                val cx = c * stepX + stepX / 2f
                val cy = r * stepY + stepY / 2f
                val radius = if (state > 0) dotRadius else dotRadius * 0.45f
                canvas.drawCircle(cx, cy, radius, paint)
            }
        }
        return bitmap
    }

    /**
     * Renders a circular dot-matrix LED gauge representing AQI level.
     */
    fun renderAqiGaugeBitmap(aqi: Int, sizePx: Int = 160): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }

        val numDots = 12
        val radiusFromCenter = sizePx * 0.36f
        val dotRadius = sizePx * 0.08f
        val cx = sizePx / 2f
        val cy = sizePx / 2f

        // Calculate how many dots should be illuminated based on AQI (0 to 200+)
        val activeCount = ((aqi.coerceIn(0, 200) / 200f) * numDots).toInt().coerceIn(1, numDots)
        val isUnhealthy = aqi > 100

        for (i in 0 until numDots) {
            val angle = Math.toRadians((i * (360.0 / numDots) - 90.0))
            val dotX = (cx + radiusFromCenter * Math.cos(angle)).toFloat()
            val dotY = (cy + radiusFromCenter * Math.sin(angle)).toFloat()

            if (i < activeCount) {
                paint.color = if (isUnhealthy && i >= numDots - 3) COLOR_AQI_WARNING_RED else COLOR_NOTHING_WHITE
                canvas.drawCircle(dotX, dotY, dotRadius, paint)
            } else {
                paint.color = COLOR_INACTIVE_DOT
                canvas.drawCircle(dotX, dotY, dotRadius * 0.5f, paint)
            }
        }

        // Center status dot
        paint.color = if (isUnhealthy) COLOR_NOTHING_RED else COLOR_NOTHING_WHITE
        canvas.drawCircle(cx, cy, dotRadius * 0.9f, paint)

        return bitmap
    }
}
