package com.example.expensetracker.ui.dashboard

import androidx.compose.runtime.Composable
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.UnitAndQuantityDialog

@Composable
fun MarketNativeItemDialogs(
    itemForUnitDialog: MarketItem?,
    onDismissUnitDialog: () -> Unit,
    onConfirmUnitDialog: (MarketItem, Double, String) -> Unit,
    promptAmountForItem: MarketItem?,
    onDismissPromptDialog: () -> Unit,
    onConfirmPromptDialog: (MarketItem, Double) -> Unit,
    itemToSubstitute: MarketItem?,
    onDismissSubstituteDialog: () -> Unit,
    onConfirmSubstituteDialog: (MarketItem, String) -> Unit,
    currencySymbol: String,
    isBangla: Boolean,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    haptics: AppHaptics
) {
    if (itemForUnitDialog != null) {
        val curItem = itemForUnitDialog
        UnitAndQuantityDialog(
            initialQuantity = curItem.quantity,
            initialUnit = curItem.unit,
            isBangla = isBangla,
            onDismiss = onDismissUnitDialog,
            onConfirm = { qty, unit -> onConfirmUnitDialog(curItem, qty, unit) }
        )
    }

    if (promptAmountForItem != null) {
        val curItem = promptAmountForItem
        val lastRate = lastPurchasedRates[curItem.name.lowercase().trim()]
        val curInsight = UnitConverterEngine.findMatchingInsight(curItem.name, productPriceInsights)
        MarketPromptAmountDialog(
            item = curItem,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            lastRate = lastRate,
            priceInsight = curInsight,
            onDismiss = onDismissPromptDialog,
            onConfirm = { amt ->
                haptics.mediumClick()
                onConfirmPromptDialog(curItem, amt)
            }
        )
    }

    if (itemToSubstitute != null) {
        val curItem = itemToSubstitute
        MarketSubstituteItemDialog(
            item = curItem,
            isBangla = isBangla,
            onDismiss = onDismissSubstituteDialog,
            onConfirmSubstitute = { newTitle -> onConfirmSubstituteDialog(curItem, newTitle) }
        )
    }
}
