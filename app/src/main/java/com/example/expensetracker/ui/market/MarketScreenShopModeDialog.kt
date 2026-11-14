package com.example.expensetracker.ui.market

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.dashboard.DashboardViewModel

@Composable
fun MarketScreenShopModeDialog(
    isActive: Boolean,
    currentViewingList: MarketListWithItems?,
    accounts: List<Account>,
    currencySymbol: String,
    isBangla: Boolean,
    dashboardViewModel: DashboardViewModel,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    onDismiss: () -> Unit
) {
    if (!isActive || currentViewingList == null) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        MarketShopModeView(
            activeList = currentViewingList,
            accounts = accounts,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            onExitShopMode = onDismiss,
            onUpdateItemAmount = { item, amount ->
                dashboardViewModel.markMarketItemPurchased(item, amount)
            },
            onUpdateItemQuantityAndUnit = { item, newQty, newUnit ->
                dashboardViewModel.updateMarketItem(item.copy(quantity = newQty, unit = newUnit))
            },
            onQuickMarkPurchased = { item ->
                val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
                val matchedInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
                val estUnitPrice = matchedInsight?.currentUnitPrice ?: lastRate?.first ?: 0.0
                val estCost = if (estUnitPrice > 0.0 && item.quantity > 0.0) {
                    UnitConverterEngine.estimateCost(estUnitPrice, item.quantity, item.unit)
                } else 0.0
                dashboardViewModel.markMarketItemPurchased(item, estCost)
            },
            onUnmarkPurchased = { item ->
                dashboardViewModel.unmarkMarketItemPurchased(item)
            },
            onDeleteItem = { item ->
                dashboardViewModel.deleteMarketItem(item.id)
            },
            onAddItem = { item ->
                dashboardViewModel.addMarketItem(item, currentViewingList.list.id)
            },
            onCompleteTrip = { selectedAccountId, rollOverItems ->
                dashboardViewModel.completeActiveMarketList(
                    listId = currentViewingList.list.id,
                    rollOverItems = rollOverItems,
                    accountId = selectedAccountId
                ) {
                    onDismiss()
                }
            },
            productPriceInsights = productPriceInsights,
            lastPurchasedRates = lastPurchasedRates
        )
    }
}
