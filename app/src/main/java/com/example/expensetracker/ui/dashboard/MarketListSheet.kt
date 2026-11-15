package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.LocalAppColors
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
    lastPurchasedRates: Map<String, Pair<Double, String>> = emptyMap(),
    productPriceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    onFetchPriceHistory: ((productName: String) -> List<ProductPriceRecord>)? = null,
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
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPriceInsightsSheet by remember { mutableStateOf(false) }

    val effectiveActiveLists = remember(activeMarketLists, marketListWithItems) {
        if (activeMarketLists.isNotEmpty()) activeMarketLists else if (marketListWithItems != null) listOf(marketListWithItems) else emptyList()
    }
    var selectedListId by remember(effectiveActiveLists.size) {
        mutableStateOf<Long?>(if (effectiveActiveLists.size == 1) effectiveActiveLists.first().list.id else null)
    }
    val currentViewingList = remember(effectiveActiveLists, selectedListId) {
        effectiveActiveLists.find { it.list.id == selectedListId } ?: (if (effectiveActiveLists.size == 1) effectiveActiveLists.first() else null)
    }

    var itemForUnitDialog by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountForItem by remember { mutableStateOf<MarketItem?>(null) }
    var itemToDeleteConfirm by remember { mutableStateOf<MarketItem?>(null) }
    var itemToSubstitute by remember { mutableStateOf<MarketItem?>(null) }
    var showForgotStaplesDialog by remember { mutableStateOf(false) }
    var listToDeleteConfirmId by remember { mutableStateOf<Long?>(null) }
    var showDeleteListConfirmDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = sheetState, containerColor = LocalAppColors.current.surface,
        dragHandle = {
            Box(modifier = Modifier.padding(top = 10.dp, bottom = 6.dp).width(44.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(LocalAppColors.current.border))
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f).padding(horizontal = 16.dp)) {
            MarketSheetHeader(
                isBangla = isBangla, createdAt = currentViewingList?.list?.createdAt,
                selectedTab = selectedTab, onTabSelected = { selectedTab = it }, onDismiss = onDismiss,
                onDeleteList = if (currentViewingList != null && onDeleteList != null) { { listToDeleteConfirmId = currentViewingList.list.id; showDeleteListConfirmDialog = true } } else null,
                currentListTitle = currentViewingList?.list?.title, activeListsCount = effectiveActiveLists.size,
                onBackToLists = if (effectiveActiveLists.size > 1 && currentViewingList != null) { { selectedListId = null } } else null,
                onOpenPriceInsights = { showPriceInsightsSheet = true }
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (selectedTab == 1) {
                CreateMarketListSection(isBangla = isBangla, onListCreated = { title, items -> onCreateListWithBulk(title, items); selectedTab = 0 }, priceInsights = productPriceInsights, currencySymbol = currencySymbol)
            } else if (effectiveActiveLists.isEmpty()) {
                MarketEmptyListsState(isBangla = isBangla, onCreateNewClick = { selectedTab = 1 }, appColors = appColors, haptics = haptics)
            } else if (effectiveActiveLists.size > 1 && currentViewingList == null) {
                ActiveMarketListsHubView(activeLists = effectiveActiveLists, currencySymbol = currencySymbol, isBangla = isBangla, onSelectList = { selectedListId = it.list.id }, onDeleteList = { listToDeleteConfirmId = it; showDeleteListConfirmDialog = true }, onCreateNewClick = { selectedTab = 1 })
            } else if (currentViewingList != null) {
                val items = currentViewingList.items
                val totalCount = items.size
                val purchasedCount = items.count { it.isPurchased }
                val totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
                var filterMode by remember { mutableIntStateOf(0) }
                val filteredItems = remember(items, filterMode) {
                    when (filterMode) { 1 -> items.filter { !it.isPurchased }; 2 -> items.filter { it.isPurchased }; else -> items }
                }
                MarketSummarySection(totalCount = totalCount, purchasedCount = purchasedCount, totalSpent = totalSpent, currencySymbol = currencySymbol, isBangla = isBangla)
                if (items.isNotEmpty()) {
                    MarketListFilterTabs(filterMode = filterMode, onFilterSelect = { filterMode = it }, totalCount = totalCount, purchasedCount = purchasedCount, isBangla = isBangla, appColors = appColors, haptics = haptics)
                }
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(filteredItems, key = { _, itm -> itm.id }) { _, item ->
                        val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
                        val lastRateText = if (!item.isPurchased && lastRate != null) {
                            val (rate, unit) = lastRate
                            val rStr = if (rate % 1.0 == 0.0) rate.toInt().toString() else "%.1f".format(rate)
                            if (isBangla) "গতবার: ৳${convertToBengaliDigits(rStr)}/$unit" else "Last: ৳$rStr/$unit"
                        } else null
                        MarketItemRow(
                            item = item, currencySymbol = currencySymbol, isBangla = isBangla,
                            onUnitClick = { itemForUnitDialog = item }, onRowClick = { promptAmountForItem = item },
                            onTickClick = { if (item.isPurchased) { haptics.lightTick(); onUnmarkPurchased(item) } else promptAmountForItem = item },
                            onRequestDelete = { itemToDeleteConfirm = item }, onSubstituteClick = { itemToSubstitute = item },
                            lastRateText = lastRateText, priceInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
                        )
                    }
                    item {
                        MarketAddNewItemRow(
                            listId = currentViewingList.list.id, isBangla = isBangla,
                            onAddItem = { onAddItem(it, currentViewingList.list.id) },
                            existingItemNames = items.map { it.name.lowercase() }.toSet(),
                            productPriceInsights = productPriceInsights, currencySymbol = currencySymbol
                        )
                    }
                }
                if (items.isNotEmpty()) {
                    MarketFinishButton(isBangla = isBangla, onComplete = {
                        val purchased = items.filter { it.isPurchased }
                        val hasStaplePurchased = purchased.any {
                            val n = it.name.lowercase()
                            n.contains("মরিচ") || n.contains("লেবু") || n.contains("ধনে") || n.contains("আদা") || n.contains("রসুন") || n.contains("chili") || n.contains("lemon")
                        }
                        if (!hasStaplePurchased && items.size >= 3) showForgotStaplesDialog = true else { onCompleteList(currentViewingList.list.id); onDismiss() }
                    })
                }
            }
        }
    }

    MarketListSheetDialogs(
        itemForUnitDialog = itemForUnitDialog, onDismissUnitDialog = { itemForUnitDialog = null }, onConfirmUnitDialog = { onUpdateItem(it); itemForUnitDialog = null },
        promptAmountForItem = promptAmountForItem, onDismissPromptAmount = { promptAmountForItem = null }, onConfirmPromptAmount = { item, amt -> onMarkPurchased(item, amt); promptAmountForItem = null },
        itemToSubstitute = itemToSubstitute, onDismissSubstitute = { itemToSubstitute = null }, onConfirmSubstitute = { item, title -> onUpdateItem(item.copy(name = title)); itemToSubstitute = null },
        lastPurchasedRates = lastPurchasedRates, productPriceInsights = productPriceInsights, currencySymbol = currencySymbol, isBangla = isBangla
    )

    if (showForgotStaplesDialog && currentViewingList != null) {
        MarketForgotStaplesDialog(isBangla = isBangla, onConfirmFinish = { showForgotStaplesDialog = false; onCompleteList(currentViewingList.list.id); onDismiss() }, onDismiss = { showForgotStaplesDialog = false }, appColors = appColors)
    }

    if (itemToDeleteConfirm != null) {
        val itemToDelete = itemToDeleteConfirm!!
        AlertDialog(
            onDismissRequest = { itemToDeleteConfirm = null },
            title = { Text(if (isBangla) "পণ্য মুছে ফেলতে চান?" else "Delete Item?", fontWeight = FontWeight.Bold, color = appColors.textPrimary) },
            text = { Text(if (isBangla) "'${itemToDelete.name}' (${itemToDelete.formatQuantity()}) পণ্যটি কি নিশ্চিতভাবে এই ফর্দ থেকে মুছে ফেলতে চান?" else "Are you sure you want to delete '${itemToDelete.name}' (${itemToDelete.formatQuantity()}) from this list?", color = appColors.textSecondary) },
            confirmButton = {
                Button(onClick = { haptics.lightTick(); onDeleteItem(itemToDelete.id); itemToDeleteConfirm = null }, colors = ButtonDefaults.buttonColors(containerColor = appColors.expense), shape = RoundedCornerShape(10.dp)) {
                    Text(if (isBangla) "মুছে ফেলুন" else "Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { itemToDeleteConfirm = null }) { Text(if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary) } },
            shape = RoundedCornerShape(16.dp), containerColor = appColors.surface
        )
    }

    if (showDeleteListConfirmDialog && listToDeleteConfirmId != null) {
        val listId = listToDeleteConfirmId!!
        AlertDialog(
            onDismissRequest = { showDeleteListConfirmDialog = false; listToDeleteConfirmId = null },
            title = { Text(if (isBangla) "বাজারের ফর্দ মুছে ফেলবেন?" else "Delete Market List?", fontWeight = FontWeight.Bold, color = appColors.textPrimary) },
            text = { Text(if (isBangla) "এই বাজারের ফর্দটি এবং এর সকল পণ্য স্থায়ীভাবে মুছে ফেলা হবে। আপনি কি নিশ্চিত?" else "This market list and all its items will be permanently deleted. Are you sure?", color = appColors.textSecondary) },
            confirmButton = {
                Button(onClick = { haptics.warningFeedback(); onDeleteList?.invoke(listId); showDeleteListConfirmDialog = false; listToDeleteConfirmId = null; selectedListId = null }, colors = ButtonDefaults.buttonColors(containerColor = appColors.expense), shape = RoundedCornerShape(10.dp)) {
                    Text(if (isBangla) "মুছে ফেলুন" else "Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteListConfirmDialog = false; listToDeleteConfirmId = null }) { Text(if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary) } },
            shape = RoundedCornerShape(16.dp), containerColor = appColors.surface
        )
    }

    if (showPriceInsightsSheet) {
        ProductPriceInsightsSheet(priceInsights = productPriceInsights, onFetchHistory = { pName -> onFetchPriceHistory?.invoke(pName) ?: emptyList() }, isBangla = isBangla, currencySymbol = currencySymbol, onDismiss = { showPriceInsightsSheet = false })
    }
}
