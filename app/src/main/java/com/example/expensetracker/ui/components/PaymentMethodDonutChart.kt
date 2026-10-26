package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.PaymentMethodSpend
import com.example.expensetracker.theme.LocalAppColors

fun getPaymentMethodColor(method: PaymentMethod, isDark: Boolean = true): Color {
    return if (isDark) {
        when (method) {
            PaymentMethod.BKASH -> Color(0xFFE2136E)
            PaymentMethod.NAGAD -> Color(0xFFF7941D)
            PaymentMethod.BANK_TRANSFER -> Color(0xFF3B8FF0)
            PaymentMethod.CASH -> Color(0xFF3DDC97)
            PaymentMethod.CREDIT_CARD -> Color(0xFF9D4EDD)
            PaymentMethod.DEBIT_CARD -> Color(0xFF5C7CFA)
            PaymentMethod.OTHER -> Color(0xFF8B90A0)
        }
    } else {
        when (method) {
            PaymentMethod.BKASH -> Color(0xFFBE123C) // Rose-700, 5.0:1 on #FFF
            PaymentMethod.NAGAD -> Color(0xFFD97706) // Amber-600, 4.5:1 on #FFF
            PaymentMethod.BANK_TRANSFER -> Color(0xFF1D4ED8) // Blue-700, 5.2:1 on #FFF
            PaymentMethod.CASH -> Color(0xFF15803D) // Green-700, 4.8:1 on #FFF
            PaymentMethod.CREDIT_CARD -> Color(0xFF7E22CE) // Purple-700, 5.1:1 on #FFF
            PaymentMethod.DEBIT_CARD -> Color(0xFF4338CA) // Indigo-700, 5.5:1 on #FFF
            PaymentMethod.OTHER -> Color(0xFF64748B) // Slate-500, 4.6:1 on #FFF
        }
    }
}

@Composable
fun PaymentMethodDonutChart(
    spends: List<PaymentMethodSpend>,
    currencySymbol: String,
    totalAmount: Double,
    centerLabel: String = "Total spent",
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
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
            if (spends.isEmpty() || totalAmount <= 0.0) {
                val emptyBorderColor = appColors.border
                Canvas(modifier = Modifier.size(chartDiameter - 10.dp)) {
                    val stroke = strokeWidthDp.toPx()
                    val radius = (size.minDimension - stroke) / 2
                    val topLeft = Offset((size.width - 2 * radius) / 2, (size.height - 2 * radius) / 2)
                    drawArc(
                        color = emptyBorderColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = stroke)
                    )
                }

                Text(
                    text = if (strings.isBangla) "এই সময়ে কোনো তথ্য নেই" else "No data for this period",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = appColors.textMuted
                )
            } else {
                val animationProgress = remember { Animatable(0f) }
                LaunchedEffect(spends, centerLabel) {
                    animationProgress.snapTo(0f)
                    animationProgress.animateTo(1f, animationSpec = tween(800))
                }

                Canvas(modifier = Modifier.size(chartDiameter - 10.dp)) {
                    val stroke = strokeWidthDp.toPx()
                    val radius = (size.minDimension - stroke) / 2
                    val topLeft = Offset((size.width - 2 * radius) / 2, (size.height - 2 * radius) / 2)
                    val arcSize = Size(radius * 2, radius * 2)

                    var startAngle = -90f
                    for (spend in spends) {
                        val sweepAngle = spend.percentage * 360f * animationProgress.value
                        val color = getPaymentMethodColor(spend.paymentMethod, appColors.isDark)

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

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = centerLabel,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = appColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCurrency(totalAmount, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.textPrimary
                    )
                }
            }
        }

        if (spends.isNotEmpty() && totalAmount > 0.0) {
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                spends.forEach { spend ->
                    val color = getPaymentMethodColor(spend.paymentMethod, appColors.isDark)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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
                                text = spend.paymentMethod.getDisplayName(strings.isBangla),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = appColors.textPrimary,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatCurrency(spend.totalAmount, currencySymbol),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = appColors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${(spend.percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = appColors.textSecondary,
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
