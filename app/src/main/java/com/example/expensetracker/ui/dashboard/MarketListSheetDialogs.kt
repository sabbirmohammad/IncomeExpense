package com.example.expensetracker.ui.dashboard

import androidx.compose.runtime.Composable
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.UnitAndQuantityDialog

@Composable
fun MarketListSheetDialogs(
    itemForUnitDialog: MarketItem?,
    onDismissUnitDialog: () -> Unit,
    onConfirmUnitDialog: (MarketItem) -> Unit,
    promptAmountForItem: MarketItem?,
    onDismissPromptAmount: () -> Unit,
    onConfirmPromptAmount: (MarketItem, Double) -> Unit,
    itemToSubstitute: MarketItem?,
    onDismissSubstitute: () -> Unit,
    onConfirmSubstitute: (MarketItem, String) -> Unit,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    currencySymbol: String,
    isBangla: Boolean
) {
    if (itemForUnitDialog != null) {
        val curItem = itemForUnitDialog
        UnitAndQuantityDialog(
            initialQuantity = curItem.quantity,
            initialUnit = curItem.unit,
            isBangla = isBangla,
            onDismiss = onDismissUnitDialog,
            onConfirm = { qty, unit ->
                onConfirmUnitDialog(curItem.copy(quantity = qty, unit = unit))
            }
        )
    }

    if (promptAmountForItem != null) {
        val targetItem = promptAmountForItem
        val lastRate = lastPurchasedRates[targetItem.name.lowercase().trim()]
        val targetInsight = UnitConverterEngine.findMatchingInsight(targetItem.name, productPriceInsights)
        MarketPromptAmountDialog(
            item = targetItem,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            lastRate = lastRate,
            priceInsight = targetInsight,
            onDismiss = onDismissPromptAmount,
            onConfirm = { amt ->
                onConfirmPromptAmount(targetItem, if (amt > 0) amt else 0.0)
            }
        )
    }

    if (itemToSubstitute != null) {
        val curItem = itemToSubstitute
        MarketSubstituteItemDialog(
            item = curItem,
            isBangla = isBangla,
            onDismiss = onDismissSubstitute,
            onConfirmSubstitute = { newTitle ->
                onConfirmSubstitute(curItem, newTitle)
            }
        )
    }
}
