package com.example.expensetracker.data.model

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.ui.dashboard.getMarketItemCategory

/**
 * Pure transformer function bridging MasterProduct table benchmarks with personal ProductPriceInsight records.
 * Guarantees that on clean installs, all 25 pre-seeded Bangladeshi staples appear with benchmark rates and "বাজার দর" tag.
 */
fun buildMarketCommodities(
    masterProducts: List<MasterProduct>,
    priceInsights: Map<String, ProductPriceInsight>
): List<MarketCommodityUiModel> {
    val commodityList = mutableListOf<MarketCommodityUiModel>()
    val visitedNames = mutableSetOf<String>()

    // 1. Process Master Products Catalog
    for (mp in masterProducts) {
        val normName = UnitConverterEngine.normalizeProductName(mp.name)
        visitedNames.add(normName)
        visitedNames.add(mp.name.lowercase().trim())

        val matchedInsight = UnitConverterEngine.findMatchingInsight(mp.name, priceInsights)
            ?: priceInsights[mp.name]
            ?: priceInsights[normName]

        if (matchedInsight != null) {
            commodityList.add(
                MarketCommodityUiModel(
                    id = mp.id,
                    name = mp.name,
                    category = mp.category.ifBlank { getMarketItemCategory(mp.name) },
                    defaultUnit = matchedInsight.baseUnit.ifBlank { mp.defaultUnit },
                    currentPrice = matchedInsight.currentUnitPrice,
                    benchmarkPrice = mp.latestUnitPrice.takeIf { it > 0.0 } ?: matchedInsight.avgUnitPrice,
                    previousPrice = matchedInsight.previousUnitPrice,
                    priceDiff = matchedInsight.priceDiff,
                    percentDiff = matchedInsight.percentDiff,
                    priceTrend = matchedInsight.priceTrend,
                    purchaseCount = maxOf(matchedInsight.purchaseCount, mp.purchaseCount),
                    lastPurchasedTimestamp = maxOf(matchedInsight.lastPurchasedTimestamp, mp.lastPurchasedAt),
                    isStaple = mp.isStaple || matchedInsight.purchaseCount >= 3,
                    isBenchmarkOnly = false,
                    recentPricePoints = matchedInsight.recentPricePoints.ifEmpty { listOf(matchedInsight.currentUnitPrice) },
                    rawInsight = matchedInsight
                )
            )
        } else {
            val prevPrice = mp.previousUnitPrice
            val diff = mp.priceDiff ?: 0.0
            val pct = mp.percentDiff ?: 0.0
            val trend = if (prevPrice != null && prevPrice > 0.0) {
                when {
                    pct > 0.5 -> PriceTrend.UP
                    pct < -0.5 -> PriceTrend.DOWN
                    else -> PriceTrend.STABLE
                }
            } else {
                PriceTrend.STABLE
            }

            commodityList.add(
                MarketCommodityUiModel(
                    id = mp.id,
                    name = mp.name,
                    category = mp.category.ifBlank { getMarketItemCategory(mp.name) },
                    defaultUnit = mp.defaultUnit,
                    currentPrice = mp.latestUnitPrice,
                    benchmarkPrice = mp.latestUnitPrice,
                    previousPrice = prevPrice,
                    priceDiff = diff,
                    percentDiff = pct,
                    priceTrend = trend,
                    purchaseCount = mp.purchaseCount,
                    lastPurchasedTimestamp = mp.lastPurchasedAt,
                    isStaple = mp.isStaple,
                    isBenchmarkOnly = true,
                    recentPricePoints = listOf(mp.latestUnitPrice),
                    rawInsight = null
                )
            )
        }
    }

    // 2. Add any custom items from personal history not in Master Catalog
    for ((name, insight) in priceInsights) {
        val norm = UnitConverterEngine.normalizeProductName(name)
        val clean = name.lowercase().trim()
        if (!visitedNames.contains(norm) && !visitedNames.contains(clean)) {
            visitedNames.add(norm)
            visitedNames.add(clean)
            commodityList.add(
                MarketCommodityUiModel(
                    id = 0,
                    name = insight.productName,
                    category = getMarketItemCategory(insight.productName),
                    defaultUnit = insight.baseUnit,
                    currentPrice = insight.currentUnitPrice,
                    benchmarkPrice = insight.avgUnitPrice,
                    previousPrice = insight.previousUnitPrice,
                    priceDiff = insight.priceDiff,
                    percentDiff = insight.percentDiff,
                    priceTrend = insight.priceTrend,
                    purchaseCount = insight.purchaseCount,
                    lastPurchasedTimestamp = insight.lastPurchasedTimestamp,
                    isStaple = insight.purchaseCount >= 3,
                    isBenchmarkOnly = false,
                    recentPricePoints = insight.recentPricePoints.ifEmpty { listOf(insight.currentUnitPrice) },
                    rawInsight = insight
                )
            )
        }
    }

    return commodityList
}

/**
 * Computes high-level bazaar sentiment pulse metrics.
 */
fun computeBazaarSentimentPulse(
    commodities: List<MarketCommodityUiModel>,
    appColors: AppColors
): BazaarSentimentPulse {
    if (commodities.isEmpty()) {
        return BazaarSentimentPulse(
            sentimentTitleBn = "স্থিতিশীল বাজার দর",
            sentimentTitleEn = "Stable Market Rates",
            sentimentColor = appColors.accent,
            averageBasketRate = 0.0,
            totalTrackedItems = 0,
            topHikeCommodity = null,
            topBargainCommodity = null,
            inflationIndexPercent = null
        )
    }

    val avgBasket = commodities.map { it.currentPrice }.average()
    val commoditiesWithDeltas = commodities.filter {
        !it.isBenchmarkOnly && it.previousPrice != null && it.previousPrice > 0.0 &&
        !it.percentDiff.isNaN() && !it.percentDiff.isInfinite()
    }
    val topHike = commoditiesWithDeltas.filter { it.percentDiff > 0.5 }.maxByOrNull { it.percentDiff }
    val topBargain = commoditiesWithDeltas.filter { it.percentDiff < -0.5 }.minByOrNull { it.percentDiff }

    val avgInflation = if (commoditiesWithDeltas.isNotEmpty()) {
        commoditiesWithDeltas.map { it.percentDiff }.average()
    } else null

    val (titleBn, titleEn, color) = when {
        avgInflation == null -> Triple("স্থিতিশীল বাজার দর", "Stable Market Rates", appColors.accent)
        avgInflation > 2.0 -> Triple("ঊর্ধ্বমুখী বাজার", "Rising Market Rates", appColors.expense)
        avgInflation < -1.0 -> Triple("সাশ্রয়ী বাজার দর", "Favorable Market Rates", appColors.income)
        else -> Triple("স্থিতিশীল বাজার দর", "Stable Market Rates", appColors.accent)
    }

    return BazaarSentimentPulse(
        sentimentTitleBn = titleBn,
        sentimentTitleEn = titleEn,
        sentimentColor = color,
        averageBasketRate = avgBasket,
        totalTrackedItems = commodities.size,
        topHikeCommodity = topHike,
        topBargainCommodity = topBargain,
        inflationIndexPercent = avgInflation
    )
}
