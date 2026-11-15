package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketSheetHeader(
    isBangla: Boolean,
    createdAt: Long? = null,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteList: (() -> Unit)? = null,
    currentListTitle: String? = null,
    activeListsCount: Int = 0,
    onBackToLists: (() -> Unit)? = null,
    onOpenPriceInsights: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (onBackToLists != null) {
                    IconButton(
                        onClick = {
                            haptics.lightTick()
                            onBackToLists()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to lists",
                            tint = appColors.textPrimary
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
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
                }

                Column(modifier = Modifier.weight(1f)) {
                    val displayTitle = currentListTitle?.takeIf { it.isNotBlank() }
                        ?: (if (isBangla) "বাজারের ফর্দ" else "Market Checklist")
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val listDate = createdAt ?: System.currentTimeMillis()
                    val syncedDateText = remember(listDate, isBangla) {
                        DateTimeHelper.formatDayGroup(listDate, isBangla)
                    }
                    Text(
                        text = syncedDateText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.accent
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (onOpenPriceInsights != null) {
                    IconButton(onClick = onOpenPriceInsights) {
                        Icon(
                            imageVector = Icons.Outlined.TrendingUp,
                            contentDescription = if (isBangla) "দরদাম ও ট্রেন্ড" else "Price Insights",
                            tint = appColors.accent
                        )
                    }
                }

                if (onDeleteList != null) {
                    IconButton(onClick = onDeleteList) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = if (isBangla) "ফর্দ মুছে ফেলুন" else "Delete List",
                            tint = appColors.expense.copy(alpha = 0.85f)
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = appColors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs: [ 0: আমার ফর্দসমূহ / চলমান ] [ 1: নতুন ফর্দ তৈরি ]
        MarketSheetTabsRow(
            isBangla = isBangla,
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            activeListsCount = activeListsCount,
            haptics = haptics
        )
    }
}
