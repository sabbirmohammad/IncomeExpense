package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.UnitAndQuantityDialog
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketListSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    marketListWithItems: MarketListWithItems?,
    currencySymbol: String = "৳",
    isBangla: Boolean = true,
    onDismiss: () -> Unit,
    onCreateListWithBulk: (title: String, items: List<MarketItem>) -> Unit,
    onAddItem: (item: MarketItem) -> Unit,
    onUpdateItem: (item: MarketItem) -> Unit,
    onDeleteItem: (itemId: Long) -> Unit,
    onMarkPurchased: (item: MarketItem, amount: Double) -> Unit,
    onUnmarkPurchased: (item: MarketItem) -> Unit,
    onCompleteList: (listId: Long) -> Unit,
    onDeleteList: ((Long) -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var selectedTab by remember { mutableIntStateOf(0) }

    var itemForUnitDialog by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountForItem by remember { mutableStateOf<MarketItem?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var showDeleteListConfirmDialog by remember { mutableStateOf(false) }

    val items = marketListWithItems?.items ?: emptyList()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LocalAppColors.current.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(LocalAppColors.current.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp)
        ) {
            MarketSheetHeader(
                isBangla = isBangla,
                createdAt = marketListWithItems?.list?.createdAt,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onDismiss = onDismiss,
                onDeleteList = if (items.isNotEmpty() && onDeleteList != null) { { showDeleteListConfirmDialog = true } } else null
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 1) {
                MarketBulkPasteSection(
                    isBangla = isBangla,
                    onListImported = { title, items ->
                        onCreateListWithBulk(title, items)
                        selectedTab = 0
                    }
                )
            } else {
                val totalCount = items.size
                val purchasedCount = items.count { it.isPurchased }
                val totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
                var filterMode by remember { mutableIntStateOf(0) }

                val filteredItems = remember(items, filterMode) {
                    when (filterMode) {
                        1 -> items.filter { !it.isPurchased }
                        2 -> items.filter { it.isPurchased }
                        else -> items
                    }
                }

                MarketSummarySection(
                    totalCount = totalCount,
                    purchasedCount = purchasedCount,
                    totalSpent = totalSpent,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla
                )

                if (items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val remainingCount = totalCount - purchasedCount

                        // Filter 0: All
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (filterMode == 0) appColors.accent.copy(alpha = 0.15f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (filterMode == 0) appColors.accent else appColors.border),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    haptics.lightTick()
                                    filterMode = 0
                                }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBangla) "সব (${convertToBengaliDigits(totalCount.toString())})" else "All ($totalCount)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = if (filterMode == 0) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (filterMode == 0) appColors.accent else appColors.textSecondary
                                )
                            }
                        }

                        // Filter 1: Remaining
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (filterMode == 1) appColors.warning.copy(alpha = 0.15f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (filterMode == 1) appColors.warning else appColors.border),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    haptics.lightTick()
                                    filterMode = 1
                                }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBangla) "বাকি (${convertToBengaliDigits(remainingCount.toString())})" else "Remaining ($remainingCount)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = if (filterMode == 1) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (filterMode == 1) appColors.warning else appColors.textSecondary
                                )
                            }
                        }

                        // Filter 2: Purchased
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (filterMode == 2) appColors.income.copy(alpha = 0.15f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (filterMode == 2) appColors.income else appColors.border),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    haptics.lightTick()
                                    filterMode = 2
                                }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBangla) "কেনা শেষ (${convertToBengaliDigits(purchasedCount.toString())})" else "Done ($purchasedCount)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = if (filterMode == 2) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (filterMode == 2) appColors.income else appColors.textSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (items.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isBangla) "বর্তমানে কোনো বাজার ফর্দ সক্রিয় নেই" else "No active market list",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.5.sp
                                    ),
                                    color = appColors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isBangla) "নিচে পণ্যের নাম লিখুন অথবা সাজেস্ট করা পণ্যে ট্যাপ করে দ্রুত ফর্দ তৈরি করুন" else "Type item name below or tap suggested items to start",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = appColors.textSecondary.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else if (filteredItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (filterMode == 1) {
                                        if (isBangla) "সব পণ্য কেনা সম্পন্ন হয়েছে! 🎉" else "All items purchased! 🎉"
                                    } else {
                                        if (isBangla) "এখনো কোনো পণ্য কেনা হয়নি" else "No items purchased yet"
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.5.sp
                                    ),
                                    color = appColors.textSecondary
                                )
                            }
                        }
                    }

                    itemsIndexed(filteredItems, key = { _, itm -> itm.id }) { _, item ->
                        MarketItemRow(
                            item = item,
                            currencySymbol = currencySymbol,
                            isBangla = isBangla,
                            onUnitClick = { itemForUnitDialog = item },
                            onAmountChanged = { newAmt ->
                                onUpdateItem(item.copy(amount = newAmt))
                            },
                            onTickClick = {
                                if (item.isPurchased) {
                                    haptics.lightTick()
                                    onUnmarkPurchased(item)
                                } else {
                                    if (item.amount != null && item.amount > 0) {
                                        haptics.mediumClick()
                                        onMarkPurchased(item, item.amount)
                                    } else {
                                        promptAmountForItem = item
                                    }
                                }
                            },
                            onDeleteClick = {
                                haptics.lightTick()
                                onDeleteItem(item.id)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        MarketAddNewItemRow(
                            listId = marketListWithItems?.list?.id ?: 0L,
                            isBangla = isBangla,
                            onAddItem = onAddItem,
                            existingItemNames = items.map { it.name.lowercase() }.toSet()
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    MarketFinishButton(
                        isBangla = isBangla,
                        onComplete = {
                            marketListWithItems?.list?.id?.let { onCompleteList(it) }
                            onDismiss()
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (itemForUnitDialog != null) {
        val curItem = itemForUnitDialog!!
        UnitAndQuantityDialog(
            initialQuantity = curItem.quantity,
            initialUnit = curItem.unit,
            isBangla = isBangla,
            onDismiss = { itemForUnitDialog = null },
            onConfirm = { qty, unit ->
                onUpdateItem(curItem.copy(quantity = qty, unit = unit))
                itemForUnitDialog = null
            }
        )
    }

    if (promptAmountForItem != null) {
        MarketPromptAmountDialog(
            item = promptAmountForItem!!,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            onDismiss = { promptAmountForItem = null },
            onConfirm = { amt ->
                onMarkPurchased(promptAmountForItem!!, amt)
                promptAmountForItem = null
            }
        )
    }

    if (showDeleteListConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteListConfirmDialog = false },
            title = {
                Text(
                    text = if (isBangla) "বাজারের ফর্দ মুছে ফেলবেন?" else "Delete Market List?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
            },
            text = {
                Text(
                    text = if (isBangla) "চলমান বাজারের ফর্দটি এবং এর সকল পণ্য স্থায়ীভাবে মুছে ফেলা হবে। আপনি কি নিশ্চিত?"
                           else "The active market list and all its items will be permanently deleted. Are you sure?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteListConfirmDialog = false
                        haptics.warningFeedback()
                        val listId = marketListWithItems?.list?.id
                        if (listId != null && onDeleteList != null) {
                            onDeleteList(listId)
                        }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isBangla) "মুছে ফেলুন" else "Delete",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteListConfirmDialog = false }) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }
}
