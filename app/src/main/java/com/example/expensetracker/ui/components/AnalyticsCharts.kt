package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary

/**
 * Donut chart per spec:
 * 160px diameter, category colors strictly from design system table.
 * Center label ("Total spent" / "Total income", 12px 400 text secondary) + hero value (18-20px 500 text primary).
 * Empty state: muted placeholder ring in border color #232838 + "No data for this period" in text muted #6f7482.
 * Legend below chart: color dot (10px circle), category name (13px 400), amount + percentage right-aligned in aligned columns.
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
    val strings = LocalAppStrings.current
    val chartDiameter = 160.dp
    val strokeWidthDp = 22.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(chartDiameter),
            contentAlignment = Alignment.Center
        ) {
            if (categorySpends.isEmpty() || totalAmount <= 0.0) {
                // Muted placeholder ring in border color #232838
                Canvas(modifier = Modifier.size(chartDiameter - 10.dp)) {
                    val stroke = strokeWidthDp.toPx()
                    val radius = (size.minDimension - stroke) / 2
                    val topLeft = Offset((size.width - 2 * radius) / 2, (size.height - 2 * radius) / 2)
                    drawArc(
                        color = ColorBorder,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = stroke)
                    )
                }

                // Center placeholder text
                Text(
                    text = if (strings.isBangla) "এই সময়ে কোনো তথ্য নেই" else "No data for this period",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = ColorTextMuted
                )
            } else {
                // Animated donut ring
                val animationProgress = remember { Animatable(0f) }
                LaunchedEffect(categorySpends, centerLabel) {
                    animationProgress.snapTo(0f)
                    animationProgress.animateTo(1f, animationSpec = tween(800))
                }

                Canvas(modifier = Modifier.size(chartDiameter - 10.dp)) {
                    val stroke = strokeWidthDp.toPx()
                    val radius = (size.minDimension - stroke) / 2
                    val topLeft = Offset((size.width - 2 * radius) / 2, (size.height - 2 * radius) / 2)
                    val arcSize = Size(radius * 2, radius * 2)

                    var startAngle = -90f
                    for (spend in categorySpends) {
                        val sweepAngle = spend.percentage * 360f * animationProgress.value
                        val color = CategoryIconHelper.parseColor(spend.categoryColor)

                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Butt)
                        )
                        startAngle += sweepAngle
                    }
                }

                // Center text: label + hero amount
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = centerLabel,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = ColorTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCurrency(totalAmount, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextPrimary
                    )
                }
            }
        }

        if (categorySpends.isNotEmpty() && totalAmount > 0.0) {
            Spacer(modifier = Modifier.height(20.dp))

            // Legend per spec: color dot (10px circle), category name (13px 400),
            // amount + percentage right-aligned in aligned columns.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categorySpends.forEach { spend ->
                    val color = CategoryIconHelper.parseColor(spend.categoryColor)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (onCategoryClick != null) {
                                    Modifier.clickable { onCategoryClick(spend.categoryId) }
                                } else Modifier
                            )
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = getLocalizedCategoryName(spend.categoryName, strings.isBangla),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = ColorTextPrimary,
                                maxLines = 1
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatCurrency(spend.totalAmount, currencySymbol),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = ColorTextPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${(spend.percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = ColorTextSecondary,
                                modifier = Modifier.width(36.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}
