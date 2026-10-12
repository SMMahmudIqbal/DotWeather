package com.example.dotweather.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dotweather.data.HourlyForecast
import com.example.dotweather.theme.NothingCardBg
import com.example.dotweather.theme.NothingCardBorder
import com.example.dotweather.theme.NothingCardInner
import com.example.dotweather.theme.NothingRed
import com.example.dotweather.theme.NothingWhite
import com.example.dotweather.theme.NothingWhite10
import com.example.dotweather.theme.NothingWhite20
import com.example.dotweather.theme.NothingWhite40
import com.example.dotweather.theme.NothingWhite70

// Author Attribution: Developed by S. M. Mahmud Iqbal

@Composable
fun HourlyTrendGraph(
    hourly: List<HourlyForecast>,
    isCelsius: Boolean,
    selectedHourIndex: Int,
    onSelectHour: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (hourly.isEmpty()) return

    val minTemp = hourly.minOfOrNull { it.temp } ?: 0
    val maxTemp = hourly.maxOfOrNull { it.temp } ?: 30
    val tempRange = (maxTemp - minTemp).coerceAtLeast(1)

    NothingCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = NothingCardBg,
        borderColor = NothingCardBorder,
        cornerRadius = 22.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
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
                        text = "TEMPERATURE & PRECIPITATION CURVE",
                        color = NothingWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = "NEXT 14H",
                    color = NothingWhite40,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Bar Graph Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                hourly.forEachIndexed { index, item ->
                    val isSelected = index == selectedHourIndex
                    val itemTemp = if (isCelsius) item.temp else (item.temp * 9 / 5 + 32)
                    val heightRatio = ((item.temp - minTemp).toFloat() / tempRange).coerceIn(0.15f, 1.0f)

                    val barColor by animateColorAsState(
                        targetValue = when {
                            isSelected -> NothingRed
                            index == 0 -> NothingWhite
                            else -> NothingWhite70
                        },
                        animationSpec = tween(250),
                        label = "BarColor"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(36.dp)
                            .clickable { onSelectHour(index) }
                    ) {
                        // Temp Label
                        Text(
                            text = "$itemTemp°",
                            color = if (isSelected) NothingRed else NothingWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Visual Step Bar
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height((52 * heightRatio).dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(barColor)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Precipitation Dot
                        if (item.precipChance > 0) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(if (item.precipChance >= 30) NothingRed else NothingWhite40)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Time
                        Text(
                            text = item.time,
                            color = if (isSelected) NothingWhite else NothingWhite40,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp
                        )
                    }
                }
            }

            // Selected Hour Telemetry Detail Chip
            val activeItem = hourly.getOrNull(selectedHourIndex) ?: hourly[0]
            val activeTemp = if (isCelsius) activeItem.temp else (activeItem.temp * 9 / 5 + 32)
            val unitStr = if (isCelsius) "°C" else "°F"

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, NothingWhite10, RoundedCornerShape(12.dp))
                    .background(NothingCardInner)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DotMatrixIcon(
                            condition = activeItem.condition,
                            size = 22.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${activeItem.time}: $activeTemp$unitStr",
                            color = NothingWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "RAIN PROBABILITY: ${activeItem.precipChance}%",
                        color = if (activeItem.precipChance > 20) NothingRed else NothingWhite70,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
