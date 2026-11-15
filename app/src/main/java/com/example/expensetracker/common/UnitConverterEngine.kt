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
     * stripping ZWJ/ZWNJ, parenthetical notes, numbers and punctuation.
     */
    fun normalizeProductName(rawName: String): String {
        val nfc = java.text.Normalizer.normalize(rawName, java.text.Normalizer.Form.NFC)
        val withoutParentheses = nfc.trim()
            .lowercase()
            .replace("\u200C", "")
            .replace("\u200D", "")
            .replace(Regex("\\([^)]*\\)"), " ")
            .replace(Regex("\\[[^]]*\\]"), " ")
            .replace(Regex("[\\{\\}\\[\\]\\(\\)\\_\\-\\:\\,\\.\\;\\*\\#\\/\\\\\\|\\!\\?\\+\\=\\d০-৯]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        return withoutParentheses.ifBlank { nfc.trim().lowercase() }
    }

    /**
     * Canonical key for commodity deduplication and merging per V7 C6:
     * Ignores spaces and ZWJ/ZWNJ so "কাঁচা মরিচ" and "কাঁচামরিচ" merge cleanly.
     */
    fun canonicalCommodityKey(rawName: String): String {
        return normalizeProductName(rawName).replace(" ", "")
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
     */
    fun estimateCost(baseUnitPrice: Double, targetQuantity: Double, targetUnit: String): Double {
        val normalized = normalize(targetQuantity, targetUnit)
        return normalized.baseQuantity * baseUnitPrice
    }

    /**
     * Safely finds a matching [ProductPriceInsight] for the given [productName].
     */
    fun findMatchingInsight(
        productName: String,
        insights: Map<String, ProductPriceInsight>
    ): ProductPriceInsight? = CommodityInsightMatcher.findMatchingInsight(productName, insights)
}
