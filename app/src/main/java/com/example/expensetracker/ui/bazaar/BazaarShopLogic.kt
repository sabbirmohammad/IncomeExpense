package com.example.expensetracker.ui.bazaar

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import kotlin.math.round

enum class PricingMode {
    PER_UNIT,
    TOTAL_PAID
}

enum class PriceTrendDirection {
    FALLING,
    RISING,
    STABLE
}

data class PriceRecommendation(
    val isGoodTimeToBuy: Boolean,
    val isPricey: Boolean,
    val variancePercent: Double,
    val varianceText: String,
    val badgeText: String,
    val direction: PriceTrendDirection
)

data class StallProgress(
    val stallKey: String,
    val doneCount: Int,
    val totalCount: Int,
    val isAllDone: Boolean
)

object BazaarShopLogic {

    /**
     * Rounds a double to two decimal places.
     */
    fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100.0) / 100.0
    }

    /**
     * Calculates line total from a per-base-unit rate (e.g. ৳60/kg for 500g -> ৳30.0).
     */
    fun calculateTotalFromUnitPrice(unitRate: Double, quantity: Double, unit: String): Double {
        if (unitRate <= 0.0 || quantity <= 0.0) return 0.0
        val normalized = UnitConverterEngine.normalize(quantity, unit)
        val total = unitRate * normalized.baseQuantity
        return roundToTwoDecimals(total)
    }

    /**
     * Calculates base unit price from a total paid amount (e.g. ৳30 for 500g -> ৳60/kg).
     */
    fun calculateUnitPriceFromTotal(totalPaid: Double, quantity: Double, unit: String): Double {
        if (totalPaid <= 0.0 || quantity <= 0.0) return 0.0
        val normalized = UnitConverterEngine.normalize(quantity, unit)
        if (normalized.baseQuantity <= 0.0) return totalPaid
        val rate = totalPaid / normalized.baseQuantity
        return roundToTwoDecimals(rate)
    }

    /**
     * Calculates variance percentage between current and comparison price.
     * Positive = more expensive, Negative = cheaper.
     */
    fun calculateVariancePercent(currentUnitPrice: Double, comparisonPrice: Double): Double {
        if (comparisonPrice <= 0.0) return 0.0
        val variance = ((currentUnitPrice - comparisonPrice) / comparisonPrice) * 100.0
        return roundToTwoDecimals(variance)
    }

    /**
     * Evaluates price recommendation based on 3b amendment 11:
     * - Good time to buy: current unit price >= 8% BELOW historical average AND purchaseCount >= 3.
     * - Pricey: current unit price >= 8% ABOVE historical average AND purchaseCount >= 3.
     * - Otherwise: shows variance vs last purchased price.
     */
    fun evaluatePriceRecommendation(
        currentUnitPrice: Double,
        avgUnitPrice: Double,
        purchaseCount: Int,
        lastUnitPrice: Double?,
        isBangla: Boolean = false
    ): PriceRecommendation {
        if (currentUnitPrice <= 0.0) {
            return PriceRecommendation(
                isGoodTimeToBuy = false,
                isPricey = false,
                variancePercent = 0.0,
                varianceText = "",
                badgeText = "",
                direction = PriceTrendDirection.STABLE
            )
        }

        // Rule: 8% threshold vs historical average only when purchaseCount >= 3
        if (purchaseCount >= 3 && avgUnitPrice > 0.0) {
            val varianceVsAvg = calculateVariancePercent(currentUnitPrice, avgUnitPrice)
            if (varianceVsAvg <= -8.0) {
                val pctStr = "${(-varianceVsAvg).toInt()}%"
                val text = if (isBangla) "গড় দামের চেয়ে $pctStr সস্তা! কেনার ভালো সময়" else "Good time to buy! $pctStr below average"
                val badge = if (isBangla) "▼ $pctStr কম" else "▼ $pctStr off"
                return PriceRecommendation(
                    isGoodTimeToBuy = true,
                    isPricey = false,
                    variancePercent = varianceVsAvg,
                    varianceText = text,
                    badgeText = badge,
                    direction = PriceTrendDirection.FALLING
                )
            } else if (varianceVsAvg >= 8.0) {
                val pctStr = "${varianceVsAvg.toInt()}%"
                val text = if (isBangla) "গড় দামের চেয়ে $pctStr বেশি" else "Pricey! $pctStr above average"
                val badge = if (isBangla) "▲ $pctStr বেশি" else "▲ $pctStr high"
                return PriceRecommendation(
                    isGoodTimeToBuy = false,
                    isPricey = true,
                    variancePercent = varianceVsAvg,
                    varianceText = text,
                    badgeText = badge,
                    direction = PriceTrendDirection.RISING
                )
            }
        }

        // Fallback: Variance vs last price
        if (lastUnitPrice != null && lastUnitPrice > 0.0) {
            val varianceVsLast = calculateVariancePercent(currentUnitPrice, lastUnitPrice)
            val diff = currentUnitPrice - lastUnitPrice
            val diffStr = "${roundToTwoDecimals(kotlin.math.abs(diff))}"
            val direction = when {
                diff > 0 -> PriceTrendDirection.RISING
                diff < 0 -> PriceTrendDirection.FALLING
                else -> PriceTrendDirection.STABLE
            }
            val text = when {
                diff > 0 -> if (isBangla) "গতবারের চেয়ে ৳$diffStr বেশি" else "+৳$diffStr vs last"
                diff < 0 -> if (isBangla) "গতবারের চেয়ে ৳$diffStr কম" else "-৳$diffStr vs last"
                else -> if (isBangla) "গতবারের দামের সমান" else "Same as last price"
            }
            val badge = when {
                diff > 0 -> "▲ ৳$diffStr"
                diff < 0 -> "▼ ৳$diffStr"
                else -> "="
            }
            return PriceRecommendation(
                isGoodTimeToBuy = false,
                isPricey = false,
                variancePercent = varianceVsLast,
                varianceText = text,
                badgeText = badge,
                direction = direction
            )
        }

        return PriceRecommendation(
            isGoodTimeToBuy = false,
            isPricey = false,
            variancePercent = 0.0,
            varianceText = if (isBangla) "নতুন পণ্য" else "First purchase",
            badgeText = "",
            direction = PriceTrendDirection.STABLE
        )
    }

    /**
     * Calculates line amount when user taps 1-tap swipe right at last price (Amendment 10).
     * Normalizes mismatched units (e.g. last bought per kg at ৳60/kg, now listed as 500g).
     */
    fun calculateLastPriceAmount(
        itemQuantity: Double,
        itemUnit: String,
        lastRate: Double,
        lastRateUnit: String
    ): Double {
        if (lastRate <= 0.0 || itemQuantity <= 0.0) return 0.0

        val normalizedLast = UnitConverterEngine.normalize(1.0, lastRateUnit)
        val normalizedItem = UnitConverterEngine.normalize(itemQuantity, itemUnit)

        return if (normalizedLast.baseUnit == normalizedItem.baseUnit && normalizedLast.baseQuantity > 0.0) {
            val perBaseUnitRate = lastRate / normalizedLast.baseQuantity
            roundToTwoDecimals(perBaseUnitRate * normalizedItem.baseQuantity)
        } else {
            roundToTwoDecimals(lastRate * itemQuantity)
        }
    }

    /**
     * Computes done/total progress and completion state for a stall.
     * Supports matching either by department key (veggies, meat_fish, etc.) or by display title.
     */
    fun calculateStallProgress(
        stallKey: String,
        items: List<MarketItem>,
        masterProductsMap: Map<String, com.example.expensetracker.data.model.MasterProduct>?,
        skippedIds: Set<Long>
    ): StallProgress {
        val stallItems = items.filter { item ->
            val dept = BazaarDomainLogic.classifyStallWithOverride(item.name, masterProductsMap)
            item.id !in skippedIds && isMatchingStall(dept, stallKey)
        }
        val total = stallItems.size
        val done = stallItems.count { it.isPurchased }
        return StallProgress(
            stallKey = stallKey,
            doneCount = done,
            totalCount = total,
            isAllDone = total > 0 && done == total
        )
    }

    fun isMatchingStall(deptKey: String, targetKey: String): Boolean {
        if (deptKey.equals(targetKey, ignoreCase = true)) return true
        return when (deptKey) {
            "veggies" -> targetKey.contains("শাকসবজি") || targetKey.contains("কাঁচাবাজার") || targetKey.contains("veggies") || targetKey.contains("🥬")
            "meat_fish" -> targetKey.contains("মাছ") || targetKey.contains("মাংস") || targetKey.contains("meat") || targetKey.contains("fish") || targetKey.contains("🐟")
            "dairy_bakery" -> targetKey.contains("দুধ") || targetKey.contains("বেকারি") || targetKey.contains("ডেয়ারি") || targetKey.contains("dairy") || targetKey.contains("🥛")
            "groceries" -> targetKey.contains("মুদি") || targetKey.contains("groceries") || targetKey.contains("🧂")
            else -> false
        }
    }

    /**
     * Finds the next unpurchased, non-skipped item for auto-advance (Amendment 12).
     */
    fun findNextUnpurchasedItem(
        items: List<MarketItem>,
        currentItemId: Long,
        skippedIds: Set<Long>
    ): MarketItem? {
        val unbought = items.filter { it.id !in skippedIds && !it.isPurchased }
        if (unbought.isEmpty()) return null
        val currentIndex = unbought.indexOfFirst { it.id == currentItemId }
        return if (currentIndex >= 0 && currentIndex + 1 < unbought.size) {
            unbought[currentIndex + 1]
        } else {
            unbought.firstOrNull { it.id != currentItemId }
        }
    }
}
