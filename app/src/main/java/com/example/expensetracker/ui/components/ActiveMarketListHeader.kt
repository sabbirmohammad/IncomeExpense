package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun ActiveMarketListHeader(
    marketListWithItems: MarketListWithItems,
    currencySymbol: String,
    isBangla: Boolean,
    onOpenFullList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val totalItems = marketListWithItems.totalItems
    val purchasedItems = marketListWithItems.purchasedItems
    val totalSpent = marketListWithItems.totalSpent

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2EC4B6).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = null,
                    tint = Color(0xFF2EC4B6),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                val activeTitle = remember(marketListWithItems.list.title, marketListWithItems.list.createdAt, isBangla) {
                    if (marketListWithItems.list.title.isNotBlank() &&
                        marketListWithItems.list.title != "বাজারের ফর্দ" &&
                        marketListWithItems.list.title != "Market Checklist" &&
                        marketListWithItems.list.title != "আজকের বাজার"
                    ) {
                        "🛒 " + marketListWithItems.list.title
                    } else {
                        "🛒 " + DateTimeHelper.formatMarketTripTitle(marketListWithItems.list.createdAt, isBangla)
                    }
                }
                Text(
                    text = activeTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
                val subText = if (isBangla) {
                    "${convertToBengaliDigits(purchasedItems.toString())}/${convertToBengaliDigits(totalItems.toString())} সম্পন্ন · খরচ: ${formatCurrency(totalSpent, currencySymbol)}"
                } else {
                    "$purchasedItems/$totalItems bought · Spent: ${formatCurrency(totalSpent, currencySymbol)}"
                }
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.textSecondary
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .defaultMinSize(minHeight = 36.dp)
                .clickable {
                    haptics.lightTick()
                    onOpenFullList()
                }
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isBangla) "সব দেখুন" else "View All",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = appColors.accent
            )
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = appColors.accent,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
