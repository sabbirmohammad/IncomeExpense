package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.convertToBengaliDigits

/**
 * Quantity stepper ([-] Qty [+]) and scrollable unit chips.
 */
@Composable
fun MarketPriceQuantityStepper(
    quantity: Double,
    onQuantityChange: (Double) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    standardUnits: List<String>,
    isBangla: Boolean,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.surface,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "পরিমাণ ও একক:" else "Quantity & Unit:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    ),
                    color = appColors.textSecondary
                )

                // Stepper: [-] quantity [+]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = {
                            haptics.lightTick()
                            val step = if (quantity <= 1.0) 0.25 else if (quantity <= 5.0) 0.5 else 1.0
                            onQuantityChange((quantity - step).coerceAtLeast(0.1))
                        },
                        shape = CircleShape,
                        color = appColors.cardBackground,
                        border = BorderStroke(HairlineInkStroke, appColors.divider),
                        modifier = Modifier
                            .size(36.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = "Decrease",
                                tint = appColors.textPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    val qtyFormatted = if (quantity % 1.0 == 0.0) quantity.toInt().toString() else "%.2f".format(quantity).trimEnd('0').trimEnd('.')
                    val qtyBengali = if (isBangla) convertToBengaliDigits(qtyFormatted) else qtyFormatted
                    Text(
                        text = "$qtyBengali $unit",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.textPrimary
                    )

                    Surface(
                        onClick = {
                            haptics.lightTick()
                            val step = if (quantity < 1.0) 0.25 else if (quantity < 5.0) 0.5 else 1.0
                            onQuantityChange(quantity + step)
                        },
                        shape = CircleShape,
                        color = appColors.cardBackground,
                        border = BorderStroke(HairlineInkStroke, appColors.divider),
                        modifier = Modifier
                            .size(36.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Increase",
                                tint = appColors.textPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Unit Chips (Scrollable row)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(standardUnits) { u ->
                    val isSelected = unit == u
                    Surface(
                        onClick = {
                            haptics.lightTick()
                            onUnitChange(u)
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) appColors.accentContainer else appColors.cardBackground,
                        border = BorderStroke(
                            HairlineInkStroke,
                            if (isSelected) appColors.accent else appColors.divider
                        ),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 36.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = u,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) appColors.accent else appColors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
