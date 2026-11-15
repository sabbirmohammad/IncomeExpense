package com.example.expensetracker.ui.market

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketShoppingFilterPills(
    filterMode: Int,
    onFilterModeChange: (Int) -> Unit,
    totalCount: Int,
    purchasedCount: Int,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val remainingCount = totalCount - purchasedCount

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val allBg by animateColorAsState(
            targetValue = if (filterMode == 0) appColors.accent.copy(alpha = 0.15f) else Color.Transparent,
            animationSpec = tween(150),
            label = "filterAllBg"
        )
        val allBorder by animateColorAsState(
            targetValue = if (filterMode == 0) appColors.accent else appColors.border,
            animationSpec = tween(150),
            label = "filterAllBorder"
        )
        Surface(
            onClick = {
                haptics.lightTick()
                onFilterModeChange(0)
            },
            shape = RoundedCornerShape(10.dp),
            color = allBg,
            border = BorderStroke(1.dp, allBorder),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "সব (${convertToBengaliDigits(totalCount.toString())})" else "All ($totalCount)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = if (filterMode == 0) FontWeight.Bold else FontWeight.Normal,
                        fontFeatureSettings = "tnum"
                    ),
                    color = if (filterMode == 0) appColors.accent else appColors.textSecondary
                )
            }
        }

        val leftBg by animateColorAsState(
            targetValue = if (filterMode == 1) appColors.warning.copy(alpha = 0.15f) else Color.Transparent,
            animationSpec = tween(150),
            label = "filterLeftBg"
        )
        val leftBorder by animateColorAsState(
            targetValue = if (filterMode == 1) appColors.warning else appColors.border,
            animationSpec = tween(150),
            label = "filterLeftBorder"
        )
        Surface(
            onClick = {
                haptics.lightTick()
                onFilterModeChange(1)
            },
            shape = RoundedCornerShape(10.dp),
            color = leftBg,
            border = BorderStroke(1.dp, leftBorder),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "বাকি (${convertToBengaliDigits(remainingCount.toString())})" else "Left ($remainingCount)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = if (filterMode == 1) FontWeight.Bold else FontWeight.Normal,
                        fontFeatureSettings = "tnum"
                    ),
                    color = if (filterMode == 1) appColors.warning else appColors.textSecondary
                )
            }
        }

        val doneBg by animateColorAsState(
            targetValue = if (filterMode == 2) appColors.income.copy(alpha = 0.15f) else Color.Transparent,
            animationSpec = tween(150),
            label = "filterDoneBg"
        )
        val doneBorder by animateColorAsState(
            targetValue = if (filterMode == 2) appColors.income else appColors.border,
            animationSpec = tween(150),
            label = "filterDoneBorder"
        )
        Surface(
            onClick = {
                haptics.lightTick()
                onFilterModeChange(2)
            },
            shape = RoundedCornerShape(10.dp),
            color = doneBg,
            border = BorderStroke(1.dp, doneBorder),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "কেনা হয়েছে (${convertToBengaliDigits(purchasedCount.toString())})" else "Done ($purchasedCount)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = if (filterMode == 2) FontWeight.Bold else FontWeight.Normal,
                        fontFeatureSettings = "tnum"
                    ),
                    color = if (filterMode == 2) appColors.income else appColors.textSecondary
                )
            }
        }
    }
}
