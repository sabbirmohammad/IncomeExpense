package com.example.expensetracker.common

import com.example.expensetracker.data.model.MarketItem

object MarketListParser {

    private val BENGALI_DIGITS = mapOf(
        '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
        '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9'
    )

    private val FRACTION_MAP = mapOf(
        "হাফ" to 0.5,
        "আধা" to 0.5,
        "১/২" to 0.5,
        "1/2" to 0.5,
        "দেড়" to 1.5,
        "দেড়" to 1.5,
        "আড়াই" to 2.5,
        "আড়াই" to 2.5,
        "১/৪" to 0.25,
        "1/4" to 0.25,
        "৩/৪" to 0.75,
        "3/4" to 0.75,
        "পোয়া" to 0.25,
        "এক পোয়া" to 0.25,
        "১ পোয়া" to 0.25
    )

    // Recognized units mapped to standard Bangla unit strings using Unicode lookaround boundaries
    private val UNIT_PATTERNS = listOf(
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(কেজি|কে\\.জি|কেজি\\.|কিলোগ্রাম|কিলো|kg|kgs|kilo|kilogram)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "কেজি",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(লিটার|লি\\.|লি|liter|litre|ltr|l|lit)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "লিটার",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(পিস|পিচ|টি|টা|দানা|piece|pieces|pcs|pc)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "পিস",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(গ্রাম|গ্রা\\.|গ্রাম\\.|gm|g|gram|grams)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "গ্রাম",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(ডজন|doz|dozen)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "ডজন",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(প্যাকেট|প্যাক|packet|packets|pack|packs|pkt)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "প্যাকেট",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(হালি|hali)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "হালি",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(পোয়া|পোয়া|powa|poa)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "পোয়া",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(পাল্লা|palla)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "পাল্লা",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(জোড়া|জোড়া|pair|pairs)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "জোড়া",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(আঁটি|আটি|মুঠা)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "আঁটি",
        Regex("(?i)(?<=^|[\\s\\d০-৯\\-\\.\\,])(বোতল|bottle|btl)(?=$|[\\s\\d০-৯\\-\\.\\,])") to "বোতল"
    )

    /**
     * Converts any Bengali digits in the string to standard ASCII digits (0-9).
     */
    fun convertBengaliDigitsToEnglish(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(BENGALI_DIGITS[ch] ?: ch)
        }
        return sb.toString()
    }

    /**
     * Parses a bulk text string (typically pasted from WhatsApp or Notes) into a list of [MarketItem].
     */
    fun parseBulkText(text: String): List<MarketItem> {
        val lines = text.split("\n", "\r\n", "।", ";")
        val items = mutableListOf<MarketItem>()

        for (rawLine in lines) {
            val cleaned = cleanLine(rawLine)
            if (cleaned.isNotBlank()) {
                val item = parseSingleLine(cleaned)
                if (item != null) {
                    items.add(item)
                }
            }
        }
        return items
    }

    /**
     * Cleans list prefixes such as "1.", "1)", "-", "*", "[ ]", etc.
     */
    private fun cleanLine(line: String): String {
        var result = line.trim()
        // Remove markdown or list bullets (including parenthesized numbers like (1) and (১))
        result = result.replace(Regex("^(\\([0-9০-৯]+\\)|\\[[0-9০-৯]+\\]|\\d+[\\.\\)\\:\\-]|[০-৯]+[\\.\\)\\:\\-]|[\\-\\*\\•\\+\\>\\–\\—\\✔\\✓\\☑\\[\\]\\(\\)])\\s*"), "")
        // Remove trailing commas, dashes, colons
        result = result.replace(Regex("[,;\\-\\:]+$"), "").trim()
        return result
    }

    /**
     * Parses a single line into a [MarketItem].
     */
    fun parseSingleLine(line: String): MarketItem? {
        val originalTrimmed = line.trim()
        if (originalTrimmed.isBlank()) return null

        var workingLine = originalTrimmed
        var detectedQuantity = 1.0
        var detectedUnit = "কেজি"
        var quantityFound = false
        var unitFound = false

        // 1. Check for special fraction words first (যেমন: দেড়, আড়াই, হাফ, পোয়া)
        for ((fractionWord, fracValue) in FRACTION_MAP) {
            if (workingLine.contains(fractionWord)) {
                detectedQuantity = fracValue
                quantityFound = true
                workingLine = workingLine.replace(fractionWord, " ").trim()
                break
            }
        }

        // 2. Detect unit from UNIT_PATTERNS
        for ((regex, stdUnit) in UNIT_PATTERNS) {
            val match = regex.find(workingLine)
            if (match != null) {
                detectedUnit = stdUnit
                unitFound = true
                workingLine = workingLine.removeRange(match.range).trim()
                break
            }
        }

        // 3. Detect numeric quantity directly supporting both Bengali and English digits
        if (!quantityFound) {
            val numberMatch = Regex("([0-9০-৯]+(\\.[0-9০-৯]+)?)").find(workingLine)
            if (numberMatch != null) {
                val rawNumStr = numberMatch.value
                val englishNumStr = convertBengaliDigitsToEnglish(rawNumStr)
                val parsedNum = englishNumStr.toDoubleOrNull()
                if (parsedNum != null && parsedNum > 0) {
                    detectedQuantity = parsedNum
                    quantityFound = true
                    workingLine = workingLine.removeRange(numberMatch.range).trim()
                }
            }
        }

        // 4. If "পোয়া" was found and unit wasn't specified or was default, map to 250 গ্রাম
        if (detectedQuantity == 0.25 && !unitFound) {
            detectedQuantity = 250.0
            detectedUnit = "গ্রাম"
        }

        // 5. Clean up product name
        var productName = workingLine
            .replace(Regex("[\\-\\:\\=\\(\\)\\,\\/]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        // If product name became empty (e.g. user entered just "2 kg"), use original
        if (productName.isBlank()) {
            productName = originalTrimmed
        }

        // If unit wasn't explicitly found, select a smart default:
        if (!unitFound) {
            detectedUnit = when {
                detectedQuantity >= 50.0 -> "গ্রাম"
                productName.contains("তেল") || productName.contains("দুধ") || productName.contains("oil", ignoreCase = true) || productName.contains("milk", ignoreCase = true) -> "লিটার"
                productName.contains("ডিম") || productName.contains("egg", ignoreCase = true) -> "পিস"
                productName.contains("সাবান") || productName.contains("শ্যাম্পু") || productName.contains("soap", ignoreCase = true) -> "পিস"
                else -> "কেজি"
            }
        }

        return MarketItem(
            name = productName,
            quantity = detectedQuantity,
            unit = detectedUnit
        )
    }
}
