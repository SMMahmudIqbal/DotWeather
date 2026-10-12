package com.example.dotweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dotweather.data.AirQuality
import com.example.dotweather.theme.NothingCardBg
import com.example.dotweather.theme.NothingCardBorder
import com.example.dotweather.theme.NothingCardInner
import com.example.dotweather.theme.NothingRed
import com.example.dotweather.theme.NothingWhite
import com.example.dotweather.theme.NothingWhite10
import com.example.dotweather.theme.NothingWhite40
import com.example.dotweather.theme.NothingWhite70

// Author Attribution: Developed by S. M. Mahmud Iqbal

@Composable
fun AirQualityDirectivesCard(
    airQuality: AirQuality,
    modifier: Modifier = Modifier
) {
    val aqi = airQuality.aqi
    val isGood = aqi <= 35
    val isModerate = aqi in 36..70
    val isUnhealthy = aqi > 70

    val workoutAdvice = when {
        isGood -> "EXCELLENT // SAFE FOR OUTDOOR RUNS & CARDIO"
        isModerate -> "ACCEPTABLE // SENSITIVE GROUPS REDUCE INTENSITY"
        else -> "ALERT // LIMIT PROLONGED OUTDOOR EXERTION"
    }

    val ventilationAdvice = when {
        isGood -> "RECOMMENDED // OPEN WINDOWS FOR FRESH AIR"
        isModerate -> "PARTIAL // VENTILATE EARLY MORNING OR NIGHT"
        else -> "RESTRICTED // KEEP WINDOWS CLOSED & SEALED"
    }

    val maskAdvice = when {
        isGood -> "NOT REQUIRED // CLEAN PARTICULATE PROFILE"
        isModerate -> "OPTIONAL FOR ALLERGY & ASTHMA PRONE"
        else -> "RECOMMENDED // WEAR N95 / FFP2 RESPIRATOR"
    }

    NothingCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = NothingCardBg,
        borderColor = NothingCardBorder,
        cornerRadius = 22.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            .background(if (isUnhealthy) NothingRed else NothingWhite)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ENVIRONMENTAL HEALTH DIRECTIVES",
                        color = NothingWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = "// WHO STANDARDS",
                    color = NothingWhite40,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }

            // Directives Grid
            DirectiveItem(
                label = "OUTDOOR ACTIVITY",
                status = workoutAdvice,
                isWarning = isUnhealthy
            )

            DirectiveItem(
                label = "NATURAL VENTILATION",
                status = ventilationAdvice,
                isWarning = isUnhealthy
            )

            DirectiveItem(
                label = "RESPIRATORY PROTECTION",
                status = maskAdvice,
                isWarning = isUnhealthy
            )
        }
    }
}

@Composable
private fun DirectiveItem(
    label: String,
    status: String,
    isWarning: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, NothingWhite10, RoundedCornerShape(12.dp))
            .background(NothingCardInner)
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = NothingWhite40,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (isWarning) NothingRed else NothingWhite70)
                )
            }

            Text(
                text = status,
                color = if (isWarning) NothingRed else NothingWhite,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp
            )
        }
    }
}
