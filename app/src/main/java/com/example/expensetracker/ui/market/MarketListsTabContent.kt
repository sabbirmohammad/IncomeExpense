package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.dashboard.ActiveMarketListsHubView

@Composable
fun MarketListsTabContent(
    effectiveActiveLists: List<MarketListWithItems>,
    currentViewingList: MarketListWithItems?,
    currencySymbol: String,
    isBangla: Boolean,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    stapleProducts: List<MasterProduct> = emptyList(),
    onCreateStarterList: ((String, List<Pair<String, String>>) -> Unit)? = null,
    onSelectList: (MarketListWithItems) -> Unit,
    onBackToAllLists: (() -> Unit)?,
    onCreateNewClick: () -> Unit,
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
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    if (effectiveActiveLists.isEmpty()) {
        MarketListsStarterTemplates(
            isBangla = isBangla,
            onCreateNewClick = onCreateNewClick,
            onCreateStarterList = onCreateStarterList,
            appColors = appColors,
            haptics = haptics,
            modifier = modifier
        )
    } else if (effectiveActiveLists.size > 1 && currentViewingList == null) {
        ActiveMarketListsHubView(
            activeLists = effectiveActiveLists,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            onSelectList = onSelectList,
            onDeleteList = onDeleteListClick,
            onCreateNewClick = onCreateNewClick,
            modifier = modifier.padding(horizontal = 20.dp)
        )
    } else if (currentViewingList != null) {
        MarketShoppingListView(
            currentViewingList = currentViewingList,
            effectiveActiveLists = effectiveActiveLists,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            lastPurchasedRates = lastPurchasedRates,
            productPriceInsights = productPriceInsights,
            stapleProducts = stapleProducts,
            onBackToAllLists = onBackToAllLists,
            onAddItem = onAddItem,
            onDeleteItemClick = onDeleteItemClick,
            onUnitClick = onUnitClick,
            onSubstituteClick = onSubstituteClick,
            onPromptAmount = onPromptAmount,
            onUnmarkPurchased = onUnmarkPurchased,
            onFinishTripClick = onFinishTripClick,
            onDeleteListClick = onDeleteListClick,
            onStartShopping = onStartShopping,
            modifier = modifier
        )
    }
}
