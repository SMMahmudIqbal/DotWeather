package com.example.dotweather.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dotweather.data.CityWeather
import com.example.dotweather.data.WeatherRepository
import com.example.dotweather.theme.NothingBlack
import com.example.dotweather.theme.NothingCardBg
import com.example.dotweather.theme.NothingCardBorder
import com.example.dotweather.theme.NothingCardInner
import com.example.dotweather.theme.NothingRed
import com.example.dotweather.theme.NothingWhite
import com.example.dotweather.theme.NothingWhite10
import com.example.dotweather.theme.NothingWhite20
import com.example.dotweather.theme.NothingWhite40
import com.example.dotweather.theme.NothingWhite70
import com.example.dotweather.ui.components.DotMatrixIcon
import com.example.dotweather.ui.components.NDotText
import com.example.dotweather.ui.components.NothingCard

// Developed by S. M. Mahmud Iqbal

@Composable
fun MainScreen(
    onItemClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCityIndex by remember { mutableIntStateOf(0) }
    var isCelsius by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: OVERVIEW, 1: AIR QUALITY, 2: 7-DAY

    val currentCity = WeatherRepository.sampleCities[selectedCityIndex]

    val displayTemp = if (isCelsius) currentCity.currentTemp else (currentCity.currentTemp * 9 / 5 + 32)
    val displayHigh = if (isCelsius) currentCity.highTemp else (currentCity.highTemp * 9 / 5 + 32)
    val displayLow = if (isCelsius) currentCity.lowTemp else (currentCity.lowTemp * 9 / 5 + 32)
    val unitSymbol = if (isCelsius) "°C" else "°F"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NothingBlack)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. NOTHING OS HEADER & CITY SELECTOR ---
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Iconic Nothing Red Recording Dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NothingRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DOTWEATHER // AIR",
                            color = NothingWhite70,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Text(
                        text = "Developed by S. M. Mahmud Iqbal",
                        color = NothingWhite40,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                // C / F Pill Toggle
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, NothingCardBorder, RoundedCornerShape(20.dp))
                        .background(NothingCardBg)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isCelsius) NothingWhite else Color.Transparent)
                            .clickable { isCelsius = true }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "°C",
                            color = if (isCelsius) NothingBlack else NothingWhite40,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (!isCelsius) NothingWhite else Color.Transparent)
                            .clickable { isCelsius = false }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "°F",
                            color = if (!isCelsius) NothingBlack else NothingWhite40,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // --- 2. CITY CHIPS (NOTHING OS HORIZONTAL PILLS) ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeatherRepository.sampleCities.forEachIndexed { index, city ->
                    val isSelected = index == selectedCityIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) NothingWhite else NothingCardBg)
                            .border(1.dp, if (isSelected) NothingWhite else NothingCardBorder, RoundedCornerShape(24.dp))
                            .clickable { selectedCityIndex = index }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${city.cityName}, ${city.country}",
                            color = if (isSelected) NothingBlack else NothingWhite70,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }

        // --- 3. MAIN HERO WIDGET (NOTHING OS SIGNATURE CARD) ---
        item {
            NothingCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = currentCity.cityName,
                                color = NothingWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = currentCity.conditionDesc,
                                color = NothingWhite40,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        DotMatrixIcon(
                            condition = currentCity.condition,
                            size = 54.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dot Matrix Digital Temperature Display
                    NDotText(
                        text = "$displayTemp°",
                        modifier = Modifier.fillMaxWidth(),
                        height = 68.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "H: $displayHigh$unitSymbol  //  L: $displayLow$unitSymbol",
                            color = NothingWhite70,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(NothingRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FEELS $currentCity.feelsLike$unitSymbol",
                                color = NothingWhite40,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 4. TAB SELECTOR (OVERVIEW / AIR / 7-DAY) ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(NothingCardBg)
                    .border(1.dp, NothingCardBorder, RoundedCornerShape(18.dp))
                    .padding(4.dp)
            ) {
                listOf("HOURLY", "AIR QUALITY", "7-DAY").forEachIndexed { idx, title ->
                    val isTabSelected = selectedTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isTabSelected) NothingCardInner else Color.Transparent)
                            .clickable { selectedTab = idx }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isTabSelected) NothingWhite else NothingWhite40,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // --- 5. TAB CONTENT ---
        when (selectedTab) {
            0 -> {
                // HOURLY CAROUSEL
                item {
                    Text(
                        text = "HOURLY TIMELINE",
                        color = NothingWhite40,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        currentCity.hourly.forEach { hour ->
                            NothingCard(
                                modifier = Modifier.width(96.dp),
                                cornerRadius = 20.dp
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = hour.time,
                                        color = NothingWhite40,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    DotMatrixIcon(condition = hour.condition, size = 32.dp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "${hour.temp}°",
                                        color = NothingWhite,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    if (hour.precipChance > 0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${hour.precipChance}%",
                                            color = NothingRed,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ENVIRONMENTAL METRICS GRID
                item {
                    Text(
                        text = "ENVIRONMENTAL SENSORS",
                        color = NothingWhite40,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // WIND CARD
                        NothingCard(
                            modifier = Modifier.weight(1f),
                            cornerRadius = 22.dp
                        ) {
                            Column {
                                Text("WIND SPEED", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${currentCity.windSpeed} km/h", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("DIR: ${currentCity.windDirection}", color = NothingWhite70, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            }
                        }
                        // HUMIDITY CARD
                        NothingCard(
                            modifier = Modifier.weight(1f),
                            cornerRadius = 22.dp
                        ) {
                            Column {
                                Text("HUMIDITY", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${currentCity.humidity}%", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("DEW POINT 11°", color = NothingWhite70, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // UV INDEX CARD
                        NothingCard(
                            modifier = Modifier.weight(1f),
                            cornerRadius = 22.dp
                        ) {
                            Column {
                                Text("UV INDEX", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${currentCity.uvIndex}", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(if (currentCity.uvIndex > 5) "HIGH EXPOSURE" else "LOW RISK", color = NothingWhite70, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            }
                        }
                        // PRESSURE CARD
                        NothingCard(
                            modifier = Modifier.weight(1f),
                            cornerRadius = 22.dp
                        ) {
                            Column {
                                Text("BAROMETER", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${currentCity.pressureHpa} hPa", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("STEADY", color = NothingWhite70, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            1 -> {
                // AIR QUALITY MATRIX
                item {
                    NothingCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AIR QUALITY INDEX",
                                    color = NothingWhite70,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = currentCity.airQuality.status,
                                    color = if (currentCity.airQuality.aqi <= 50) NothingWhite else NothingRed,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            NDotText(
                                text = "${currentCity.airQuality.aqi}",
                                height = 52.dp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // AQI Meter Bar (Dotted Style)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(16) { barIdx ->
                                    val activeBars = (currentCity.airQuality.aqi / 15).coerceIn(1, 16)
                                    val isFilled = barIdx < activeBars
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(
                                                if (isFilled) {
                                                    if (barIdx > 12) NothingRed else NothingWhite
                                                } else NothingWhite10
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = NothingCardBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Pollutant breakdown
                            Text("POLLUTANT METRICS", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                PollutantStat(name = "PM2.5", value = "${currentCity.airQuality.pm25} µg/m³")
                                PollutantStat(name = "PM10", value = "${currentCity.airQuality.pm10} µg/m³")
                                PollutantStat(name = "O3", value = "${currentCity.airQuality.o3} µg/m³")
                                PollutantStat(name = "NO2", value = "${currentCity.airQuality.no2} µg/m³")
                            }
                        }
                    }
                }
            }

            2 -> {
                // 7-DAY FORECAST
                item {
                    NothingCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "7-DAY OUTLOOK",
                                color = NothingWhite70,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            currentCity.daily.forEach { forecast ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = forecast.day,
                                        color = NothingWhite,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.width(80.dp)
                                    )
                                    DotMatrixIcon(condition = forecast.condition, size = 28.dp)
                                    Row(
                                        modifier = Modifier.width(100.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = "${forecast.maxTemp}°",
                                            color = NothingWhite,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "${forecast.minTemp}°",
                                            color = NothingWhite40,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 6. DEVELOPER ATTRIBUTION FOOTER ---
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Developed by S. M. Mahmud Iqbal",
                        color = NothingWhite70,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NOTHING OS DESIGN LANGUAGE // EDITION 3.0",
                        color = NothingWhite20,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PollutantStat(name: String, value: String) {
    Column {
        Text(name, color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
