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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
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
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ColorTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = ColorTextSecondary
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (isBangla) "মোট বাজার খরচ" else "Total Spent",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = ColorTextMuted
            )
            Text(
                text = formatCurrency(totalSpent, currencySymbol),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ColorSuccessIncome
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
            color = if (isAllCompleted) ColorSuccessIncome.copy(alpha = 0.12f) else Color(0xFF2EC4B6).copy(alpha = 0.12f),
            border = BorderStroke(
                1.dp,
                if (isAllCompleted) ColorSuccessIncome.copy(alpha = 0.35f) else Color(0xFF2EC4B6).copy(alpha = 0.35f)
            )
        ) {
            Text(
                text = countText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isAllCompleted) ColorSuccessIncome else Color(0xFF2EC4B6),
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ColorAccent.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.25f)),
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
                        color = ColorAccent
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Box {
                IconButton(
                    onClick = { onShowOptionsMenu(true) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Options",
                        tint = ColorTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showOptionsMenu,
                    onDismissRequest = { onShowOptionsMenu(false) },
                    modifier = Modifier.background(ColorSurface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isBangla) "বাজার ফর্দ মুছে ফেলুন" else "Delete Market List",
                                color = ColorDangerExpense,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = ColorDangerExpense,
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
