package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.*
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.DateTimeHelper

@Composable
fun ProductPriceHistorySection(
    history: List<ProductPriceRecord>,
    isBangla: Boolean,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = appColors.accent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isBangla) "ক্রয়ের বিস্তারিত টাইমলাইন (${convertToBengaliDigits(history.size.toString())}টি)"
                       else "Purchase History Timeline (${history.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = WeightBold, fontSize = 14.sp),
                color = appColors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = appColors.cardBackground,
            border = BorderStroke(HairlineInkStroke, appColors.divider),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isBangla) "কোনো অতীত ক্রয়ের রেকর্ড পাওয়া যায়নি" else "No purchase history found",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = appColors.textSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(history) { index, record ->
                        val dateFormatted = DateTimeHelper.formatDayGroup(record.timestamp, isBangla)
                        val rateFormatted = if (record.unitPrice % 1.0 == 0.0) record.unitPrice.toInt().toString() else "%.1f".format(record.unitPrice)
                        val rateText = if (isBangla) convertToBengaliDigits(rateFormatted) else rateFormatted
                        val amtFormatted = if (record.totalAmount % 1.0 == 0.0) record.totalAmount.toInt().toString() else "%.1f".format(record.totalAmount)
                        val amtText = if (isBangla) convertToBengaliDigits(amtFormatted) else amtFormatted
                        val qtyFormatted = if (record.rawQuantity % 1.0 == 0.0) record.rawQuantity.toInt().toString() else "%.1f".format(record.rawQuantity)
                        val qtyText = if (isBangla) convertToBengaliDigits(qtyFormatted) else qtyFormatted

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dateFormatted,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = WeightMedium),
                                    color = appColors.textPrimary
                                )
                                Text(
                                    text = "$qtyText ${record.rawUnit} • মোট $currencySymbol$amtText",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontFeatureSettings = TabularNumFeature),
                                    color = appColors.textSecondary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (index == 0) appColors.accent.copy(alpha = 0.12f) else appColors.cardBackground,
                                border = BorderStroke(HairlineInkStroke, if (index == 0) appColors.accent.copy(alpha = 0.35f) else appColors.divider)
                            ) {
                                Text(
                                    text = "$currencySymbol$rateText/${record.baseUnit}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = WeightBold,
                                        fontSize = 12.sp,
                                        fontFeatureSettings = TabularNumFeature
                                    ),
                                    color = if (index == 0) appColors.accent else appColors.textPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
