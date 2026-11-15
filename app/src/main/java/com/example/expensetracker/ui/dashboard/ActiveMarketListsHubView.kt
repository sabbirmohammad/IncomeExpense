package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
            ActiveMarketListsHubHeader(
                activeCount = activeLists.size,
                isBangla = isBangla,
                onCreateNewClick = onCreateNewClick
            )
        }

        items(activeLists, key = { it.list.id }) { listWithItems ->
            val list = listWithItems.list
            val totalItems = listWithItems.totalItems
            val purchasedItems = listWithItems.purchasedItems
            val totalSpent = listWithItems.totalSpent
            val progress = listWithItems.progress
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "hubListProgress"
            )

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
                            modifier = Modifier.size(44.dp)
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
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = appColors.income,
                        trackColor = LocalAppColors.current.border
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ActiveMarketListStatsRow(
                        purchasedItems = purchasedItems,
                        totalItems = totalItems,
                        totalSpent = totalSpent,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla
                    )
                }
            }
        }
    }
}
