package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.ui.components.convertToBengaliDigits

fun LazyListScope.marketRecentPurchasesSection(
    records: List<ProductPriceRecord>,
    currencySymbol: String,
    isBangla: Boolean,
    appColors: AppColors
) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = appColors.accent,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            val recentCount = minOf(10, records.size)
            val countText = if (isBangla) convertToBengaliDigits(recentCount.toString()) else recentCount.toString()
            Text(
                text = if (isBangla) "সাম্প্রতিক ক্রয়ের ইতিহাস (শেষ ${countText}টি)"
                else "Recent Purchases (Last $countText)",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = WeightBold,
                    fontSize = 14.5.sp
                ),
                color = appColors.textPrimary
            )
        }
    }

    if (records.isEmpty()) {
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = appColors.cardBackground,
                border = BorderStroke(HairlineInkStroke, appColors.divider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) "কোনো অতীত ক্রয়ের রেকর্ড পাওয়া যায়নি" else "No purchase history found",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = appColors.textSecondary
                    )
                }
            }
        }
    } else {
        val last10 = records.take(10)
        itemsIndexed(last10) { index, record ->
            PurchaseHistoryItemCard(
                record = record,
                isFirst = index == 0,
                currencySymbol = currencySymbol,
                isBangla = isBangla
            )
        }
    }
}
