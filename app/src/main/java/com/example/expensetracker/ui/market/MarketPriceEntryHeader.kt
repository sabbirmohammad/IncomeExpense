package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits

/**
 * Top title row for MarketPriceEntrySheet displaying commodity name, last buy rate, and close button.
 */
@Composable
fun MarketPriceEntryHeader(
    itemName: String,
    baselineUnitPrice: Double?,
    baseUnit: String,
    currencySymbol: String,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 24.sp
                ),
                color = appColors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (baselineUnitPrice != null && baselineUnitPrice > 0) {
                val rateStr = if (baselineUnitPrice % 1.0 == 0.0) baselineUnitPrice.toInt().toString() else "%.1f".format(baselineUnitPrice)
                val rateDigits = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
                Text(
                    text = "${strings.marketLastTime}: $currencySymbol$rateDigits/$baseUnit",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.textSecondary
                )
            }
        }

        IconButton(
            onClick = {
                haptics.lightTick()
                onDismiss()
            },
            modifier = Modifier.minimumInteractiveComponentSize()
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                tint = appColors.textSecondary
            )
        }
    }
}
