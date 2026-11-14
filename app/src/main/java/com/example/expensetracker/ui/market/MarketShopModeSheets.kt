package com.example.expensetracker.ui.market

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ProductPriceInsight

@Composable
fun MarketShopModeSheets(
    state: MarketShopModeState,
    trip: MarketListWithItems,
    accounts: List<Account>,
    currencySymbol: String,
    isBangla: Boolean,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    onUpdateItemQuantityAndUnit: (MarketItem, Double, String) -> Unit,
    onUpdateItemAmount: (MarketItem, Double) -> Unit,
    onCompleteTrip: (selectedAccountId: Long?, rollOverItems: List<MarketItem>) -> Unit,
    onExitShopMode: () -> Unit
) {
    state.activePriceSheetItem?.let { currentItem ->
        val nextPendingItem = remember(state.allItems, currentItem.id) {
            state.allItems.firstOrNull { it.id != currentItem.id && !it.isPurchased }
        }
        MarketPriceEntrySheet(
            item = currentItem,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            hasNextPendingItem = nextPendingItem != null,
            onDismiss = { state.activePriceSheetItem = null },
            onSaveItem = { updatedQty, updatedUnit, totalAmount, shouldAdvanceToNext ->
                if (updatedQty != currentItem.quantity || updatedUnit != currentItem.unit) {
                    onUpdateItemQuantityAndUnit(currentItem, updatedQty, updatedUnit)
                }
                onUpdateItemAmount(currentItem, totalAmount)
                state.activePriceSheetItem = if (shouldAdvanceToNext && nextPendingItem != null) nextPendingItem else null
            },
            lastRate = lastPurchasedRates[currentItem.name.lowercase().trim()],
            priceInsight = UnitConverterEngine.findMatchingInsight(currentItem.name, productPriceInsights)
        )
    }

    if (state.showFinishSummarySheet) {
        MarketTripFinishSummarySheet(
            trip = trip,
            accounts = accounts,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            onDismiss = { state.showFinishSummarySheet = false },
            onConfirmComplete = { selectedAccountId, leftoverOption ->
                state.showFinishSummarySheet = false
                val rollOverItems = when (leftoverOption) {
                    LeftoverHandlingOption.MOVE_TO_NEXT -> state.unboughtItems
                    LeftoverHandlingOption.KEEP_IN_LIST, LeftoverHandlingOption.DISCARD -> emptyList()
                }
                onCompleteTrip(selectedAccountId, rollOverItems)
                onExitShopMode()
            },
            productPriceInsights = productPriceInsights
        )
    }
}
