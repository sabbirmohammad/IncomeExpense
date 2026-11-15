package com.example.expensetracker.ui.dashboard

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight

object MarketTripSavingsCalculator {
    fun calculateSavings(
        allItems: List<MarketItem>,
        productPriceInsights: Map<String, ProductPriceInsight>
    ): Double {
        val pricedItems = allItems.filter { (it.amount ?: 0.0) > 0.0 }
        val estForPriced = pricedItems.sumOf { item ->
            val actual = item.amount ?: 0.0
            val matched = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)
            if (matched != null && matched.currentUnitPrice > 0.0 && item.quantity > 0.0) {
                UnitConverterEngine.estimateCost(matched.currentUnitPrice, item.quantity, item.unit)
            } else {
                actual
            }
        }
        val spentForPriced = pricedItems.sumOf { it.amount ?: 0.0 }
        return if (estForPriced > spentForPriced && spentForPriced > 0.0) estForPriced - spentForPriced else 0.0
    }
}
