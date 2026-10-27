package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Hub view showing all active market lists when multiple exist.
 * Allows user to choose which list they want to access/shop from.
 */
@Composable
fun ActiveMarketListsHubView(
    activeLists: List<MarketListWithItems>,
    currencySymbol: String,
    isBangla: Boolean,
    onSelectList: (MarketListWithItems) -> Unit,
    onDeleteList: (Long) -> Unit,
    onCreateNewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla)
                        "চলমান ফর্দসমূহ (${convertToBengaliDigits(activeLists.size.toString())}টি সক্রিয়)"
                    else
                        "Active Market Lists (${activeLists.size} active)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = appColors.textPrimary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.accent.copy(alpha = 0.12f),
                    modifier = Modifier.clickable {
                        haptics.lightTick()
                        onCreateNewClick()
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = appColors.accent, modifier = Modifier.size(14.dp))
                        Text(
                            text = if (isBangla) "নতুন ফর্দ" else "New List",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = appColors.accent
                        )
                    }
                }
            }
        }

        items(activeLists, key = { it.list.id }) { listWithItems ->
            val list = listWithItems.list
            val totalItems = listWithItems.totalItems
            val purchasedItems = listWithItems.purchasedItems
            val totalSpent = listWithItems.totalSpent
            val progress = listWithItems.progress

            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        haptics.mediumClick()
                        onSelectList(listWithItems)
                    },
                padding = 14.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
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
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = list.title.ifBlank { if (isBangla) "বাজারের ফর্দ" else "Market Checklist" },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp
                                    ),
                                    color = appColors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                val dateText = DateTimeHelper.formatDayGroup(list.createdAt, isBangla)
                                Text(
                                    text = dateText,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = appColors.textSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                haptics.lightTick()
                                onDeleteList(list.id)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete list",
                                tint = appColors.expense.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
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

                    // Stats and Open Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statText = if (isBangla) {
                            "${convertToBengaliDigits(purchasedItems.toString())}/${convertToBengaliDigits(totalItems.toString())} সম্পন্ন · খরচ: ${formatCurrency(totalSpent, currencySymbol)}"
                        } else {
                            "$purchasedItems/$totalItems done · Spent: ${formatCurrency(totalSpent, currencySymbol)}"
                        }

                        Text(
                            text = statText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.textSecondary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = if (isBangla) "ফর্দ দেখুন" else "Open List",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = appColors.accent
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
