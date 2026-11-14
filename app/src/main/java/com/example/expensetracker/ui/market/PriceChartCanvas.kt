package com.example.expensetracker.ui.market

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun PriceChartCanvas(
    points: List<Double>,
    strokeColor: Color,
    selectedIndex: Int?,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    val minVal = points.minOrNull() ?: 0.0
    val maxVal = points.maxOrNull() ?: 1.0
    val isFlat = (maxVal - minVal) < 0.001
    val span = if (isFlat) 1.0 else (maxVal - minVal)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(points) {
                    detectTapGestures { offset ->
                        val width = size.width
                        val padX = 8.dp.toPx()
                        val usableWidth = (width - (padX * 2)).coerceAtLeast(1f)
                        if (points.size <= 1) {
                            onSelectIndex(0)
                        } else {
                            val clampedX = (offset.x - padX).coerceIn(0f, usableWidth)
                            val fraction = clampedX / usableWidth
                            val nearestIdx = (fraction * (points.size - 1)).toInt().coerceIn(0, points.size - 1)
                            onSelectIndex(nearestIdx)
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val padX = 8.dp.toPx()
            val padY = 12.dp.toPx()
            val usableWidth = (width - (padX * 2)).coerceAtLeast(1f)
            val usableHeight = (height - (padY * 2)).coerceAtLeast(1f)

            val coords = points.mapIndexed { idx, p ->
                val x = if (points.size == 1) width / 2f
                else padX + (idx.toFloat() / (points.size - 1)) * usableWidth
                val y = if (isFlat) height / 2f
                else height - padY - (((p - minVal) / span) * usableHeight).toFloat()
                Offset(x, y)
            }

            if (coords.size >= 2) {
                val strokePath = Path().apply {
                    moveTo(coords[0].x, coords[0].y)
                    for (i in 0 until coords.size - 1) {
                        val p0 = coords[i]
                        val p1 = coords[i + 1]
                        val midX = (p0.x + p1.x) / 2f
                        cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
                    }
                }

                // Gradient fill under curve
                val fillPath = Path().apply {
                    addPath(strokePath)
                    lineTo(coords.last().x, height)
                    lineTo(coords.first().x, height)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(strokeColor.copy(alpha = 0.22f), strokeColor.copy(alpha = 0.01f)),
                        startY = padY,
                        endY = height
                    )
                )

                // Path stroke
                drawPath(
                    path = strokePath,
                    color = strokeColor,
                    style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Draw point dots
            coords.forEachIndexed { i, pt ->
                val isSelected = selectedIndex == i
                val radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx()
                drawCircle(
                    color = if (isSelected) appColors.accent else strokeColor,
                    radius = radius,
                    center = pt
                )
                if (isSelected) {
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = pt
                    )
                }
            }
        }
    }
}
