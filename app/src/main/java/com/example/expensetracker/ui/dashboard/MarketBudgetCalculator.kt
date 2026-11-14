package com.example.expensetracker.ui.dashboard

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight

object MarketBudgetCalculator {
    fun calculateTotalEstimatedBudget(
        items: List<MarketItem>,
        productPriceInsights: Map<String, ProductPriceInsight>,
        lastPurchasedRates: Map<String, Pair<Double, String>>
    ): Double {
        return items.sumOf { itm ->
            if (itm.isPurchased) {
                itm.amount ?: 0.0
            } else {
                val insight = UnitConverterEngine.findMatchingInsight(itm.name, productPriceInsights)
                if (insight != null) {
                    UnitConverterEngine.estimateCost(insight.currentUnitPrice, itm.quantity, itm.unit)
                } else {
                    val fallback = lastPurchasedRates[itm.name.lowercase().trim()]
                    if (fallback != null) {
                        UnitConverterEngine.estimateCost(fallback.first, itm.quantity, itm.unit)
                    } else 0.0
                }
            }
        }
    }
}
