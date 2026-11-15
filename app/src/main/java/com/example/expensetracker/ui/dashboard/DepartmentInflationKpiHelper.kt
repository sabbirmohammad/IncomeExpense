package com.example.expensetracker.ui.dashboard

import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.convertToBengaliDigits

enum class PriceSortOrder(
    val titleBn: String,
    val titleEn: String,
    val iconEmoji: String
) {
    RECENT("সাম্প্রতিক", "Recent", "🕒"),
    HIGHEST_HIKE("সর্বোচ্চ বৃদ্ধি", "Highest Hike", "🔺"),
    BIGGEST_DROP("সর্বোচ্চ সাশ্রয়", "Biggest Drop", "🔻"),
    MOST_PURCHASED("সর্বাধিক কেনা", "Most Purchased", "🛍️"),
    ALPHABETICAL("বর্ণানুক্রমিক", "A-Z", "🔤")
}

data class DepartmentInflationKpi(
    val department: DepartmentMeta,
    val trackedItemsCount: Int,
    val itemsWithHistoryCount: Int,
    val averageInflationPercent: Double?,
    val trend: PriceTrend
)

fun computeDepartmentInflationKpis(
    priceInsights: Collection<ProductPriceInsight>
): List<DepartmentInflationKpi> {
    val grouped = priceInsights.groupBy { getMarketItemCategory(it.productName) }
    return ALL_DEPARTMENTS.map { dept ->
        val deptInsights = grouped[dept.key] ?: emptyList()
        val withHistory = deptInsights.filter {
            it.previousUnitPrice != null && it.previousUnitPrice > 0.0 &&
            !it.percentDiff.isNaN() && !it.percentDiff.isInfinite()
        }
        val avgPct = if (withHistory.isNotEmpty()) withHistory.map { it.percentDiff }.average() else null
        val trend = when {
            avgPct == null -> PriceTrend.STABLE
            avgPct > 0.5 -> PriceTrend.UP
            avgPct < -0.5 -> PriceTrend.DOWN
            else -> PriceTrend.STABLE
        }
        DepartmentInflationKpi(
            department = dept,
            trackedItemsCount = deptInsights.size,
            itemsWithHistoryCount = withHistory.size,
            averageInflationPercent = avgPct,
            trend = trend
        )
    }
}

fun computeCommodityDepartmentInflationKpis(
    commodities: Collection<MarketCommodityUiModel>
): List<DepartmentInflationKpi> {
    val grouped = commodities.groupBy { it.category }
    return ALL_DEPARTMENTS.map { dept ->
        val deptCommodities = grouped[dept.key] ?: emptyList()
        val withHistory = deptCommodities.filter {
            !it.isBenchmarkOnly && it.previousPrice != null && it.previousPrice > 0.0 &&
            !it.percentDiff.isNaN() && !it.percentDiff.isInfinite()
        }
        val avgPct = if (withHistory.isNotEmpty()) withHistory.map { it.percentDiff }.average() else null
        val trend = when {
            avgPct == null -> PriceTrend.STABLE
            avgPct > 0.5 -> PriceTrend.UP
            avgPct < -0.5 -> PriceTrend.DOWN
            else -> PriceTrend.STABLE
        }
        DepartmentInflationKpi(
            department = dept,
            trackedItemsCount = deptCommodities.size,
            itemsWithHistoryCount = withHistory.size,
            averageInflationPercent = avgPct,
            trend = trend
        )
    }
}

fun formatInflationPct(pct: Double?, isBangla: Boolean): String {
    if (pct == null) return if (isBangla) "নতুন" else "New"
    val absVal = "%.1f".format(kotlin.math.abs(pct))
    val numStr = if (isBangla) convertToBengaliDigits(absVal) else absVal
    return when {
        pct > 0.05 -> "+$numStr%"
        pct < -0.05 -> "-$numStr%"
        else -> if (isBangla) "০.০%" else "0.0%"
    }
}
