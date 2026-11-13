package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.clip
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
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = appColors.textPrimary
                            )
                            if (spend.income > 0) {
                                Text(
                                    text = "+${formatCurrency(spend.income, currencySymbol)}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = appColors.income
                                )
                            }
                            if (spend.expense > 0) {
                                Text(
                                    text = "-${formatCurrency(spend.expense, currencySymbol)}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFeatureSettings = "tnum"
                                    ),
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
                val expenseHeight = if (spend.expense > 0) ((spend.expense / maxVal) * 96 * animProgress.value).toFloat().coerceAtLeast(6f) else 0f
                val incomeHeight = if (spend.income > 0) ((spend.income / maxVal) * 96 * animProgress.value).toFloat().coerceAtLeast(6f) else 0f

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            selectedSpend = if (selectedSpend == spend) null else spend
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Income Bar: #3ddc97
                        if (spend.income > 0) {
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(incomeHeight.dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (selectedSpend == null || isSelected) appColors.income else appColors.income.copy(alpha = 0.4f))
                            )
                        }
                        // Expense Bar: #f0716b
                        if (spend.expense > 0) {
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(expenseHeight.dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (selectedSpend == null || isSelected) appColors.expense else appColors.expense.copy(alpha = 0.4f))
                            )
                        } else if (spend.income <= 0) {
                            // Subtle neutral indicator on days with no spend or income
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(3.dp)
                                    .clip(CircleShape)
                                    .background(appColors.border)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Day label with Today highlight
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = spend.dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = when {
                                isSelected -> appColors.accent
                                isToday -> appColors.accent
                                else -> appColors.textSecondary
                            }
                        )
                        if (isToday) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(appColors.accent)
                            )
                        }
                    }
                }
            }
        }

        // Legend row per spec: 10px circles, Income green #3ddc97, Expense danger #f0716b
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(appColors.income))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = strings.income,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary
            )

            Spacer(modifier = Modifier.width(20.dp))

            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(appColors.expense))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = strings.expense,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary
            )
        }
    }
}
