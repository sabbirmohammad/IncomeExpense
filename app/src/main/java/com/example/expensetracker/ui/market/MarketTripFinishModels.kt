package com.example.expensetracker.ui.market

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import kotlin.math.abs

enum class LeftoverHandlingOption {
    MOVE_TO_NEXT,
    KEEP_IN_LIST,
    DISCARD
}

data class PriceChangeInsight(
    val itemName: String,
    val diffAmount: Double,
    val diffPercentage: Double,
    val isIncrease: Boolean
)

fun calculateTripPriceChanges(
    boughtItems: List<MarketItem>,
    productPriceInsights: Map<String, ProductPriceInsight>
): List<PriceChangeInsight> {
    val list = mutableListOf<PriceChangeInsight>()
    for (item in boughtItems) {
        val amt = item.amount ?: continue
        if (amt <= 0 || item.quantity <= 0) continue
        val (currentRate, _) = UnitConverterEngine.calculateBaseUnitPrice(amt, item.quantity, item.unit)
        val insight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
        val prevRate = insight?.currentUnitPrice ?: continue
        if (prevRate > 0) {
            val diff = currentRate - prevRate
            val pct = (diff / prevRate) * 100.0
            if (abs(pct) >= 3.0) {
                list.add(
                    PriceChangeInsight(
                        itemName = item.name,
                        diffAmount = abs(diff),
                        diffPercentage = abs(pct),
                        isIncrease = diff > 0
                    )
                )
            }
        }
    }
    return list.sortedByDescending { it.diffPercentage }.take(3)
}
