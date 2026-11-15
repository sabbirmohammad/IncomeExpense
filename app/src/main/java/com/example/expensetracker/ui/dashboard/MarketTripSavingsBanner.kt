package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun MarketTripSavingsBanner(
    calculatedSavings: Double,
    currencySymbol: String,
    isBangla: Boolean,
    appColors: AppColors,
    modifier: Modifier = Modifier
) {
    if (calculatedSavings > 0.0) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = appColors.income.copy(alpha = 0.12f),
            border = BorderStroke(HairlineInkStroke, appColors.income.copy(alpha = 0.35f)),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = appColors.income,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = if (isBangla) "🎉 চমৎকার! আনুমানিক সাশ্রয়:" else "🎉 Estimated Savings:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        color = appColors.income
                    )
                    Text(
                        text = formatCurrency(calculatedSavings, currencySymbol),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.income
                    )
                }
            }
        }
    } else {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = appColors.cardBackground,
            border = BorderStroke(HairlineInkStroke, appColors.divider),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isBangla) "💡 সুপরিকল্পিত ও গোছানো বাজার সম্পন্ন হচ্ছে" else "💡 Well-planned shopping trip completed",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )
            }
        }
    }
}
