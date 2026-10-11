package com.example.dotweather.data

import com.example.dotweather.ui.components.WeatherCondition
import kotlin.math.abs

// Author Attribution: Developed by S. M. Mahmud Iqbal

data class HourlyForecast(
    val time: String,
    val temp: Int,
    val condition: WeatherCondition,
    val precipChance: Int
)

data class DailyForecast(
    val day: String,
    val minTemp: Int,
    val maxTemp: Int,
    val condition: WeatherCondition
)

data class AirQuality(
    val aqi: Int,
    val status: String,
    val pm25: Float,
    val pm10: Float,
    val o3: Float,
    val no2: Float
)

data class CityWeather(
    val cityName: String,
    val country: String,
    val currentTemp: Int,
    val feelsLike: Int,
    val condition: WeatherCondition,
    val conditionDesc: String,
    val highTemp: Int,
    val lowTemp: Int,
    val humidity: Int,
    val windSpeed: Int,
    val windDirection: String,
    val uvIndex: Int,
    val pressureHpa: Int,
    val visibilityKm: Int,
    val airQuality: AirQuality,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val isCustomLocation: Boolean = false
)

object WeatherRepository {
    val predefinedCities = listOf(
        CityWeather(
            cityName = "LONDON",
            country = "UK",
            currentTemp = 18,
            feelsLike = 17,
            condition = WeatherCondition.PARTLY_CLOUDY,
            conditionDesc = "PARTLY CLOUDY // BREEZY",
            highTemp = 21,
            lowTemp = 14,
            humidity = 68,
            windSpeed = 16,
            windDirection = "NW",
            uvIndex = 4,
            pressureHpa = 1014,
            visibilityKm = 10,
            airQuality = AirQuality(
                aqi = 28,
                status = "GOOD",
                pm25 = 7.4f,
                pm10 = 12.1f,
                o3 = 34.0f,
                no2 = 18.2f
            ),
            hourly = listOf(
                HourlyForecast("NOW", 18, WeatherCondition.PARTLY_CLOUDY, 10),
                HourlyForecast("10:00", 19, WeatherCondition.PARTLY_CLOUDY, 10),
                HourlyForecast("11:00", 20, WeatherCondition.SUNNY, 5),
                HourlyForecast("12:00", 21, WeatherCondition.SUNNY, 0),
                HourlyForecast("13:00", 21, WeatherCondition.CLOUDY, 20),
                HourlyForecast("14:00", 20, WeatherCondition.RAINY, 65),
                HourlyForecast("15:00", 19, WeatherCondition.RAINY, 80),
                HourlyForecast("16:00", 18, WeatherCondition.PARTLY_CLOUDY, 30),
                HourlyForecast("17:00", 17, WeatherCondition.SUNNY, 10),
                HourlyForecast("18:00", 16, WeatherCondition.SUNNY, 0)
            ),
            daily = listOf(
                DailyForecast("TODAY", 14, 21, WeatherCondition.PARTLY_CLOUDY),
                DailyForecast("MON", 13, 20, WeatherCondition.RAINY),
                DailyForecast("TUE", 12, 19, WeatherCondition.THUNDERSTORM),
                DailyForecast("WED", 14, 22, WeatherCondition.SUNNY),
                DailyForecast("THU", 15, 23, WeatherCondition.SUNNY),
                DailyForecast("FRI", 13, 20, WeatherCondition.WINDY),
                DailyForecast("SAT", 12, 18, WeatherCondition.PARTLY_CLOUDY)
            )
        ),
        CityWeather(
            cityName = "TOKYO",
            country = "JP",
            currentTemp = 24,
            feelsLike = 25,
            condition = WeatherCondition.RAINY,
            conditionDesc = "LIGHT DRIZZLE // HUMID",
            highTemp = 26,
            lowTemp = 20,
            humidity = 82,
            windSpeed = 12,
            windDirection = "E",
            uvIndex = 2,
            pressureHpa = 1008,
            visibilityKm = 8,
            airQuality = AirQuality(
                aqi = 35,
                status = "GOOD",
                pm25 = 9.2f,
                pm10 = 15.0f,
                o3 = 28.5f,
                no2 = 22.1f
            ),
            hourly = listOf(
                HourlyForecast("NOW", 24, WeatherCondition.RAINY, 75),
                HourlyForecast("18:00", 24, WeatherCondition.RAINY, 80),
                HourlyForecast("19:00", 23, WeatherCondition.THUNDERSTORM, 90),
                HourlyForecast("20:00", 22, WeatherCondition.CLOUDY, 40),
                HourlyForecast("21:00", 21, WeatherCondition.CLOUDY, 20),
                HourlyForecast("22:00", 21, WeatherCondition.PARTLY_CLOUDY, 10)
            ),
            daily = listOf(
                DailyForecast("TODAY", 20, 26, WeatherCondition.RAINY),
                DailyForecast("TOMORROW", 19, 27, WeatherCondition.SUNNY),
                DailyForecast("TUE", 21, 28, WeatherCondition.SUNNY),
                DailyForecast("WED", 18, 24, WeatherCondition.CLOUDY),
                DailyForecast("THU", 17, 23, WeatherCondition.RAINY)
            )
        ),
        CityWeather(
            cityName = "NEW YORK",
            country = "US",
            currentTemp = 15,
            feelsLike = 13,
            condition = WeatherCondition.WINDY,
            conditionDesc = "CLEAR // CRISP GUSTS",
            highTemp = 18,
            lowTemp = 9,
            humidity = 45,
            windSpeed = 28,
            windDirection = "WNW",
            uvIndex = 5,
            pressureHpa = 1022,
            visibilityKm = 16,
            airQuality = AirQuality(
                aqi = 42,
                status = "MODERATE",
                pm25 = 11.5f,
                pm10 = 22.0f,
                o3 = 42.1f,
                no2 = 25.8f
            ),
            hourly = listOf(
                HourlyForecast("NOW", 15, WeatherCondition.WINDY, 0),
                HourlyForecast("07:00", 14, WeatherCondition.SUNNY, 0),
                HourlyForecast("08:00", 15, WeatherCondition.SUNNY, 0),
                HourlyForecast("09:00", 16, WeatherCondition.SUNNY, 0),
                HourlyForecast("10:00", 17, WeatherCondition.SUNNY, 0),
                HourlyForecast("11:00", 18, WeatherCondition.PARTLY_CLOUDY, 5)
            ),
            daily = listOf(
                DailyForecast("TODAY", 9, 18, WeatherCondition.WINDY),
                DailyForecast("TOMORROW", 10, 19, WeatherCondition.SUNNY),
                DailyForecast("TUE", 11, 20, WeatherCondition.SUNNY),
                DailyForecast("WED", 12, 17, WeatherCondition.RAINY)
            )
        ),
        CityWeather(
            cityName = "PARIS",
            country = "FR",
            currentTemp = 16,
            feelsLike = 15,
            condition = WeatherCondition.CLOUDY,
            conditionDesc = "OVERCAST // SCATTERED FOG",
            highTemp = 19,
            lowTemp = 11,
            humidity = 72,
            windSpeed = 14,
            windDirection = "W",
            uvIndex = 3,
            pressureHpa = 1016,
            visibilityKm = 9,
            airQuality = AirQuality(
                aqi = 31,
                status = "GOOD",
                pm25 = 8.1f,
                pm10 = 13.5f,
                o3 = 30.2f,
                no2 = 19.4f
            ),
            hourly = listOf(
                HourlyForecast("NOW", 16, WeatherCondition.CLOUDY, 15),
                HourlyForecast("12:00", 17, WeatherCondition.CLOUDY, 20),
                HourlyForecast("13:00", 18, WeatherCondition.PARTLY_CLOUDY, 10),
                HourlyForecast("14:00", 19, WeatherCondition.SUNNY, 5),
                HourlyForecast("15:00", 18, WeatherCondition.PARTLY_CLOUDY, 10),
                HourlyForecast("16:00", 17, WeatherCondition.CLOUDY, 15)
            ),
            daily = listOf(
                DailyForecast("TODAY", 11, 19, WeatherCondition.CLOUDY),
                DailyForecast("MON", 12, 20, WeatherCondition.SUNNY),
                DailyForecast("TUE", 13, 21, WeatherCondition.SUNNY),
                DailyForecast("WED", 11, 18, WeatherCondition.RAINY)
            )
        ),
        CityWeather(
            cityName = "DUBAI",
            country = "AE",
            currentTemp = 36,
            feelsLike = 39,
            condition = WeatherCondition.SUNNY,
            conditionDesc = "SUNNY // DRY HEAT",
            highTemp = 39,
            lowTemp = 28,
            humidity = 38,
            windSpeed = 19,
            windDirection = "SE",
            uvIndex = 9,
            pressureHpa = 1006,
            visibilityKm = 10,
            airQuality = AirQuality(
                aqi = 68,
                status = "MODERATE",
                pm25 = 18.2f,
                pm10 = 42.0f,
                o3 = 45.0f,
                no2 = 29.1f
            ),
            hourly = listOf(
                HourlyForecast("NOW", 36, WeatherCondition.SUNNY, 0),
                HourlyForecast("14:00", 38, WeatherCondition.SUNNY, 0),
                HourlyForecast("15:00", 39, WeatherCondition.SUNNY, 0),
                HourlyForecast("16:00", 37, WeatherCondition.SUNNY, 0),
                HourlyForecast("17:00", 35, WeatherCondition.SUNNY, 0),
                HourlyForecast("18:00", 33, WeatherCondition.SUNNY, 0)
            ),
            daily = listOf(
                DailyForecast("TODAY", 28, 39, WeatherCondition.SUNNY),
                DailyForecast("MON", 29, 40, WeatherCondition.SUNNY),
                DailyForecast("TUE", 28, 39, WeatherCondition.SUNNY),
                DailyForecast("WED", 27, 38, WeatherCondition.WINDY)
            )
        ),
        CityWeather(
            cityName = "BERLIN",
            country = "DE",
            currentTemp = 13,
            feelsLike = 11,
            condition = WeatherCondition.THUNDERSTORM,
            conditionDesc = "STORM CELLS // SQUALLS",
            highTemp = 16,
            lowTemp = 8,
            humidity = 79,
            windSpeed = 32,
            windDirection = "SW",
            uvIndex = 2,
            pressureHpa = 1002,
            visibilityKm = 6,
            airQuality = AirQuality(
                aqi = 22,
                status = "GOOD",
                pm25 = 5.6f,
                pm10 = 10.4f,
                o3 = 26.0f,
                no2 = 14.8f
            ),
            hourly = listOf(
                HourlyForecast("NOW", 13, WeatherCondition.THUNDERSTORM, 85),
                HourlyForecast("15:00", 14, WeatherCondition.THUNDERSTORM, 90),
                HourlyForecast("16:00", 13, WeatherCondition.RAINY, 70),
                HourlyForecast("17:00", 12, WeatherCondition.CLOUDY, 30),
                HourlyForecast("18:00", 11, WeatherCondition.PARTLY_CLOUDY, 10)
            ),
            daily = listOf(
                DailyForecast("TODAY", 8, 16, WeatherCondition.THUNDERSTORM),
                DailyForecast("MON", 9, 17, WeatherCondition.RAINY),
                DailyForecast("TUE", 10, 18, WeatherCondition.CLOUDY),
                DailyForecast("WED", 11, 19, WeatherCondition.SUNNY)
            )
        )
    )

    /**
     * Synthesizes consistent meteorological telemetry for any custom city name or location searched by the user.
     * Uses deterministic hashing so the exact same city search always yields coherent realistic data.
     */
    fun createWeatherForLocation(locationInput: String): CityWeather {
        val trimmed = locationInput.trim().uppercase()
        val parts = trimmed.split(',', '-', '/').map { it.trim() }
        val cityName = parts[0]
        val country = if (parts.size > 1 && parts[1].isNotEmpty()) parts[1] else "LOCAL"

        val seed = abs(trimmed.hashCode())
        val conditions = WeatherCondition.values()
        val condition = conditions[seed % conditions.size]

        val baseTemp = (seed % 32) + 5 // Range 5°C to 36°C
        val currentTemp = when (condition) {
            WeatherCondition.SNOW -> (seed % 5) - 3
            WeatherCondition.SUNNY -> (seed % 15) + 22
            WeatherCondition.THUNDERSTORM -> (seed % 10) + 15
            else -> baseTemp
        }
        val highTemp = currentTemp + (seed % 6) + 2
        val lowTemp = currentTemp - (seed % 6) - 2
        val feelsLike = currentTemp + if (seed % 2 == 0) -1 else 1

        val conditionDesc = when (condition) {
            WeatherCondition.SUNNY -> "CLEAR HORIZON // RADIANT"
            WeatherCondition.PARTLY_CLOUDY -> "SCATTERED CLOUDS // LIGHT BREEZE"
            WeatherCondition.CLOUDY -> "OVERCAST CEILING // CALM"
            WeatherCondition.RAINY -> "PRECIPITATION // DAMP AIR"
            WeatherCondition.THUNDERSTORM -> "ELECTRIC CELLS // GUSTS"
            WeatherCondition.WINDY -> "TURBULENT WINDS // FRESH"
            WeatherCondition.SNOW -> "FROST / FLURRIES // CRISP"
        }

        val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val windDir = directions[seed % directions.size]
        val windSpeed = (seed % 28) + 8
        val humidity = (seed % 50) + 40
        val aqi = (seed % 75) + 15
        val aqiStatus = when {
            aqi <= 50 -> "GOOD"
            aqi <= 100 -> "MODERATE"
            else -> "UNHEALTHY"
        }

        val days = listOf("TODAY", "TOMORROW", "WED", "THU", "FRI", "SAT", "SUN")
        val daily = days.mapIndexed { idx, day ->
            val dayCond = conditions[(seed + idx) % conditions.size]
            val max = highTemp + ((seed + idx) % 4) - 2
            val min = lowTemp + ((seed + idx) % 4) - 2
            DailyForecast(day, min, max, dayCond)
        }

        val hourlyTimes = listOf("NOW", "+1H", "+2H", "+3H", "+4H", "+5H", "+6H")
        val hourly = hourlyTimes.mapIndexed { idx, time ->
            val hourCond = conditions[(seed + idx * 2) % conditions.size]
            val hourTemp = currentTemp + if (idx % 2 == 0) 1 else -1
            val rainChance = if (hourCond == WeatherCondition.RAINY || hourCond == WeatherCondition.THUNDERSTORM) (seed % 40) + 55 else 0
            HourlyForecast(time, hourTemp, hourCond, rainChance)
        }

        return CityWeather(
            cityName = cityName,
            country = country,
            currentTemp = currentTemp,
            feelsLike = feelsLike,
            condition = condition,
            conditionDesc = conditionDesc,
            highTemp = highTemp,
            lowTemp = lowTemp,
            humidity = humidity,
            windSpeed = windSpeed,
            windDirection = windDir,
            uvIndex = (seed % 8) + 1,
            pressureHpa = 1005 + (seed % 20),
            visibilityKm = 10 + (seed % 10),
            airQuality = AirQuality(
                aqi = aqi,
                status = aqiStatus,
                pm25 = (aqi * 0.28f),
                pm10 = (aqi * 0.45f),
                o3 = 25f + (seed % 20),
                no2 = 15f + (seed % 15)
            ),
            hourly = hourly,
            daily = daily,
            isCustomLocation = true
        )
    }
}
