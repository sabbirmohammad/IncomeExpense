package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * TabularRun:
 * Represents either a contiguous sequence of digits (which receive fixed-slot tabular layout)
 * or a contiguous sequence of non-digit characters (rendered as a single unbroken string
 * so complex script shaping, e.g. Bengali vowels/hasanta/ligatures, remains completely intact).
 */
sealed interface TabularRun {
    val content: String
    data class Digits(override val content: String) : TabularRun
    data class NonDigits(override val content: String) : TabularRun
}

/**
 * Segments [text] into contiguous runs of digits and non-digits.
 *
 * Guaranteed invariants:
 * 1. Non-digit sequences (Bengali consonants, vowel signs like 'ে', 'ি', hasanta, conjuncts, spaces, punctuation)
 *    are NEVER split character-by-character, keeping grapheme clusters and HarfBuzz font shaping intact.
 * 2. Digits ('0'..'9' and '০'..'৯') are grouped into contiguous digit runs.
 */
fun segmentIntoTabularRuns(text: String): List<TabularRun> {
    if (text.isEmpty()) return emptyList()
    val runs = mutableListOf<TabularRun>()
    var currentIsDigit = text[0].isDigit()
    val currentBuffer = StringBuilder()

    for (ch in text) {
        val isDigit = ch.isDigit()
        if (isDigit == currentIsDigit) {
            currentBuffer.append(ch)
        } else {
            val s = currentBuffer.toString()
            if (currentIsDigit) runs.add(TabularRun.Digits(s)) else runs.add(TabularRun.NonDigits(s))
            currentBuffer.clear()
            currentBuffer.append(ch)
            currentIsDigit = isDigit
        }
    }
    if (currentBuffer.isNotEmpty()) {
        val s = currentBuffer.toString()
        if (currentIsDigit) runs.add(TabularRun.Digits(s)) else runs.add(TabularRun.NonDigits(s))
    }
    return runs
}

/**
 * PulseTabularText:
 * Guarantees vertical tabular digit column alignment for fonts (such as Hind Siliguri)
 * that lack OpenType "tnum" glyph tables.
 *
 * Only numeric characters ('0'..'9' or '০'..'৯') are rendered in fixed-pitch slots
 * matching the widest digit. Non-digit runs (currency symbols, punctuation, text)
 * are rendered as unbroken Text spans to preserve script shaping.
 */
@Composable
fun PulseTabularText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = 1,
    softWrap: Boolean = false
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

    val runs = remember(text) { segmentIntoTabularRuns(text) }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        runs.forEach { run ->
            when (run) {
                is TabularRun.Digits -> {
                    run.content.forEach { digitChar ->
                        Box(
                            modifier = Modifier.width(digitSlotWidth),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digitChar.toString(),
                                style = style,
                                color = color,
                                textAlign = TextAlign.Center,
                                maxLines = maxLines,
                                softWrap = softWrap
                            )
                        }
                    }
                }
                is TabularRun.NonDigits -> {
                    Text(
                        text = run.content,
                        style = style,
                        color = color,
                        textAlign = textAlign,
                        maxLines = maxLines,
                        softWrap = softWrap
                    )
                }
            }
        }
    }
}

@Preview(name = "Tabular Runs Bengali Shaping Preview", showBackground = true)
@Composable
private fun PreviewPulseTabularTextBengaliRuns() {
    Surface(modifier = Modifier.padding(16.dp)) {
        Column {
            PulseTabularText(text = "২ কেজি", style = TextStyle(fontSize = 16.sp))
            Spacer(modifier = Modifier.height(8.dp))
            PulseTabularText(text = "মোট খরচ: ৳১৪৫", style = TextStyle(fontSize = 16.sp))
            Spacer(modifier = Modifier.height(8.dp))
            PulseTabularText(text = "১/১", style = TextStyle(fontSize = 16.sp))
            Spacer(modifier = Modifier.height(8.dp))
            PulseTabularText(text = "(০/০)", style = TextStyle(fontSize = 16.sp))
            Spacer(modifier = Modifier.height(8.dp))
            PulseTabularText(text = "৳১,২৩৪.৫০", style = TextStyle(fontSize = 16.sp))
        }
    }
}
