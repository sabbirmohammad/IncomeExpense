package com.example.expensetracker.data.model

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
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
 * Bilingual English dictionary for standard commodities to enable seamless cross-language search.
 */
val COMMODITY_ENGLISH_DICTIONARY: Map<String, String> = mapOf(
    // Veggies & Produce
    "আলু" to "Potato",
    "দেশি পেঁয়াজ" to "Local Onion",
    "পেঁয়াজ" to "Onion",
    "পেঁয়াজ" to "Onion",
    "কাঁচামরিচ" to "Green Chili",
    "কাঁচা মরিচ" to "Green Chili",
    "রসুন" to "Garlic",
    "আদা" to "Ginger",
    "টমেটো" to "Tomato",
    "বেগুন" to "Eggplant",
    "ধনেপাতা" to "Coriander Leaves",
    "লেবু" to "Lemon",
    "শসা" to "Cucumber",
    "গাজর" to "Carrot",
    "লাউ" to "Bottle Gourd",
    "মিষ্টি কুমড়া" to "Sweet Pumpkin",
    "কুমড়া" to "Pumpkin",
    "কুমড়া" to "Pumpkin",
    "পেঁপে" to "Papaya",
    "পেপে" to "Papaya",
    "পটল" to "Pointed Gourd",
    "ফুলকপি" to "Cauliflower",
    "বাঁধাকপি" to "Cabbage",
    "ঢেঁড়স" to "Okra",
    "মুলা" to "Radish",

    // Meat, Fish & Protein
    "ব্রয়লার মুরগি" to "Broiler Chicken",
    "মুরগি" to "Chicken",
    "দেশি মুরগি" to "Local Chicken",
    "গরুর মাংস" to "Beef",
    "খাসির মাংস" to "Mutton",
    "রুই মাছ" to "Rui Fish",
    "কাতলা মাছ" to "Catla Fish",
    "ইলিশ মাছ" to "Hilsa Fish",
    "ইলিশ" to "Hilsa",
    "তেলাপিয়া মাছ" to "Tilapia Fish",
    "তেলাপিয়া" to "Tilapia",
    "পাঙ্গাস মাছ" to "Pangas Fish",
    "পাঙ্গাস" to "Pangas",
    "চিংড়ি" to "Prawn",
    "চিংড়ি" to "Prawn",

    // Dairy & Eggs
    "ফার্মের ডিম" to "Farm Eggs",
    "ডিম" to "Eggs",
    "তরল দুধ" to "Liquid Milk",
    "দুধ" to "Milk",
    "দই" to "Yogurt",
    "মাখন" to "Butter",
    "পাউরুটি" to "Bread",

    // Dry Groceries & Spices
    "মিনিকেট চাল" to "Miniket Rice",
    "নাজিরশাইল চাল" to "Nazirshail Rice",
    "চাল" to "Rice",
    "সয়াবিন তেল" to "Soybean Oil",
    "সরিষার তেল" to "Mustard Oil",
    "তেল" to "Cooking Oil",
    "ঘি" to "Ghee",
    "মসুর ডাল" to "Red Lentils",
    "মুগ ডাল" to "Moong Dal",
    "ডাল" to "Lentils",
    "সাদা চিনি" to "White Sugar",
    "চিনি" to "Sugar",
    "আটা" to "Atta (Wheat Flour)",
    "ময়দা" to "Flour",
    "আয়োডিনযুক্ত লবণ" to "Iodized Salt",
    "লবণ" to "Salt",
    "হলুদ গুঁড়া" to "Turmeric Powder",
    "হলুদ" to "Turmeric",
    "মরিচ গুঁড়া" to "Chili Powder",
    "জিরা" to "Cumin",
    "এলাচ" to "Cardamom",
    "দারুচিনি" to "Cinnamon",
    "চা পাতা" to "Tea Leaves"
)

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
 * Daily bazaar sentiment pulse metrics.
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
