package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits

/**
 * Purchase history timeline row card.
 */
@Composable
fun PurchaseHistoryItemCard(
    record: ProductPriceRecord,
    isFirst: Boolean,
    currencySymbol: String,
    isBangla: Boolean
) {
    val appColors = LocalAppColors.current

    val dateFormatted = DateTimeHelper.formatDayGroup(record.timestamp, isBangla)
    val rateFormatted = if (record.unitPrice % 1.0 == 0.0) record.unitPrice.toInt().toString() else "%.1f".format(record.unitPrice)
    val rateText = if (isBangla) convertToBengaliDigits(rateFormatted) else rateFormatted
    val amtFormatted = if (record.totalAmount % 1.0 == 0.0) record.totalAmount.toInt().toString() else "%.1f".format(record.totalAmount)
    val amtText = if (isBangla) convertToBengaliDigits(amtFormatted) else amtFormatted
    val qtyFormatted = if (record.rawQuantity % 1.0 == 0.0) record.rawQuantity.toInt().toString() else "%.1f".format(record.rawQuantity)
    val qtyText = if (isBangla) convertToBengaliDigits(qtyFormatted) else qtyFormatted

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isFirst) appColors.cardBackground else appColors.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(HairlineInkStroke, if (isFirst) appColors.accent.copy(alpha = 0.35f) else appColors.divider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isFirst) WeightBold else WeightMedium,
                            fontSize = 13.5.sp
                        ),
                        color = appColors.textPrimary
                    )
                    if (isFirst) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = appColors.accent.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = if (isBangla) "সর্বশেষ" else "Latest",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = WeightBold
                                ),
                                color = appColors.accent,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$qtyText ${record.rawUnit} • মোট $currencySymbol$amtText",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontFeatureSettings = TabularNumFeature
                    ),
                    color = appColors.textSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isFirst) appColors.accent.copy(alpha = 0.12f) else appColors.cardBackground,
                border = BorderStroke(HairlineInkStroke, if (isFirst) appColors.accent.copy(alpha = 0.4f) else appColors.divider)
            ) {
                Text(
                    text = "$currencySymbol$rateText/${record.baseUnit}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = WeightBold,
                        fontSize = 13.5.sp,
                        fontFeatureSettings = TabularNumFeature
                    ),
                    color = if (isFirst) appColors.accent else appColors.textPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
