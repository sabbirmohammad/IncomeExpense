package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import com.example.expensetracker.ui.dashboard.MarketAddNewItemRow
import com.example.expensetracker.ui.dashboard.MarketFinishButton

@Composable
fun MarketShoppingListView(
    currentViewingList: MarketListWithItems,
    effectiveActiveLists: List<MarketListWithItems>,
    currencySymbol: String,
    isBangla: Boolean,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    stapleProducts: List<MasterProduct> = emptyList(),
    onBackToAllLists: (() -> Unit)?,
    onAddItem: (MarketItem, Long?) -> Unit,
    onDeleteItemClick: (MarketItem) -> Unit,
    onUnitClick: (MarketItem) -> Unit,
    onSubstituteClick: (MarketItem) -> Unit,
    onPromptAmount: (MarketItem) -> Unit,
    onUnmarkPurchased: (MarketItem) -> Unit,
    onFinishTripClick: () -> Unit,
    onDeleteListClick: (Long) -> Unit,
    onStartShopping: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val items = currentViewingList.items
    var isEditMode by remember(currentViewingList.list.id) { mutableStateOf(items.isEmpty()) }
    val effectiveEditMode = isEditMode || items.isEmpty()

    val totalCount = items.size
    val purchasedCount = items.count { it.isPurchased }
    val totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
    val totalEstimatedBudget = remember(items, productPriceInsights, lastPurchasedRates) {
        items.sumOf { itm ->
            if (itm.isPurchased) itm.amount ?: 0.0
            else {
                val insight = UnitConverterEngine.findMatchingInsight(itm.name, productPriceInsights)
                if (insight != null) UnitConverterEngine.estimateCost(insight.currentUnitPrice, itm.quantity, itm.unit)
                else {
                    val fallback = lastPurchasedRates[itm.name.lowercase().trim()]
                    if (fallback != null) UnitConverterEngine.estimateCost(fallback.first, itm.quantity, itm.unit)
                    else 0.0
                }
            }
        }
    }

    var filterMode by remember { mutableIntStateOf(0) }
    val filteredItems = remember(items, filterMode) {
        when (filterMode) {
            1 -> items.filter { !it.isPurchased }
            2 -> items.filter { it.isPurchased }
            else -> items
        }
    }
    val collapsedDepts = remember(currentViewingList.list.id) { mutableStateMapOf<String, Boolean>() }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        MarketSummaryHeader(
            listTitle = currentViewingList.list.title,
            totalCount = totalCount,
            purchasedCount = purchasedCount,
            totalSpent = totalSpent,
            estimatedBudget = if (totalEstimatedBudget > 0) totalEstimatedBudget else null,
            currencySymbol = currencySymbol,
            onStartShopping = onStartShopping
        )

        Box(modifier = Modifier.size(0.dp)) {
            MarketShoppingHeaderHUD(
                totalCount = totalCount,
                purchasedCount = purchasedCount,
                totalSpent = totalSpent,
                estimatedBudget = if (totalEstimatedBudget > 0) totalEstimatedBudget else null,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                isEditMode = effectiveEditMode,
                hasMultipleLists = effectiveActiveLists.size > 1,
                listTitle = currentViewingList.list.title,
                onBackToAllLists = onBackToAllLists,
                onEnterEditMode = { isEditMode = true },
                onExitEditMode = { isEditMode = false },
                onDeleteList = { onDeleteListClick(currentViewingList.list.id) }
            )
        }

        if (items.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            MarketShoppingFilterPills(
                filterMode = filterMode,
                onFilterModeChange = { filterMode = it },
                totalCount = totalCount,
                purchasedCount = purchasedCount,
                isBangla = isBangla
            )
        }

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val itemsByDept = filteredItems.groupBy { getMarketItemCategory(it.name) }

            for (dept in ALL_DEPARTMENTS) {
                val deptItems = itemsByDept[dept.key] ?: continue
                if (deptItems.isEmpty()) continue

                val isCollapsed = collapsedDepts[dept.key] == true
                item(key = "dept_header_${dept.key}") {
                    MarketDepartmentSectionHeader(
                        dept = dept,
                        completedCount = deptItems.count { it.isPurchased },
                        totalCount = deptItems.size,
                        isExpanded = !isCollapsed,
                        onToggleExpand = { collapsedDepts[dept.key] = !isCollapsed },
                        isBangla = isBangla
                    )
                }

                if (!isCollapsed) {
                    itemsIndexed(deptItems, key = { _, item -> item.id }) { _, item ->
                        MarketShoppingItemEntry(
                            item = item,
                            currencySymbol = currencySymbol,
                            isBangla = isBangla,
                            effectiveEditMode = effectiveEditMode,
                            lastPurchasedRates = lastPurchasedRates,
                            productPriceInsights = productPriceInsights,
                            onUnitClick = onUnitClick,
                            onPromptAmount = onPromptAmount,
                            onUnmarkPurchased = onUnmarkPurchased,
                            onDeleteItemClick = onDeleteItemClick,
                            onSubstituteClick = onSubstituteClick
                        )
                    }
                }
            }

            val otherItems = filteredItems.filter { item -> ALL_DEPARTMENTS.none { it.key == getMarketItemCategory(item.name) } }
            if (otherItems.isNotEmpty()) {
                itemsIndexed(otherItems, key = { _, item -> item.id }) { _, item ->
                    MarketShoppingItemEntry(
                        item = item,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla,
                        effectiveEditMode = effectiveEditMode,
                        lastPurchasedRates = lastPurchasedRates,
                        productPriceInsights = productPriceInsights,
                        onUnitClick = onUnitClick,
                        onPromptAmount = onPromptAmount,
                        onUnmarkPurchased = onUnmarkPurchased,
                        onDeleteItemClick = onDeleteItemClick,
                        onSubstituteClick = onSubstituteClick
                    )
                }
            }

            if (effectiveEditMode) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    MarketAddNewItemRow(
                        listId = currentViewingList.list.id,
                        isBangla = isBangla,
                        onAddItem = { newItem -> onAddItem(newItem, currentViewingList.list.id) },
                        existingItemNames = items.map { it.name.lowercase() }.toSet(),
                        productPriceInsights = productPriceInsights,
                        stapleProducts = stapleProducts,
                        currencySymbol = currencySymbol
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        if (items.isNotEmpty() && !effectiveEditMode) {
            MarketBottomTripSummaryBar(
                totalSpent = totalSpent,
                totalCount = totalCount,
                purchasedCount = purchasedCount,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                onFinishShopping = onFinishTripClick,
                finishButton = { MarketFinishButton(isBangla = isBangla, onComplete = onFinishTripClick) }
            )
        }
    }
}
