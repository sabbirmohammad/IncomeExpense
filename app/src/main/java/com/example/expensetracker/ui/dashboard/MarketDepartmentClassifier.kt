package com.example.expensetracker.ui.dashboard

/**
 * Heuristic classifier to categorize market items into standard grocery departments:
 * - veggies (শাকসবজি, কাঁচাবাজার)
 * - meat_fish (মাছ, মাংস)
 * - dairy_bakery (দুধ, ডিম, বেকারি)
 * - groceries (চাল, ডাল, তেল, মশলা ও অন্যান্য মুদি)
 */
internal fun getMarketItemCategory(name: String): String {
    val lower = name.lowercase()
    return when {
        lower.contains("শাক") || lower.contains("আলু") || lower.contains("পেঁয়াজ") || lower.contains("মরিচ") ||
        lower.contains("টমেটো") || lower.contains("বেগুন") || lower.contains("পটল") || lower.contains("লাউ") ||
        lower.contains("কুমড়া") || lower.contains("শসা") || lower.contains("গাজর") || lower.contains("রসুন") ||
        lower.contains("আদা") || lower.contains("লেবু") || lower.contains("সবজি") || lower.contains("কলা") ||
        lower.contains("ধনেপাতা") || lower.contains("কাঁচা") || lower.contains("vegetable") || lower.contains("potato") ||
        lower.contains("onion") || lower.contains("tomato") || lower.contains("chilli") -> "veggies"

        lower.contains("মাছ") || lower.contains("মাংস") || lower.contains("মুরগি") || lower.contains("গরু") ||
        lower.contains("খাসি") || lower.contains("ইলিশ") || lower.contains("রুই") || lower.contains("কাতলা") ||
        lower.contains("চিংড়ি") || lower.contains("fish") || lower.contains("meat") || lower.contains("chicken") ||
        lower.contains("beef") || lower.contains("mutton") || lower.contains("prawn") || lower.contains("shrimp") -> "meat_fish"

        lower.contains("দুধ") || lower.contains("ডিম") || lower.contains("দই") || lower.contains("মাখন") ||
        lower.contains("পনির") || lower.contains("রুটি") || lower.contains("বিস্কুট") || lower.contains("কেক") ||
        lower.contains("milk") || lower.contains("egg") || lower.contains("bread") || lower.contains("butter") ||
        lower.contains("cheese") || lower.contains("yogurt") || lower.contains("biscuit") -> "dairy_bakery"

        else -> "groceries"
    }
}

data class DepartmentMeta(
    val key: String,
    val titleBn: String,
    val titleEn: String,
    val icon: String
)

val ALL_DEPARTMENTS = listOf(
    DepartmentMeta("veggies", "কাঁচাবাজার", "Vegetables", "🥬"),
    DepartmentMeta("meat_fish", "মাছ ও মাংস", "Meat & Fish", "🐟"),
    DepartmentMeta("dairy_bakery", "দুধ ও বেকারি", "Dairy & Bakery", "🥛"),
    DepartmentMeta("groceries", "মুদি সদাই", "Groceries", "🧂")
)

fun getDepartmentMeta(category: String): DepartmentMeta {
    return ALL_DEPARTMENTS.find { it.key == category }
        ?: DepartmentMeta("groceries", "মুদি সদাই", "Groceries", "🧂")
}
