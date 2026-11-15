package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.StyleDisplayAmount
import com.example.expensetracker.theme.TabularNumFeature

/**
 * AmountDisplay:
 * Living Ledger 2.0 Hero Amount Display:
 * - Large displayAmount text (40sp, bold, tabular numbers)
 * - Auto-shrinks (down to 28sp) for longer numbers to prevent clipping
 * - Live math expression line shown above result when operators are present
 * - Currency symbol (৳) prefix
 * - Privacy mode: does NOT mask while editing (user is actively typing),
 *   masks with "••••••" when not editing and privacy mode is active
 * - Full TalkBack semantic announcements
 */
@Composable
fun AmountDisplay(
    expression: String,
    modifier: Modifier = Modifier,
    currencySymbol: String = "৳",
    isBangla: Boolean = false,
    isEditing: Boolean = true,
    isPrivacyMode: Boolean = false
) {
    val appColors = LocalAppColors.current

    val hasOperators = remember(expression) { AmountKeypadLogic.hasOperators(expression) }
    val evaluatedValue = remember(expression) { AmountKeypadLogic.evaluate(expression) }

    // Evaluated or formatted amount display string
    val amountToDisplay = remember(expression, evaluatedValue, isEditing, isBangla, isPrivacyMode) {
        if (!isEditing && isPrivacyMode) {
            "••••••"
        } else if (expression.isBlank()) {
            if (isBangla) "০" else "0"
        } else if (hasOperators && evaluatedValue != null) {
            AmountKeypadLogic.formatEvaluated(evaluatedValue, isBangla)
        } else {
            if (isBangla) convertToBengaliDigits(expression) else expression
        }
    }

    // Dynamic auto-shrinking text size
    val dynamicFontSize = remember(amountToDisplay) {
        when {
            amountToDisplay.length > 12 -> 28.sp
            amountToDisplay.length > 8 -> 32.sp
            amountToDisplay.length > 6 -> 36.sp
            else -> 40.sp
        }
    }

    val liveExpressionDisplay = remember(expression, isBangla) {
        if (isBangla) convertToBengaliDigits(expression) else expression
    }

    val semanticsDesc = if (!isEditing && isPrivacyMode) {
        if (isBangla) "টাকার পরিমাণ গোপন করা হয়েছে" else "Amount hidden"
    } else {
        "$currencySymbol $amountToDisplay"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = semanticsDesc
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Live expression line (shown above main amount when operators are present)
        AnimatedVisibility(
            visible = hasOperators && expression.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = liveExpressionDisplay,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontFeatureSettings = TabularNumFeature
                ),
                color = appColors.textMuted,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        // Hero evaluated amount
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$currencySymbol ",
                style = StyleDisplayAmount.copy(
                    fontSize = dynamicFontSize * 0.85f,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.textSecondary
            )
            Text(
                text = amountToDisplay,
                style = StyleDisplayAmount.copy(
                    fontSize = dynamicFontSize,
                    lineHeight = dynamicFontSize * 1.15f
                ),
                color = appColors.textPrimary
            )
        }
    }
}

// =========================================================================
// PREVIEWS IN ALL 4 THEMES (EN + BN)
// =========================================================================

@Preview(name = "AmountDisplay - Dark En", showBackground = true)
@Composable
private fun AmountDisplayDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AmountDisplay(
                expression = "1500+250",
                currencySymbol = "৳",
                isBangla = false
            )
        }
    }
}

@Preview(name = "AmountDisplay - Light Bn", showBackground = true)
@Composable
private fun AmountDisplayLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AmountDisplay(
                expression = "2500",
                currencySymbol = "৳",
                isBangla = true
            )
        }
    }
}

@Preview(name = "AmountDisplay - AMOLED Privacy Mode", showBackground = true)
@Composable
private fun AmountDisplayAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AmountDisplay(
                expression = "9500",
                currencySymbol = "৳",
                isEditing = false,
                isPrivacyMode = true
            )
        }
    }
}

@Preview(name = "AmountDisplay - System Long Number", showBackground = true)
@Composable
private fun AmountDisplaySystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AmountDisplay(
                expression = "12500000",
                currencySymbol = "৳",
                isBangla = true
            )
        }
    }
}
