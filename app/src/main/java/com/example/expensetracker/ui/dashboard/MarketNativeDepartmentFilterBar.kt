package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun MarketNativeDepartmentFilterBar(
    selectedDepartment: String,
    onSelectDepartment: (String) -> Unit,
    itemsByDept: Map<String, List<MarketItem>>,
    totalCount: Int,
    isBangla: Boolean,
    appColors: AppColors,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            val isAllSelected = selectedDepartment == "all"
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isAllSelected) appColors.accent else appColors.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, if (isAllSelected) appColors.accent else appColors.border),
                modifier = Modifier.clickable {
                    haptics.lightTick()
                    onSelectDepartment("all")
                }
            ) {
                Text(
                    text = if (isBangla) "🛒 সব (${convertToBengaliDigits(totalCount.toString())})"
                           else "🛒 All ($totalCount)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isAllSelected) Color.White else appColors.textSecondary,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                )
            }
        }

        items(ALL_DEPARTMENTS.filter { itemsByDept[it.key]?.isNotEmpty() == true }) { dept ->
            val isSelected = selectedDepartment == dept.key
            val deptCount = itemsByDept[dept.key]?.size ?: 0
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) appColors.accent else appColors.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, if (isSelected) appColors.accent else appColors.border),
                modifier = Modifier.clickable {
                    haptics.lightTick()
                    onSelectDepartment(dept.key)
                }
            ) {
                val title = if (isBangla) dept.titleBn else dept.titleEn
                val countStr = if (isBangla) convertToBengaliDigits(deptCount.toString()) else deptCount.toString()
                Text(
                    text = "${dept.icon} $title ($countStr)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) Color.White else appColors.textSecondary,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                )
            }
        }
    }
}
