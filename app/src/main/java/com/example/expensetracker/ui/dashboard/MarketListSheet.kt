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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
    activeMarketLists: List<MarketListWithItems> = emptyList(),
    currencySymbol: String = "৳",
    isBangla: Boolean = true,
    onDismiss: () -> Unit,
    onCreateListWithBulk: (title: String, items: List<MarketItem>) -> Unit,
    onAddItem: (item: MarketItem, targetListId: Long?) -> Unit = { item, _ -> },
    onUpdateItem: (item: MarketItem) -> Unit,
    onDeleteItem: (itemId: Long) -> Unit,
    onMarkPurchased: (item: MarketItem, amount: Double) -> Unit,
    onUnmarkPurchased: (item: MarketItem) -> Unit,
    onCompleteList: (listId: Long) -> Unit,
    onDeleteList: ((Long) -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    // 0: Checklist/Shopping Hub, 1: Create New List
    var selectedTab by remember { mutableIntStateOf(0) }

    // Consolidated active lists
    val effectiveActiveLists = remember(activeMarketLists, marketListWithItems) {
        if (activeMarketLists.isNotEmpty()) {
            activeMarketLists
        } else if (marketListWithItems != null) {
            listOf(marketListWithItems)
        } else {
            emptyList()
        }
    }

    // Which specific list is currently open in Shopping Mode
    // Default to the first list if only 1 exists, else null (hub view)
    var selectedListId by remember(effectiveActiveLists.size) {
        mutableStateOf<Long?>(
            if (effectiveActiveLists.size == 1) effectiveActiveLists.first().list.id else null
        )
    }

    val currentViewingList = remember(effectiveActiveLists, selectedListId) {
        effectiveActiveLists.find { it.list.id == selectedListId }
            ?: (if (effectiveActiveLists.size == 1) effectiveActiveLists.first() else null)
    }

    var itemForUnitDialog by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountForItem by remember { mutableStateOf<MarketItem?>(null) }
    var itemToDeleteConfirm by remember { mutableStateOf<MarketItem?>(null) }
    var listToDeleteConfirmId by remember { mutableStateOf<Long?>(null) }
    var showDeleteListConfirmDialog by remember { mutableStateOf(false) }

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
            // Header
            MarketSheetHeader(
                isBangla = isBangla,
                createdAt = currentViewingList?.list?.createdAt,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onDismiss = onDismiss,
                onDeleteList = if (currentViewingList != null && onDeleteList != null) {
                    { listToDeleteConfirmId = currentViewingList.list.id }
                } else null,
                currentListTitle = currentViewingList?.list?.title,
                activeListsCount = effectiveActiveLists.size,
                onBackToLists = if (effectiveActiveLists.size > 1 && currentViewingList != null) {
                    { selectedListId = null }
                } else null
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 1) {
                // TAB 1: Create New Market List (WhatsApp Paste or Manual)
                CreateMarketListSection(
                    isBangla = isBangla,
                    onListCreated = { title, items ->
                        onCreateListWithBulk(title, items)
                        selectedTab = 0
                        // Next recomposition will pick up the new list
                    }
                )
            } else {
                // TAB 0: Active Lists Directory OR Single List Shopping View
                if (effectiveActiveLists.isEmpty()) {
                    // Empty state: No active lists
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBangla) "বর্তমানে কোনো বাজার ফর্দ সক্রিয় নেই" else "No active market list",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBangla)
                                "নতুন ফর্দ তৈরি করতে উপরের 'নতুন ফর্দ তৈরি' ট্যাবে যান অথবা নিচের বাটনে চাপ দিন"
                            else
                                "Tap below to create a morning, evening, or weekly market list",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                haptics.mediumClick()
                                selectedTab = 1
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                        ) {
                            Text(text = if (isBangla) "➕ নতুন ফর্দ তৈরি করুন" else "➕ Create New List")
                        }
                    }
                } else if (effectiveActiveLists.size > 1 && currentViewingList == null) {
                    // Multi-List Hub View (Choose which list to shop from)
                    ActiveMarketListsHubView(
                        activeLists = effectiveActiveLists,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla,
                        onSelectList = { listWithItems ->
                            selectedListId = listWithItems.list.id
                        },
                        onDeleteList = { listId ->
                            listToDeleteConfirmId = listId
                        },
                        onCreateNewClick = {
                            selectedTab = 1
                        }
                    )
                } else if (currentViewingList != null) {
                    // Shopping View for the selected list
                    val items = currentViewingList.items
                    val totalCount = items.size
                    val purchasedCount = items.count { it.isPurchased }
                    val totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
                    var filterMode by remember { mutableIntStateOf(0) } // 0: All, 1: Remaining, 2: Done

                    val filteredItems = remember(items, filterMode) {
                        when (filterMode) {
                            1 -> items.filter { !it.isPurchased }
                            2 -> items.filter { it.isPurchased }
                            else -> items
                        }
                    }

                    // Summary Section
                    MarketSummarySection(
                        totalCount = totalCount,
                        purchasedCount = purchasedCount,
                        totalSpent = totalSpent,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla
                    )

                    if (items.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Filter Pills: All | Remaining | Done
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

                            // Filter 2: Done
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

                    // Items List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                        text = if (isBangla) "এই ফর্দে এখনো কোনো পণ্য যোগ করা হয়নি" else "No items in this list yet",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.5.sp
                                        ),
                                        color = appColors.textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isBangla) "নিচে পণ্যের নাম লিখে সহজে যোগ করুন" else "Add items using the field below",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = appColors.textSecondary.copy(alpha = 0.7f)
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
                                onRowClick = {
                                    if (item.isPurchased) {
                                        // Edit price or unmark
                                        promptAmountForItem = item
                                    } else {
                                        // Record price & mark purchased
                                        promptAmountForItem = item
                                    }
                                },
                                onTickClick = {
                                    if (item.isPurchased) {
                                        haptics.lightTick()
                                        onUnmarkPurchased(item)
                                    } else {
                                        promptAmountForItem = item
                                    }
                                },
                                onRequestDelete = {
                                    itemToDeleteConfirm = item
                                }
                            )
                        }

                        // Add item row at the end of the list
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            MarketAddNewItemRow(
                                listId = currentViewingList.list.id,
                                isBangla = isBangla,
                                onAddItem = { newItem ->
                                    onAddItem(newItem, currentViewingList.list.id)
                                },
                                existingItemNames = items.map { it.name.lowercase() }.toSet()
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Finish Trip Button
                    if (items.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketFinishButton(
                            isBangla = isBangla,
                            onComplete = {
                                onCompleteList(currentViewingList.list.id)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Dialog 1: Unit & Quantity Dialog
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

    // Dialog 2: Clean Popup to Enter Price when item is purchased
    if (promptAmountForItem != null) {
        val targetItem = promptAmountForItem!!
        MarketPromptAmountDialog(
            item = targetItem,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            onDismiss = { promptAmountForItem = null },
            onConfirm = { amt ->
                if (amt > 0) {
                    onMarkPurchased(targetItem, amt)
                } else {
                    onMarkPurchased(targetItem, 0.0)
                }
                promptAmountForItem = null
            }
        )
    }

    // Dialog 3: Swipe to Delete Item Confirmation Dialog
    if (itemToDeleteConfirm != null) {
        val itemToDelete = itemToDeleteConfirm!!
        AlertDialog(
            onDismissRequest = { itemToDeleteConfirm = null },
            title = {
                Text(
                    text = if (isBangla) "পণ্য মুছে ফেলতে চান?" else "Delete Item?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
            },
            text = {
                Text(
                    text = if (isBangla)
                        "'${itemToDelete.name}' (${itemToDelete.formatQuantity()}) পণ্যটি কি নিশ্চিতভাবে এই ফর্দ থেকে মুছে ফেলতে চান?"
                    else
                        "Are you sure you want to delete '${itemToDelete.name}' (${itemToDelete.formatQuantity()}) from this list?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        haptics.lightTick()
                        onDeleteItem(itemToDelete.id)
                        itemToDeleteConfirm = null
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
                TextButton(onClick = { itemToDeleteConfirm = null }) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }

    // Dialog 4: Delete Entire List Confirmation Dialog
    if (listToDeleteConfirmId != null) {
        val listId = listToDeleteConfirmId!!
        AlertDialog(
            onDismissRequest = { listToDeleteConfirmId = null },
            title = {
                Text(
                    text = if (isBangla) "বাজারের ফর্দ মুছে ফেলবেন?" else "Delete Market List?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
            },
            text = {
                Text(
                    text = if (isBangla) "এই বাজারের ফর্দটি এবং এর সকল পণ্য স্থায়ীভাবে মুছে ফেলা হবে। আপনি কি নিশ্চিত?"
                           else "This market list and all its items will be permanently deleted. Are you sure?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        haptics.warningFeedback()
                        if (onDeleteList != null) {
                            onDeleteList(listId)
                        }
                        listToDeleteConfirmId = null
                        selectedListId = null
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
                TextButton(onClick = { listToDeleteConfirmId = null }) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }
}
