package com.example.expensetracker.common

import com.example.expensetracker.data.model.ProductPriceInsight

internal object CommodityInsightMatcher {

    /**
     * Safely finds a matching [ProductPriceInsight] for the given [productName].
     * 1. Exact match (case-insensitive on normalized names).
     * 2. Spacing-invariant exact match (e.g. "কাঁচামরিচ" vs "কাঁচা মরিচ").
     * 3. Chandrabindu-insensitive spacing-invariant match (e.g. "পেয়াজ" vs "পেঁয়াজ").
     * 4. Multi-tier token matching with qualifier discrimination.
     */
    fun findMatchingInsight(
        productName: String,
        insights: Map<String, ProductPriceInsight>
    ): ProductPriceInsight? {
        if (productName.isBlank() || insights.isEmpty()) return null

        val cleanQuery = UnitConverterEngine.normalizeProductName(productName)
        if (cleanQuery.isBlank()) return null

        // 1. Exact match in map key
        insights[cleanQuery]?.let { return it }

        // 1b. Exact case-insensitive match on keys or insight.productName
        val exactMatch = insights.entries.firstOrNull {
            it.key.equals(cleanQuery, ignoreCase = true) ||
            UnitConverterEngine.normalizeProductName(it.value.productName).equals(cleanQuery, ignoreCase = true)
        }
        if (exactMatch != null) return exactMatch.value

        // 2. Compact / Spacing-invariant match (e.g., "কাঁচা মরিচ" vs "কাঁচামরিচ")
        val compactQuery = cleanQuery.replace(" ", "")
        if (compactQuery.isNotBlank()) {
            val compactMatch = insights.entries.firstOrNull {
                it.key.replace(" ", "") == compactQuery ||
                UnitConverterEngine.normalizeProductName(it.value.productName).replace(" ", "") == compactQuery
            }
            if (compactMatch != null) return compactMatch.value
        }

        // 3. Chandrabindu-insensitive spacing-invariant match (e.g. "পেয়াজ" vs "পেঁয়াজ")
        val cbQuery = UnitConverterEngine.stripChandrabindu(compactQuery)
        if (cbQuery.isNotBlank()) {
            val cbMatch = insights.entries.firstOrNull {
                UnitConverterEngine.stripChandrabindu(it.key.replace(" ", "")) == cbQuery ||
                UnitConverterEngine.stripChandrabindu(UnitConverterEngine.normalizeProductName(it.value.productName).replace(" ", "")) == cbQuery
            }
            if (cbMatch != null) return cbMatch.value
        }

        // 4. Whole-word / token matching with qualifier discrimination
        val queryTokens = cleanQuery.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (queryTokens.isEmpty()) return null

        val candidates = mutableListOf<Pair<ProductPriceInsight, Int>>()

        for ((key, insight) in insights) {
            val keyClean = UnitConverterEngine.normalizeProductName(if (key.isNotBlank()) key else insight.productName)
            val keyTokens = keyClean.split(Regex("\\s+")).filter { it.isNotBlank() }
            if (keyTokens.isEmpty()) continue

            fun tokensEqual(t1: String, t2: String): Boolean {
                if (t1.equals(t2, ignoreCase = true)) return true
                return UnitConverterEngine.stripChandrabindu(t1).equals(UnitConverterEngine.stripChandrabindu(t2), ignoreCase = true)
            }

            val isQuerySubsetOfKey = queryTokens.all { qTok -> keyTokens.any { kTok -> tokensEqual(kTok, qTok) } }
            val isKeySubsetOfQuery = keyTokens.all { kTok -> queryTokens.any { qTok -> tokensEqual(qTok, kTok) } }

            if (isQuerySubsetOfKey || isKeySubsetOfQuery) {
                val sharedTokens = queryTokens.count { qTok -> keyTokens.any { kTok -> tokensEqual(kTok, qTok) } }

                val unsharedTokens = if (isKeySubsetOfQuery) {
                    queryTokens.filterNot { qTok -> keyTokens.any { kTok -> tokensEqual(qTok, kTok) } }
                } else {
                    keyTokens.filterNot { kTok -> queryTokens.any { qTok -> tokensEqual(qTok, kTok) } }
                }

                val areUnsharedAllQualifiers = unsharedTokens.all { unshared ->
                    val cleanUnshared = UnitConverterEngine.stripChandrabindu(unshared.lowercase())
                    UnitConverterEngine.GENERIC_QUALIFIERS.any { q -> UnitConverterEngine.stripChandrabindu(q.lowercase()) == cleanUnshared }
                }

                val isValidMatch = if (sharedTokens >= 2) {
                    true
                } else if (sharedTokens == 1) {
                    areUnsharedAllQualifiers
                } else {
                    false
                }

                if (isValidMatch) {
                    val lengthDiff = kotlin.math.abs(queryTokens.size - keyTokens.size)
                    val score = (sharedTokens * 100) - lengthDiff
                    candidates.add(Pair(insight, score))
                }
            }
        }

        return candidates.maxWithOrNull(
            compareBy<Pair<ProductPriceInsight, Int>> { it.second }
                .thenBy { it.first.lastPurchasedTimestamp }
        )?.first
    }
}
