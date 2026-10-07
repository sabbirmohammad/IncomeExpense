package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors

/**
 * Empty / Invitation Card on Dashboard when there's no active market list.
 */
@Composable
fun CreateMarketListPromptCard(
    isBangla: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()
    AppCard(
        modifier = modifier.clickable {
            haptics.mediumClick()
            onClick()
        },
        padding = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2EC4B6).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        tint = Color(0xFF2EC4B6),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = if (isBangla) "বাজার লিস্ট তৈরি করুন" else "Create Market List",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2EC4B6))
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "তৈরি করুন" else "Create",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

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
                            color = ColorTextPrimary
                        )
                        val subText = if (isBangla) {
                            "${convertToBengaliDigits(purchasedItems.toString())}/${convertToBengaliDigits(totalItems.toString())} সম্পন্ন · খরচ: ${formatCurrency(totalSpent, currencySymbol)}"
                        } else {
                            "$purchasedItems/$totalItems bought · Spent: ${formatCurrency(totalSpent, currencySymbol)}"
                        }
                        Text(
                            text = subText,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ColorTextSecondary
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
                        color = ColorAccent
                    )
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = ColorAccent,
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
                color = ColorSuccessIncome,
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
                    color = ColorAccent,
                    modifier = Modifier
                        .clickable { onOpenFullList() }
                        .padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        haptics.lightTick()
                        onOpenFullList()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, LocalAppColors.current.border)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (isBangla) "পণ্য যোগ / এডিট" else "Add / Edit",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                            color = ColorTextPrimary
                        )
                    }
                }

                Button(
                    onClick = {
                        haptics.mediumClick()
                        onCompleteList(marketListWithItems.list.id)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorSuccessIncome)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (isBangla) "বাজার শেষ" else "Finish",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardMarketItemRow(
    item: MarketItem,
    currencySymbol: String,
    onAmountChanged: (Double?) -> Unit,
    onMarkPurchased: (Double) -> Unit,
    onUnmarkPurchased: () -> Unit,
    onRowClick: () -> Unit
) {
    val haptics = rememberAppHaptics()
    var inlineAmountText by remember(item.amount) {
        mutableStateOf(item.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (item.isPurchased) ColorSuccessIncome.copy(alpha = 0.05f) else LocalAppColors.current.background,
        border = BorderStroke(
            1.dp,
            if (item.isPurchased) ColorSuccessIncome.copy(alpha = 0.25f) else LocalAppColors.current.border
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Tick / Check circle
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.isPurchased) ColorSuccessIncome else LocalAppColors.current.surface
                        )
                        .border(
                            width = 1.dp,
                            color = if (item.isPurchased) ColorSuccessIncome else LocalAppColors.current.border,
                            shape = CircleShape
                        )
                        .clickable {
                            if (item.isPurchased) {
                                haptics.lightTick()
                                onUnmarkPurchased()
                            } else {
                                val cleanNum = MarketListParser.convertBengaliDigitsToEnglish(inlineAmountText)
                                val amt = cleanNum.toDoubleOrNull()
                                if (amt != null && amt > 0) {
                                    haptics.mediumClick()
                                    onMarkPurchased(amt)
                                } else {
                                    onRowClick()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isPurchased) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Name & Qty
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (item.isPurchased) ColorTextSecondary else ColorTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Qty badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (item.isPurchased) LocalAppColors.current.surface else ColorAccent.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = item.formatQuantity(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (item.isPurchased) ColorTextSecondary else ColorAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Price display or entry
            if (item.isPurchased) {
                Text(
                    text = formatCurrency(item.amount ?: 0.0, currencySymbol),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ColorSuccessIncome
                )
            } else {
                OutlinedTextField(
                    value = inlineAmountText,
                    onValueChange = {
                        inlineAmountText = it
                        val cleanNum = MarketListParser.convertBengaliDigitsToEnglish(it)
                        val amt = cleanNum.toDoubleOrNull()
                        onAmountChanged(amt)
                    },
                    placeholder = { Text(currencySymbol, fontSize = 11.sp, color = ColorTextSecondary) },
                    modifier = Modifier.width(72.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorAccent,
                        unfocusedBorderColor = LocalAppColors.current.border,
                        focusedContainerColor = LocalAppColors.current.surface,
                        unfocusedContainerColor = LocalAppColors.current.surface
                    )
                )
            }
        }
    }
}
