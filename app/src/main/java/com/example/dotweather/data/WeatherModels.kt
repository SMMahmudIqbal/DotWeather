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
    val defaultFallbackCities = listOf(
        CityWeather(
            cityName = "LONDON",
            country = "UK",
            currentTemp = 16,
            feelsLike = 15,
            condition = WeatherCondition.PARTLY_CLOUDY,
            conditionDesc = "LIVE SYNCHRONIZING...",
            highTemp = 19,
            lowTemp = 12,
            humidity = 70,
            windSpeed = 15,
            windDirection = "NW",
            uvIndex = 3,
            pressureHpa = 1015,
            visibilityKm = 10,
            sunrise = "06:45",
            sunset = "18:20",
            airQuality = AirQuality(25, "GOOD", 6.5f, 11.2f, 28.0f, 15.0f),
            hourly = listOf(
                HourlyForecast("NOW", 16, WeatherCondition.PARTLY_CLOUDY, 10),
                HourlyForecast("+1H", 17, WeatherCondition.PARTLY_CLOUDY, 10),
                HourlyForecast("+2H", 18, WeatherCondition.SUNNY, 5),
                HourlyForecast("+3H", 18, WeatherCondition.SUNNY, 0)
            ),
            daily = listOf(
                DailyForecast("TODAY", 12, 19, WeatherCondition.PARTLY_CLOUDY),
                DailyForecast("TOMORROW", 11, 18, WeatherCondition.RAINY)
            ),
            latitude = 51.5074,
            longitude = -0.1278,
            lastUpdated = "JUST NOW"
        )
    )
}
