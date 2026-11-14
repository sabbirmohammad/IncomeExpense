package com.example.expensetracker.ui.market

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import kotlinx.coroutines.launch

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * V7 Target Design D: Immersive Shop Mode.
 * - Full screen: global bottom bar HIDDEN.
 * - Top bar: ✕ Exit, list title, bought/total, search toggle.
 * - Screen kept awake via FLAG_KEEP_SCREEN_ON.
 * - 72dp rows with larger text, pending items first, bought items collapsed at bottom.
 * - Sticky bottom dock: animated total spent count-up, count ratio, budget comparison.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketShopModeView(
    activeList: MarketListWithItems,
    accounts: List<Account>,
    currencySymbol: String,
    isBangla: Boolean,
    onExitShopMode: () -> Unit,
    onUpdateItemAmount: (MarketItem, Double) -> Unit,
    onUpdateItemQuantityAndUnit: (MarketItem, Double, String) -> Unit,
    onQuickMarkPurchased: (MarketItem) -> Unit,
    onUnmarkPurchased: (MarketItem) -> Unit,
    onDeleteItem: (MarketItem) -> Unit,
    onAddItem: (MarketItem) -> Unit,
    onCompleteTrip: (selectedAccountId: Long?, rollOverItems: List<MarketItem>) -> Unit,
    productPriceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    lastPurchasedRates: Map<String, Pair<Double, String>> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    DisposableEffect(Unit) {
        val activity = context.findActivity()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }

    BackHandler { onExitShopMode() }

    val state = rememberMarketShopModeState(
        activeList = activeList,
        lastPurchasedRates = lastPurchasedRates,
        productPriceInsights = productPriceInsights
    )

    Surface(modifier = modifier.fillMaxSize(), color = appColors.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                MarketShopModeTopBar(
                    title = activeList.list.title,
                    purchasedCount = state.purchasedCount,
                    totalCount = state.totalCount,
                    isBangla = isBangla,
                    searchQuery = state.searchQuery,
                    isSearchActive = state.isSearchActive,
                    onSearchQueryChange = { state.searchQuery = it },
                    onToggleSearch = {
                        state.isSearchActive = !state.isSearchActive
                        if (!state.isSearchActive) state.searchQuery = ""
                    },
                    onExitShopMode = onExitShopMode
                )

                MarketShopModeItemList(
                    filteredPending = state.filteredPending,
                    filteredBought = state.filteredBought,
                    isBoughtCollapsed = state.isBoughtCollapsed,
                    lastPurchasedRates = lastPurchasedRates,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla,
                    onItemClick = { item ->
                        haptics.lightTick()
                        state.activePriceSheetItem = item
                    },
                    onQuickMarkPurchased = { item ->
                        haptics.mediumClick()
                        onQuickMarkPurchased(item)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "${item.name} ${strings.marketBought}",
                                actionLabel = strings.undo,
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) onUnmarkPurchased(item)
                        }
                    },
                    onUnmarkPurchased = { item ->
                        haptics.mediumClick()
                        onUnmarkPurchased(item)
                    },
                    onDeleteItem = { item ->
                        haptics.mediumClick()
                        onDeleteItem(item)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "${item.name} ${strings.delete}",
                                actionLabel = strings.undo,
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) onAddItem(item)
                        }
                    },
                    onToggleBoughtCollapse = { state.isBoughtCollapsed = !state.isBoughtCollapsed },
                    modifier = Modifier.weight(1f)
                )

                MarketShopModeDock(
                    animatedTotalSpent = state.animatedTotalSpent.value,
                    totalSpent = state.totalSpent,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla,
                    isOverspent = state.isOverspent,
                    estimatedBudget = state.estimatedBudget,
                    purchasedCount = state.purchasedCount,
                    totalCount = state.totalCount,
                    onFinishClick = { state.showFinishSummarySheet = true }
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 90.dp)
            )
        }
    }

    MarketShopModeSheets(
        state = state,
        trip = activeList,
        accounts = accounts,
        currencySymbol = currencySymbol,
        isBangla = isBangla,
        lastPurchasedRates = lastPurchasedRates,
        productPriceInsights = productPriceInsights,
        onUpdateItemQuantityAndUnit = onUpdateItemQuantityAndUnit,
        onUpdateItemAmount = onUpdateItemAmount,
        onCompleteTrip = onCompleteTrip,
        onExitShopMode = onExitShopMode
    )
}
