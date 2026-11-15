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
     * Applies a quick delta adjustment to an amount or rate, ensuring non-negative result.
     */
    fun applyQuickAdjustment(currentVal: Double, delta: Double): Double {
        return roundToTwoDecimals((currentVal + delta).coerceAtLeast(0.0))
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

    /**
     * Evaluates swipe action decision as a pure function:
     * - START_TO_END (swipe right):
     *     - if item has valid last purchase rate -> MarkDone with unit-converted amount
     *     - if no last purchase rate -> OpenPricePad
     * - END_TO_START (swipe left): Skip
     * - SETTLED: SnapBack
     */
    fun evaluateSwipeDecision(
        direction: SwipeDismissDirection,
        item: MarketItem,
        lastRate: Pair<Double, String>?
    ): SwipeDecision {
        return when (direction) {
            SwipeDismissDirection.START_TO_END -> {
                if (lastRate != null && lastRate.first > 0.0) {
                    val amount = calculateLastPriceAmount(
                        itemQuantity = item.quantity,
                        itemUnit = item.unit,
                        lastRate = lastRate.first,
                        lastRateUnit = lastRate.second
                    )
                    SwipeDecision.MarkDone(amount)
                } else {
                    SwipeDecision.OpenPricePad
                }
            }
            SwipeDismissDirection.END_TO_START -> SwipeDecision.Skip
            SwipeDismissDirection.SETTLED -> SwipeDecision.SnapBack
        }
    }

    /**
     * Determines whether SwipeToDismissBox should snap back to center.
     * In Shop Mode, confirmValueChange always returns false so items do not disappear offscreen.
     */
    fun shouldDismissBoxSnapBack(direction: SwipeDismissDirection): Boolean {
        return true
    }

    /**
     * Determines which swipe background to compose.
     * Guaranteed invariant:
     * When the card is at rest (progress <= 0.001f and targetDirection is SETTLED,
     * or direction is SETTLED), returns NONE so zero background is composed or rendered.
     * When swiping right (START_TO_END with progress > 0), returns DONE.
     * When swiping left (END_TO_START with progress > 0), returns SKIP.
     */
    fun resolveSwipeBackgroundKind(
        direction: SwipeDismissDirection,
        progress: Float = 0f,
        targetDirection: SwipeDismissDirection = direction
    ): SwipeBackgroundKind {
        if (direction == SwipeDismissDirection.SETTLED) {
            return SwipeBackgroundKind.NONE
        }
        if (progress <= 0.001f && targetDirection == SwipeDismissDirection.SETTLED) {
            return SwipeBackgroundKind.NONE
        }
        return when (direction) {
            SwipeDismissDirection.START_TO_END -> SwipeBackgroundKind.DONE
            SwipeDismissDirection.END_TO_START -> SwipeBackgroundKind.SKIP
            SwipeDismissDirection.SETTLED -> SwipeBackgroundKind.NONE
        }
    }

    /**
     * Resolves the initial keypad expression when Price Pad opens.
     */
    fun resolvePricePadInitialExpression(
        item: MarketItem,
        initialMode: PricingMode,
        lastRate: Pair<Double, String>?
    ): String {
        return when {
            item.amount != null && item.amount > 0.0 -> {
                if (initialMode == PricingMode.TOTAL_PAID) {
                    if (item.amount % 1.0 == 0.0) item.amount.toInt().toString() else "%.1f".format(item.amount)
                } else {
                    val rate = calculateUnitPriceFromTotal(item.amount, item.quantity, item.unit)
                    if (rate % 1.0 == 0.0) rate.toInt().toString() else "%.1f".format(rate)
                }
            }
            lastRate != null && lastRate.first > 0.0 -> {
                if (initialMode == PricingMode.PER_UNIT) {
                    if (lastRate.first % 1.0 == 0.0) lastRate.first.toInt().toString() else "%.1f".format(lastRate.first)
                } else {
                    val total = calculateLastPriceAmount(item.quantity, item.unit, lastRate.first, lastRate.second)
                    if (total % 1.0 == 0.0) total.toInt().toString() else "%.1f".format(total)
                }
            }
            else -> ""
        }
    }

    /**
     * Computes the new keypad expression when user toggles pricing mode.
     */
    fun computePricePadModeSwitchExpression(
        newMode: PricingMode,
        currentUnitPrice: Double,
        currentTotalAmount: Double
    ): String {
        return if (newMode == PricingMode.PER_UNIT) {
            if (currentUnitPrice > 0.0) {
                if (currentUnitPrice % 1.0 == 0.0) currentUnitPrice.toInt().toString() else "%.1f".format(currentUnitPrice)
            } else ""
        } else {
            if (currentTotalAmount > 0.0) {
                if (currentTotalAmount % 1.0 == 0.0) currentTotalAmount.toInt().toString() else "%.1f".format(currentTotalAmount)
            } else ""
        }
    }

    /**
     * Evaluates action when user taps an item line in the live receipt panel.
     */
    fun evaluateReceiptLineTap(item: MarketItem?): ReceiptLineTapDecision {
        if (item == null) return ReceiptLineTapDecision.NoOp
        return ReceiptLineTapDecision.OpenEditPricePad(
            item = item,
            canUndo = item.isPurchased
        )
    }

    /**
     * Calculates checkout financial breakdown, adjustments, and validation invariants.
     */
    fun calculateCheckoutSummary(
        items: List<MarketItem>,
        customTotal: Double?,
        otherCosts: Double
    ): CheckoutSummary {
        val purchasedItems = items.filter { it.isPurchased }
        val purchasedCount = purchasedItems.size
        val unpricedCount = purchasedItems.count { it.amount == null || it.amount <= 0.0 }
        val itemSum = purchasedItems.mapNotNull { it.amount }.filter { it > 0.0 }.sum()

        val safeOtherCosts = otherCosts.coerceAtLeast(0.0)
        val editedTotal = (customTotal ?: itemSum).coerceAtLeast(0.0)
        val adjustment = roundToTwoDecimals(editedTotal - itemSum)
        val finalExpenseAmount = roundToTwoDecimals(editedTotal + safeOtherCosts)

        val isZeroItemsAndZeroOther = purchasedCount == 0 && safeOtherCosts <= 0.0
        val isNegative = (customTotal != null && customTotal < 0.0) || otherCosts < 0.0
        val requiresZeroConfirm = purchasedCount > 0 && editedTotal == 0.0 && safeOtherCosts == 0.0

        val isCompleteEnabled = !isZeroItemsAndZeroOther && !isNegative
        val validationMessage = when {
            isZeroItemsAndZeroOther -> "কোনো পণ্য কেনা হয়নি বা অন্যান্য খরচ যোগ করা হয়নি"
            isNegative -> "খরচের পরিমাণ ঋণাত্মক হতে পারবে না"
            requiresZeroConfirm -> "মোট খরচ ৳০ নির্ধারণ করা হয়েছে"
            else -> null
        }

        return CheckoutSummary(
            purchasedCount = purchasedCount,
            unpricedCount = unpricedCount,
            itemSum = roundToTwoDecimals(itemSum),
            editedTotal = roundToTwoDecimals(editedTotal),
            otherCosts = roundToTwoDecimals(safeOtherCosts),
            adjustment = adjustment,
            finalExpenseAmount = finalExpenseAmount,
            isCompleteEnabled = isCompleteEnabled,
            requiresZeroConfirm = requiresZeroConfirm,
            validationMessage = validationMessage
        )
    }

    /**
     * Computes trip price insights comparing each commodity in this trip against its latest
     * purchase price before this trip, aggregating multi-line commodities into a single unit price.
     */
    fun calculateTripPriceInsights(
        tripItems: List<MarketItem>,
        historicalPrices: List<HistoricalPriceEntry>,
        budgetCap: Double?,
        totalSpent: Double,
        tripTimestamp: Long,
        tripTransactionId: Long?
    ): BazaarDoneInsights {
        val purchasedItems = tripItems.filter { it.isPurchased && (it.amount ?: 0.0) > 0.0 && it.quantity > 0.0 }
        val grouped = purchasedItems.groupBy { UnitConverterEngine.normalizeProductName(it.name) }

        var cheaper = 0
        var pricier = 0
        var isLatestForAll = true

        for ((normName, itemsForCommodity) in grouped) {
            val totalAmount = itemsForCommodity.sumOf { it.amount ?: 0.0 }
            val totalBaseQty = itemsForCommodity.sumOf {
                UnitConverterEngine.normalize(it.quantity, it.unit).baseQuantity
            }
            if (totalBaseQty <= 0.0) continue
            val tripRate = roundToTwoDecimals(totalAmount / totalBaseQty)

            val entriesForCommodity = historicalPrices.filter {
                UnitConverterEngine.normalizeProductName(it.productName) == normName
            }

            // If a strictly newer entry exists in history, this trip is no longer the latest
            val hasNewerTrip = entriesForCommodity.any { it.timestamp > tripTimestamp }
            if (hasNewerTrip) {
                isLatestForAll = false
            }

            // Previous entry = latest entry recorded before or at tripTimestamp (excluding this trip)
            val prevEntry = entriesForCommodity
                .filter { it.timestamp <= tripTimestamp && (tripTransactionId == null || it.transactionId != tripTransactionId) }
                .maxByOrNull { it.timestamp }

            if (prevEntry != null && prevEntry.unitPrice > 0.0) {
                if (tripRate < prevEntry.unitPrice - 0.01) {
                    cheaper++
                } else if (tripRate > prevEntry.unitPrice + 0.01) {
                    pricier++
                }
            }
        }

        val delta = if (budgetCap != null && budgetCap > 0.0) {
            roundToTwoDecimals(budgetCap - totalSpent)
        } else null

        return BazaarDoneInsights(
            cheaperCount = cheaper,
            pricierCount = pricier,
            budgetDelta = delta,
            budgetCap = budgetCap,
            totalSpent = totalSpent,
            isLatestTrip = isLatestForAll
        )
    }
}

data class CheckoutSummary(
    val purchasedCount: Int,
    val unpricedCount: Int,
    val itemSum: Double,
    val editedTotal: Double,
    val otherCosts: Double,
    val adjustment: Double,
    val finalExpenseAmount: Double,
    val isCompleteEnabled: Boolean,
    val requiresZeroConfirm: Boolean,
    val validationMessage: String?
)

data class HistoricalPriceEntry(
    val productName: String,
    val unitPrice: Double,
    val timestamp: Long,
    val transactionId: Long?
)

data class BazaarDoneInsights(
    val cheaperCount: Int,
    val pricierCount: Int,
    val budgetDelta: Double?,
    val budgetCap: Double?,
    val totalSpent: Double,
    val isLatestTrip: Boolean
)

enum class SwipeDismissDirection {
    START_TO_END,
    END_TO_START,
    SETTLED
}

enum class SwipeBackgroundKind {
    NONE,
    DONE,
    SKIP
}

sealed class SwipeDecision {
    data class MarkDone(val calculatedAmount: Double) : SwipeDecision()
    data object OpenPricePad : SwipeDecision()
    data object Skip : SwipeDecision()
    data object SnapBack : SwipeDecision()
}

sealed class ReceiptLineTapDecision {
    data class OpenEditPricePad(val item: MarketItem, val canUndo: Boolean) : ReceiptLineTapDecision()
    data object NoOp : ReceiptLineTapDecision()
}
