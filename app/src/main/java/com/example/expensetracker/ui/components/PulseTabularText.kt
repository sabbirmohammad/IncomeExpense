package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

/**
 * PulseTabularText:
 * Guarantees vertical tabular digit column alignment for fonts (such as Hind Siliguri)
 * that lack OpenType "tnum" glyph tables.
 *
 * Each numeric character ('0'..'9' or '০'..'৯') is rendered in a fixed-pitch slot
 * matching the widest digit, ensuring that "1111", "8888", "১১১১", and "৮৮৮৮"
 * have exactly identical measured layout widths in amount columns.
 */
@Composable
fun PulseTabularText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Start
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    // Compute max digit cell width for ASCII and Bengali digits
    val digitSlotWidth: Dp = remember(style, density) {
        val allDigits = listOf(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯"
        )
        val maxWidthPx = allDigits.maxOf { d ->
            textMeasurer.measure(d, style).size.width
        }
        with(density) { maxWidthPx.toDp() }
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        text.forEach { ch ->
            if (ch.isDigit()) {
                Box(
                    modifier = Modifier.width(digitSlotWidth),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ch.toString(),
                        style = style,
                        color = color,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = ch.toString(),
                    style = style,
                    color = color,
                    textAlign = textAlign
                )
            }
        }
    }
}
