package com.example.dotweather.data

import com.example.dotweather.ui.components.WeatherCondition

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
    val sunrise: String = "--:--",
    val sunset: String = "--:--",
    val airQuality: AirQuality,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val isCustomLocation: Boolean = false,
    val isGpsLocation: Boolean = false,
    val daylightDurationDesc: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val lastUpdated: String = "LIVE"
)

data class InitialCityTarget(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double
)

object WeatherRepository {
    // Primary worldwide anchor locations
    val initialTargets = listOf(
        InitialCityTarget("LONDON", "UK", 51.5074, -0.1278),
        InitialCityTarget("TOKYO", "JP", 35.6762, 139.6503),
        InitialCityTarget("NEW YORK", "US", 40.7128, -74.0060),
        InitialCityTarget("PARIS", "FR", 48.8566, 2.3522),
        InitialCityTarget("DUBAI", "AE", 25.2048, 55.2708),
        InitialCityTarget("BERLIN", "DE", 52.5200, 13.4050)
    )

    // Immediate offline-safe defaults that get dynamically updated by live API data
    fun getInitialFallback(activeCityName: String? = null, activeCountry: String? = null): List<CityWeather> {
        val name = activeCityName ?: "DHAKA"
        val country = if (!activeCountry.isNullOrBlank()) activeCountry else "BD"
        return listOf(
            CityWeather(
                cityName = name.uppercase(),
                country = country.uppercase(),
                currentTemp = 32,
                feelsLike = 35,
                condition = WeatherCondition.SUNNY,
                conditionDesc = "LIVE SYNCHRONIZING...",
                highTemp = 34,
                lowTemp = 25,
                humidity = 65,
                windSpeed = 8,
                windDirection = "N",
                uvIndex = 6,
                pressureHpa = 1012,
                visibilityKm = 10,
                sunrise = "05:50",
                sunset = "17:35",
                daylightDurationDesc = "11H 45M DAYLIGHT",
                airQuality = AirQuality(40, "GOOD", 9.2f, 16.5f, 26.0f, 13.0f),
                hourly = listOf(
                    HourlyForecast("NOW", 32, WeatherCondition.SUNNY, 0),
                    HourlyForecast("+1H", 32, WeatherCondition.SUNNY, 0),
                    HourlyForecast("+2H", 31, WeatherCondition.PARTLY_CLOUDY, 5),
                    HourlyForecast("+3H", 29, WeatherCondition.PARTLY_CLOUDY, 5)
                ),
                daily = listOf(
                    DailyForecast("TODAY", 25, 34, WeatherCondition.SUNNY),
                    DailyForecast("TOMORROW", 25, 33, WeatherCondition.PARTLY_CLOUDY)
                ),
                isCustomLocation = true,
                isGpsLocation = true,
                lastUpdated = "JUST NOW"
            )
        )
    }

    val defaultFallbackCities = getInitialFallback()
}
