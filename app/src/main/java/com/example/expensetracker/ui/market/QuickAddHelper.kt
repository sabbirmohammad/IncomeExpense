package com.example.expensetracker.ui.market

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.ProductPriceInsight

fun filterAutocompleteSuggestions(
    query: String,
    productPriceInsights: Map<String, ProductPriceInsight>,
    masterProducts: List<MasterProduct>,
    stapleProducts: List<MasterProduct>,
    lastPurchasedRates: Map<String, Pair<Double, String>>
): List<Triple<String, Double?, String>> {
    val q = query.trim()
    if (q.length < 1) return emptyList()

    val key = UnitConverterEngine.canonicalCommodityKey(q)
    val pool = mutableMapOf<String, Pair<Double?, String>>()
    for ((name, insight) in productPriceInsights) {
        pool[name] = Pair(insight.currentUnitPrice, insight.baseUnit)
    }
    for (mp in masterProducts) {
        if (!pool.containsKey(mp.name)) {
            val rate = lastPurchasedRates[mp.name.lowercase().trim()]
            val price = if (mp.latestUnitPrice > 0) mp.latestUnitPrice else rate?.first
            pool[mp.name] = Pair(price, mp.defaultUnit)
        }
    }
    for (sp in stapleProducts) {
        if (!pool.containsKey(sp.name)) {
            val rate = lastPurchasedRates[sp.name.lowercase().trim()]
            val price = if (sp.latestUnitPrice > 0) sp.latestUnitPrice else rate?.first
            pool[sp.name] = Pair(price, sp.defaultUnit)
        }
    }

    return pool.entries.filter { (name, _) ->
        val candKey = UnitConverterEngine.canonicalCommodityKey(name)
        candKey.contains(key, ignoreCase = true) || name.contains(q, ignoreCase = true)
    }.take(6).map { (name, meta) ->
        Triple(name, meta.first, meta.second)
    }
}
