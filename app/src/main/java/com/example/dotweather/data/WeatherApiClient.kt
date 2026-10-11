package com.example.dotweather.data

import com.example.dotweather.ui.components.WeatherCondition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Author Attribution: Developed by S. M. Mahmud Iqbal

/**
 * Live Weather API Client using Open-Meteo & Open-Meteo Geocoding APIs.
 * 100% Free, no API key required, high precision global weather & air quality.
 */
object WeatherApiClient {

    data class GeoResult(
        val name: String,
        val country: String,
        val latitude: Double,
        val longitude: Double
    )

    private fun mapWeatherCodeToCondition(wmoCode: Int): Pair<WeatherCondition, String> {
        return when (wmoCode) {
            0 -> WeatherCondition.SUNNY to "CLEAR SKY // RADIANT"
            1 -> WeatherCondition.SUNNY to "MAINLY CLEAR // BRIGHT"
            2 -> WeatherCondition.PARTLY_CLOUDY to "PARTLY CLOUDY // BREEZE"
            3 -> WeatherCondition.CLOUDY to "OVERCAST // DENSE CLOUDS"
            45, 48 -> WeatherCondition.CLOUDY to "FOG // REDUCED VISIBILITY"
            51, 53, 55 -> WeatherCondition.RAINY to "DRIZZLE // MIST"
            61, 63 -> WeatherCondition.RAINY to "RAIN // SHOWERS"
            65 -> WeatherCondition.RAINY to "HEAVY RAIN // DOWNPOUR"
            71, 73, 75, 77, 85, 86 -> WeatherCondition.SNOW to "SNOW // FLURRIES"
            80, 81, 82 -> WeatherCondition.RAINY to "RAIN SHOWERS // GUSTY"
            95 -> WeatherCondition.THUNDERSTORM to "THUNDERSTORM // LIGHTNING"
            96, 99 -> WeatherCondition.THUNDERSTORM to "STORM CELLS // HAIL"
            else -> WeatherCondition.PARTLY_CLOUDY to "VARIABLE CONDITIONS"
        }
    }

    private fun degreesToDirection(degrees: Double): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = (((degrees % 360) / 22.5) + 0.5).toInt() % 16
        return directions[index]
    }

    private fun httpGet(urlString: String): String {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        conn.setRequestProperty("User-Agent", "DotWeather-Android-NothingOS/1.2")
        conn.inputStream.bufferedReader().use { reader ->
            return reader.readText()
        }
    }

    /**
     * Resolves location name to latitude & longitude using Open-Meteo Geocoding
     */
    suspend fun searchGeocoding(cityName: String): GeoResult? = withContext(Dispatchers.IO) {
        try {
            val encoded = URLEncoder.encode(cityName.trim(), "UTF-8")
            val url = "https://geocoding-api.open-meteo.com/v1/search?name=$encoded&count=1&language=en&format=json"
            val response = httpGet(url)
            val json = JSONObject(response)
            if (json.has("results")) {
                val array = json.getJSONArray("results")
                if (array.length() > 0) {
                    val first = array.getJSONObject(0)
                    val name = first.optString("name", cityName.uppercase())
                    val country = first.optString("country_code", first.optString("country", "GLOBAL")).uppercase()
                    val lat = first.getDouble("latitude")
                    val lon = first.getDouble("longitude")
                    return@withContext GeoResult(name, country, lat, lon)
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Fetches live weather and air quality for given coordinates.
     */
    suspend fun fetchLiveWeather(
        name: String,
        country: String,
        latitude: Double,
        longitude: Double,
        isCustom: Boolean = false
    ): CityWeather? = withContext(Dispatchers.IO) {
        try {
            // 1. Fetch Forecast
            val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,surface_pressure,wind_speed_10m,wind_direction_10m&hourly=temperature_2m,precipitation_probability,weather_code&daily=weather_code,temperature_2m_max,temperature_2m_min,uv_index_max&timezone=auto"
            val weatherResponse = httpGet(weatherUrl)
            val weatherJson = JSONObject(weatherResponse)

            // Current
            val current = weatherJson.getJSONObject("current")
            val currentTemp = current.getDouble("temperature_2m").toInt()
            val feelsLike = current.getDouble("apparent_temperature").toInt()
            val humidity = current.getInt("relative_humidity_2m")
            val wmoCode = current.getInt("weather_code")
            val pressure = current.getDouble("surface_pressure").toInt()
            val windSpeed = current.getDouble("wind_speed_10m").toInt()
            val windDirDeg = current.getDouble("wind_direction_10m")
            val windDirection = degreesToDirection(windDirDeg)

            val (condition, conditionDesc) = mapWeatherCodeToCondition(wmoCode)

            // Daily Forecast
            val daily = weatherJson.getJSONObject("daily")
            val dailyDates = daily.getJSONArray("time")
            val dailyCodes = daily.getJSONArray("weather_code")
            val dailyMaxs = daily.getJSONArray("temperature_2m_max")
            val dailyMins = daily.getJSONArray("temperature_2m_min")
            val dailyUv = daily.optJSONArray("uv_index_max")

            val todayHigh = if (dailyMaxs.length() > 0) dailyMaxs.getDouble(0).toInt() else currentTemp + 3
            val todayLow = if (dailyMins.length() > 0) dailyMins.getDouble(0).toInt() else currentTemp - 4
            val todayUv = if (dailyUv != null && dailyUv.length() > 0) dailyUv.getDouble(0).toInt() else 4

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dayNameFormat = SimpleDateFormat("EEE", Locale.ENGLISH)

            val dailyForecasts = mutableListOf<DailyForecast>()
            val countDaily = minOf(dailyDates.length(), 7)
            for (i in 0 until countDaily) {
                val dateStr = dailyDates.getString(i)
                val dayName = if (i == 0) "TODAY" else {
                    try {
                        val parsed = dateFormat.parse(dateStr)
                        dayNameFormat.format(parsed ?: Date()).uppercase()
                    } catch (e: Exception) {
                        "DAY $i"
                    }
                }
                val dCode = dailyCodes.getInt(i)
                val dMax = dailyMaxs.getDouble(i).toInt()
                val dMin = dailyMins.getDouble(i).toInt()
                val (dCond, _) = mapWeatherCodeToCondition(dCode)
                dailyForecasts.add(DailyForecast(dayName, dMin, dMax, dCond))
            }

            // Hourly Forecast (Next 10 hours)
            val hourly = weatherJson.getJSONObject("hourly")
            val hourlyTimes = hourly.getJSONArray("time")
            val hourlyTemps = hourly.getJSONArray("temperature_2m")
            val hourlyPrecip = hourly.getJSONArray("precipitation_probability")
            val hourlyCodes = hourly.getJSONArray("weather_code")

            val hourlyForecasts = mutableListOf<HourlyForecast>()
            val timeParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
            val hourDisplayFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

            val countHourly = minOf(hourlyTimes.length(), 10)
            for (i in 0 until countHourly) {
                val rawTime = hourlyTimes.getString(i)
                val displayTime = if (i == 0) "NOW" else {
                    try {
                        val parsed = timeParser.parse(rawTime)
                        hourDisplayFormat.format(parsed ?: Date())
                    } catch (e: Exception) {
                        "+${i}H"
                    }
                }
                val hTemp = hourlyTemps.getDouble(i).toInt()
                val hPrecip = hourlyPrecip.optInt(i, 0)
                val hCode = hourlyCodes.getInt(i)
                val (hCond, _) = mapWeatherCodeToCondition(hCode)
                hourlyForecasts.add(HourlyForecast(displayTime, hTemp, hCond, hPrecip))
            }

            // 2. Fetch Live Air Quality (PM2.5, PM10, Ozone, Nitrogen Dioxide, AQI)
            var airQuality = AirQuality(
                aqi = 25,
                status = "GOOD",
                pm25 = 6.2f,
                pm10 = 12.0f,
                o3 = 30.0f,
                no2 = 14.5f
            )

            try {
                val aqiUrl = "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=$latitude&longitude=$longitude&current=european_aqi,pm10,pm2_5,ozone,nitrogen_dioxide"
                val aqiResponse = httpGet(aqiUrl)
                val aqiJson = JSONObject(aqiResponse)
                if (aqiJson.has("current")) {
                    val aqiCurrent = aqiJson.getJSONObject("current")
                    val rawAqi = aqiCurrent.optInt("european_aqi", 25).coerceAtLeast(10)
                    val pm25 = aqiCurrent.optDouble("pm2_5", 8.0).toFloat()
                    val pm10 = aqiCurrent.optDouble("pm10", 14.0).toFloat()
                    val o3 = aqiCurrent.optDouble("ozone", 28.0).toFloat()
                    val no2 = aqiCurrent.optDouble("nitrogen_dioxide", 16.0).toFloat()

                    val aqiStatus = when {
                        rawAqi <= 35 -> "GOOD"
                        rawAqi <= 70 -> "MODERATE"
                        else -> "POOR"
                    }
                    airQuality = AirQuality(rawAqi, aqiStatus, pm25, pm10, o3, no2)
                }
            } catch (e: Exception) {
                // Keep default calculated values if AQI endpoint has momentary lag
            }

            CityWeather(
                cityName = name.uppercase(),
                country = country.uppercase(),
                currentTemp = currentTemp,
                feelsLike = feelsLike,
                condition = condition,
                conditionDesc = conditionDesc,
                highTemp = todayHigh,
                lowTemp = todayLow,
                humidity = humidity,
                windSpeed = windSpeed,
                windDirection = windDirection,
                uvIndex = todayUv,
                pressureHpa = pressure,
                visibilityKm = 10,
                airQuality = airQuality,
                hourly = hourlyForecasts,
                daily = dailyForecasts,
                isCustomLocation = isCustom
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
