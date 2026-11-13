package com.example.expensetracker.ui.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.theme.WeightSemiBold
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Interactive Price Chart with tap tooltip.
 */
@Composable
fun InteractivePriceChartCard(
    records: List<ProductPriceRecord>,
    commodity: MarketCommodityUiModel,
    currencySymbol: String,
    isBangla: Boolean
) {
    val appColors = LocalAppColors.current

    // Chronological points (oldest to newest)
    val chronological = remember(records) { records.sortedBy { it.timestamp } }
    val points = remember(chronological, commodity.recentPricePoints) {
        if (chronological.isNotEmpty()) {
            chronological.map { it.unitPrice }
        } else if (commodity.recentPricePoints.isNotEmpty()) {
            commodity.recentPricePoints
        } else {
            listOf(commodity.currentPrice, commodity.currentPrice)
        }
    }

    var selectedIndex by remember(points) { mutableStateOf<Int?>(null) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "মূল্যের গতিপ্রকৃতি" else "Price Trajectory",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = WeightSemiBold,
                        fontSize = 13.5.sp
                    ),
                    color = appColors.textPrimary
                )

                Text(
                    text = if (isBangla) "বিন্দু স্পর্শ করুন" else "Tap a point for details",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp
                    ),
                    color = appColors.textSecondary.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tooltip preview when a point is tapped
            AnimatedVisibility(
                visible = selectedIndex != null && selectedIndex in points.indices,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val idx = selectedIndex ?: 0
                val record = chronological.getOrNull(idx)
                val rate = points.getOrNull(idx) ?: commodity.currentPrice
                val rateFormatted = if (rate % 1.0 == 0.0) rate.toInt().toString() else "%.1f".format(rate)
                val rateText = if (isBangla) convertToBengaliDigits(rateFormatted) else rateFormatted
                val dateText = if (record != null) DateTimeHelper.formatDayGroup(record.timestamp, isBangla) else ""
                val qtyText = if (record != null) {
                    val q = if (record.rawQuantity % 1.0 == 0.0) record.rawQuantity.toInt().toString() else "%.1f".format(record.rawQuantity)
                    val qNum = if (isBangla) convertToBengaliDigits(q) else q
                    "$qNum ${record.rawUnit}"
                } else ""

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.accent.copy(alpha = 0.10f),
                    border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            if (dateText.isNotBlank()) {
                                Text(
                                    text = dateText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = WeightMedium
                                    ),
                                    color = appColors.textPrimary
                                )
                            }
                            if (qtyText.isNotBlank()) {
                                Text(
                                    text = qtyText,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                    color = appColors.textSecondary
                                )
                            }
                        }

                        Text(
                            text = "$currencySymbol$rateText/${commodity.defaultUnit}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = WeightBold,
                                fontSize = 13.5.sp,
                                fontFeatureSettings = TabularNumFeature
                            ),
                            color = appColors.accent
                        )
                    }
                }
            }

            // Interactive Canvas Line Chart
            val strokeColor = when (commodity.priceTrend) {
                PriceTrend.UP -> appColors.expense
                PriceTrend.DOWN -> appColors.income
                PriceTrend.STABLE -> appColors.accent
            }

            val minVal = points.minOrNull() ?: 0.0
            val maxVal = points.maxOrNull() ?: 1.0
            val isFlat = (maxVal - minVal) < 0.001
            val span = if (isFlat) 1.0 else (maxVal - minVal)

            Box(
                modifier = Modifier
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
                                    selectedIndex = 0
                                } else {
                                    val clampedX = (offset.x - padX).coerceIn(0f, usableWidth)
                                    val fraction = clampedX / usableWidth
                                    val nearestIdx = (fraction * (points.size - 1)).toInt().coerceIn(0, points.size - 1)
                                    selectedIndex = nearestIdx
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
    }
}

/**
 * Metric card for Min / Avg / Max 3-column stats row.
 */
