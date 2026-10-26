package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun MarketTripCardHeader(
    displayTitle: String,
    dateText: String,
    totalSpent: Double,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val marketTeal = if (appColors.isDark) Color(0xFF2EC4B6) else Color(0xFF0F766E)

    Row(
        modifier = modifier.fillMaxWidth(),
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
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(marketTeal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = null,
                    tint = marketTeal,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = appColors.textSecondary
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (isBangla) "মোট বাজার খরচ" else "Total Spent",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = appColors.textMuted
            )
            Text(
                text = formatCurrency(totalSpent, currencySymbol),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = appColors.income
            )
        }
    }
}

@Composable
fun MarketTripCardActionsRow(
    purchasedItems: Int,
    totalItems: Int,
    isBangla: Boolean,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    showOptionsMenu: Boolean,
    onShowOptionsMenu: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val marketTeal = if (appColors.isDark) Color(0xFF2EC4B6) else Color(0xFF0F766E)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isAllCompleted = purchasedItems == totalItems && totalItems > 0
        val countText = if (isBangla) {
            if (isAllCompleted) {
                "✓ ${convertToBengaliDigits(purchasedItems.toString())}/${convertToBengaliDigits(totalItems.toString())}টি সম্পন্ন"
            } else {
                "${convertToBengaliDigits(purchasedItems.toString())}/${convertToBengaliDigits(totalItems.toString())}টি কেনা হয়েছে"
            }
        } else {
            if (isAllCompleted) "✓ All $totalItems completed" else "$purchasedItems/$totalItems purchased"
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isAllCompleted) appColors.income.copy(alpha = 0.12f) else marketTeal.copy(alpha = 0.12f),
            border = BorderStroke(
                1.dp,
                if (isAllCompleted) appColors.income.copy(alpha = 0.35f) else marketTeal.copy(alpha = 0.35f)
            )
        ) {
            Text(
                text = countText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isAllCompleted) appColors.income else marketTeal,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = appColors.accent.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.25f)),
                modifier = Modifier.clickable(onClick = onToggleExpand)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (expanded) (if (isBangla) "সংক্ষেপ" else "Collapse") else (if (isBangla) "বিস্তারিত" else "Details"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = appColors.accent
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Box {
                IconButton(
                    onClick = { onShowOptionsMenu(true) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Options",
                        tint = appColors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showOptionsMenu,
                    onDismissRequest = { onShowOptionsMenu(false) },
                    modifier = Modifier.background(appColors.surface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isBangla) "বাজার ফর্দ মুছে ফেলুন" else "Delete Market List",
                                color = appColors.expense,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = appColors.expense,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            onShowOptionsMenu(false)
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}
