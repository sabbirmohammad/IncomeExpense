package com.example.expensetracker.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.example.expensetracker.theme.LocalAppColors

/**
 * Standard Pulse text component with guaranteed fallback to [appColors.textPrimary].
 * Prevents unintended invisible or low-contrast text in custom themes.
 */
@Composable
fun PulseText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalAppColors.current.textPrimary,
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
        fontWeight = fontWeight,
        fontSize = fontSize,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        lineHeight = lineHeight
    )
}
