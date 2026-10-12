package com.example.dotweather.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import com.example.dotweather.theme.NothingCardBg
import com.example.dotweather.theme.NothingCardBorder
import com.example.dotweather.theme.NothingRed
import com.example.dotweather.theme.NothingWhite
import com.example.dotweather.theme.NothingWhite10
import com.example.dotweather.theme.NothingWhite40
import com.example.dotweather.theme.NothingWhite70

// Author Attribution: Developed by S. M. Mahmud Iqbal

@Composable
fun AtmosphereGlyphBar(
    pressureHpa: Int,
    windSpeed: Int,
    condition: WeatherCondition,
    modifier: Modifier = Modifier
) {
    val isStormy = condition == WeatherCondition.THUNDERSTORM || windSpeed > 35
    val isUnstable = condition == WeatherCondition.RAINY || pressureHpa < 1005

    // Infinite breathing LED pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "GlyphPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isStormy) 600 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val pressureStatus = when {
        pressureHpa < 1002 -> "FALLING // STORM RISK"
        pressureHpa < 1010 -> "LOW PRESSURE // UNSETTLED"
        pressureHpa in 1010..1018 -> "STEADY // ATMOSPHERIC STABILITY"
        else -> "RISING // DRY & CLEAR"
    }

    val activeBarCount = when {
        isStormy -> 10
        isUnstable -> 7
        windSpeed > 15 -> 5
        else -> 3
    }

    NothingCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = NothingCardBg,
        borderColor = NothingCardBorder,
        cornerRadius = 18.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
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
                            .background(if (isStormy) NothingRed else NothingWhite)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GLYPH ATMOSPHERE PULSE",
                        color = NothingWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = "$pressureHpa hPa",
                    color = NothingWhite70,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 10-Segment Glyph LED Light Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(10) { index ->
                    val isSegmentLit = index < activeBarCount
                    val segmentColor = when {
                        !isSegmentLit -> NothingWhite10
                        isStormy && index >= 7 -> NothingRed.copy(alpha = pulseAlpha)
                        else -> NothingWhite.copy(alpha = if (isSegmentLit) pulseAlpha else 0.15f)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(segmentColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pressureStatus,
                    color = if (isStormy) NothingRed else NothingWhite40,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )

                Text(
                    text = "WIND FLOW: ${windSpeed} KM/H",
                    color = NothingWhite70,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }
        }
    }
}
