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
        return com.example.expensetracker.ui.dashboard.classifyStallWithOverride(productName, masterProductsMap)
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

    /**
     * Builds structured, deduplicated suggestions for empty Plan Mode.
     * Groups: Last Trip, Weekday Frequency, Staples, Templates.
     * Empty groups are omitted. Items already on the list are deduped.
     */
    fun buildEmptyStateSuggestions(
        existingItems: List<MarketItem>,
        completedTrips: List<MarketListWithItems>,
        stapleProducts: List<MasterProduct>,
        currentDayOfWeek: Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    ): List<PlanSuggestionGroup> {
        val groups = mutableListOf<PlanSuggestionGroup>()
        val existingNormNames = existingItems.map { UnitConverterEngine.normalizeProductName(it.name) }.toSet()

        // 1. "From your last trip" (most recent completed trip)
        val lastTrip = completedTrips.firstOrNull()
        if (lastTrip != null && lastTrip.items.isNotEmpty()) {
            val purchasedItems = lastTrip.items.filter { it.isPurchased }
            val lastTripChips = purchasedItems
                .filter { UnitConverterEngine.normalizeProductName(it.name) !in existingNormNames }
                .distinctBy { UnitConverterEngine.normalizeProductName(it.name) }
                .map { item ->
                    PlanSuggestionChip(
                        id = "last_trip_${item.id}",
                        name = item.name,
                        defaultUnit = item.unit.ifBlank { "কেজি" },
                        defaultQuantity = if (item.quantity > 0) item.quantity else 1.0,
                        estimatedPrice = if (item.amount != null && item.amount > 0 && item.quantity > 0) item.amount / item.quantity else null
                    )
                }
            if (lastTripChips.isNotEmpty()) {
                groups.add(
                    PlanSuggestionGroup(
                        id = "last_trip",
                        titleBn = "গত বাজারের সদাই",
                        titleEn = "From your last trip",
                        chips = lastTripChips
                    )
                )
            }
        }

        // 2. "You often buy on <weekday>" (>= 2 occurrences)
        val dayNameBn = when (currentDayOfWeek) {
            Calendar.SUNDAY -> "রবিবার"
            Calendar.MONDAY -> "সোমবার"
            Calendar.TUESDAY -> "মঙ্গলবার"
            Calendar.WEDNESDAY -> "বুধবার"
            Calendar.THURSDAY -> "বৃহস্পতিবার"
            Calendar.FRIDAY -> "শুক্রবার"
            Calendar.SATURDAY -> "শনিবার"
            else -> "আজ"
        }
        val dayNameEn = when (currentDayOfWeek) {
            Calendar.SUNDAY -> "Sundays"
            Calendar.MONDAY -> "Mondays"
            Calendar.TUESDAY -> "Tuesdays"
            Calendar.WEDNESDAY -> "Wednesdays"
            Calendar.THURSDAY -> "Thursdays"
            Calendar.FRIDAY -> "Fridays"
            Calendar.SATURDAY -> "Saturdays"
            else -> "today"
        }
        val freqNames = computeSmartSuggestions(completedTrips, currentDayOfWeek, minFrequency = 2)
        val freqChips = freqNames
            .filter { UnitConverterEngine.normalizeProductName(it) !in existingNormNames }
            .map { name ->
                PlanSuggestionChip(
                    id = "freq_$name",
                    name = name,
                    defaultUnit = "কেজি"
                )
            }
        if (freqChips.isNotEmpty()) {
            groups.add(
                PlanSuggestionGroup(
                    id = "weekday_freq",
                    titleBn = "প্রতি $dayNameBn আপনি কেনেন",
                    titleEn = "You often buy on $dayNameEn",
                    chips = freqChips
                )
            )
        }

        // 3. "Staples" (regularly purchased staple commodities)
        if (stapleProducts.isNotEmpty()) {
            val stapleChips = stapleProducts
                .filter { product ->
                    UnitConverterEngine.normalizeProductName(product.name) !in existingNormNames &&
                    product.normalizedName !in existingNormNames
                }
                .take(12)
                .map { product ->
                    PlanSuggestionChip(
                        id = "staple_${product.id}_${product.normalizedName}",
                        name = product.name,
                        defaultUnit = product.defaultUnit.ifBlank { "কেজি" },
                        estimatedPrice = if (product.latestUnitPrice > 0) product.latestUnitPrice else null
                    )
                }
            if (stapleChips.isNotEmpty()) {
                groups.add(
                    PlanSuggestionGroup(
                        id = "staples",
                        titleBn = "নিয়মিত প্রয়োজনীয় পণ্য",
                        titleEn = "Staples",
                        chips = stapleChips
                    )
                )
            }
        }

        // 4. "Templates" (Weekly staples, Fish & meat, Eid shopping)
        val weeklyItems = listOf(
            MarketItem(name = "আলু", quantity = 2.0, unit = "কেজি"),
            MarketItem(name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি"),
            MarketItem(name = "সয়াবিন তেল", quantity = 2.0, unit = "লিটার"),
            MarketItem(name = "মসুর ডাল", quantity = 1.0, unit = "কেজি"),
            MarketItem(name = "ডিম", quantity = 1.0, unit = "ডজন")
        )
        val fishMeatItems = listOf(
            MarketItem(name = "রুই মাছ", quantity = 1.0, unit = "কেজি"),
            MarketItem(name = "ব্রয়লার মুরগি", quantity = 2.0, unit = "কেজি"),
            MarketItem(name = "গরুর মাংস", quantity = 1.0, unit = "কেজি")
        )
        val eidItems = listOf(
            MarketItem(name = "চিনিগুঁড়া পোলাও চাল", quantity = 2.0, unit = "কেজি"),
            MarketItem(name = "খাসির মাংস", quantity = 2.0, unit = "কেজি"),
            MarketItem(name = "লাচ্ছা সেমাই", quantity = 2.0, unit = "প্যাকেট"),
            MarketItem(name = "কিশমিশ", quantity = 100.0, unit = "গ্রাম"),
            MarketItem(name = "কাজুবাদাম", quantity = 100.0, unit = "গ্রাম"),
            MarketItem(name = "গাওয়া ঘি", quantity = 1.0, unit = "বোতল")
        )

        val templateChips = mutableListOf<PlanSuggestionChip>()
        if (weeklyItems.any { UnitConverterEngine.normalizeProductName(it.name) !in existingNormNames }) {
            templateChips.add(
                PlanSuggestionChip(
                    id = "template_weekly",
                    name = "সাপ্তাহিক বাজার",
                    isTemplate = true,
                    templateItems = weeklyItems
                )
            )
        }
        if (fishMeatItems.any { UnitConverterEngine.normalizeProductName(it.name) !in existingNormNames }) {
            templateChips.add(
                PlanSuggestionChip(
                    id = "template_fish_meat",
                    name = "মাছ ও মাংস",
                    isTemplate = true,
                    templateItems = fishMeatItems
                )
            )
        }
        if (eidItems.any { UnitConverterEngine.normalizeProductName(it.name) !in existingNormNames }) {
            templateChips.add(
                PlanSuggestionChip(
                    id = "template_eid",
                    name = "ঈদের বাজার",
                    isTemplate = true,
                    templateItems = eidItems
                )
            )
        }

        if (templateChips.isNotEmpty()) {
            groups.add(
                PlanSuggestionGroup(
                    id = "templates",
                    titleBn = "রেডিমেড ফর্দ",
                    titleEn = "Shopping Templates",
                    chips = templateChips
                )
            )
        }

        return groups
    }

    private val BN_MONTH_NAMES = arrayOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    private val EN_MONTH_NAMES = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    /**
     * Groups completed market trips by year and month.
     * Invariants:
     * - Only includes completed trips (isCompleted == true)
     * - Item counts reflect purchased items only
     * - Total spend reflects purchased items sum
     * - Trips sorted chronologically descending within each month
     * - Months sorted chronologically descending
     */
    fun groupCompletedTripsByMonth(
        completedLists: List<MarketListWithItems>,
        isBangla: Boolean = true
    ): List<BazaarHistoryMonthGroup> {
        val completed = completedLists.filter { it.list.isCompleted }
        if (completed.isEmpty()) return emptyList()

        val cal = Calendar.getInstance()
        val tripSummaries = completed.map { withItems ->
            val purchased = withItems.items.filter { it.isPurchased }
            val unbought = withItems.items.filter { !it.isPurchased }
            val totalSpend = purchased.sumOf { it.amount ?: 0.0 }
            val preview = purchased.map { it.name }.take(4)
            val allNames = withItems.items.map { it.name }
            BazaarTripSummary(
                listId = withItems.list.id,
                title = withItems.list.title,
                createdAt = withItems.list.createdAt,
                purchasedItemCount = purchased.size,
                unboughtItemCount = unbought.size,
                totalSpend = totalSpend,
                purchasedItemsPreview = preview,
                allItemNames = allNames
            )
        }.sortedByDescending { it.createdAt }

        // Group by year and month
        val groupedMap = linkedMapOf<Pair<Int, Int>, MutableList<BazaarTripSummary>>()
        for (trip in tripSummaries) {
            cal.timeInMillis = trip.createdAt
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val key = Pair(y, m)
            groupedMap.getOrPut(key) { mutableListOf() }.add(trip)
        }

        return groupedMap.map { (ym, trips) ->
            val y = ym.first
            val m = ym.second.coerceIn(0, 11)
            val labelBn = "${BN_MONTH_NAMES[m]} ${DigitConverter.toBengaliDigits(y.toString())}"
            val labelEn = "${EN_MONTH_NAMES[m]} $y"
            BazaarHistoryMonthGroup(
                year = y,
                month = m,
                monthLabelBn = labelBn,
                monthLabelEn = labelEn,
                trips = trips,
                totalSpend = trips.sumOf { it.totalSpend },
                tripCount = trips.size
            )
        }
    }

    /**
     * Filters history month groups by matching list title OR any item name inside the trip.
     * Empty groups after filtering are excluded.
     */
    fun filterHistoryGroups(
        groups: List<BazaarHistoryMonthGroup>,
        query: String
    ): List<BazaarHistoryMonthGroup> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return groups

        return groups.mapNotNull { group ->
            val filteredTrips = group.trips.filter { trip ->
                trip.title.contains(trimmed, ignoreCase = true) ||
                trip.allItemNames.any { it.contains(trimmed, ignoreCase = true) }
            }
            if (filteredTrips.isEmpty()) {
                null
            } else {
                group.copy(
                    trips = filteredTrips,
                    totalSpend = filteredTrips.sumOf { it.totalSpend },
                    tripCount = filteredTrips.size
                )
            }
        }
    }

    /**
     * Calculates trip detail statistics for a completed or inspected market trip.
     * Invariants:
     * - Purchased items list: isPurchased == true
     * - Not bought items list: isPurchased == false
     * - Total spend: sum of purchased items
     */
    fun calculateTripDetailStats(trip: MarketListWithItems): BazaarTripDetailStats {
        val purchased = trip.items.filter { it.isPurchased }
        val unbought = trip.items.filter { !it.isPurchased }
        val totalSpend = purchased.sumOf { it.amount ?: 0.0 }
        return BazaarTripDetailStats(
            listId = trip.list.id,
            title = trip.list.title,
            createdAt = trip.list.createdAt,
            purchasedItems = purchased,
            unboughtItems = unbought,
            totalSpend = totalSpend
        )
    }
}

data class BazaarTripSummary(
    val listId: Long,
    val title: String,
    val createdAt: Long,
    val purchasedItemCount: Int,
    val unboughtItemCount: Int,
    val totalSpend: Double,
    val purchasedItemsPreview: List<String>,
    val allItemNames: List<String>
)

data class BazaarHistoryMonthGroup(
    val year: Int,
    val month: Int,
    val monthLabelBn: String,
    val monthLabelEn: String,
    val trips: List<BazaarTripSummary>,
    val totalSpend: Double,
    val tripCount: Int
)

data class BazaarTripDetailStats(
    val listId: Long,
    val title: String,
    val createdAt: Long,
    val purchasedItems: List<MarketItem>,
    val unboughtItems: List<MarketItem>,
    val totalSpend: Double,
    val purchasedCount: Int = purchasedItems.size,
    val unboughtCount: Int = unboughtItems.size
)

data class PlanSuggestionChip(
    val id: String,
    val name: String,
    val defaultUnit: String = "কেজি",
    val defaultQuantity: Double = 1.0,
    val estimatedPrice: Double? = null,
    val isTemplate: Boolean = false,
    val templateItems: List<MarketItem> = emptyList()
)

data class PlanSuggestionGroup(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val chips: List<PlanSuggestionChip>
)
