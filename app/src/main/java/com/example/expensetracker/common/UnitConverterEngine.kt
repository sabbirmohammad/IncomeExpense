package com.example.expensetracker.common

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
     * Normalizes a product name by trimming, lowercasing, stripping parenthetical notes, numbers and punctuation.
     */
    fun normalizeProductName(rawName: String): String {
        val withoutParentheses = rawName.trim()
            .lowercase()
            .replace(Regex("\\([^)]*\\)"), " ")
            .replace(Regex("\\[[^]]*\\]"), " ")
            .replace(Regex("[\\{\\}\\_\\-\\:\\,\\.\\;\\*\\#\\d০-৯]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        return withoutParentheses.ifBlank { rawName.trim().lowercase() }
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
}
