package com.example.expensetracker.ui.market

import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.common.UnitConverterEngine

/**
 * Group of duplicate commodity entries identified by canonical key normalization.
 */
data class DuplicateCommodityGroup(
    val canonicalKey: String,
    val primaryName: String,
    val variants: List<MarketCommodityUiModel>
)

/**
 * Scans list of commodities and groups spelling/whitespace/ZWJ duplicates.
 */
fun findDuplicateCommodities(commodities: List<MarketCommodityUiModel>): List<DuplicateCommodityGroup> {
    val grouped = commodities.groupBy { UnitConverterEngine.canonicalCommodityKey(it.name) }
    return grouped.filter { (_, items) ->
        items.map { it.name.trim().lowercase() }.distinct().size > 1
    }.map { (key, items) ->
        val sorted = items.sortedByDescending { it.purchaseCount }
        DuplicateCommodityGroup(
            canonicalKey = key,
            primaryName = sorted.first().name,
            variants = sorted
        )
    }
}
