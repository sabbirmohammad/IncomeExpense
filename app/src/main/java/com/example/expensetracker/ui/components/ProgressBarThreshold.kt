package com.example.expensetracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalReduceMotion

/**
 * ProgressBarThreshold:
 * Pulse Design System Linear Progress Bar with Semantic Threshold Colors:
 * - < 75%: Emerald (income / safe)
 * - 75% - 99%: Amber (warning)
 * - >= 100%: Red / Coral (over-budget)
 * - Height: 8dp default, fully rounded pill ends
 */
@Composable
fun ProgressBarThreshold(
    progress: Float, // 0f..1f (or > 1f for over-budget)
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    trackColor: Color = LocalAppColors.current.surfaceVariant
) {
    val appColors = LocalAppColors.current
    val reduceMotion = LocalReduceMotion.current

    val thresholdColor = when {
        progress >= 1f -> appColors.expense
        progress >= 0.75f -> appColors.warning
        else -> appColors.income
    }

    val animatedColor by animateColorAsState(
        targetValue = thresholdColor,
        animationSpec = if (reduceMotion) tween(0) else tween(300),
        label = "ProgressBarColor"
    )

    val animatedFraction by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (reduceMotion) tween(0) else tween(400),
        label = "ProgressBarFraction"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedFraction)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(animatedColor)
        )
    }
}

@Preview(name = "ProgressBarThreshold - Safe <75%")
@Composable
private fun ProgressBarThresholdSafePreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        ProgressBarThreshold(progress = 0.5f)
    }
}

@Preview(name = "ProgressBarThreshold - Warning 85%")
@Composable
private fun ProgressBarThresholdWarningPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        ProgressBarThreshold(progress = 0.85f)
    }
}

@Preview(name = "ProgressBarThreshold - Overbudget 110%")
@Composable
private fun ProgressBarThresholdOverbudgetPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        ProgressBarThreshold(progress = 1.1f)
    }
}
