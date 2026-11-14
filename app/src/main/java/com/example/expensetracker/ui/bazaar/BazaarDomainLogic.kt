package com.example.expensetracker.ui.bazaar

import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import java.util.Calendar

/**
 * Item price estimate container.
 * If unitPrice is null, item has no known past price and lineTotal is null ("৳—").
 */
data class MarketItemPriceEstimate(
    val item: MarketItem,
    val unitPrice: Double?,
    val lineTotal: Double?,
    val isEstimated: Boolean
)

/**
 * Summary of a market list's estimated cost vs budget cap.
 * Items with no price are counted explicitly and not treated as zero.
 */
data class MarketListEstimateSummary(
    val pricedItemsCount: Int,
    val unpricedItemsCount: Int,
    val totalEstimatedPrice: Double,
    val hasUnpricedItems: Boolean = unpricedItemsCount > 0,
    val budgetCap: Double? = null,
    val isOverBudget: Boolean = budgetCap != null && totalEstimatedPrice > budgetCap,
    val overBudgetAmount: Double = if (budgetCap != null && totalEstimatedPrice > budgetCap) totalEstimatedPrice - budgetCap else 0.0
)

data class ParsedPreviewItem(
    val id: Long = 0L,
    val name: String,
    val quantity: Double,
    val unit: String,
    val stallKey: String,
    val originalLine: String
)

data class FlaggedLine(
    val lineText: String,
    val suggestedName: String
)

data class PastePreviewResult(
    val validItems: List<ParsedPreviewItem>,
    val flaggedLines: List<FlaggedLine>
)

/**
 * Pure Kotlin domain functions for Bazaar (Market) Plan Mode, Catalog, Estimates, and Paste list.
 * Zero Android or Compose dependencies; 100% deterministic & unit-testable.
 */
object BazaarDomainLogic {

    /**
     * Resolves unit price from last purchased rates first, then master products catalog.
     */
    fun resolveItemUnitPrice(
        productName: String,
        itemUnit: String,
        lastPurchasedRates: Map<String, Pair<Double, String>>,
        masterProducts: Map<String, MasterProduct>
    ): Double? {
        val clean = UnitConverterEngine.normalizeProductName(productName)
        if (clean.isBlank()) return null

        // 1. Try last purchased rates
        val lastRate = lastPurchasedRates[clean]
            ?: lastPurchasedRates.entries.find { it.key.equals(productName.trim(), ignoreCase = true) }?.value
        if (lastRate != null && lastRate.first > 0.0) {
            val ratePrice = lastRate.first
            val rateUnit = lastRate.second
            val normalizedRate = UnitConverterEngine.normalize(1.0, rateUnit)
            val normalizedItem = UnitConverterEngine.normalize(1.0, itemUnit)
            return if (normalizedRate.baseUnit == normalizedItem.baseUnit && normalizedRate.baseQuantity > 0.0) {
                (ratePrice / normalizedRate.baseQuantity) * normalizedItem.baseQuantity
            } else {
                ratePrice
            }
        }

        // 2. Try master products
        val master = masterProducts[clean]
            ?: masterProducts.values.find {
                it.name.equals(productName.trim(), ignoreCase = true) || it.normalizedName == clean
            }
        if (master != null && master.latestUnitPrice > 0.0) {
            val masterPrice = master.latestUnitPrice
            val masterUnit = master.defaultUnit
            val normalizedMaster = UnitConverterEngine.normalize(1.0, masterUnit)
            val normalizedItem = UnitConverterEngine.normalize(1.0, itemUnit)
            return if (normalizedMaster.baseUnit == normalizedItem.baseUnit && normalizedMaster.baseQuantity > 0.0) {
                (masterPrice / normalizedMaster.baseQuantity) * normalizedItem.baseQuantity
            } else {
                masterPrice
            }
        }

        return null
    }

    /**
     * Calculates estimate for a list of market items.
     * Unpriced items are tracked explicitly and never silently ignored or zeroed out.
     */
    fun calculateMarketListEstimate(
        items: List<MarketItem>,
        lastPurchasedRates: Map<String, Pair<Double, String>>,
        masterProducts: Map<String, MasterProduct>,
        budgetCap: Double? = null
    ): Pair<MarketListEstimateSummary, List<MarketItemPriceEstimate>> {
        var pricedCount = 0
        var unpricedCount = 0
        var totalEst = 0.0
        val lineEstimates = mutableListOf<MarketItemPriceEstimate>()

        for (item in items) {
            // If already purchased with actual amount, use that
            if (item.isPurchased && item.amount != null && item.amount > 0.0) {
                pricedCount++
                totalEst += item.amount
                lineEstimates.add(
                    MarketItemPriceEstimate(
                        item = item,
                        unitPrice = item.amount / if (item.quantity > 0) item.quantity else 1.0,
                        lineTotal = item.amount,
                        isEstimated = false
                    )
                )
                continue
            }

            val unitPrice = resolveItemUnitPrice(item.name, item.unit, lastPurchasedRates, masterProducts)
            if (unitPrice != null && unitPrice > 0.0) {
                val lineTotal = UnitConverterEngine.estimateCost(unitPrice, item.quantity, item.unit)
                pricedCount++
                totalEst += lineTotal
                lineEstimates.add(
                    MarketItemPriceEstimate(
                        item = item,
                        unitPrice = unitPrice,
                        lineTotal = lineTotal,
                        isEstimated = true
                    )
                )
            } else {
                unpricedCount++
                lineEstimates.add(
                    MarketItemPriceEstimate(
                        item = item,
                        unitPrice = null,
                        lineTotal = null,
                        isEstimated = false
                    )
                )
            }
        }

        val summary = MarketListEstimateSummary(
            pricedItemsCount = pricedCount,
            unpricedItemsCount = unpricedCount,
            totalEstimatedPrice = totalEst,
            hasUnpricedItems = unpricedCount > 0,
            budgetCap = budgetCap,
            isOverBudget = budgetCap != null && totalEst > budgetCap,
            overBudgetAmount = if (budgetCap != null && totalEst > budgetCap) totalEst - budgetCap else 0.0
        )

        return Pair(summary, lineEstimates)
    }

    /**
     * Parses raw paste text (e.g. WhatsApp / SMS / Notes) into an editable preview.
     * Flags unparseable / corrupt lines for user correction without dropping data.
     */
    fun parseTextForPastePreview(
        rawText: String,
        masterProducts: Map<String, MasterProduct>? = null
    ): PastePreviewResult {
        val lines = rawText.split("\n", "\r\n", ";", "।")
        val validItems = mutableListOf<ParsedPreviewItem>()
        val flagged = mutableListOf<FlaggedLine>()

        for (raw in lines) {
            val cleaned = MarketListParser.cleanLine(raw)
            if (cleaned.isBlank()) continue

            // Check if line contains commas separating items (e.g. "আলু ১ কেজি, পেঁয়াজ ২ কেজি")
            if (cleaned.contains(",") && !cleaned.matches(Regex(".*\\d+,\\d+.*"))) {
                val subParts = cleaned.split(",")
                for (part in subParts) {
                    val subCleaned = MarketListParser.cleanLine(part)
                    if (subCleaned.isNotBlank()) {
                        processSingleLineForPreview(subCleaned, masterProducts, validItems, flagged)
                    }
                }
            } else {
                processSingleLineForPreview(cleaned, masterProducts, validItems, flagged)
            }
        }

        return PastePreviewResult(validItems = validItems, flaggedLines = flagged)
    }

    private fun processSingleLineForPreview(
        line: String,
        masterProducts: Map<String, MasterProduct>?,
        validList: MutableList<ParsedPreviewItem>,
        flaggedList: MutableList<FlaggedLine>
    ) {
        val parsed = MarketListParser.parseSingleLine(line)
        if (parsed != null && parsed.name.isNotBlank()) {
            val cleanName = UnitConverterEngine.normalizeProductName(parsed.name)
            val hasLetters = parsed.name.any { it.isLetter() }
            // If the name has no letters at all (e.g. punctuation, symbols, lone numbers)
            if (!hasLetters || cleanName.isBlank() || cleanName.none { it.isLetter() }) {
                flaggedList.add(FlaggedLine(lineText = line, suggestedName = line.trim()))
            } else {
                val stall = classifyStallWithOverride(parsed.name, masterProducts)
                validList.add(
                    ParsedPreviewItem(
                        name = parsed.name,
                        quantity = parsed.quantity,
                        unit = parsed.unit,
                        stallKey = stall,
                        originalLine = line
                    )
                )
            }
        } else {
            flaggedList.add(FlaggedLine(lineText = line, suggestedName = line.trim()))
        }
    }

    /**
     * Clones items from a completed trip into a fresh shopping list.
     * Preserves quantities and units, resets all price and purchased state.
     */
    fun cloneTripItems(items: List<MarketItem>): List<MarketItem> {
        return items.map { item ->
            MarketItem(
                id = 0L,
                listId = 0L,
                name = item.name.trim(),
                quantity = item.quantity,
                unit = item.unit,
                amount = null,
                isPurchased = false,
                transactionId = null,
                createdAt = System.currentTimeMillis()
            )
        }
    }

    /**
     * Tapping a catalog tile adds 1 default unit, or increments quantity by 1.0 if already present.
     */
    fun addOrIncrementCatalogItem(
        existingItems: List<MarketItem>,
        product: MasterProduct,
        listId: Long = 0L
    ): List<MarketItem> {
        val cleanTarget = UnitConverterEngine.normalizeProductName(product.name)
        val index = existingItems.indexOfFirst {
            UnitConverterEngine.normalizeProductName(it.name) == cleanTarget
        }

        return if (index >= 0) {
            existingItems.mapIndexed { idx, itm ->
                if (idx == index) itm.copy(quantity = itm.quantity + 1.0) else itm
            }
        } else {
            existingItems + MarketItem(
                id = 0L,
                listId = listId,
                name = product.name,
                quantity = 1.0,
                unit = product.defaultUnit,
                amount = null,
                isPurchased = false,
                transactionId = null,
                createdAt = System.currentTimeMillis()
            )
        }
    }

    /**
     * Determines stall category, prioritizing user's master_products category override
     * before falling back to heuristic classification. Zero-schema!
     */
    fun classifyStallWithOverride(
        productName: String,
        masterProductsMap: Map<String, MasterProduct>?
    ): String {
        if (masterProductsMap != null && productName.isNotBlank()) {
            val clean = UnitConverterEngine.normalizeProductName(productName)
            val overrideCategory = masterProductsMap[clean]?.category
                ?: masterProductsMap.values.find {
                    it.name.equals(productName.trim(), ignoreCase = true) || it.normalizedName == clean
                }?.category
            if (!overrideCategory.isNullOrBlank() && ALL_DEPARTMENTS.any { it.key == overrideCategory }) {
                return overrideCategory
            }
        }
        return getMarketItemCategory(productName)
    }

    /**
     * Computes smart suggestions from completed trips matching the target day-of-week.
     * Without AI or network, simple frequency counting.
     */
    fun computeSmartSuggestions(
        completedTrips: List<MarketListWithItems>,
        targetDayOfWeek: Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK),
        minFrequency: Int = 2
    ): List<String> {
        val dayTrips = completedTrips.filter { trip ->
            val cal = Calendar.getInstance().apply { timeInMillis = trip.list.createdAt }
            cal.get(Calendar.DAY_OF_WEEK) == targetDayOfWeek
        }

        val counts = mutableMapOf<String, Int>()
        for (trip in dayTrips) {
            val uniqueProductNamesInTrip = trip.items.map { it.name.trim() }.distinct()
            for (name in uniqueProductNamesInTrip) {
                counts[name] = (counts[name] ?: 0) + 1
            }
        }

        return counts.entries
            .filter { it.value >= minFrequency }
            .sortedByDescending { it.value }
            .map { it.key }
    }

    /**
     * Formats list into a clean, WhatsApp/SMS friendly text grouped by stall with checkboxes.
     */
    fun formatListAsWhatsAppText(
        listTitle: String,
        items: List<MarketItem>,
        masterProductsMap: Map<String, MasterProduct>? = null,
        isBangla: Boolean = true
    ): String {
        if (items.isEmpty()) return ""

        val titleHeader = if (isBangla) "*বাজারের ফর্দ: $listTitle*" else "*Market List: $listTitle*"
        val grouped = items.groupBy { classifyStallWithOverride(it.name, masterProductsMap) }

        val sb = StringBuilder()
        sb.append(titleHeader).append("\n\n")

        for (dept in ALL_DEPARTMENTS) {
            val stallItems = grouped[dept.key] ?: continue
            if (stallItems.isEmpty()) continue

            val deptTitle = if (isBangla) "${dept.icon} ${dept.titleBn}:" else "${dept.icon} ${dept.titleEn}:"
            sb.append(deptTitle).append("\n")

            for (itm in stallItems) {
                val rawQtyStr = itm.formatQuantity()
                val qtyStr = if (isBangla) DigitConverter.toBengaliDigits(rawQtyStr) else rawQtyStr
                sb.append("□ ${itm.name} - $qtyStr\n")
            }
            sb.append("\n")
        }

        return sb.toString().trimEnd()
    }
}
