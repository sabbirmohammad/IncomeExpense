package com.example.expensetracker.common

import com.example.expensetracker.data.model.ProductPriceInsight

/**
 * Intelligent unit conversion and base unit normalization engine.
 * Converts real-world purchasing units (e.g. 500 grams, 1 hali, 1 powa, 1 palla)
 * into standard base units (কেজি, লিটার, পিস) for apples-to-apples price comparisons.
 */
object UnitConverterEngine {

    data class NormalizedQuantity(
        val baseQuantity: Double,
        val baseUnit: String,
        val isStandardUnit: Boolean = true
    )

    /**
     * Set of non-defining generic qualifiers (adjectives, origins, sizes, packaging)
     * that can safely be added or omitted without altering the underlying commodity.
     */
    val GENERIC_QUALIFIERS = setOf(
        // Bengali size/origin/package qualifiers
        "দেশি", "দেশী", "ফার্মের", "ফার্ম", "লাল", "সাদা", "কালো", "বড়", "বড়", "ছোট",
        "ফ্রেশ", "খোলা", "প্যাকেট", "প্যাক", "আমদানি", "হাইব্রিড", "নতুন", "পুরান",
        "পুরানো", "বগুড়ার", "বগুড়া", "মুন্সিগঞ্জের", "মুন্সিগঞ্জ", "রাজশাহীর", "রাজশাহী",
        "পাবনার", "পাবনা", "চায়না", "চাইনিজ", "ভারতীয়", "আস্ত", "কাটা", "কুচি",
        // English size/origin/package qualifiers
        "fresh", "local", "deshi", "red", "white", "black", "big", "small", "mini",
        "packet", "pack", "loose", "organic", "imported", "raw", "pure", "farm", "hybrid"
    ).map { java.text.Normalizer.normalize(it.lowercase(), java.text.Normalizer.Form.NFC) }.toSet()

    fun stripChandrabindu(text: String): String = text.replace("\u0981", "")

    /**
     * Normalizes a product name by canonicalizing Unicode (NFC), trimming, lowercasing,
     * stripping parenthetical notes, numbers and punctuation.
     */
    fun normalizeProductName(rawName: String): String {
        val nfc = java.text.Normalizer.normalize(rawName, java.text.Normalizer.Form.NFC)
        val withoutParentheses = nfc.trim()
            .lowercase()
            .replace(Regex("\\([^)]*\\)"), " ")
            .replace(Regex("\\[[^]]*\\]"), " ")
            .replace(Regex("[\\{\\}\\[\\]\\(\\)\\_\\-\\:\\,\\.\\;\\*\\#\\/\\\\\\|\\!\\?\\+\\=\\d০-৯]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        return withoutParentheses.ifBlank { nfc.trim().lowercase() }
    }

    /**
     * Converts any recognized quantity and unit into a standard base unit representation.
     */
    fun normalize(quantity: Double, unit: String): NormalizedQuantity {
        if (quantity <= 0.0) {
            return NormalizedQuantity(baseQuantity = 1.0, baseUnit = unit.trim().ifBlank { "পিস" })
        }

        val cleanUnit = unit.trim().lowercase()

        // 1. Weight / Mass -> Standard base unit: "কেজি"
        when {
            cleanUnit in listOf("কেজি", "কে.জি", "কেজি.", "কিলোগ্রাম", "কিলো", "kg", "kgs", "kilo", "kilogram") -> {
                return NormalizedQuantity(baseQuantity = quantity, baseUnit = "কেজি")
            }
            cleanUnit in listOf("গ্রাম", "গ্রা.", "গ্রাম.", "gm", "g", "gram", "grams") -> {
                return NormalizedQuantity(baseQuantity = quantity / 1000.0, baseUnit = "কেজি")
            }
            cleanUnit in listOf("পোয়া", "পোয়া", "powa", "poa") -> {
                return NormalizedQuantity(baseQuantity = quantity * 0.25, baseUnit = "কেজি")
            }
            cleanUnit in listOf("পাল্লা", "palla") -> {
                return NormalizedQuantity(baseQuantity = quantity * 5.0, baseUnit = "কেজি")
            }
            cleanUnit in listOf("ছটাক") -> {
                return NormalizedQuantity(baseQuantity = quantity * 0.06, baseUnit = "কেজি")
            }
        }

        // 2. Volume -> Standard base unit: "লিটার"
        when {
            cleanUnit in listOf("লিটার", "লি.", "লি", "liter", "litre", "ltr", "l", "lit") -> {
                return NormalizedQuantity(baseQuantity = quantity, baseUnit = "লিটার")
            }
            cleanUnit in listOf("মিলি", "মিলি.", "ml", "milliliter") -> {
                return NormalizedQuantity(baseQuantity = quantity / 1000.0, baseUnit = "লিটার")
            }
        }

        // 3. Count / Discrete -> Standard base unit: "পিস"
        when {
            cleanUnit in listOf("পিস", "পিচ", "টি", "টা", "দানা", "piece", "pieces", "pcs", "pc") -> {
                return NormalizedQuantity(baseQuantity = quantity, baseUnit = "পিস")
            }
            cleanUnit in listOf("হালি", "hali") -> {
                return NormalizedQuantity(baseQuantity = quantity * 4.0, baseUnit = "পিস")
            }
            cleanUnit in listOf("ডজন", "doz", "dozen") -> {
                return NormalizedQuantity(baseQuantity = quantity * 12.0, baseUnit = "পিস")
            }
            cleanUnit in listOf("জোড়া", "জোড়া", "pair", "pairs") -> {
                return NormalizedQuantity(baseQuantity = quantity * 2.0, baseUnit = "পিস")
            }
        }

        // 4. Packaging / Non-convertible standard units
        val standardPackUnits = listOf("প্যাকেট", "বোতল", "আঁটি", "বক্স", "ক্যান")
        for (std in standardPackUnits) {
            if (cleanUnit.contains(std)) {
                return NormalizedQuantity(baseQuantity = quantity, baseUnit = std)
            }
        }

        return NormalizedQuantity(baseQuantity = quantity, baseUnit = unit.trim().ifBlank { "পিস" }, isStandardUnit = false)
    }

    /**
     * Calculates the price per base unit given the total paid amount and raw quantity/unit.
     * Example: 5 kg for 300 tk -> 60 tk/kg.
     * Example: 500 g for 35 tk -> baseQty=0.5 kg -> 70 tk/kg.
     */
    fun calculateBaseUnitPrice(totalAmount: Double, quantity: Double, unit: String): Pair<Double, String> {
        val normalized = normalize(quantity, unit)
        val rate = if (normalized.baseQuantity > 0.0) {
            totalAmount / normalized.baseQuantity
        } else {
            totalAmount
        }
        return Pair(rate, normalized.baseUnit)
    }

    /**
     * Estimates cost for a target quantity and unit based on a known base unit price.
     * Example: base unit price = 60 tk/kg. Target = 2.5 kg -> 150 tk.
     * Target = 500 g -> baseQty=0.5 -> 30 tk.
     */
     fun estimateCost(baseUnitPrice: Double, targetQuantity: Double, targetUnit: String): Double {
         val normalized = normalize(targetQuantity, targetUnit)
         return normalized.baseQuantity * baseUnitPrice
     }

    /**
     * Safely finds a matching [ProductPriceInsight] for the given [productName].
     * 1. Exact match (case-insensitive on normalized names).
     * 2. Spacing-invariant exact match (e.g. "কাঁচামরিচ" vs "কাঁচা মরিচ").
     * 3. Chandrabindu-insensitive spacing-invariant match (e.g. "পেয়াজ" vs "পেঁয়াজ").
     * 4. Multi-tier token matching:
     *    - Matches multi-token commodity phrases (e.g. "রূপচাঁদা সয়াবিন তেল" vs "সয়াবিন তেল").
     *    - Allows standard qualifiers from [GENERIC_QUALIFIERS] ("দেশি আলু" vs "আলু").
     *    - Strictly rejects false single-token collisions on distinct commodities
     *      (e.g. "মিষ্টি আলু" must NOT match "আলু", "কাঁচা কলা" must NOT match "কলা",
     *      "চাল কুমড়া" must NOT match "কুমড়া", "আম" must NOT match "আমড়া").
     */
    fun findMatchingInsight(
        productName: String,
        insights: Map<String, ProductPriceInsight>
    ): ProductPriceInsight? {
        if (productName.isBlank() || insights.isEmpty()) return null

        val cleanQuery = normalizeProductName(productName)
        if (cleanQuery.isBlank()) return null

        // 1. Exact match in map key
        insights[cleanQuery]?.let { return it }

        // 1b. Exact case-insensitive match on keys or insight.productName
        val exactMatch = insights.entries.firstOrNull {
            it.key.equals(cleanQuery, ignoreCase = true) ||
            normalizeProductName(it.value.productName).equals(cleanQuery, ignoreCase = true)
        }
        if (exactMatch != null) return exactMatch.value

        // 2. Compact / Spacing-invariant match (e.g., "কাঁচা মরিচ" vs "কাঁচামরিচ")
        val compactQuery = cleanQuery.replace(" ", "")
        if (compactQuery.isNotBlank()) {
            val compactMatch = insights.entries.firstOrNull {
                it.key.replace(" ", "") == compactQuery ||
                normalizeProductName(it.value.productName).replace(" ", "") == compactQuery
            }
            if (compactMatch != null) return compactMatch.value
        }

        // 3. Chandrabindu-insensitive spacing-invariant match (e.g. "পেয়াজ" vs "পেঁয়াজ")
        val cbQuery = stripChandrabindu(compactQuery)
        if (cbQuery.isNotBlank()) {
            val cbMatch = insights.entries.firstOrNull {
                stripChandrabindu(it.key.replace(" ", "")) == cbQuery ||
                stripChandrabindu(normalizeProductName(it.value.productName).replace(" ", "")) == cbQuery
            }
            if (cbMatch != null) return cbMatch.value
        }

        // 4. Whole-word / token matching with qualifier discrimination
        val queryTokens = cleanQuery.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (queryTokens.isEmpty()) return null

        val candidates = mutableListOf<Pair<ProductPriceInsight, Int>>()

        for ((key, insight) in insights) {
            val keyClean = normalizeProductName(if (key.isNotBlank()) key else insight.productName)
            val keyTokens = keyClean.split(Regex("\\s+")).filter { it.isNotBlank() }
            if (keyTokens.isEmpty()) continue

            // Helper to check token equivalence (case & chandrabindu insensitive)
            fun tokensEqual(t1: String, t2: String): Boolean {
                if (t1.equals(t2, ignoreCase = true)) return true
                return stripChandrabindu(t1).equals(stripChandrabindu(t2), ignoreCase = true)
            }

            val isQuerySubsetOfKey = queryTokens.all { qTok -> keyTokens.any { kTok -> tokensEqual(kTok, qTok) } }
            val isKeySubsetOfQuery = keyTokens.all { kTok -> queryTokens.any { qTok -> tokensEqual(qTok, kTok) } }

            if (isQuerySubsetOfKey || isKeySubsetOfQuery) {
                val sharedTokens = queryTokens.count { qTok -> keyTokens.any { kTok -> tokensEqual(kTok, qTok) } }

                // Check extra (unshared) tokens
                val unsharedTokens = if (isKeySubsetOfQuery) {
                    queryTokens.filterNot { qTok -> keyTokens.any { kTok -> tokensEqual(kTok, qTok) } }
                } else {
                    keyTokens.filterNot { kTok -> queryTokens.any { qTok -> tokensEqual(qTok, kTok) } }
                }

                // If only 1 token is shared, ALL unshared tokens MUST be recognized generic qualifiers.
                // This prevents "মিষ্টি আলু" (sweet potato) from falsely matching "আলু" (potato),
                // "কাঁচা কলা" from falsely matching "কলা", "চাল কুমড়া" from matching "কুমড়া", etc.
                val areUnsharedAllQualifiers = unsharedTokens.all { unshared ->
                    val cleanUnshared = stripChandrabindu(unshared.lowercase())
                    GENERIC_QUALIFIERS.any { q -> stripChandrabindu(q.lowercase()) == cleanUnshared }
                }

                val isValidMatch = if (sharedTokens >= 2) {
                    true // High-confidence multi-token commodity match (e.g. "রূপচাঁদা সয়াবিন তেল" vs "সয়াবিন তেল")
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
