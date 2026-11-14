package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun SpendingBarChartItem(
    spend: DailySpend,
    isSelected: Boolean,
    isToday: Boolean,
    maxVal: Double,
    animProgress: Float,
    hasSelection: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val expenseHeight = if (spend.expense > 0) ((spend.expense / maxVal) * 96 * animProgress).toFloat().coerceAtLeast(6f) else 0f
    val incomeHeight = if (spend.income > 0) ((spend.income / maxVal) * 96 * animProgress).toFloat().coerceAtLeast(6f) else 0f

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
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
                        .background(if (!hasSelection || isSelected) appColors.income else appColors.income.copy(alpha = 0.4f))
                )
            }
            // Expense Bar: #f0716b
            if (spend.expense > 0) {
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(expenseHeight.dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(if (!hasSelection || isSelected) appColors.expense else appColors.expense.copy(alpha = 0.4f))
                )
            } else if (spend.income <= 0) {
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
                    isSelected || isToday -> appColors.accent
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
