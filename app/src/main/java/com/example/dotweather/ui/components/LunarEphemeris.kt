package com.example.dotweather.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dotweather.theme.NothingCardBg
import com.example.dotweather.theme.NothingCardBorder
import com.example.dotweather.theme.NothingRed
import com.example.dotweather.theme.NothingWhite
import com.example.dotweather.theme.NothingWhite40
import com.example.dotweather.theme.NothingWhite70
import java.util.Date
import kotlin.math.cos

// Author Attribution: Developed by S. M. Mahmud Iqbal

data class MoonPhaseInfo(
    val name: String,
    val pattern: Array<IntArray>,
    val illumination: Int,
    val ageDays: String,
    val trajectory: String
)

object LunarCalculator {
    /**
     * Calculates astronomical moon phase based on synodic lunar cycle.
     */
    fun getMoonPhase(date: Date = Date()): MoonPhaseInfo {
        // Known reference new moon: January 11, 2024, 11:57 UTC (epoch ms: 1704974220000L)
        val refNewMoonMs = 1704974220000L
        val synodicMonthMs = 29.530588853 * 86400000.0

        val diffMs = (date.time - refNewMoonMs).toDouble()
        val cycles = diffMs / synodicMonthMs
        val currentAgeDays = ((cycles - cycles.toInt()) * 29.530588853).let { if (it < 0) it + 29.530588853 else it }

        val illuminationFraction = 0.5 * (1.0 - cos(2.0 * Math.PI * currentAgeDays / 29.530588853))
        val illuminationPercent = (illuminationFraction * 100).toInt().coerceIn(0, 100)

        val (name, pattern, trajectory) = when {
            currentAgeDays < 1.84 -> Triple("NEW MOON", DotMatrixPatterns.MOON_NEW, "CONJUNCTION")
            currentAgeDays < 7.38 -> Triple("WAXING CRESCENT", DotMatrixPatterns.MOON_CRESCENT, "WAXING // INCREASING")
            currentAgeDays < 9.23 -> Triple("FIRST QUARTER", DotMatrixPatterns.MOON_QUARTER, "HALF ILLUMINATION")
            currentAgeDays < 14.77 -> Triple("WAXING GIBBOUS", DotMatrixPatterns.MOON_GIBBOUS, "WAXING // APPROACHING FULL")
            currentAgeDays < 16.61 -> Triple("FULL MOON", DotMatrixPatterns.MOON_FULL, "MAXIMUM ILLUMINATION")
            currentAgeDays < 22.15 -> Triple("WANING GIBBOUS", DotMatrixPatterns.MOON_GIBBOUS, "WANING // DECREASING")
            currentAgeDays < 23.99 -> Triple("LAST QUARTER", DotMatrixPatterns.MOON_QUARTER, "HALF ILLUMINATION")
            else -> Triple("WANING CRESCENT", DotMatrixPatterns.MOON_CRESCENT, "WANING // APPROACHING NEW")
        }

        return MoonPhaseInfo(
            name = name,
            pattern = pattern,
            illumination = illuminationPercent,
            ageDays = String.format(java.util.Locale.ENGLISH, "%.1f", currentAgeDays),
            trajectory = trajectory
        )
    }
}

@Composable
fun LunarEphemerisCard(
    modifier: Modifier = Modifier
) {
    val moon = remember { LunarCalculator.getMoonPhase() }

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
                        text = "LUNAR EPHEMERIS",
                        color = NothingWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp
                    )
                }
                Text(
                    text = "// ORBIT TRACKER",
                    color = NothingWhite40,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DotMatrixPatternIcon(
                    pattern = moon.pattern,
                    size = 52.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = moon.name,
                        color = NothingWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${moon.illumination}% ILLUMINATED • DAY ${moon.ageDays} / 29.5",
                        color = NothingWhite70,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "TRAJECTORY: ${moon.trajectory}",
                        color = NothingWhite40,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}
