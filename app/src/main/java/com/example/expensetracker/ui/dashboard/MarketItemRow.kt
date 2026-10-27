package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Clean & modern Market Checklist Item Card:
 * - Shows: Circular Checkbox, Item Name, Quantity Badge.
 * - Clicking row or checkbox triggers price entry or completion.
 * - Purchased items display the logged price in a green pill.
 * - Swipe left to delete with a safety confirmation dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketItemRow(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    onUnitClick: () -> Unit,
    onTickClick: () -> Unit,
    onRowClick: () -> Unit = onTickClick,
    onAmountChanged: (Double) -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onRequestDelete: () -> Unit = onDeleteClick,
    modifier: Modifier = Modifier,
    lastRateText: String? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                haptics.mediumClick()
                onRequestDelete()
                // Return false so item doesn't disappear until confirmed in dialog
                false
            } else {
                false
            }
        }
    )

    // Helper for amount input and price clear
    val handleAmountChange: (String) -> Unit = {
        if (it.isBlank()) {
            onAmountChanged(0.0)
        } else {
            val cleanNum = com.example.expensetracker.common.MarketListParser.convertBengaliDigitsToEnglish(it)
            cleanNum.toDoubleOrNull()?.let { amt -> onAmountChanged(amt) }
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            if (direction == SwipeToDismissBoxValue.EndToStart) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(appColors.expense)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isBangla) "মুছে ফেলুন" else "Delete",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (item.isPurchased) appColors.income.copy(alpha = 0.08f) else appColors.surface,
            border = BorderStroke(
                1.dp,
                if (item.isPurchased) appColors.income.copy(alpha = 0.25f) else appColors.border
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    haptics.lightTick()
                    onRowClick()
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Circular Checkbox
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .clickable {
                            haptics.lightTick()
                            onTickClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (item.isPurchased) appColors.income else Color.Transparent
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (item.isPurchased) appColors.income else appColors.border,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.isPurchased) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Purchased",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                // 2. Product Name & Quantity (Center Content)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.SemiBold,
                            textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (item.isPurchased) appColors.textSecondary else appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Quantity pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (item.isPurchased) appColors.background
                                    else appColors.accent.copy(alpha = 0.10f)
                                )
                                .clickable(enabled = !item.isPurchased) {
                                    haptics.lightTick()
                                    onUnitClick()
                                }
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.formatQuantity(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (item.isPurchased) appColors.textSecondary else appColors.accent
                            )
                            if (!item.isPurchased) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit quantity",
                                    tint = appColors.accent.copy(alpha = 0.8f),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }

                        if (!item.isPurchased && lastRateText != null) {
                            Text(
                                text = lastRateText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = appColors.textSecondary.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // 3. Right: Price badge (Only when purchased)
                if (item.isPurchased) {
                    val amt = item.amount ?: 0.0
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.income.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, appColors.income.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (amt > 0) formatCurrency(amt, currencySymbol) else (if (isBangla) "কেনা হয়েছে" else "Done"),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = appColors.income,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
