package com.example.dotweather.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dotweather.data.CityStorage
import com.example.dotweather.data.CityWeather
import com.example.dotweather.data.SavedLocation
import com.example.dotweather.data.WeatherApiClient
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
import com.example.dotweather.ui.components.LocationSearchCard
import com.example.dotweather.ui.components.NDotText
import com.example.dotweather.ui.components.NothingCard
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.example.dotweather.ui.components.AirQualityDirectivesCard
import com.example.dotweather.ui.components.AtmosphereGlyphBar
import com.example.dotweather.ui.components.HourlyTrendGraph
import com.example.dotweather.ui.components.LunarEphemerisCard
import com.example.dotweather.widget.WeatherAirQualityWidgetProvider
import com.example.dotweather.widget.WeatherForecastWidgetProvider
import com.example.dotweather.widget.WeatherQuickWidgetProvider
import com.example.dotweather.widget.WidgetDataStorage
import com.example.dotweather.widget.WidgetUpdateManager

// Author Attribution: Developed by S. M. Mahmud Iqbal

@Composable
fun MainScreen(
    onItemClick: () -> Unit = {},
    initialTab: Int = -1,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val cityStorage = remember { CityStorage(context) }

    // Read the user's previously chosen active city & cached payload
    val initialActiveName = remember { cityStorage.getActiveCityName() }
    val initialActiveCountry = remember { cityStorage.getActiveCityCountry() }
    val cachedInitial = remember { cityStorage.getCachedCityWeather() }

    var citiesList by remember {
        mutableStateOf(
            if (cachedInitial != null) listOf(cachedInitial)
            else WeatherRepository.getInitialFallback(initialActiveName, initialActiveCountry)
        )
    }
    var selectedCityIndex by remember { mutableIntStateOf(0) }
    var selectedHourIndex by remember { mutableIntStateOf(0) }
    var isCelsius by remember { mutableStateOf(cityStorage.getUnitIsCelsius()) }
    var selectedTab by remember { mutableIntStateOf(if (initialTab in 0..2) initialTab else 0) } // 0: HOURLY, 1: AIR QUALITY, 2: 7-DAY
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<WeatherApiClient.GeoResult>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLocating by remember { mutableStateOf(false) }
    var networkErrorMsg by remember { mutableStateOf<String?>(null) }

    // Synchronize home screen widgets & offline cache with current telemetry
    fun syncWidgets(city: CityWeather, celsius: Boolean) {
        try {
            cityStorage.saveCachedCityWeather(city)
            WidgetDataStorage.saveLatestWeather(context, city, celsius)
            WidgetUpdateManager.updateAllWidgets(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Synchronize all saved cities to persistent local storage
    fun persistCities(list: List<CityWeather>) {
        val toSave = list.map {
            SavedLocation(
                name = it.cityName,
                country = it.country,
                lat = it.latitude,
                lon = it.longitude,
                isCustom = it.isCustomLocation,
                isGps = it.isGpsLocation
            )
        }
        cityStorage.saveLocations(toSave)
    }

    // Set selected city and permanently store it as the user's default active location
    fun selectCity(index: Int) {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        selectedCityIndex = index
        val city = citiesList.getOrNull(index) ?: return
        cityStorage.setActiveCity(city.cityName, city.country, city.latitude, city.longitude)
        persistCities(citiesList)
        syncWidgets(city, isCelsius)
    }

    // Refresh telemetry for current active city
    fun refreshCurrentCity(city: CityWeather) {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        coroutineScope.launch {
            isRefreshing = true
            networkErrorMsg = null
            val updated = WeatherApiClient.fetchLiveWeather(
                name = city.cityName,
                country = city.country,
                latitude = city.latitude,
                longitude = city.longitude,
                isCustom = city.isCustomLocation,
                isGps = city.isGpsLocation
            )
            if (updated != null) {
                citiesList = citiesList.mapIndexed { idx, item ->
                    if (idx == selectedCityIndex) updated else item
                }
                persistCities(citiesList)
                syncWidgets(updated, isCelsius)
            } else {
                networkErrorMsg = "FAILED TO REFRESH TELEMETRY"
            }
            isRefreshing = false
        }
    }

    // Auto-detect current device location and lock it as active default
    fun triggerAutoLocation() {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        coroutineScope.launch {
            isLocating = true
            networkErrorMsg = null
            val detected = WeatherApiClient.detectCurrentLocation()
            if (detected != null) {
                val liveWeather = WeatherApiClient.fetchLiveWeather(
                    name = detected.name,
                    country = detected.country,
                    latitude = detected.latitude,
                    longitude = detected.longitude,
                    isCustom = true,
                    isGps = true
                )
                if (liveWeather != null) {
                    val filtered = citiesList.filterNot { it.cityName == liveWeather.cityName && it.country == liveWeather.country }
                    citiesList = listOf(liveWeather) + filtered
                    selectedCityIndex = 0
                    cityStorage.setActiveCity(liveWeather.cityName, liveWeather.country, liveWeather.latitude, liveWeather.longitude)
                    persistCities(citiesList)
                    syncWidgets(liveWeather, isCelsius)
                    isSearchExpanded = false
                    searchQuery = ""
                    searchResults = emptyList()
                } else {
                    networkErrorMsg = "LOCATION LOCATED, BUT TELEMETRY FAILED"
                }
            } else {
                networkErrorMsg = "AUTO-LOCATION TIMEOUT. CHECK CONNECTION."
            }
            isLocating = false
        }
    }

    // Load saved cities and restore user's selected active city
    LaunchedEffect(Unit) {
        isRefreshing = true
        val saved = cityStorage.getSavedLocations()
        val activeName = cityStorage.getActiveCityName()

        val targets = mutableListOf<SavedLocation>()
        if (saved.isNotEmpty()) {
            targets.addAll(saved)
        } else {
            // If activeCityName was previously saved
            if (activeName != null) {
                val activeLat = cityStorage.getActiveCityLat()
                val activeLon = cityStorage.getActiveCityLon()
                if (!activeLat.isNaN() && !activeLon.isNaN()) {
                    targets.add(
                        SavedLocation(
                            name = activeName,
                            country = cityStorage.getActiveCityCountry(),
                            lat = activeLat,
                            lon = activeLon,
                            isCustom = true,
                            isGps = true
                        )
                    )
                }
            } else {
                // First time ever: auto-detect device location!
                val autoLoc = WeatherApiClient.detectCurrentLocation()
                if (autoLoc != null) {
                    targets.add(
                        SavedLocation(
                            name = autoLoc.name,
                            country = autoLoc.country,
                            lat = autoLoc.latitude,
                            lon = autoLoc.longitude,
                            isCustom = true,
                            isGps = true
                        )
                    )
                    cityStorage.setActiveCity(autoLoc.name, autoLoc.country, autoLoc.latitude, autoLoc.longitude)
                }
            }

            // Append other world cities as secondary choices
            for (initial in WeatherRepository.initialTargets) {
                if (targets.none { it.name.equals(initial.name, ignoreCase = true) }) {
                    targets.add(SavedLocation(initial.name, initial.country, initial.lat, initial.lon, isCustom = false, isGps = false))
                }
            }
        }

        // Fetch targets in parallel
        val deferredList = targets.map { target ->
            async {
                WeatherApiClient.fetchLiveWeather(
                    name = target.name,
                    country = target.country,
                    latitude = target.lat,
                    longitude = target.lon,
                    isCustom = target.isCustom,
                    isGps = target.isGps
                )
            }
        }
        val results = deferredList.awaitAll().filterNotNull()
        if (results.isNotEmpty()) {
            citiesList = results
            val targetActive = cityStorage.getActiveCityName()
            val matchIdx = if (targetActive != null) {
                results.indexOfFirst { it.cityName.equals(targetActive, ignoreCase = true) }
            } else 0
            selectedCityIndex = if (matchIdx >= 0) matchIdx else 0
            val current = results.getOrNull(selectedCityIndex) ?: results.firstOrNull()
            if (current != null) {
                syncWidgets(current, isCelsius)
            }
        }
        isRefreshing = false
    }

    val currentCity = citiesList.getOrElse(selectedCityIndex) { citiesList[0] }

    val displayTemp = if (isCelsius) currentCity.currentTemp else (currentCity.currentTemp * 9 / 5 + 32)
    val displayHigh = if (isCelsius) currentCity.highTemp else (currentCity.highTemp * 9 / 5 + 32)
    val displayLow = if (isCelsius) currentCity.lowTemp else (currentCity.lowTemp * 9 / 5 + 32)
    val displayFeelsLike = if (isCelsius) currentCity.feelsLike else (currentCity.feelsLike * 9 / 5 + 32)
    val unitSymbol = if (isCelsius) "°C" else "°F"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NothingBlack),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. NOTHING OS HEADER & CITY CONTROLS ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Header Title with iconic Nothing Red Dot
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(NothingRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DOTWEATHER",
                        color = NothingWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.2.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location Search Trigger Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, if (isSearchExpanded) NothingWhite else NothingCardBorder, RoundedCornerShape(20.dp))
                            .background(if (isSearchExpanded) NothingWhite else NothingCardBg)
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                isSearchExpanded = !isSearchExpanded
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = if (isSearchExpanded) "CLOSE" else "+ LOCATION",
                            color = if (isSearchExpanded) NothingBlack else NothingWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Symmetrical, Perfectly Even Nothing OS Dual Unit Switch (°C / °F)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, NothingCardBorder, RoundedCornerShape(20.dp))
                            .background(NothingCardBg)
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // °C button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isCelsius) NothingWhite else Color.Transparent)
                                .clickable {
                                    if (!isCelsius) {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isCelsius = true
                                        cityStorage.setUnitIsCelsius(true)
                                        syncWidgets(currentCity, true)
                                    }
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "°C",
                                color = if (isCelsius) NothingBlack else NothingWhite40,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        // °F button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (!isCelsius) NothingWhite else Color.Transparent)
                                .clickable {
                                    if (isCelsius) {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isCelsius = false
                                        cityStorage.setUnitIsCelsius(false)
                                        syncWidgets(currentCity, false)
                                    }
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp),
                            contentAlignment = Alignment.Center
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
        }

        // --- 1.1 ANIMATED SEARCH DRAWER ---
        item {
            AnimatedVisibility(
                visible = isSearchExpanded,
                enter = expandVertically(tween(220)) + fadeIn(tween(220)),
                exit = shrinkVertically(tween(220)) + fadeOut(tween(220))
            ) {
                LocationSearchCard(
                    searchQuery = searchQuery,
                    onQueryChange = { query ->
                        searchQuery = query
                        if (query.trim().length >= 2) {
                            coroutineScope.launch {
                                val results = WeatherApiClient.searchGeocodingMulti(query, count = 4)
                                searchResults = results
                            }
                        } else {
                            searchResults = emptyList()
                        }
                    },
                    onSearchSubmit = { query ->
                        val trimmed = query.trim()
                        if (trimmed.isNotEmpty()) {
                            coroutineScope.launch {
                                isRefreshing = true
                                networkErrorMsg = null
                                val geo = WeatherApiClient.searchGeocoding(trimmed)
                                if (geo != null) {
                                    val liveWeather = WeatherApiClient.fetchLiveWeather(
                                        name = geo.name,
                                        country = geo.country,
                                        latitude = geo.latitude,
                                        longitude = geo.longitude,
                                        isCustom = true
                                    )
                                    if (liveWeather != null) {
                                        citiesList = listOf(liveWeather) + citiesList.filterNot { it.cityName == liveWeather.cityName }
                                        selectedCityIndex = 0
                                        cityStorage.setActiveCity(liveWeather.cityName, liveWeather.country, liveWeather.latitude, liveWeather.longitude)
                                        persistCities(citiesList)
                                        searchQuery = ""
                                        searchResults = emptyList()
                                        isSearchExpanded = false
                                    } else {
                                        networkErrorMsg = "FAILED TO FETCH WEATHER TELEMETRY"
                                    }
                                } else {
                                    networkErrorMsg = "LOCATION NOT FOUND. CHECK SPELLING."
                                }
                                isRefreshing = false
                            }
                        }
                    },
                    onAutoDetectLocation = { triggerAutoLocation() },
                    isLocating = isLocating,
                    searchResults = searchResults,
                    onSelectResult = { selectedResult ->
                        coroutineScope.launch {
                            isRefreshing = true
                            networkErrorMsg = null
                            val liveWeather = WeatherApiClient.fetchLiveWeather(
                                name = selectedResult.name,
                                country = selectedResult.country,
                                latitude = selectedResult.latitude,
                                longitude = selectedResult.longitude,
                                isCustom = true
                            )
                            if (liveWeather != null) {
                                citiesList = listOf(liveWeather) + citiesList.filterNot { it.cityName == liveWeather.cityName }
                                selectedCityIndex = 0
                                cityStorage.setActiveCity(liveWeather.cityName, liveWeather.country, liveWeather.latitude, liveWeather.longitude)
                                persistCities(citiesList)
                                syncWidgets(liveWeather, isCelsius)
                                searchQuery = ""
                                searchResults = emptyList()
                                isSearchExpanded = false
                            }
                            isRefreshing = false
                        }
                    },
                    onClose = { isSearchExpanded = false }
                )
            }
        }

        // --- 1.2 LIVE NETWORK STATUS / REFRESH BANNER ---
        if (isRefreshing || networkErrorMsg != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NothingCardBg)
                        .border(1.dp, if (networkErrorMsg != null) NothingRed else NothingCardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = NothingWhite,
                                strokeWidth = 1.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "CONNECTING TO LIVE METEO SENSORS...",
                                color = NothingWhite70,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        } else if (networkErrorMsg != null) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(NothingRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = networkErrorMsg ?: "",
                                color = NothingRed,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
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
                citiesList.forEachIndexed { index, city ->
                    val isSelected = index == selectedCityIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) NothingWhite else NothingCardBg)
                            .border(1.dp, if (isSelected) NothingWhite else NothingCardBorder, RoundedCornerShape(24.dp))
                            .clickable { selectCity(index) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (city.isGpsLocation) {
                                Text(
                                    text = "📍 ",
                                    fontSize = 11.sp
                                )
                            } else if (city.isCustomLocation) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) NothingRed else NothingWhite40)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = "${city.cityName}, ${city.country}",
                                color = if (isSelected) NothingBlack else NothingWhite70,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                letterSpacing = 0.8.sp
                            )
                            if ((city.isCustomLocation || city.isGpsLocation) && citiesList.size > 1) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "×",
                                    color = if (isSelected) NothingBlack else NothingWhite40,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.clickable {
                                        val mutable = citiesList.toMutableList()
                                        mutable.removeAt(index)
                                        citiesList = mutable
                                        persistCities(citiesList)
                                        if (selectedCityIndex >= citiesList.size) {
                                            selectedCityIndex = (citiesList.size - 1).coerceAtLeast(0)
                                            val newActive = citiesList.getOrNull(selectedCityIndex)
                                            if (newActive != null) {
                                                cityStorage.setActiveCity(newActive.cityName, newActive.country, newActive.latitude, newActive.longitude)
                                                syncWidgets(newActive, isCelsius)
                                            }
                                        }
                                    }
                                )
                            }
                        }
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentCity.cityName,
                                    color = NothingWhite,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NothingCardInner)
                                        .border(1.dp, NothingCardBorder, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LIVE ${currentCity.lastUpdated}",
                                        color = NothingRed,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = currentCity.conditionDesc,
                                color = NothingWhite40,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Tap dot matrix weather icon to immediately re-sync live telemetry
                        Box(modifier = Modifier.clickable { refreshCurrentCity(currentCity) }) {
                            DotMatrixIcon(
                                condition = currentCity.condition,
                                size = 52.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated Dot Matrix Digital Temperature Display
                    AnimatedContent(
                        targetState = "$displayTemp°",
                        transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(180)) },
                        label = "temp_anim"
                    ) { targetText ->
                        NDotText(
                            text = targetText,
                            modifier = Modifier.fillMaxWidth(),
                            height = 58.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

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
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(NothingRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FEELS ${displayFeelsLike}$unitSymbol",
                                color = NothingWhite40,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 3.1 NOTHING GLYPH ATMOSPHERE PULSE BAR ---
        item {
            AtmosphereGlyphBar(
                pressureHpa = currentCity.pressureHpa,
                windSpeed = currentCity.windSpeed,
                condition = currentCity.condition
            )
        }

        // --- 4. TAB SELECTOR (HOURLY / AIR QUALITY / 7-DAY) ---
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
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedTab = idx
                            }
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
                // HOURLY INTERACTIVE TREND GRAPH
                item {
                    HourlyTrendGraph(
                        hourly = currentCity.hourly,
                        isCelsius = isCelsius,
                        selectedHourIndex = selectedHourIndex,
                        onSelectHour = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            selectedHourIndex = it
                        }
                    )
                }

                // HOURLY TIMELINE
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
                        currentCity.hourly.forEachIndexed { hIdx, hour ->
                            val isHourSelected = hIdx == selectedHourIndex
                            val hourTemp = if (isCelsius) hour.temp else (hour.temp * 9 / 5 + 32)
                            NothingCard(
                                modifier = Modifier
                                    .width(96.dp)
                                    .clickable {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        selectedHourIndex = hIdx
                                    },
                                borderColor = if (isHourSelected) NothingRed else NothingCardBorder,
                                cornerRadius = 20.dp
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = hour.time,
                                        color = if (isHourSelected) NothingRed else NothingWhite40,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isHourSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    DotMatrixIcon(condition = hour.condition, size = 32.dp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "$hourTemp°",
                                        color = NothingWhite,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (hour.precipChance > 0) {
                                        Text(
                                            text = "${hour.precipChance}%",
                                            color = NothingRed,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )
                                    } else {
                                        Text(
                                            text = "--",
                                            color = NothingWhite20,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ENVIRONMENTAL SENSORS (Expanded: Wind, Humidity, UV, Barometer, Visibility, Sunrise, Sunset)
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
                                Text("${currentCity.windSpeed} km/h", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                Text("DIR: ${currentCity.windDirection}", color = NothingWhite70, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            }
                        }
                        // HUMIDITY & DEW POINT CARD
                        NothingCard(
                            modifier = Modifier.weight(1f),
                            cornerRadius = 22.dp
                        ) {
                            Column {
                                Text("HUMIDITY", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${currentCity.humidity}%", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                val dewPoint = currentCity.currentTemp - ((100 - currentCity.humidity) / 5)
                                Text("DEW POINT ${dewPoint}°", color = NothingWhite70, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
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
                                Text("${currentCity.uvIndex}", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                Text(if (currentCity.uvIndex > 5) "HIGH EXPOSURE" else "LOW RISK", color = NothingWhite70, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            }
                        }
                        // BAROMETRIC PRESSURE CARD
                        NothingCard(
                            modifier = Modifier.weight(1f),
                            cornerRadius = 22.dp
                        ) {
                            Column {
                                Text("BAROMETER", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${currentCity.pressureHpa} hPa", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                val presDesc = when {
                                    currentCity.pressureHpa > 1020 -> "HIGH // DRY"
                                    currentCity.pressureHpa < 1005 -> "LOW // STORMY"
                                    else -> "NORMAL // STABLE"
                                }
                                Text(presDesc, color = NothingWhite70, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            }
                        }
                    }
                }

                // VISIBILITY CARD
                item {
                    NothingCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("VISIBILITY RANGE", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${currentCity.visibilityKm} KM", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Text(
                                text = if (currentCity.visibilityKm >= 10) "OPTIMAL CLARITY" else "HAZY / REDUCED",
                                color = NothingWhite70,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // ASTRONOMICAL SOLAR CYCLE & DAYLIGHT CARD
                item {
                    NothingCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("SOLAR CYCLE", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                if (currentCity.daylightDurationDesc.isNotEmpty()) {
                                    Text(currentCity.daylightDurationDesc, color = NothingRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("SUNRISE", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(currentCity.sunrise, color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                    Text("DAWN ORBIT", color = NothingWhite70, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                }

                                // Visual dot-line celestial separator
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(7) { dotIdx ->
                                        Box(
                                            modifier = Modifier
                                                .size(if (dotIdx == 3) 6.dp else 3.dp)
                                                .clip(CircleShape)
                                                .background(if (dotIdx == 3) NothingRed else NothingWhite20)
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("SUNSET", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(currentCity.sunset, color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                    Text("DUSK ORBIT", color = NothingWhite70, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // ASTRONOMICAL LUNAR EPHEMERIS
                item {
                    LunarEphemerisCard()
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
                                    color = if (currentCity.airQuality.aqi <= 35) NothingWhite else NothingRed,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            NDotText(
                                text = "${currentCity.airQuality.aqi}",
                                height = 50.dp,
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

                // ACTIONABLE HEALTH & ENVIRONMENTAL DIRECTIVES
                item {
                    AirQualityDirectivesCard(airQuality = currentCity.airQuality)
                }
            }

            2 -> {
                // 7-DAY FORECAST WITH MIN-MAX COMPARISON BARS
                item {
                    val weekMin = currentCity.daily.minOfOrNull { it.minTemp } ?: 10
                    val weekMax = currentCity.daily.maxOfOrNull { it.maxTemp } ?: 30
                    val tempSpan = (weekMax - weekMin).coerceAtLeast(1)

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
                                val maxTemp = if (isCelsius) forecast.maxTemp else (forecast.maxTemp * 9 / 5 + 32)
                                val minTemp = if (isCelsius) forecast.minTemp else (forecast.minTemp * 9 / 5 + 32)

                                val leftRatio = ((forecast.minTemp - weekMin).toFloat() / tempSpan).coerceIn(0f, 1f)
                                val barRatio = ((forecast.maxTemp - forecast.minTemp).toFloat() / tempSpan).coerceIn(0.1f, 1f)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = forecast.day,
                                        color = if (forecast.day == "TODAY") NothingWhite else NothingWhite70,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (forecast.day == "TODAY") FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.width(68.dp)
                                    )

                                    DotMatrixIcon(condition = forecast.condition, size = 26.dp)

                                    // Visual horizontal temperature bar
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$minTemp°",
                                            color = NothingWhite40,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            modifier = Modifier.width(28.dp)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(NothingWhite10)
                                        ) {
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                if (leftRatio > 0.05f) {
                                                    Spacer(modifier = Modifier.weight(leftRatio))
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .weight(barRatio)
                                                        .height(4.dp)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(if (forecast.day == "TODAY") NothingRed else NothingWhite70)
                                                )
                                                val rightRatio = (1f - leftRatio - barRatio).coerceAtLeast(0f)
                                                if (rightRatio > 0.05f) {
                                                    Spacer(modifier = Modifier.weight(rightRatio))
                                                }
                                            }
                                        }

                                        Text(
                                            text = "$maxTemp°",
                                            color = NothingWhite,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.width(32.dp).padding(start = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 5. NOTHING OS HOME SCREEN WIDGETS SECTION ---
        item {
            NothingWidgetsSection(
                currentCity = currentCity,
                isCelsius = isCelsius,
                onPinWidget = { providerClass ->
                    syncWidgets(currentCity, isCelsius)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val appWidgetManager = AppWidgetManager.getInstance(context)
                        if (appWidgetManager.isRequestPinAppWidgetSupported) {
                            val provider = ComponentName(context, providerClass)
                            val snapshot = WidgetDataStorage.loadLatestWeather(context)
                            val previewViews = when (providerClass) {
                                WeatherForecastWidgetProvider::class.java ->
                                    WeatherForecastWidgetProvider.buildRemoteViews(context, 0, snapshot)
                                WeatherAirQualityWidgetProvider::class.java ->
                                    WeatherAirQualityWidgetProvider.buildRemoteViews(context, 0, snapshot)
                                else ->
                                    WeatherQuickWidgetProvider.buildRemoteViews(context, 0, snapshot)
                            }
                            val extras = Bundle().apply {
                                putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, previewViews)
                            }
                            appWidgetManager.requestPinAppWidget(provider, extras, null)
                            Toast.makeText(context, "PINNING NOTHING WIDGET TO HOME SCREEN...", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "LAUNCHER PINNING NOT SUPPORTED. TOUCH & HOLD HOME SCREEN TO ADD.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "TOUCH & HOLD HOME SCREEN TO ADD WIDGET.", Toast.LENGTH_LONG).show()
                    }
                }
            )
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
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun NothingWidgetsSection(
    currentCity: CityWeather,
    isCelsius: Boolean,
    onPinWidget: (Class<*>) -> Unit
) {
    val displayTemp = if (isCelsius) currentCity.currentTemp else (currentCity.currentTemp * 9 / 5 + 32)
    val displayHigh = if (isCelsius) currentCity.highTemp else (currentCity.highTemp * 9 / 5 + 32)
    val displayLow = if (isCelsius) currentCity.lowTemp else (currentCity.lowTemp * 9 / 5 + 32)
    val unitSymbol = if (isCelsius) "°C" else "°F"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(NothingRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NOTHING WIDGETS",
                    color = NothingWhite,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = "// 3 HOMESCREEN STYLES",
                color = NothingWhite40,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                letterSpacing = 0.5.sp
            )
        }

        Text(
            text = "Authentic Nothing OS 3.0 widgets. Tap PIN to add directly or long-press your home screen.",
            color = NothingWhite40,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            lineHeight = 14.sp
        )

        // 1. WIDGET 1: WEATHER QUICK GLANCE (2x2)
        NothingCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NothingCardBg,
            borderColor = NothingCardBorder,
            cornerRadius = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "01 // QUICK GLANCE (2X2)",
                        color = NothingWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "SQUIRCLE",
                        color = NothingWhite40,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }

                // Interactive Mini Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, NothingCardBorder, RoundedCornerShape(16.dp))
                        .background(NothingBlack)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(NothingRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${currentCity.cityName} // ${currentCity.country}",
                                    color = NothingWhite,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "LIVE",
                                color = NothingWhite40,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DotMatrixIcon(
                                condition = currentCity.condition,
                                size = 46.dp
                            )
                            Text(
                                text = "$displayTemp$unitSymbol",
                                color = NothingWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentCity.conditionDesc.uppercase(),
                                color = NothingWhite70,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "H:$displayHigh° L:$displayLow°",
                                color = NothingWhite40,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // Pin Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, NothingWhite20, RoundedCornerShape(12.dp))
                        .background(NothingCardInner)
                        .clickable { onPinWidget(WeatherQuickWidgetProvider::class.java) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NothingRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ PIN QUICK GLANCE TO HOME SCREEN",
                            color = NothingWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // 2. WIDGET 2: HOURLY FORECAST STRIP (4x2)
        NothingCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NothingCardBg,
            borderColor = NothingCardBorder,
            cornerRadius = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "02 // FORECAST STRIP (4X2)",
                        color = NothingWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "TIMELINE",
                        color = NothingWhite40,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }

                // Mini Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, NothingCardBorder, RoundedCornerShape(16.dp))
                        .background(NothingBlack)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(NothingRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentCity.cityName,
                                    color = NothingWhite,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "// HOURLY STRIP",
                                    color = NothingWhite40,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp
                                )
                            }
                            Text(
                                text = "$displayTemp°",
                                color = NothingWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            currentCity.hourly.take(4).forEach { h ->
                                val hTemp = if (isCelsius) h.temp else (h.temp * 9 / 5 + 32)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = h.time,
                                        color = NothingWhite40,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp
                                    )
                                    DotMatrixIcon(
                                        condition = h.condition,
                                        size = 22.dp
                                    )
                                    Text(
                                        text = "$hTemp°",
                                        color = NothingWhite,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "${h.precipChance}%",
                                        color = NothingWhite40,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 7.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Pin Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, NothingWhite20, RoundedCornerShape(12.dp))
                        .background(NothingCardInner)
                        .clickable { onPinWidget(WeatherForecastWidgetProvider::class.java) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NothingRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ PIN FORECAST STRIP TO HOME SCREEN",
                            color = NothingWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // 3. WIDGET 3: AIR QUALITY & ENVIRONMENTAL HUB (2x2)
        NothingCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NothingCardBg,
            borderColor = NothingCardBorder,
            cornerRadius = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "03 // SENSOR & AQI HUB (2X2)",
                        color = NothingWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "ENVIRONMENT",
                        color = NothingWhite40,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }

                // Mini Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, NothingCardBorder, RoundedCornerShape(16.dp))
                        .background(NothingBlack)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(NothingRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AIR // AQI",
                                    color = NothingWhite,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = currentCity.airQuality.status.uppercase(),
                                color = NothingWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, NothingCardBorder, RoundedCornerShape(10.dp))
                                    .background(NothingCardInner)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentCity.airQuality.aqi.toString(),
                                color = NothingWhite,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "INDEX",
                                color = NothingWhite40,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("PM2.5", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 7.sp)
                                Text("${currentCity.airQuality.pm25} µg", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                            Column {
                                Text("WIND", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 7.sp)
                                Text("${currentCity.windSpeed} KM/H", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                            Column {
                                Text("HUMIDITY", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 7.sp)
                                Text("${currentCity.humidity}%", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                            Column {
                                Text("UV INDEX", color = NothingWhite40, fontFamily = FontFamily.Monospace, fontSize = 7.sp)
                                Text("${currentCity.uvIndex}", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }
                    }
                }

                // Pin Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, NothingWhite20, RoundedCornerShape(12.dp))
                        .background(NothingCardInner)
                        .clickable { onPinWidget(WeatherAirQualityWidgetProvider::class.java) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NothingRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ PIN SENSOR HUB TO HOME SCREEN",
                            color = NothingWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
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
