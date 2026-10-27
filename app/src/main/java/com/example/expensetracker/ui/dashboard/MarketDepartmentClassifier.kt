package com.example.expensetracker.ui.dashboard

/**
 * Heuristic classifier to categorize market items into standard grocery departments:
 * - veggies (শাকসবজি, কাঁচাবাজার) - e.g. আলু, পটল, পেঁয়াজ, শাক
 * - meat_fish (মাছ, মাংস) - e.g. ইলিশ, মুরগি, খাসি, গরু
 * - dairy_bakery (দুধ, ডিম, বেকারি) - e.g. দুধ, ডিম, পাউরুটি, মাখন
 * - groceries (চাল, ডাল, তেল, মশলা ও অন্যান্য মুদি) - e.g. সয়াবিন তেল, সরিষার তেল, চাল, মসুর ডাল, লবণ
 */
fun getMarketItemCategory(name: String): String {
    val lower = name.lowercase().trim()
    return when {
        // Priority 1: Oils, Spices, Dry Grocery, Grains (Must precede veggie keywords to prevent 'মরিচ গুঁড়া' going to veggies)
        lower.contains("তেল") || lower.contains("oil") || lower.contains("সয়াবিন") || lower.contains("সয়াবিন") ||
        lower.contains("সরিষা") || lower.contains("ঘি") || lower.contains("ghee") ||
        lower.contains("চাল") || lower.contains("rice") || lower.contains("ডাল") || lower.contains("lentil") ||
        lower.contains("মসলা") || lower.contains("মশলা") || lower.contains("spice") || lower.contains("লবণ") ||
        lower.contains("নুন") || lower.contains("salt") || lower.contains("চিনি") || lower.contains("sugar") ||
        lower.contains("আটা") || lower.contains("ময়দা") || lower.contains("ময়দা") || lower.contains("সুজি") ||
        lower.contains("flour") || lower.contains("চা পাতা") || lower.contains("tea") || lower.contains("হলুদ") ||
        lower.contains("জিরা") || lower.contains("ধনিয়া") || lower.contains("এলাচ") || lower.contains("দারুচিনি") ||
        lower.contains("তেজপাতা") || lower.contains("গুঁড়া") || lower.contains("গুড়া") || lower.contains("powder") ||
        lower.contains("সস") || lower.contains("sauce") || lower.contains("ভিনেগার") || lower.contains("vinegar") ||
        lower.contains("নুডলস") || lower.contains("noodles") || lower.contains("পাস্তা") || lower.contains("pasta") -> "groceries"

        // Priority 2: Fresh Vegetables & Produce (কাঁচাবাজার)
        lower.contains("শাক") || lower.contains("আলু") || lower.contains("পেঁয়াজ") || lower.contains("পেয়াজ") ||
        lower.contains("মরিচ") || lower.contains("টমেটো") || lower.contains("বেগুন") || lower.contains("পটল") ||
        lower.contains("লাউ") || lower.contains("কুমড়া") || lower.contains("কুমড়া") || lower.contains("শসা") ||
        lower.contains("গাজর") || lower.contains("রসুন") || lower.contains("আদা") || lower.contains("লেবু") ||
        lower.contains("সবজি") || lower.contains("কলা") || lower.contains("ধনেপাতা") || lower.contains("কাঁচা") ||
        lower.contains("ফুলকপি") || lower.contains("বাঁধাকপি") || lower.contains("ঢেঁড়স") || lower.contains("ঢেঁড়শ") ||
        lower.contains("চিচিঙ্গা") || lower.contains("ঝিঙে") || lower.contains("ঝিঙ্গা") || lower.contains("করলা") ||
        lower.contains("উচ্ছে") || lower.contains("পেঁপে") || lower.contains("পেপে") || lower.contains("সজনে") ||
        lower.contains("সিম") || lower.contains("শিম") || lower.contains("বরবটি") || lower.contains("মুলা") ||
        lower.contains("মটরশুঁটি") || lower.contains("ক্যাপসিকাম") || lower.contains("vegetable") || lower.contains("potato") ||
        lower.contains("onion") || lower.contains("tomato") || lower.contains("chilli") || lower.contains("cabbage") ||
        lower.contains("cauliflower") || lower.contains("carrot") || lower.contains("cucumber") || lower.contains("ginger") ||
        lower.contains("garlic") || lower.contains("lemon") || lower.contains("coriander") || lower.contains("spinach") ||
        lower.contains("brinjal") || lower.contains("eggplant") || lower.contains("gourd") -> "veggies"

        // Priority 3: Fish & Meat (মাছ ও মাংস)
        lower.contains("মাছ") || lower.contains("মাংস") || lower.contains("মুরগি") || lower.contains("মুরগী") ||
        lower.contains("চিকেন") || lower.contains("গরু") || lower.contains("বিফ") || lower.contains("খাসি") ||
        lower.contains("মাটন") || lower.contains("ইলিশ") || lower.contains("রুই") || lower.contains("কাতলা") ||
        lower.contains("চিংড়ি") || lower.contains("চিংড়ী") || lower.contains("পাঙ্গাস") || lower.contains("তেলাপিয়া") ||
        lower.contains("কৈ") || lower.contains("শিং") || lower.contains("মাগুর") || lower.contains("বোয়াল") ||
        lower.contains("fish") || lower.contains("meat") || lower.contains("chicken") || lower.contains("beef") ||
        lower.contains("mutton") || lower.contains("prawn") || lower.contains("shrimp") -> "meat_fish"

        // Priority 4: Dairy & Bakery (দুধ ও বেকারি)
        lower.contains("দুধ") || lower.contains("ডিম") || lower.contains("দই") || lower.contains("মাখন") ||
        lower.contains("পনির") || lower.contains("রুটি") || lower.contains("পাউরুটি") || lower.contains("টোস্ট") ||
        lower.contains("বিস্কুট") || lower.contains("কেক") || lower.contains("milk") || lower.contains("egg") ||
        lower.contains("bread") || lower.contains("butter") || lower.contains("cheese") || lower.contains("yogurt") ||
        lower.contains("biscuit") || lower.contains("toast") -> "dairy_bakery"

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
