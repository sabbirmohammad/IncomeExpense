package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.PaymentMethodSpend
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion
import kotlin.math.atan2

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
            PaymentMethod.BKASH -> Color(0xFFBE123C)
            PaymentMethod.NAGAD -> Color(0xFFD97706)
            PaymentMethod.BANK_TRANSFER -> Color(0xFF1D4ED8)
            PaymentMethod.CASH -> Color(0xFF15803D)
            PaymentMethod.CREDIT_CARD -> Color(0xFF7E22CE)
            PaymentMethod.DEBIT_CARD -> Color(0xFF4338CA)
            PaymentMethod.OTHER -> Color(0xFF64748B)
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
    val motion = LocalMotion.current
    val chartDiameter = 160.dp
    val baseStrokeWidthDp = 22.dp

    var selectedIndex by remember(spends) { mutableStateOf<Int?>(null) }

    val accessibilitySummary = remember(spends, totalAmount) {
        val topThree = spends.take(3).joinToString { "${it.paymentMethod.getDisplayName(strings.isBangla)} ${(it.percentage * 100).toInt()}%" }
        "Payment methods donut chart. Total: ${totalAmount}. Top: $topThree"
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
            if (spends.isEmpty() || totalAmount <= 0.0) {
                val emptyBorderColor = appColors.border
                Canvas(modifier = Modifier.size(chartDiameter - 10.dp)) {
                    val stroke = baseStrokeWidthDp.toPx()
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
                val animationProgress = remember { Animatable(if (motion.reduceMotion) 1f else 0f) }
                LaunchedEffect(spends, centerLabel) {
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
                        .pointerInput(spends) {
                            detectTapGestures { tapOffset ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val dx = tapOffset.x - center.x
                                val dy = tapOffset.y - center.y
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                if (angle < 0) angle += 360f
                                val normalizedAngle = (angle + 90f) % 360f

                                var accumulated = 0f
                                var tappedIdx: Int? = null
                                for ((idx, s) in spends.withIndex()) {
                                    val sweep = s.percentage * 360f
                                    if (normalizedAngle >= accumulated && normalizedAngle < accumulated + sweep) {
                                        tappedIdx = idx
                                        break
                                    }
                                    accumulated += sweep
                                }
                                selectedIndex = if (selectedIndex == tappedIdx) null else tappedIdx
                            }
                        }
                ) {
                    val baseStroke = baseStrokeWidthDp.toPx()
                    val radius = (size.minDimension - baseStroke) / 2
                    val topLeft = Offset((size.width - 2 * radius) / 2, (size.height - 2 * radius) / 2)
                    val arcSize = Size(radius * 2, radius * 2)

                    var startAngle = -90f
                    for ((idx, s) in spends.withIndex()) {
                        val sweepAngle = s.percentage * 360f * animationProgress.value
                        val baseColor = getPaymentMethodColor(s.paymentMethod, appColors.isDark)
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

                val activeSpend = selectedIndex?.let { spends.getOrNull(it) }
                val displayLabel = activeSpend?.paymentMethod?.getDisplayName(strings.isBangla) ?: centerLabel
                val displayAmount = activeSpend?.totalAmount ?: totalAmount

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp)
                ) {
                    Text(
                        text = displayLabel,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCurrency(displayAmount, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.textPrimary
                    )
                    if (activeSpend != null) {
                        Text(
                            text = "${(activeSpend.percentage * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.accent
                        )
                    }
                }
            }
        }

        if (spends.isNotEmpty() && totalAmount > 0.0) {
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                spends.forEachIndexed { idx, s ->
                    val color = getPaymentMethodColor(s.paymentMethod, appColors.isDark)
                    val isSelected = selectedIndex == idx
                    val isAnySelected = selectedIndex != null

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(if (isSelected) appColors.accentContainer else Color.Transparent)
                            .clickable { selectedIndex = if (selectedIndex == idx) null else idx }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
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
                                    .background(if (!isAnySelected || isSelected) color else color.copy(alpha = 0.4f))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = s.paymentMethod.getDisplayName(strings.isBangla),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (!isAnySelected || isSelected) appColors.textPrimary else appColors.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatCurrency(s.totalAmount, currencySymbol),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = if (!isAnySelected || isSelected) appColors.textPrimary else appColors.textMuted
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${(s.percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = if (!isAnySelected || isSelected) appColors.textSecondary else appColors.textMuted,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}
