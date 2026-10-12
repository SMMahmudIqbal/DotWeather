package com.example.dotweather.data

import android.content.Context
import android.content.SharedPreferences
import com.example.dotweather.ui.components.WeatherCondition
import org.json.JSONArray
import org.json.JSONObject

// Author Attribution: Developed by S. M. Mahmud Iqbal

data class SavedLocation(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val isCustom: Boolean = true,
    val isGps: Boolean = false
)

class CityStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("dotweather_prefs", Context.MODE_PRIVATE)

    fun getUnitIsCelsius(): Boolean = prefs.getBoolean("is_celsius", true)
    fun setUnitIsCelsius(isCelsius: Boolean) = prefs.edit().putBoolean("is_celsius", isCelsius).apply()

    fun getActiveCityName(): String? = prefs.getString("active_city_name", null)
    fun getActiveCityCountry(): String = prefs.getString("active_city_country", "") ?: ""
    fun getActiveCityLat(): Double = prefs.getString("active_city_lat", null)?.toDoubleOrNull() ?: Double.NaN
    fun getActiveCityLon(): Double = prefs.getString("active_city_lon", null)?.toDoubleOrNull() ?: Double.NaN

    fun setActiveCity(name: String, country: String, lat: Double, lon: Double) {
        prefs.edit()
            .putString("active_city_name", name.uppercase())
            .putString("active_city_country", country.uppercase())
            .putString("active_city_lat", lat.toString())
            .putString("active_city_lon", lon.toString())
            .apply()
    }

    fun getSavedLocations(): List<SavedLocation> {
        val raw = prefs.getString("saved_locations", null) ?: return emptyList()
        val list = mutableListOf<SavedLocation>()
        try {
            val jsonArray = JSONArray(raw)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SavedLocation(
                        name = obj.getString("name").uppercase(),
                        country = obj.getString("country").uppercase(),
                        lat = obj.getDouble("lat"),
                        lon = obj.getDouble("lon"),
                        isCustom = obj.optBoolean("isCustom", true),
                        isGps = obj.optBoolean("isGps", false)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveLocations(locations: List<SavedLocation>) {
        val jsonArray = JSONArray()
        for (loc in locations) {
            val obj = JSONObject().apply {
                put("name", loc.name.uppercase())
                put("country", loc.country.uppercase())
                put("lat", loc.lat)
                put("lon", loc.lon)
                put("isCustom", loc.isCustom)
                put("isGps", loc.isGps)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("saved_locations", jsonArray.toString()).apply()
    }

    fun getCachedPayload(): String? = prefs.getString("cached_weather_payload", null)
    fun setCachedPayload(json: String) = prefs.edit().putString("cached_weather_payload", json).apply()

    fun saveCachedCityWeather(city: CityWeather) {
        try {
            val obj = JSONObject().apply {
                put("cityName", city.cityName)
                put("country", city.country)
                put("currentTemp", city.currentTemp)
                put("feelsLike", city.feelsLike)
                put("condition", city.condition.name)
                put("conditionDesc", city.conditionDesc)
                put("highTemp", city.highTemp)
                put("lowTemp", city.lowTemp)
                put("humidity", city.humidity)
                put("windSpeed", city.windSpeed)
                put("windDirection", city.windDirection)
                put("uvIndex", city.uvIndex)
                put("pressureHpa", city.pressureHpa)
                put("visibilityKm", city.visibilityKm)
                put("sunrise", city.sunrise)
                put("sunset", city.sunset)
                put("daylightDurationDesc", city.daylightDurationDesc)
                put("isCustomLocation", city.isCustomLocation)
                put("isGpsLocation", city.isGpsLocation)
                put("latitude", city.latitude)
                put("longitude", city.longitude)
                put("lastUpdated", city.lastUpdated)

                val aqiObj = JSONObject().apply {
                    put("aqi", city.airQuality.aqi)
                    put("status", city.airQuality.status)
                    put("pm25", city.airQuality.pm25.toDouble())
                    put("pm10", city.airQuality.pm10.toDouble())
                    put("o3", city.airQuality.o3.toDouble())
                    put("no2", city.airQuality.no2.toDouble())
                }
                put("airQuality", aqiObj)

                val hourlyArray = JSONArray()
                for (h in city.hourly) {
                    val hObj = JSONObject().apply {
                        put("time", h.time)
                        put("temp", h.temp)
                        put("condition", h.condition.name)
                        put("precipChance", h.precipChance)
                    }
                    hourlyArray.put(hObj)
                }
                put("hourly", hourlyArray)

                val dailyArray = JSONArray()
                for (d in city.daily) {
                    val dObj = JSONObject().apply {
                        put("day", d.day)
                        put("minTemp", d.minTemp)
                        put("maxTemp", d.maxTemp)
                        put("condition", d.condition.name)
                    }
                    dailyArray.put(dObj)
                }
                put("daily", dailyArray)
            }
            setCachedPayload(obj.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCachedCityWeather(): CityWeather? {
        val raw = getCachedPayload() ?: return null
        return try {
            val obj = JSONObject(raw)
            val aqiObj = obj.optJSONObject("airQuality")
            val airQuality = AirQuality(
                aqi = aqiObj?.optInt("aqi", 25) ?: 25,
                status = aqiObj?.optString("status", "GOOD") ?: "GOOD",
                pm25 = aqiObj?.optDouble("pm25", 8.0)?.toFloat() ?: 8.0f,
                pm10 = aqiObj?.optDouble("pm10", 14.0)?.toFloat() ?: 14.0f,
                o3 = aqiObj?.optDouble("o3", 28.0)?.toFloat() ?: 28.0f,
                no2 = aqiObj?.optDouble("no2", 16.0)?.toFloat() ?: 16.0f
            )

            val hourly = mutableListOf<HourlyForecast>()
            val hArr = obj.optJSONArray("hourly")
            if (hArr != null) {
                for (i in 0 until hArr.length()) {
                    val hObj = hArr.getJSONObject(i)
                    hourly.add(
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

            val daily = mutableListOf<DailyForecast>()
            val dArr = obj.optJSONArray("daily")
            if (dArr != null) {
                for (i in 0 until dArr.length()) {
                    val dObj = dArr.getJSONObject(i)
                    daily.add(
                        DailyForecast(
                            day = dObj.optString("day", "TODAY"),
                            minTemp = dObj.optInt("minTemp", 24),
                            maxTemp = dObj.optInt("maxTemp", 32),
                            condition = try {
                                WeatherCondition.valueOf(dObj.optString("condition", "SUNNY"))
                            } catch (e: Exception) {
                                WeatherCondition.SUNNY
                            }
                        )
                    )
                }
            }

            CityWeather(
                cityName = obj.getString("cityName"),
                country = obj.optString("country", ""),
                currentTemp = obj.optInt("currentTemp", 30),
                feelsLike = obj.optInt("feelsLike", 33),
                condition = try {
                    WeatherCondition.valueOf(obj.optString("condition", "SUNNY"))
                } catch (e: Exception) {
                    WeatherCondition.SUNNY
                },
                conditionDesc = obj.optString("conditionDesc", "CACHED TELEMETRY"),
                highTemp = obj.optInt("highTemp", 33),
                lowTemp = obj.optInt("lowTemp", 24),
                humidity = obj.optInt("humidity", 65),
                windSpeed = obj.optInt("windSpeed", 10),
                windDirection = obj.optString("windDirection", "N"),
                uvIndex = obj.optInt("uvIndex", 5),
                pressureHpa = obj.optInt("pressureHpa", 1012),
                visibilityKm = obj.optInt("visibilityKm", 10),
                sunrise = obj.optString("sunrise", "--:--"),
                sunset = obj.optString("sunset", "--:--"),
                daylightDurationDesc = obj.optString("daylightDurationDesc", ""),
                airQuality = airQuality,
                hourly = hourly,
                daily = daily,
                isCustomLocation = obj.optBoolean("isCustomLocation", true),
                isGpsLocation = obj.optBoolean("isGpsLocation", false),
                latitude = obj.optDouble("latitude", 0.0),
                longitude = obj.optDouble("longitude", 0.0),
                lastUpdated = obj.optString("lastUpdated", "CACHED")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
