package com.example.expensetracker.ui.market

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ProductPriceInsight

class MarketShopModeState(
    val allItems: List<MarketItem>,
    val boughtItems: List<MarketItem>,
    val unboughtItems: List<MarketItem>,
    val totalSpent: Double,
    val estimatedBudget: Double,
    val isOverspent: Boolean,
    val animatedTotalSpent: State<Float>,
    searchQueryInit: String,
    isSearchActiveInit: Boolean,
    isBoughtCollapsedInit: Boolean
) {
    var searchQuery by mutableStateOf(searchQueryInit)
    var isSearchActive by mutableStateOf(isSearchActiveInit)
    var isBoughtCollapsed by mutableStateOf(isBoughtCollapsedInit)
    var activePriceSheetItem by mutableStateOf<MarketItem?>(null)
    var showFinishSummarySheet by mutableStateOf(false)

    val totalCount: Int get() = allItems.size
    val purchasedCount: Int get() = boughtItems.size

    val filteredPending: List<MarketItem>
        get() = if (searchQuery.isBlank()) unboughtItems
        else unboughtItems.filter { it.name.contains(searchQuery, ignoreCase = true) }

    val filteredBought: List<MarketItem>
        get() = if (searchQuery.isBlank()) boughtItems
        else boughtItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
}

@Composable
fun rememberMarketShopModeState(
    activeList: MarketListWithItems,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>
): MarketShopModeState {
    val searchQuery = rememberSaveable { mutableStateOf("") }
    val isSearchActive = rememberSaveable { mutableStateOf(false) }
    val isBoughtCollapsed = rememberSaveable { mutableStateOf(false) }

    val allItems = activeList.items
    val boughtItems = remember(allItems) { allItems.filter { it.isPurchased } }
    val unboughtItems = remember(allItems) { allItems.filter { !it.isPurchased } }

    val totalSpent = remember(boughtItems) { boughtItems.sumOf { it.amount ?: 0.0 } }
    val estimatedBudget = remember(allItems, lastPurchasedRates, productPriceInsights) {
        allItems.sumOf { item ->
            val rate = lastPurchasedRates[item.name.lowercase().trim()]?.first
                ?: productPriceInsights[UnitConverterEngine.normalizeProductName(item.name)]?.currentUnitPrice
            if (rate != null && rate > 0.0 && item.quantity > 0.0) {
                UnitConverterEngine.estimateCost(rate, item.quantity, item.unit)
            } else 0.0
        }
    }

    val isOverspent = estimatedBudget > 0 && totalSpent > estimatedBudget
    val animatedTotalSpent = animateFloatAsState(
        targetValue = totalSpent.toFloat(),
        animationSpec = tween(300),
        label = "animatedTotalSpent"
    )

    return remember(activeList, lastPurchasedRates, productPriceInsights) {
        MarketShopModeState(
            allItems = allItems,
            boughtItems = boughtItems,
            unboughtItems = unboughtItems,
            totalSpent = totalSpent,
            estimatedBudget = estimatedBudget,
            isOverspent = isOverspent,
            animatedTotalSpent = animatedTotalSpent,
            searchQueryInit = searchQuery.value,
            isSearchActiveInit = isSearchActive.value,
            isBoughtCollapsedInit = isBoughtCollapsed.value
        )
    }
}
