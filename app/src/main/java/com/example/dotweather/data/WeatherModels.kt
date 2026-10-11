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
    val airQuality: AirQuality,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>
)

object WeatherRepository {
    val sampleCities = listOf(
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
        )
    )
}
