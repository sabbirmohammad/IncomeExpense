package com.example.expensetracker.ui.market

import androidx.compose.runtime.Composable
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.dashboard.MarketItemRow

@Composable
fun MarketShoppingItemEntry(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    effectiveEditMode: Boolean,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    onUnitClick: (MarketItem) -> Unit,
    onPromptAmount: (MarketItem) -> Unit,
    onUnmarkPurchased: (MarketItem) -> Unit,
    onDeleteItemClick: (MarketItem) -> Unit,
    onSubstituteClick: (MarketItem) -> Unit
) {
    val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
    val lastRateText = if (lastRate != null && lastRate.first > 0) {
        val rateStr = if (lastRate.first % 1.0 == 0.0) lastRate.first.toInt().toString() else "%.1f".format(lastRate.first)
        val rateBengali = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
        if (isBangla) "গত দর: ৳$rateBengali / ${lastRate.second}" else "Last: ৳$rateBengali / ${lastRate.second}"
    } else null

    MarketItemRow(
        item = item,
        currencySymbol = currencySymbol,
        isBangla = isBangla,
        onUnitClick = { onUnitClick(item) },
        onTickClick = { if (!item.isPurchased) onPromptAmount(item) else onUnmarkPurchased(item) },
        onDeleteClick = { onDeleteItemClick(item) },
        onRequestDelete = { onDeleteItemClick(item) },
        onSubstituteClick = { onSubstituteClick(item) },
        isEditMode = effectiveEditMode,
        lastRateText = lastRateText,
        priceInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
    )
}
