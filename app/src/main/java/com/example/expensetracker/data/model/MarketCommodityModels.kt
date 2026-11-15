package com.example.expensetracker.data.model

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.ui.components.convertToBengaliDigits
import kotlinx.serialization.Serializable

/**
 * Price tier brackets for commodity price exploration.
 * Classifies staples into Budget (< ৳100), Mid (৳100 - ৳500), and Premium (৳500+).
 */
enum class PriceTier(
    val titleBn: String,
    val titleEn: String
) {
    ALL("সকল পণ্য", "All Items"),
    BUDGET_UNDER_100("< ৳১০০", "Under ৳100"),
    ESSENTIAL_100_500("৳১০০ - ৳৫০০", "৳100 - ৳500"),
    PREMIUM_OVER_500("৳৫০০+", "৳500+");

    fun matches(price: Double): Boolean = when (this) {
        ALL -> true
        BUDGET_UNDER_100 -> price < 100.0
        ESSENTIAL_100_500 -> price in 100.0..500.0
        PREMIUM_OVER_500 -> price > 500.0
    }

    companion object {
        fun fromPrice(price: Double): PriceTier = when {
            price < 100.0 -> BUDGET_UNDER_100
            price <= 500.0 -> ESSENTIAL_100_500
            else -> PREMIUM_OVER_500
        }
    }
}

/**
 * Directional price movement filter.
 */
enum class MovementFilter(
    val titleBn: String,
    val titleEn: String
) {
    ALL("সব", "All"),
    UP("বেড়েছে", "Hiked"),
    DOWN("কমেছে", "Dropped"),
    STABLE("সাশ্রয়ী", "Stable")
}

/**
 * Unified commodity UI model bridging master catalog benchmark rates with personal trip history.
 */
@Serializable
data class MarketCommodityUiModel(
    val id: Long = 0,
    val name: String,
    val category: String = "groceries",
    val defaultUnit: String = "কেজি",
    val currentPrice: Double,
    val benchmarkPrice: Double = currentPrice,
    val previousPrice: Double? = null,
    val priceDiff: Double = 0.0,
    val percentDiff: Double = 0.0,
    val priceTrend: PriceTrend = PriceTrend.STABLE,
    val purchaseCount: Int = 1,
    val lastPurchasedTimestamp: Long = System.currentTimeMillis(),
    val isStaple: Boolean = false,
    val isBenchmarkOnly: Boolean = false,
    val recentPricePoints: List<Double> = emptyList(),
    val rawInsight: ProductPriceInsight? = null
) {
    val priceTier: PriceTier get() = PriceTier.fromPrice(currentPrice)
    val isPriceUp: Boolean get() = priceTrend == PriceTrend.UP && !isBenchmarkOnly
    val isPriceDown: Boolean get() = priceTrend == PriceTrend.DOWN && !isBenchmarkOnly
    val isPriceStable: Boolean get() = priceTrend == PriceTrend.STABLE || isBenchmarkOnly

    val englishName: String
        get() = COMMODITY_ENGLISH_DICTIONARY[name]
            ?: COMMODITY_ENGLISH_DICTIONARY[UnitConverterEngine.normalizeProductName(name)]
            ?: name

    fun formatCurrentRate(currencySymbol: String = "৳", isBangla: Boolean = true): String {
        val rateFormatted = if (currentPrice % 1.0 == 0.0) {
            currentPrice.toInt().toString()
        } else {
            "%.1f".format(currentPrice)
        }
        val rateText = if (isBangla) convertToBengaliDigits(rateFormatted) else rateFormatted
        return "$currencySymbol$rateText/$defaultUnit"
    }

    fun formatDiff(isBangla: Boolean = true): String {
        if (isBenchmarkOnly || previousPrice == null) {
            return if (isBangla) "বাজার দর" else "Benchmark"
        }
        val diffAbs = kotlin.math.abs(priceDiff)
        val diffFormatted = if (diffAbs % 1.0 == 0.0) diffAbs.toInt().toString() else "%.1f".format(diffAbs)
        val diffText = if (isBangla) convertToBengaliDigits(diffFormatted) else diffFormatted
        val safePct = if (percentDiff.isNaN() || percentDiff.isInfinite()) 0.0 else kotlin.math.abs(percentDiff)
        val pctFormatted = "%.1f".format(safePct)
        val pctText = if (isBangla) convertToBengaliDigits(pctFormatted) else pctFormatted
        return if (priceDiff > 0) "+৳$diffText ($pctText%)"
        else if (priceDiff < 0) "-৳$diffText ($pctText%)"
        else if (isBangla) "০%" else "0%"
    }
}

/**
 * High-level market pulse metrics for the summary banner.
 */
data class BazaarSentimentPulse(
    val sentimentTitleBn: String,
    val sentimentTitleEn: String,
    val sentimentColor: Color,
    val averageBasketRate: Double,
    val totalTrackedItems: Int,
    val topHikeCommodity: MarketCommodityUiModel?,
    val topBargainCommodity: MarketCommodityUiModel?,
    val inflationIndexPercent: Double?
)
