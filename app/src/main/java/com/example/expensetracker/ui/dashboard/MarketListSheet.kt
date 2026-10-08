package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.UnitAndQuantityDialog
import com.example.expensetracker.ui.components.rememberAppHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketListSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    marketListWithItems: MarketListWithItems?,
    currencySymbol: String = "৳",
    isBangla: Boolean = true,
    geminiApiKey: String? = null,
    onDismiss: () -> Unit,
    onCreateListWithBulk: (title: String, items: List<MarketItem>) -> Unit,
    onAddItem: (item: MarketItem) -> Unit,
    onUpdateItem: (item: MarketItem) -> Unit,
    onDeleteItem: (itemId: Long) -> Unit,
    onMarkPurchased: (item: MarketItem, amount: Double) -> Unit,
    onUnmarkPurchased: (item: MarketItem) -> Unit,
    onCompleteList: (listId: Long) -> Unit
) {
    val haptics = rememberAppHaptics()
    var selectedTab by remember {
        mutableIntStateOf(if (marketListWithItems == null || marketListWithItems.items.isEmpty()) 1 else 0)
    }

    var itemForUnitDialog by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountForItem by remember { mutableStateOf<MarketItem?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

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
                onDismiss = onDismiss
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
                val items = marketListWithItems?.items ?: emptyList()
                val totalCount = items.size
                val purchasedCount = items.count { it.isPurchased }
                val totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }

                MarketSummarySection(
                    totalCount = totalCount,
                    purchasedCount = purchasedCount,
                    totalSpent = totalSpent,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla,
                    selectedCategoryFilter = selectedCategoryFilter,
                    onCategorySelected = { selectedCategoryFilter = it }
                )

                Spacer(modifier = Modifier.height(4.dp))

                val displayedItems = remember(items, selectedCategoryFilter) {
                    if (selectedCategoryFilter == null) items
                    else items.filter { getMarketItemCategory(it.name) == selectedCategoryFilter }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(displayedItems, key = { _, itm -> itm.id }) { _, item ->
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
                            onAddItem = onAddItem
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
}
