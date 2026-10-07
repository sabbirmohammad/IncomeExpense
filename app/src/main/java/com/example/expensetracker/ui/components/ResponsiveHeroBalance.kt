package com.example.expensetracker.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorTextPrimary

/**
 * Responsive Hero Balance:
 * Adapts typography dynamically to available card width and number length.
 * Enforces single-line presentation (maxLines = 1, softWrap = false) with non-breaking space
 * between currency symbol and number so line wrapping can NEVER occur.
 * Currency symbol matches the amount's font size and weight.
 * Turns red (ColorDangerExpense) when amount is negative.
 */
@Composable
fun ResponsiveHeroBalance(
    amount: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled,
    maxFontSize: TextUnit = 50.sp,
    minFontSize: TextUnit = 22.sp,
    isPrivacyMode: Boolean = false
) {
    val animatedAmount by animateFloatAsState(
        targetValue = amount.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "heroAmountAnimation"
    )

    val formattedNumber = remember(animatedAmount, amount, useBengaliNumerals, isPrivacyMode) {
        if (isPrivacyMode) {
            "• • • • • •"
        } else {
            val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).apply {
                minimumFractionDigits = 0
                maximumFractionDigits = 2
            }
            val formatted = formatter.format(animatedAmount.toDouble())
            if (useBengaliNumerals) convertToBengaliDigits(formatted) else formatted
        }
    }

    val isNegative = amount < 0 && !isPrivacyMode
    val balanceColor = if (isNegative) ColorDangerExpense else ColorTextPrimary

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val availableWidthDp = maxWidth.value
        val totalChars = formattedNumber.length + 1.8f

        // Initial responsive calculation: ensure (totalChars * charWidth) <= availableWidthDp
        val calculatedSp = (availableWidthDp / (totalChars * 0.58f))
        val initialSize = calculatedSp.coerceIn(minFontSize.value, maxFontSize.value).sp

        var currentFontSize by remember(amount, availableWidthDp) {
            mutableStateOf(initialSize)
        }
        var readyToDraw by remember(amount, availableWidthDp) {
            mutableStateOf(true)
        }

        val annotatedBalance = remember(formattedNumber, amount, currencySymbol, currentFontSize, balanceColor) {
            buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontSize = currentFontSize,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.0).sp,
                        color = balanceColor
                    )
                ) {
                    append(currencySymbol)
                }
                append("\u00A0") // Non-breaking space
                withStyle(
                    SpanStyle(
                        fontSize = currentFontSize,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.2).sp,
                        color = balanceColor
                    )
                ) {
                    append(formattedNumber)
                }
            }
        }

        Text(
            text = annotatedBalance,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    if (readyToDraw) drawContent()
                },
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.didOverflowWidth && currentFontSize.value > minFontSize.value) {
                    readyToDraw = false
                    currentFontSize = (currentFontSize.value - 2f).coerceAtLeast(minFontSize.value).sp
                } else {
                    readyToDraw = true
                }
            }
        )
    }
}
