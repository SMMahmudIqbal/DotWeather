package com.example.dotweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dotweather.data.WeatherApiClient
import com.example.dotweather.theme.NothingBlack
import com.example.dotweather.theme.NothingCardBg
import com.example.dotweather.theme.NothingCardBorder
import com.example.dotweather.theme.NothingRed
import com.example.dotweather.theme.NothingWhite
import com.example.dotweather.theme.NothingWhite10
import com.example.dotweather.theme.NothingWhite20
import com.example.dotweather.theme.NothingWhite40
import com.example.dotweather.theme.NothingWhite70

// Author Attribution: Developed by S. M. Mahmud Iqbal

/**
 * Nothing OS stylized location search bar, approximate IP-location trigger, and search results list.
 */
@Composable
fun LocationSearchCard(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onAutoDetectLocation: () -> Unit,
    isLocating: Boolean = false,
    searchResults: List<WeatherApiClient.GeoResult> = emptyList(),
    onSelectResult: (WeatherApiClient.GeoResult) -> Unit = {},
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val suggestedLocations = listOf(
        "DHAKA, BD", "LONDON, UK", "TOKYO, JP", "NEW YORK, US",
        "PARIS, FR", "DUBAI, AE", "BERLIN, DE", "SYDNEY, AU",
        "SINGAPORE", "TORONTO, CA", "SEOUL, KR", "MUMBAI, IN"
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    NothingCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 22.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                        text = "LOCATION SENSOR // QUERY",
                        color = NothingWhite70,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "[CLOSE]",
                    color = NothingWhite40,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable { onClose() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. AUTO-DETECT CURRENT LOCATION BUTTON
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NothingBlack)
                    .border(1.dp, NothingRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable(enabled = !isLocating) {
                        focusManager.clearFocus()
                        onAutoDetectLocation()
                    }
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(NothingRed)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "📍 AUTO-DETECT MY LOCATION",
                            color = NothingWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Approximate IP location · not GPS",
                            color = NothingWhite40,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }

                if (isLocating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = NothingWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "LOCATE",
                        color = NothingRed,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. TEXT SEARCH INPUT FIELD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NothingBlack)
                    .border(1.dp, NothingCardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ">",
                    color = NothingRed,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "SEARCH CITY (E.G. DUBAI, PARIS...)",
                            color = NothingWhite20,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(
                            color = NothingWhite,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(NothingRed),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.isNotBlank()) {
                                    focusManager.clearFocus()
                                    onSearchSubmit(searchQuery)
                                }
                            }
                        )
                    )
                }

                if (searchQuery.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(NothingWhite)
                            .clickable {
                                focusManager.clearFocus()
                                onSearchSubmit(searchQuery)
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "FIND",
                            color = NothingBlack,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 3. SEARCH RESULTS LIST (IF ANY)
            if (searchResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "SEARCH RESULTS (${searchResults.size})",
                    color = NothingWhite40,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NothingBlack)
                        .border(1.dp, NothingCardBorder, RoundedCornerShape(14.dp))
                ) {
                    searchResults.forEachIndexed { index, result ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    focusManager.clearFocus()
                                    onSelectResult(result)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${result.name}, ${result.country}",
                                    color = NothingWhite,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                if (result.adminRegion.isNotEmpty()) {
                                    Text(
                                        text = result.adminRegion,
                                        color = NothingWhite40,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NothingWhite10)
                                    .border(1.dp, NothingWhite20, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+ ADD",
                                    color = NothingWhite,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        if (index < searchResults.size - 1) {
                            HorizontalDivider(color = NothingWhite10)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. QUICK SUGGESTED REGIONS
            Text(
                text = "POPULAR LOCATIONS",
                color = NothingWhite40,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestedLocations) { location ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(NothingBlack)
                            .border(1.dp, NothingCardBorder, RoundedCornerShape(18.dp))
                            .clickable {
                                focusManager.clearFocus()
                                onSearchSubmit(location)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = location,
                            color = NothingWhite70,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
