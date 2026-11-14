package com.example.expensetracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalReduceMotion
import com.example.expensetracker.theme.TabularNumFeature

/**
 * ProgressRing:
 * Pulse Design System Circular Progress Ring:
 * - Animated sweep angle (respects reduce motion)
 * - Track color from surfaceVariant or container tint
 * - Progress color supports semantic thresholds
 * - Optional centered content (text/percentage)
 */
@Composable
fun ProgressRing(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    color: Color = LocalAppColors.current.accent,
    trackColor: Color = LocalAppColors.current.surfaceVariant,
    strokeWidth: Dp = 6.dp,
    size: Dp = 48.dp,
    centerContent: (@Composable () -> Unit)? = null
) {
    val reduceMotion = LocalReduceMotion.current
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (reduceMotion) tween(0) else tween(durationMillis = 400),
        label = "ProgressRingSweep"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            // Track
            drawCircle(
                color = trackColor,
                radius = (this.size.minDimension - stroke) / 2,
                style = Stroke(width = stroke)
            )
            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        centerContent?.invoke()
    }
}

@Preview(name = "ProgressRing - Dark")
@Composable
private fun ProgressRingDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        ProgressRing(
            progress = 0.65f,
            color = LocalAppColors.current.income
        ) {
            Text(
                text = "65%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LocalAppColors.current.textPrimary
            )
        }
    }
}
