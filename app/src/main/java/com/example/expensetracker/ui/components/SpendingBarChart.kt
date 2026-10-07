package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextSecondary
import kotlin.math.max

/**
 * Spending trend bar chart per spec:
 * Dual bars (income green #3ddc97, expense danger #f0716b).
 */
@Composable
fun SpendingBarChart(
    dailySpends: List<DailySpend>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
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
                color = ColorTextMuted
            )
        }
        return
    }

    val maxVal = max(dailySpends.maxOfOrNull { max(it.income, it.expense) } ?: 1.0, 1.0)
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(dailySpends) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(800))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            dailySpends.takeLast(7).forEach { spend ->
                val expenseHeight = if (spend.expense > 0) ((spend.expense / maxVal) * 110 * animProgress.value).toFloat().coerceAtLeast(6f) else 0f
                val incomeHeight = if (spend.income > 0) ((spend.income / maxVal) * 110 * animProgress.value).toFloat().coerceAtLeast(6f) else 0f

                Column(
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
                                    .background(ColorSuccessIncome)
                            )
                        }
                        // Expense Bar: #f0716b
                        if (spend.expense > 0) {
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(expenseHeight.dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(ColorDangerExpense)
                            )
                        } else if (spend.income <= 0) {
                            // Subtle neutral indicator on days with no spend or income
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(3.dp)
                                    .clip(CircleShape)
                                    .background(ColorBorder)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = spend.dayLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = ColorTextSecondary
                    )
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
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ColorSuccessIncome))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = strings.income,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = ColorTextSecondary
            )

            Spacer(modifier = Modifier.width(20.dp))

            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ColorDangerExpense))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = strings.expense,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = ColorTextSecondary
            )
        }
    }
}
