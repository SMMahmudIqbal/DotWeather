package com.example.dotweather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dotweather.theme.NothingRed
import com.example.dotweather.theme.NothingWhite
import com.example.dotweather.theme.NothingWhite10

// Author Attribution: Developed by S. M. Mahmud Iqbal

/**
 * Renders characters in an authentic 5x7 Nothing OS dot-matrix grid.
 * Extended with full alphanumeric and weather symbol support.
 */
object NDotFont {
    val GLYPHS = mapOf(
        '0' to arrayOf(
            " 111 ",
            "10001",
            "10011",
            "10101",
            "11001",
            "10001",
            " 111 "
        ),
        '1' to arrayOf(
            "  11 ",
            " 011 ",
            "  11 ",
            "  11 ",
            "  11 ",
            "  11 ",
            " 1111"
        ),
        '2' to arrayOf(
            " 111 ",
            "10001",
            "00001",
            "  110",
            " 1000",
            "10000",
            "11111"
        ),
        '3' to arrayOf(
            "1111 ",
            "00001",
            "00001",
            " 111 ",
            "00001",
            "00001",
            "1111 "
        ),
        '4' to arrayOf(
            "10001",
            "10001",
            "10001",
            "11111",
            "00001",
            "00001",
            "00001"
        ),
        '5' to arrayOf(
            "11111",
            "10000",
            "1111 ",
            "00001",
            "00001",
            "10001",
            " 111 "
        ),
        '6' to arrayOf(
            " 111 ",
            "10000",
            "10000",
            "1111 ",
            "10001",
            "10001",
            " 111 "
        ),
        '7' to arrayOf(
            "11111",
            "00001",
            "00010",
            "  100",
            " 0100",
            " 0100",
            " 0100"
        ),
        '8' to arrayOf(
            " 111 ",
            "10001",
            "10001",
            " 111 ",
            "10001",
            "10001",
            " 111 "
        ),
        '9' to arrayOf(
            " 111 ",
            "10001",
            "10001",
            " 1111",
            "00001",
            "00001",
            " 111 "
        ),
        '°' to arrayOf(
            " 11  ",
            "1001 ",
            " 11  ",
            "     ",
            "     ",
            "     ",
            "     "
        ),
        ':' to arrayOf(
            "     ",
            "  1  ",
            "  1  ",
            "     ",
            "  1  ",
            "  1  ",
            "     "
        ),
        ' ' to arrayOf(
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     "
        ),
        '-' to arrayOf(
            "     ",
            "     ",
            "     ",
            "1111 ",
            "     ",
            "     ",
            "     "
        ),
        '%' to arrayOf(
            "11001",
            "11010",
            "00100",
            "01000",
            "01011",
            "10011",
            "     "
        ),
        'C' to arrayOf(
            " 1111",
            "10000",
            "10000",
            "10000",
            "10000",
            "10000",
            " 1111"
        ),
        'F' to arrayOf(
            "11111",
            "10000",
            "10000",
            "1111 ",
            "10000",
            "10000",
            "10000"
        )
    )
}

@Composable
fun NDotText(
    text: String,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    activeColor: Color = NothingWhite,
    inactiveColor: Color = NothingWhite10,
    accentCharIndex: Int = -1,
    accentColor: Color = NothingRed
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val totalChars = text.length
            if (totalChars == 0) return@Canvas

            val rows = 7
            val cols = 5

            val maxAvailableWidth = size.width * 0.90f
            val spacingFactor = 0.20f
            val charWidthFromHeight = size.height * (5f / 7f)
            val charWidthFromWidth = maxAvailableWidth / (totalChars + (totalChars - 1) * spacingFactor)
            val charWidth = charWidthFromHeight.coerceAtMost(charWidthFromWidth)

            val spacing = charWidth * spacingFactor
            val totalContentWidth = totalChars * charWidth + (totalChars - 1) * spacing
            val startX = (size.width - totalContentWidth) / 2f

            val dotRadius = (charWidth / (cols * 2.3f)).coerceAtMost(size.height / (rows * 2.5f))
            val stepY = size.height / rows
            val stepX = charWidth / cols

            text.forEachIndexed { charIdx, ch ->
                val pattern = NDotFont.GLYPHS[ch] ?: NDotFont.GLYPHS[' ']!!
                val charOriginX = startX + charIdx * (charWidth + spacing)

                for (r in 0 until rows) {
                    val rowStr = pattern[r]
                    for (c in 0 until cols) {
                        val isActive = c < rowStr.length && rowStr[c] == '1'
                        val isAccent = (charIdx == accentCharIndex) && isActive

                        val color = when {
                            isAccent -> accentColor
                            isActive -> activeColor
                            else -> inactiveColor
                        }

                        val cx = charOriginX + c * stepX + stepX / 2f
                        val cy = r * stepY + stepY / 2f

                        drawCircle(
                            color = color,
                            radius = if (isActive) dotRadius else dotRadius * 0.40f,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }
    }
}
