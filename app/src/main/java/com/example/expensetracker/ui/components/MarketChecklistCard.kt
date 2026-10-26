package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.LocalAppColors

/**
 * Active Bazaar / Market Checklist Card on the Dashboard.
 */
@Composable
fun ActiveMarketListDashboardCard(
    marketListWithItems: MarketListWithItems,
    currencySymbol: String,
    isBangla: Boolean,
    onOpenFullList: () -> Unit,
    onMarkPurchased: (item: MarketItem, amount: Double) -> Unit,
    onUnmarkPurchased: (item: MarketItem) -> Unit,
    onCompleteList: (listId: Long) -> Unit,
    onUpdateItemAmount: (item: MarketItem, amount: Double?) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val totalItems = marketListWithItems.totalItems
    val purchasedItems = marketListWithItems.purchasedItems
    val totalSpent = marketListWithItems.totalSpent
    val progress = marketListWithItems.progress
    val displayItems = marketListWithItems.items.take(4) // Show top 4 items on dashboard

    AppCard(
        modifier = modifier,
        padding = 14.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        val activeTitle = remember(marketListWithItems.list.createdAt, isBangla) {
                            "🛒 " + DateTimeHelper.formatMarketTripTitle(marketListWithItems.list.createdAt, isBangla)
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
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = appColors.textSecondary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        haptics.lightTick()
                        onOpenFullList()
                    }
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

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = appColors.income,
                trackColor = LocalAppColors.current.border
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Items List (First 4 items)
            displayItems.forEachIndexed { index, item ->
                DashboardMarketItemRow(
                    item = item,
                    currencySymbol = currencySymbol,
                    onAmountChanged = { amt -> onUpdateItemAmount(item, amt) },
                    onMarkPurchased = { amt -> onMarkPurchased(item, amt) },
                    onUnmarkPurchased = { onUnmarkPurchased(item) },
                    onRowClick = onOpenFullList
                )
                if (index < displayItems.lastIndex) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            if (totalItems > 4) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isBangla) "+ আরও ${convertToBengaliDigits((totalItems - 4).toString())}টি পণ্য তালিকায় আছে..."
                    else "+ ${totalItems - 4} more items in list...",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = appColors.accent,
                    modifier = Modifier
                        .clickable { onOpenFullList() }
                        .padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Footer
            DashboardMarketActionButtons(
                isBangla = isBangla,
                onOpenFullList = onOpenFullList,
                onCompleteList = { onCompleteList(marketListWithItems.list.id) }
            )
        }
    }
}
