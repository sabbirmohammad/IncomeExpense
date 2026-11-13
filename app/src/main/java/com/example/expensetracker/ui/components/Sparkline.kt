package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion

/**
 * Reusable smooth monotone cubic Bézier Sparkline for KPI and trend visualizations.
 */
@Composable
fun Sparkline(
    points: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color? = null,
    width: Dp = 64.dp,
    height: Dp = 28.dp,
    showGradientFill: Boolean = true,
    showEndDot: Boolean = true
) {
    val appColors = LocalAppColors.current
    val motion = LocalMotion.current
    val effectiveColor = lineColor ?: appColors.accent

    if (points.isEmpty()) return

    val animProgress = remember { Animatable(if (motion.reduceMotion) 1f else 0f) }
    LaunchedEffect(points) {
        if (!motion.reduceMotion) {
            animProgress.snapTo(0f)
            animProgress.animateTo(1f, animationSpec = tween(motion.durationStandard))
        } else {
            animProgress.snapTo(1f)
        }
    }

    val minVal = points.minOrNull() ?: 0.0
    val maxVal = points.maxOrNull() ?: 1.0
    val span = if (maxVal > minVal) (maxVal - minVal) else 1.0

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .semantics { contentDescription = "Trend sparkline with ${points.size} data points" }
    ) {
        val w = size.width
        val h = size.height
        val padX = 2f
        val padY = 3f
        val availableW = (w - padX * 2).coerceAtLeast(1f)
        val availableH = (h - padY * 2).coerceAtLeast(1f)

        val coords = points.mapIndexed { idx, p ->
            val x = padX + (idx.toFloat() / (points.size - 1).coerceAtLeast(1)) * availableW
            val normalizedY = ((p - minVal) / span).toFloat().coerceIn(0f, 1f)
            val y = padY + (1f - normalizedY * animProgress.value) * availableH
            Offset(x, y)
        }

        if (coords.size == 1) {
            drawCircle(effectiveColor, radius = 3f, center = coords[0])
            return@Canvas
        }

        val strokePath = Path().apply {
            moveTo(coords[0].x, coords[0].y)
            for (i in 0 until coords.size - 1) {
                val p0 = coords[i]
                val p1 = coords[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
        }

        if (showGradientFill) {
            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(coords.last().x, h)
                lineTo(coords.first().x, h)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        effectiveColor.copy(alpha = 0.28f * animProgress.value),
                        effectiveColor.copy(alpha = 0.0f)
                    ),
                    startY = padY,
                    endY = h
                )
            )
        }

        drawPath(
            path = strokePath,
            color = effectiveColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        if (showEndDot && coords.isNotEmpty()) {
            drawCircle(
                color = effectiveColor,
                radius = 3.dp.toPx(),
                center = coords.last()
            )
        }
    }
}
