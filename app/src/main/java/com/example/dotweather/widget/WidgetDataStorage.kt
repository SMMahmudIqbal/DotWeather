package com.example.dotweather.widget

import android.content.Context
import android.content.SharedPreferences
import com.example.dotweather.data.AirQuality
import com.example.dotweather.data.CityWeather
import com.example.dotweather.data.HourlyForecast
import com.example.dotweather.ui.components.WeatherCondition
import org.json.JSONArray
import org.json.JSONObject

// Author Attribution: Developed by S. M. Mahmud Iqbal

data class WidgetWeatherSnapshot(
    val cityName: String,
    val country: String,
    val currentTemp: Int,
    val highTemp: Int,
    val lowTemp: Int,
    val condition: WeatherCondition,
    val conditionDesc: String,
    val isCelsius: Boolean,
    val aqi: Int,
    val aqiStatus: String,
    val pm25: Float,
    val windSpeed: Int,
    val windDirection: String,
    val humidity: Int,
    val uvIndex: Int,
    val hourly: List<HourlyForecast>,
    val lastUpdated: String
)

object WidgetDataStorage {
    private const val PREFS_NAME = "dotweather_widget_prefs"

    fun saveLatestWeather(context: Context, city: CityWeather, isCelsius: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = JSONObject().apply {
            put("cityName", city.cityName)
            put("country", city.country)
            put("currentTemp", city.currentTemp)
            put("highTemp", city.highTemp)
            put("lowTemp", city.lowTemp)
            put("condition", city.condition.name)
            put("conditionDesc", city.conditionDesc)
            put("isCelsius", isCelsius)
            put("aqi", city.airQuality.aqi)
            put("aqiStatus", city.airQuality.status)
            put("pm25", city.airQuality.pm25.toDouble())
            put("windSpeed", city.windSpeed)
            put("windDirection", city.windDirection)
            put("humidity", city.humidity)
            put("uvIndex", city.uvIndex)
            put("lastUpdated", city.lastUpdated)

            val hourlyArray = JSONArray()
            for (h in city.hourly.take(5)) {
                val hObj = JSONObject().apply {
                    put("time", h.time)
                    put("temp", h.temp)
                    put("condition", h.condition.name)
                    put("precipChance", h.precipChance)
                }
                hourlyArray.put(hObj)
            }
            put("hourly", hourlyArray)
        }
        prefs.edit().putString("latest_snapshot", json.toString()).apply()
    }

    fun loadLatestWeather(context: Context): WidgetWeatherSnapshot {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString("latest_snapshot", null)
        if (raw != null) {
            try {
                val obj = JSONObject(raw)
                val hourlyList = mutableListOf<HourlyForecast>()
                val hourlyArray = obj.optJSONArray("hourly")
                if (hourlyArray != null) {
                    for (i in 0 until hourlyArray.length()) {
                        val hObj = hourlyArray.getJSONObject(i)
                        hourlyList.add(
                            HourlyForecast(
                                time = hObj.optString("time", "NOW"),
                                temp = hObj.optInt("temp", 30),
                                condition = try {
                                    WeatherCondition.valueOf(hObj.optString("condition", "SUNNY"))
                                } catch (e: Exception) {
                                    WeatherCondition.SUNNY
                                },
                                precipChance = hObj.optInt("precipChance", 0)
                            )
                        )
                    }
                }

                return WidgetWeatherSnapshot(
                    cityName = obj.optString("cityName", "DHAKA"),
                    country = obj.optString("country", "BD"),
                    currentTemp = obj.optInt("currentTemp", 32),
                    highTemp = obj.optInt("highTemp", 34),
                    lowTemp = obj.optInt("lowTemp", 25),
                    condition = try {
                        WeatherCondition.valueOf(obj.optString("condition", "SUNNY"))
                    } catch (e: Exception) {
                        WeatherCondition.SUNNY
                    },
                    conditionDesc = obj.optString("conditionDesc", "SUNNY // CLEAR"),
                    isCelsius = obj.optBoolean("isCelsius", true),
                    aqi = obj.optInt("aqi", 42),
                    aqiStatus = obj.optString("aqiStatus", "GOOD"),
                    pm25 = obj.optDouble("pm25", 9.2).toFloat(),
                    windSpeed = obj.optInt("windSpeed", 8),
                    windDirection = obj.optString("windDirection", "N"),
                    humidity = obj.optInt("humidity", 65),
                    uvIndex = obj.optInt("uvIndex", 6),
                    hourly = if (hourlyList.isNotEmpty()) hourlyList else getFallbackHourly(),
                    lastUpdated = obj.optString("lastUpdated", "LIVE")
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Return sensible Nothing OS fallback snapshot
        return WidgetWeatherSnapshot(
            cityName = "DHAKA",
            country = "BD",
            currentTemp = 32,
            highTemp = 34,
            lowTemp = 25,
            condition = WeatherCondition.SUNNY,
            conditionDesc = "SUNNY // CLEAR",
            isCelsius = true,
            aqi = 42,
            aqiStatus = "GOOD",
            pm25 = 9.2f,
            windSpeed = 8,
            windDirection = "N",
            humidity = 65,
            uvIndex = 6,
            hourly = getFallbackHourly(),
            lastUpdated = "LIVE"
        )
    }

    private fun getFallbackHourly(): List<HourlyForecast> = listOf(
        HourlyForecast("NOW", 32, WeatherCondition.SUNNY, 0),
        HourlyForecast("+1H", 32, WeatherCondition.SUNNY, 0),
        HourlyForecast("+2H", 31, WeatherCondition.PARTLY_CLOUDY, 5),
        HourlyForecast("+3H", 29, WeatherCondition.PARTLY_CLOUDY, 10)
    )
}
