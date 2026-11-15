package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun MarketListFilterTabs(
    filterMode: Int,
    onFilterSelect: (Int) -> Unit,
    totalCount: Int,
    purchasedCount: Int,
    isBangla: Boolean,
    appColors: AppColors,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val remainingCount = totalCount - purchasedCount

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Filter 0: All
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (filterMode == 0) appColors.accent.copy(alpha = 0.15f) else Color.Transparent,
            border = BorderStroke(1.dp, if (filterMode == 0) appColors.accent else appColors.border),
            modifier = Modifier
                .weight(1f)
                .clickable {
                    haptics.lightTick()
                    onFilterSelect(0)
                }
        ) {
            Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isBangla) "সব (${convertToBengaliDigits(totalCount.toString())})" else "All ($totalCount)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = if (filterMode == 0) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (filterMode == 0) appColors.accent else appColors.textSecondary
                )
            }
        }

        // Filter 1: Remaining
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (filterMode == 1) appColors.warning.copy(alpha = 0.15f) else Color.Transparent,
            border = BorderStroke(1.dp, if (filterMode == 1) appColors.warning else appColors.border),
            modifier = Modifier
                .weight(1f)
                .clickable {
                    haptics.lightTick()
                    onFilterSelect(1)
                }
        ) {
            Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isBangla) "বাকি (${convertToBengaliDigits(remainingCount.toString())})" else "Remaining ($remainingCount)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = if (filterMode == 1) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (filterMode == 1) appColors.warning else appColors.textSecondary
                )
            }
        }

        // Filter 2: Done
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (filterMode == 2) appColors.income.copy(alpha = 0.15f) else Color.Transparent,
            border = BorderStroke(1.dp, if (filterMode == 2) appColors.income else appColors.border),
            modifier = Modifier
                .weight(1f)
                .clickable {
                    haptics.lightTick()
                    onFilterSelect(2)
                }
        ) {
            Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isBangla) "কেনা শেষ (${convertToBengaliDigits(purchasedCount.toString())})" else "Done ($purchasedCount)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = if (filterMode == 2) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (filterMode == 2) appColors.income else appColors.textSecondary
                )
            }
        }
    }
}
