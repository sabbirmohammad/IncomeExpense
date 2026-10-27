package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun DashboardMarketItemRow(
    item: MarketItem,
    currencySymbol: String,
    onAmountChanged: (Double?) -> Unit = {},
    onMarkPurchased: (Double) -> Unit = {},
    onUnmarkPurchased: () -> Unit = {},
    onRowClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (item.isPurchased) appColors.income.copy(alpha = 0.05f) else LocalAppColors.current.background,
        border = BorderStroke(
            1.dp,
            if (item.isPurchased) appColors.income.copy(alpha = 0.25f) else LocalAppColors.current.border
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptics.lightTick()
                onRowClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Circular Checkbox
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.isPurchased) appColors.income else LocalAppColors.current.surface
                        )
                        .border(
                            width = 1.2.dp,
                            color = if (item.isPurchased) appColors.income else LocalAppColors.current.border,
                            shape = CircleShape
                        )
                        .clickable {
                            haptics.lightTick()
                            if (item.isPurchased) {
                                onUnmarkPurchased()
                            } else {
                                onRowClick()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isPurchased) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Name
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (item.isPurchased) appColors.textSecondary else appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Quantity badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (item.isPurchased) LocalAppColors.current.surface else appColors.accent.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = item.formatQuantity(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (item.isPurchased) appColors.textSecondary else appColors.accent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Price badge (only when purchased)
            if (item.isPurchased) {
                val amt = item.amount ?: 0.0
                Text(
                    text = if (amt > 0) formatCurrency(amt, currencySymbol) else "✓",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.income
                )
            }
        }
    }
}
