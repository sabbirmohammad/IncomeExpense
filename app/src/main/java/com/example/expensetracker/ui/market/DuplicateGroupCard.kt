package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.theme.WeightSemiBold
import com.example.expensetracker.ui.components.convertToBengaliDigits

/**
 * Individual Duplicate Group Card with radio selectors for canonical name.
 */
@Composable
fun DuplicateGroupCard(
    group: DuplicateCommodityGroup,
    chosenTarget: String,
    onSelectTarget: (String) -> Unit,
    onMergeClick: () -> Unit,
    isBangla: Boolean,
    currencySymbol: String
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "প্রমিত নাম নির্বাচন করুন:" else "Choose primary name:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = WeightSemiBold,
                        fontSize = 12.sp
                    ),
                    color = appColors.textSecondary
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = appColors.accent.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isBangla) "${convertToBengaliDigits(group.variants.size.toString())}টি সংস্করণ"
                        else "${group.variants.size} variants",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = WeightBold,
                            fontSize = 12.sp,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = appColors.accent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Variant Options with Radio Buttons
            group.variants.forEach { variant ->
                val isSelected = variant.name == chosenTarget
                val countText = if (isBangla) convertToBengaliDigits(variant.purchaseCount.toString()) else variant.purchaseCount.toString()
                val rateText = if (isBangla) convertToBengaliDigits(variant.currentPrice.toInt().toString()) else variant.currentPrice.toInt().toString()

                Surface(
                    onClick = { onSelectTarget(variant.name) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) appColors.accent.copy(alpha = 0.08f) else appColors.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(HairlineInkStroke, if (isSelected) appColors.accent.copy(alpha = 0.4f) else appColors.divider),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .defaultMinSize(minHeight = 48.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectTarget(variant.name) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = appColors.accent,
                                unselectedColor = appColors.textSecondary
                            ),
                            modifier = Modifier.size(36.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = variant.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) WeightBold else WeightMedium,
                                    fontSize = 14.sp
                                ),
                                color = appColors.textPrimary
                            )
                            Text(
                                text = if (isBangla) "$countText বার কেনা • দর $currencySymbol$rateText/${variant.defaultUnit}"
                                else "$countText logged • $currencySymbol$rateText/${variant.defaultUnit}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontFeatureSettings = TabularNumFeature
                                ),
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Preview and Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val totalPurchases = group.variants.sumOf { it.purchaseCount }
                val totalText = if (isBangla) convertToBengaliDigits(totalPurchases.toString()) else totalPurchases.toString()

                Text(
                    text = if (isBangla) "মার্জের পর: ${totalText}টি রেকর্ড থাকবে" else "Result: $totalPurchases total records",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontFeatureSettings = TabularNumFeature
                    ),
                    color = appColors.textSecondary
                )

                Button(
                    onClick = onMergeClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Text(
                        text = if (isBangla) "মার্জ করুন" else "Merge",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = WeightBold,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }
    }
}
