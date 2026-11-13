package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits

/**
 * Quick Delta chips: "Same as last", "+৳5", "−৳5" for rapid market price input.
 */
@Composable
fun MarketPriceQuickChips(
    baselineUnitPrice: Double?,
    enteredNumber: Double,
    onApplyPrice: (newExpression: String, newPriceMode: Int) -> Unit,
    isBangla: Boolean,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (baselineUnitPrice != null && baselineUnitPrice > 0) {
            val prevPriceFormatted = if (baselineUnitPrice % 1.0 == 0.0) baselineUnitPrice.toInt().toString() else baselineUnitPrice.toString()
            val prevPriceDigits = if (isBangla) convertToBengaliDigits(prevPriceFormatted) else prevPriceFormatted
            Surface(
                onClick = {
                    haptics.lightTick()
                    onApplyPrice(prevPriceFormatted, 0)
                },
                shape = RoundedCornerShape(10.dp),
                color = appColors.accentContainer.copy(alpha = 0.35f),
                border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.35f)),
                modifier = Modifier
                    .weight(1.2f)
                    .defaultMinSize(minHeight = 44.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${strings.marketSameAsLast} ৳$prevPriceDigits",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // "+৳5" Chip
        Surface(
            onClick = {
                haptics.lightTick()
                val next = (enteredNumber + 5.0).coerceAtLeast(0.0)
                val formatted = if (next % 1.0 == 0.0) next.toInt().toString() else next.toString()
                onApplyPrice(formatted, -1) // keep current mode
            },
            shape = RoundedCornerShape(10.dp),
            color = appColors.surface,
            border = BorderStroke(HairlineInkStroke, appColors.divider),
            modifier = Modifier
                .weight(0.9f)
                .defaultMinSize(minHeight = 44.dp)
                .minimumInteractiveComponentSize()
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "+৳৫" else "+৳5",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.textPrimary
                )
            }
        }

        // "−৳5" Chip
        Surface(
            onClick = {
                haptics.lightTick()
                val next = (enteredNumber - 5.0).coerceAtLeast(0.0)
                val formatted = if (next % 1.0 == 0.0) next.toInt().toString() else next.toString()
                onApplyPrice(formatted, -1) // keep current mode
            },
            shape = RoundedCornerShape(10.dp),
            color = appColors.surface,
            border = BorderStroke(HairlineInkStroke, appColors.divider),
            modifier = Modifier
                .weight(0.9f)
                .defaultMinSize(minHeight = 44.dp)
                .minimumInteractiveComponentSize()
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "−৳৫" else "−৳5",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.textPrimary
                )
            }
        }
    }
}
