package com.example.dotweather.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import com.example.dotweather.MainActivity
import com.example.dotweather.R

// Author Attribution: Developed by S. M. Mahmud Iqbal

private const val TAG = "WeatherWidgets"

/**
 * 1. Minimalist Weather Squircle / Pill Widget (2x2)
 */
class WeatherQuickWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val snapshot = WidgetDataStorage.loadLatestWeather(context)
        for (appWidgetId in appWidgetIds) {
            try {
                val views = buildRemoteViews(context, appWidgetId, snapshot)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating QuickWidget #$appWidgetId", e)
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val snapshot = WidgetDataStorage.loadLatestWeather(context)
        try {
            val views = buildRemoteViews(context, appWidgetId, snapshot)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        } catch (e: Exception) {
            Log.e(TAG, "Error onAppWidgetOptionsChanged QuickWidget #$appWidgetId", e)
        }
    }

    companion object {
        fun buildRemoteViews(
            context: Context,
            appWidgetId: Int,
            snapshot: WidgetWeatherSnapshot
        ): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_quick_weather)

            val displayTemp = if (snapshot.isCelsius) snapshot.currentTemp else (snapshot.currentTemp * 9 / 5 + 32)
            val displayHigh = if (snapshot.isCelsius) snapshot.highTemp else (snapshot.highTemp * 9 / 5 + 32)
            val displayLow = if (snapshot.isCelsius) snapshot.lowTemp else (snapshot.lowTemp * 9 / 5 + 32)
            val unitStr = if (snapshot.isCelsius) "°" else "°F"

            views.setTextViewText(R.id.widget_quick_city, "${snapshot.cityName} // ${snapshot.country}")
            views.setTextViewText(R.id.widget_quick_tag, snapshot.lastUpdated)
            views.setTextViewText(R.id.widget_quick_temp, "$displayTemp$unitStr")
            views.setTextViewText(R.id.widget_quick_desc, snapshot.conditionDesc.uppercase())
            views.setTextViewText(R.id.widget_quick_range, "H:$displayHigh° L:$displayLow°")

            // Render crisp Nothing OS 7x7 dot-matrix bitmap
            try {
                val iconBitmap = DotMatrixWidgetRenderer.renderConditionBitmap(snapshot.condition, sizePx = 180)
                views.setImageViewBitmap(R.id.widget_quick_icon, iconBitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to render dot matrix icon", e)
            }

            // Click opens app to active city
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_quick_root, pendingIntent)

            return views
        }
    }
}

/**
 * 2. Hourly Forecast Timeline Strip Widget (4x2 / 4x1)
 */
class WeatherForecastWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val snapshot = WidgetDataStorage.loadLatestWeather(context)
        for (appWidgetId in appWidgetIds) {
            try {
                val views = buildRemoteViews(context, appWidgetId, snapshot)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating ForecastWidget #$appWidgetId", e)
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val snapshot = WidgetDataStorage.loadLatestWeather(context)
        try {
            val views = buildRemoteViews(context, appWidgetId, snapshot)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        } catch (e: Exception) {
            Log.e(TAG, "Error onAppWidgetOptionsChanged ForecastWidget #$appWidgetId", e)
        }
    }

    companion object {
        fun buildRemoteViews(
            context: Context,
            appWidgetId: Int,
            snapshot: WidgetWeatherSnapshot
        ): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_forecast_strip)

            val currentTemp = if (snapshot.isCelsius) snapshot.currentTemp else (snapshot.currentTemp * 9 / 5 + 32)
            views.setTextViewText(R.id.widget_forecast_city, snapshot.cityName)
            views.setTextViewText(R.id.widget_forecast_current_temp, "$currentTemp°")

            // Populate up to 4 hourly forecast slots
            val hourly = snapshot.hourly
            val timeViews = intArrayOf(
                R.id.widget_forecast_time_0,
                R.id.widget_forecast_time_1,
                R.id.widget_forecast_time_2,
                R.id.widget_forecast_time_3
            )
            val iconViews = intArrayOf(
                R.id.widget_forecast_icon_0,
                R.id.widget_forecast_icon_1,
                R.id.widget_forecast_icon_2,
                R.id.widget_forecast_icon_3
            )
            val tempViews = intArrayOf(
                R.id.widget_forecast_temp_0,
                R.id.widget_forecast_temp_1,
                R.id.widget_forecast_temp_2,
                R.id.widget_forecast_temp_3
            )
            val popViews = intArrayOf(
                R.id.widget_forecast_pop_0,
                R.id.widget_forecast_pop_1,
                R.id.widget_forecast_pop_2,
                R.id.widget_forecast_pop_3
            )

            for (i in 0 until 4) {
                val item = hourly.getOrNull(i)
                if (item != null) {
                    val itemTemp = if (snapshot.isCelsius) item.temp else (item.temp * 9 / 5 + 32)
                    views.setTextViewText(timeViews[i], item.time)
                    views.setTextViewText(tempViews[i], "$itemTemp°")
                    views.setTextViewText(popViews[i], "${item.precipChance}%")

                    try {
                        val miniBitmap = DotMatrixWidgetRenderer.renderConditionBitmap(item.condition, sizePx = 90)
                        views.setImageViewBitmap(iconViews[i], miniBitmap)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed mini condition icon at index $i", e)
                    }
                }
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("TARGET_TAB", 0) // HOURLY TAB
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_forecast_root, pendingIntent)

            return views
        }
    }
}

/**
 * 3. Air Quality & Environmental Sensor Hub Widget (2x2)
 */
class WeatherAirQualityWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val snapshot = WidgetDataStorage.loadLatestWeather(context)
        for (appWidgetId in appWidgetIds) {
            try {
                val views = buildRemoteViews(context, appWidgetId, snapshot)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating AirQualityWidget #$appWidgetId", e)
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val snapshot = WidgetDataStorage.loadLatestWeather(context)
        try {
            val views = buildRemoteViews(context, appWidgetId, snapshot)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        } catch (e: Exception) {
            Log.e(TAG, "Error onAppWidgetOptionsChanged AirQualityWidget #$appWidgetId", e)
        }
    }

    companion object {
        fun buildRemoteViews(
            context: Context,
            appWidgetId: Int,
            snapshot: WidgetWeatherSnapshot
        ): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_air_quality)

            views.setTextViewText(R.id.widget_aqi_badge, snapshot.aqiStatus.uppercase())
            views.setTextViewText(R.id.widget_aqi_val, snapshot.aqi.toString())

            // Gauge bitmap
            try {
                val gaugeBitmap = DotMatrixWidgetRenderer.renderAqiGaugeBitmap(snapshot.aqi, sizePx = 140)
                views.setImageViewBitmap(R.id.widget_aqi_meter, gaugeBitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Failed gauge bitmap", e)
            }

            // Environmental metrics
            views.setTextViewText(R.id.widget_aqi_pm25, "${snapshot.pm25} µg")
            views.setTextViewText(R.id.widget_aqi_wind, "${snapshot.windSpeed} KM/H ${snapshot.windDirection}")
            views.setTextViewText(R.id.widget_aqi_humidity, "${snapshot.humidity}%")
            views.setTextViewText(R.id.widget_aqi_uv, snapshot.uvIndex.toString())

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("TARGET_TAB", 1) // AIR QUALITY TAB
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_aqi_root, pendingIntent)

            return views
        }
    }
}

/**
 * Global update dispatcher for all widgets
 */
object WidgetUpdateManager {
    fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)

        // 1. Quick Glance
        val quickComponent = ComponentName(context, WeatherQuickWidgetProvider::class.java)
        val quickIds = appWidgetManager.getAppWidgetIds(quickComponent)
        if (quickIds.isNotEmpty()) {
            WeatherQuickWidgetProvider().onUpdate(context, appWidgetManager, quickIds)
        }

        // 2. Forecast Strip
        val forecastComponent = ComponentName(context, WeatherForecastWidgetProvider::class.java)
        val forecastIds = appWidgetManager.getAppWidgetIds(forecastComponent)
        if (forecastIds.isNotEmpty()) {
            WeatherForecastWidgetProvider().onUpdate(context, appWidgetManager, forecastIds)
        }

        // 3. Air Quality
        val aqiComponent = ComponentName(context, WeatherAirQualityWidgetProvider::class.java)
        val aqiIds = appWidgetManager.getAppWidgetIds(aqiComponent)
        if (aqiIds.isNotEmpty()) {
            WeatherAirQualityWidgetProvider().onUpdate(context, appWidgetManager, aqiIds)
        }
    }
}
