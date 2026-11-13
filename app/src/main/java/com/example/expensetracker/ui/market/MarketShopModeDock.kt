package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketShopModeDock(
    animatedTotalSpent: Float,
    totalSpent: Double,
    currencySymbol: String,
    isBangla: Boolean,
    isOverspent: Boolean,
    estimatedBudget: Double,
    purchasedCount: Int,
    totalCount: Int,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    Surface(
        color = if (isOverspent) appColors.warning.copy(alpha = 0.14f) else appColors.cardBackground,
        border = BorderStroke(
            HairlineInkStroke,
            if (isOverspent) appColors.warning else appColors.divider
        ),
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Column: Total count-up + Bought ratio + Est comparison
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val totalSpentFormatted = formatCurrency(animatedTotalSpent.toDouble(), currencySymbol)
                Text(
                    text = "${strings.total} $totalSpentFormatted",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = if (isOverspent) appColors.warning else appColors.textPrimary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val pCount = if (isBangla) convertToBengaliDigits(purchasedCount.toString()) else purchasedCount.toString()
                    val tCount = if (isBangla) convertToBengaliDigits(totalCount.toString()) else totalCount.toString()
                    Text(
                        text = "$pCount/$tCount ${strings.marketBought}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.textSecondary
                    )

                    if (isOverspent) {
                        val overDiff = totalSpent - estimatedBudget
                        val overStr = formatCurrency(overDiff, currencySymbol)
                        Text(
                            text = "• ⚠️ +$overStr",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.warning
                        )
                    } else if (estimatedBudget > 0) {
                        val estStr = formatCurrency(estimatedBudget, currencySymbol)
                        Text(
                            text = "• ${strings.marketEstimated}: $estStr",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.textMuted
                        )
                    }
                }
            }

            // Right Column: Primary "Finish ✓" Button
            Button(
                onClick = {
                    haptics.mediumClick()
                    onFinishClick()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = strings.marketFinish,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
