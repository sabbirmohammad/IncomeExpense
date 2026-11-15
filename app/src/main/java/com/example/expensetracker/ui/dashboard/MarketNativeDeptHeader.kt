package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun MarketNativeDeptHeader(
    dept: DepartmentMeta,
    purchasedInDept: Int,
    deptItemsCount: Int,
    isBangla: Boolean,
    appColors: AppColors,
    modifier: Modifier = Modifier
) {
    val isDeptCompleted = purchasedInDept == deptItemsCount
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp, start = 2.dp, end = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = dept.icon, fontSize = 15.sp)
            Text(
                text = if (isBangla) dept.titleBn else dept.titleEn,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = appColors.textPrimary
            )
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isDeptCompleted) appColors.income.copy(alpha = 0.15f) else appColors.surfaceVariant,
            border = BorderStroke(1.dp, if (isDeptCompleted) appColors.income.copy(alpha = 0.3f) else appColors.border)
        ) {
            Text(
                text = if (isDeptCompleted) {
                    if (isBangla) "✓ সম্পন্ন" else "✓ Done"
                } else {
                    if (isBangla) "${convertToBengaliDigits(purchasedInDept.toString())}/${convertToBengaliDigits(deptItemsCount.toString())}"
                    else "$purchasedInDept/$deptItemsCount"
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isDeptCompleted) appColors.income else appColors.textSecondary,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
