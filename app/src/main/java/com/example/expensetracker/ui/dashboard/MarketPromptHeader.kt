package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke

@Composable
fun MarketPromptHeader(
    item: MarketItem,
    isBangla: Boolean,
    appColors: AppColors,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = appColors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = appColors.accent.copy(alpha = 0.12f),
            border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.25f))
        ) {
            Text(
                text = item.formatQuantity(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFeatureSettings = "tnum"
                ),
                color = appColors.accent,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = if (isBangla) "পণ্যটির ক্রয়মূল্য লিখুন:" else "Enter actual purchase price:",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
        color = appColors.textSecondary
    )
}
