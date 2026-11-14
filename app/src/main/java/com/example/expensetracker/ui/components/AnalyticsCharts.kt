package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion

/**
 * Donut chart per V7 spec:
 * 160dp diameter, category colors strictly from design system.
 * Center label ("Total spent" / category name) + hero value with tabular numbers (`tnum`).
 * Tap-to-highlight segment on donut or legend with animated emphasis.
 * Accessibility content description summarizing chart categories.
 * Respects reduce-motion for animations.
 */
@Composable
fun DonutChart(
    categorySpends: List<CategorySpend>,
    currencySymbol: String,
    totalAmount: Double,
    centerLabel: String = "Total spent",
    onCategoryClick: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val motion = LocalMotion.current
    val chartDiameter = 160.dp
    val baseStrokeWidthDp = 22.dp

    var selectedIndex by remember(categorySpends) { mutableStateOf<Int?>(null) }

    val accessibilitySummary = remember(categorySpends, totalAmount) {
        val topThree = categorySpends.take(3).joinToString { "${it.categoryName} ${(it.percentage * 100).toInt()}%" }
        "Donut chart. Total: ${totalAmount}. Top: $topThree"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = accessibilitySummary },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(chartDiameter),
            contentAlignment = Alignment.Center
        ) {
            if (categorySpends.isEmpty() || totalAmount <= 0.0) {
                val emptyBorderColor = appColors.border
                Canvas(modifier = Modifier.size(chartDiameter - 10.dp)) {
                    val stroke = baseStrokeWidthDp.toPx()
                    val radius = (size.minDimension - stroke) / 2
                    val topLeft = Offset((size.width - 2 * radius) / 2, (size.height - 2 * radius) / 2)
                    drawArc(color = emptyBorderColor, startAngle = 0f, sweepAngle = 360f, useCenter = false, topLeft = topLeft, size = Size(radius * 2, radius * 2), style = Stroke(width = stroke))
                }
                Text(text = if (strings.isBangla) "এই সময়ে কোনো তথ্য নেই" else "No data for this period", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = appColors.textMuted)
            } else {
                val animationProgress = remember { Animatable(if (motion.reduceMotion) 1f else 0f) }
                LaunchedEffect(categorySpends, centerLabel) {
                    if (!motion.reduceMotion) {
                        animationProgress.snapTo(0f)
                        animationProgress.animateTo(1f, animationSpec = tween(motion.durationEmphasized))
                    } else {
                        animationProgress.snapTo(1f)
                    }
                }

                Canvas(
                    modifier = Modifier
                        .size(chartDiameter - 10.dp)
                        .pointerInput(categorySpends) {
                            detectTapGestures { tapOffset ->
                                val tappedIdx = DonutChartTouchHelper.calculateTappedIndex(tapOffset, size, categorySpends)
                                selectedIndex = if (selectedIndex == tappedIdx) null else tappedIdx
                            }
                        }
                ) {
                    val baseStroke = baseStrokeWidthDp.toPx()
                    val radius = (size.minDimension - baseStroke) / 2
                    val topLeft = Offset((size.width - 2 * radius) / 2, (size.height - 2 * radius) / 2)
                    val arcSize = Size(radius * 2, radius * 2)

                    var startAngle = -90f
                    for ((idx, spend) in categorySpends.withIndex()) {
                        val sweepAngle = spend.percentage * 360f * animationProgress.value
                        val baseColor = CategoryIconHelper.parseColor(spend.categoryColor)
                        val isSelected = selectedIndex == idx
                        val isAnySelected = selectedIndex != null

                        val effectiveColor = when {
                            isSelected -> baseColor
                            isAnySelected -> baseColor.copy(alpha = 0.35f)
                            else -> baseColor
                        }

                        val strokeWidth = if (isSelected) baseStroke + 4.dp.toPx() else baseStroke

                        drawArc(
                            color = effectiveColor,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweepAngle
                    }
                }

                // Center text: active selection or default total
                val activeCategory = selectedIndex?.let { categorySpends.getOrNull(it) }
                val displayLabel = activeCategory?.let { getLocalizedCategoryName(it.categoryName, strings.isBangla) } ?: centerLabel
                val displayAmount = activeCategory?.totalAmount ?: totalAmount

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 14.dp)) {
                    Text(text = displayLabel, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.Medium), color = appColors.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCurrency(displayAmount, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"),
                        color = appColors.textPrimary
                    )
                    if (activeCategory != null) {
                        Text(
                            text = "${(activeCategory.percentage * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"),
                            color = appColors.accent
                        )
                    }
                }
            }
        }

        if (categorySpends.isNotEmpty() && totalAmount > 0.0) {
            Spacer(modifier = Modifier.height(20.dp))
            DonutChartLegend(
                categorySpends = categorySpends,
                selectedIndex = selectedIndex,
                onSelectedIndexChanged = { selectedIndex = it },
                currencySymbol = currencySymbol,
                isBangla = strings.isBangla,
                onCategoryClick = onCategoryClick
            )
        }
    }
}
