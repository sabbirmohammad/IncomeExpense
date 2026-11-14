package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion
import java.util.Calendar
import kotlin.math.max

private fun isSameDay(dateMillis: Long): Boolean {
    if (dateMillis <= 0L) return false
    val cal1 = Calendar.getInstance().apply { timeInMillis = dateMillis }
    val cal2 = Calendar.getInstance()
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

/**
 * Spending trend bar chart per V7 spec:
 * Dual bars (income green #3ddc97 / accent, expense danger #f0716b).
 * Interactive tap tooltip showing day details in tabular figures (`tnum`).
 * Current day / Today visual highlight.
 * Accessible TalkBack summary semantics.
 * Respects reduce-motion for entry animation.
 */
@Composable
fun SpendingBarChart(
    dailySpends: List<DailySpend>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val motion = LocalMotion.current

    if (dailySpends.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (strings.isBangla) "কোনো সাম্প্রতিক ট্রেন্ড তথ্য নেই" else "No recent trend data",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = appColors.textMuted
            )
        }
        return
    }

    val displaySpends = remember(dailySpends) { dailySpends.takeLast(7) }
    val maxVal = max(displaySpends.maxOfOrNull { max(it.income, it.expense) } ?: 1.0, 1.0)
    var selectedSpend by remember(displaySpends) { mutableStateOf<DailySpend?>(null) }

    val animProgress = remember { Animatable(if (motion.reduceMotion) 1f else 0f) }
    LaunchedEffect(dailySpends) {
        if (!motion.reduceMotion) {
            animProgress.snapTo(0f)
            animProgress.animateTo(1f, animationSpec = tween(motion.durationEmphasized))
        } else {
            animProgress.snapTo(1f)
        }
    }

    // Accessibility content description
    val a11ySummary = remember(displaySpends, currencySymbol) {
        val totalExp = displaySpends.sumOf { it.expense }
        val highestDay = displaySpends.maxByOrNull { it.expense }
        "Spending trend for ${displaySpends.size} days. Total expense: ${totalExp}. Highest spending day: ${highestDay?.dayLabel ?: "none"}"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = a11ySummary }
    ) {
        // Floating Tooltip Callout if a column is tapped
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            contentAlignment = Alignment.Center
        ) {
            this@Column.AnimatedVisibility(
                visible = selectedSpend != null,
                enter = fadeIn(tween(motion.durationFast)),
                exit = fadeOut(tween(motion.durationFast))
            ) {
                selectedSpend?.let { spend ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = appColors.surfaceRaised,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = spend.dayLabel,
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.5.sp, fontWeight = FontWeight.Bold),
                                color = appColors.textPrimary
                            )
                            if (spend.income > 0) {
                                Text(
                                    text = "+${formatCurrency(spend.income, currencySymbol)}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, fontFeatureSettings = "tnum"),
                                    color = appColors.income
                                )
                            }
                            if (spend.expense > 0) {
                                Text(
                                    text = "-${formatCurrency(spend.expense, currencySymbol)}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, fontFeatureSettings = "tnum"),
                                    color = appColors.expense
                                )
                            }
                            if (spend.income <= 0 && spend.expense <= 0) {
                                Text(
                                    text = if (strings.isBangla) "কোনো লেনদেন নেই" else "No transactions",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = appColors.textMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Dual Bars Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            displaySpends.forEach { spend ->
                val isSelected = selectedSpend == spend
                val isToday = isSameDay(spend.dateMillis)
                SpendingBarChartItem(
                    spend = spend,
                    isSelected = isSelected,
                    isToday = isToday,
                    maxVal = maxVal,
                    animProgress = animProgress.value,
                    hasSelection = selectedSpend != null,
                    onClick = { selectedSpend = if (selectedSpend == spend) null else spend }
                )
            }
        }

        // Legend row
        SpendingBarChartLegend()
    }
}
