package com.example.expensetracker.ui.dashboard

import com.example.expensetracker.common.UnitConverterEngine
import java.text.Normalizer
import java.util.concurrent.ConcurrentHashMap

/**
 * Registry for runtime department classification overrides.
 */
object MarketDepartmentClassifier {
    private val runtimeOverrides = ConcurrentHashMap<String, String>()

    fun setOverride(productName: String, deptId: String) {
        val normalized = UnitConverterEngine.normalizeProductName(productName)
        if (normalized.isNotBlank()) {
            runtimeOverrides[normalized] = deptId
        }
    }

    fun getOverride(productName: String): String? {
        val normalized = UnitConverterEngine.normalizeProductName(productName)
        return runtimeOverrides[normalized]
    }

    fun clearOverrides() {
        runtimeOverrides.clear()
    }

    fun getMarketItemCategory(name: String): String {
        return com.example.expensetracker.ui.dashboard.getMarketItemCategory(name)
    }
}

/**
 * Heuristic classifier to categorize market items into standard grocery departments:
 * - veggies (শাকসবজি, কাঁচাবাজার) - e.g. আলু, পটল, পেঁয়াজ, শাক
 * - meat_fish (মাছ, মাংস) - e.g. ইলিশ, মুরগি, খাসি, গরু
 * - dairy_bakery (দুধ, ডিম, বেকারি) - e.g. দুধ, ডিম, পাউরুটি, মাখন
 * - groceries (চাল, ডাল, তেল, মশলা ও অন্যান্য মুদি) - e.g. সয়াবিন তেল, সরিষার তেল, চাল, মসুর ডাল, লবণ
 */
fun getMarketItemCategory(name: String): String {
    val override = MarketDepartmentClassifier.getOverride(name)
    if (override != null) return override

    val normalized = Normalizer.normalize(name.trim().lowercase(), Normalizer.Form.NFC)
    val lower = normalized
        .replace("\u09a1\u09bc", "\u09dc") // ড + nukta -> ড়
        .replace("\u09a2\u09bc", "\u09dd") // ঢ + nukta -> ঢ়
        .replace("\u09af\u09bc", "\u09df") // য + nukta -> য়
    return when {
        // High-Priority Disambiguation: Fish roe (মাছের ডিম) must classify as meat_fish despite containing 'ডিম'
        lower.contains("মাছের ডিম") || lower.contains("মাছের ডিম্ব") -> "meat_fish"

        // Priority 1: Oils, Spices, Dry Grocery, Grains
        // Must precede veggie keywords to prevent 'মরিচ গুঁড়া' going to veggies.
        // Tilapia ('তেলাপিয়া'), Wax gourd ('চালকুমড়া'), Mustard greens ('সরিষার শাক'), and Powdered milk ('গুঁড়া দুধ')
        // are guarded so they route to their natural departments (meat_fish, veggies, dairy_bakery).
        ((lower.contains("তেল") || lower.contains("oil")) && !lower.contains("তেলাপিয়া") && !lower.contains("তেলাপিয়া") && !lower.contains("tilapia")) ||
        lower.contains("সয়াবিন") || lower.contains("সয়াবিন") ||
        (lower.contains("সরিষা") && !lower.contains("শাক")) ||
        lower.contains("ঘি") || lower.contains("ghee") ||
        ((lower.contains("চাল") || lower.contains("rice")) && !lower.contains("চালকুমড়া") && !lower.contains("চালকুমড়া") && !lower.contains("চাল কুমড়া") && !lower.contains("চাল কুমড়া")) ||
        lower.contains("ডাল") || lower.contains("lentil") ||
        lower.contains("মসলা") || lower.contains("মশলা") || lower.contains("spice") ||
        lower.contains("লবণ") || lower.contains("নুন") || lower.contains("salt") ||
        lower.contains("চিনি") || lower.contains("sugar") ||
        lower.contains("আটা") || lower.contains("ময়দা") || lower.contains("ময়দা") || lower.contains("সুজি") ||
        lower.contains("flour") || lower.contains("চা পাতা") || lower.contains("tea") ||
        lower.contains("হলুদ") || lower.contains("জিরা") || lower.contains("ধনিয়া") || lower.contains("এলাচ") ||
        lower.contains("দারুচিনি") || lower.contains("তেজপাতা") ||
        ((lower.contains("গুঁড়া") || lower.contains("গুড়া") || lower.contains("গুঁড়ো") || lower.contains("গুড়ো") || lower.contains("powder")) &&
            !lower.contains("বগুড়া") && !lower.contains("দই") && !lower.contains("দুধ") && !lower.contains("milk") && !lower.contains("আলু")) ||
        ((lower.contains("সস") || lower.contains("sauce")) && !lower.contains("সবজি")) ||
        lower.contains("ভিনেগার") || lower.contains("vinegar") ||
        lower.contains("নুডলস") || lower.contains("noodles") || lower.contains("পাস্তা") || lower.contains("pasta") -> "groceries"

        // Priority 2: Fresh Vegetables & Produce (কাঁচাবাজার)
        lower.contains("শাক") || lower.contains("সবজি") || lower.contains("vegetable") ||
        lower.contains("আলু") || lower.contains("potato") ||
        lower.contains("পেঁয়াজ") || lower.contains("পেয়াজ") || lower.contains("পেঁয়াজ") || lower.contains("পেয়াজ") || lower.contains("onion") || lower.contains("shallot") ||
        lower.contains("মরিচ") || lower.contains("chilli") || lower.contains("chili") ||
        lower.contains("টমেটো") || lower.contains("tomato") ||
        lower.contains("বেগুন") || lower.contains("তালবেগুন") || lower.contains("eggplant") || lower.contains("brinjal") ||
        lower.contains("পটল") || lower.contains("লাউ") ||
        lower.contains("কুমড়া") || lower.contains("কুমড়া") || lower.contains("চালকুমড়া") || lower.contains("চালকুমড়া") || lower.contains("চাল কুমড়া") || lower.contains("চাল কুমড়া") || lower.contains("মিষ্টি কুমড়া") || lower.contains("মিষ্টি কুমড়া") || lower.contains("gourd") || lower.contains("pumpkin") ||
        lower.contains("শসা") || lower.contains("cucumber") ||
        lower.contains("গাজর") || lower.contains("carrot") ||
        lower.contains("রসুন") || lower.contains("garlic") ||
        lower.contains("আদা") || lower.contains("ginger") ||
        lower.contains("লেবু") || lower.contains("lemon") || lower.contains("lime") ||
        lower.contains("কলা") || lower.contains("ধনেপাতা") || lower.contains("ধনে পাতা") || lower.contains("coriander") || lower.contains("spinach") ||
        lower.contains("কাঁচা") ||
        lower.contains("ফুলকপি") || lower.contains("ফুল কপি") || lower.contains("বাঁধাকপি") || lower.contains("বাঁধা কপি") || lower.contains("পাতাকপি") || lower.contains("পাতা কপি") || lower.contains("কপি") || lower.contains("cabbage") || lower.contains("cauliflower") ||
        lower.contains("ঢেঁড়স") || lower.contains("ঢেঁড়শ") || lower.contains("ঢেড়স") || lower.contains("ঢেড়শ") || lower.contains("ঢেঁড়স") || lower.contains("ঢেঁড়শ") || lower.contains("ঢেড়স") || lower.contains("ঢেড়শ") || lower.contains("okra") || lower.contains("lady finger") || lower.contains("ladies finger") || lower.contains("ভেন্ডি") ||
        lower.contains("চিচিঙ্গা") || lower.contains("ঝিঙে") || lower.contains("ঝিঙ্গা") || lower.contains("করলা") || lower.contains("উচ্ছে") ||
        lower.contains("পেঁপে") || lower.contains("পেপে") || lower.contains("সজনে") ||
        lower.contains("সিম") || lower.contains("শিম") || lower.contains("বরবটি") || lower.contains("মুলা") ||
        lower.contains("মটরশুঁটি") || lower.contains("মটরশুটি") ||
        lower.contains("ক্যাপসিকাম") || lower.contains("capsicum") || lower.contains("bell pepper") -> "veggies"

        // Priority 3: Dairy & Bakery (দুধ, ডিম, বেকারি)
        // Evaluated before meat prefixes so 'গরুর দুধ' and 'মুরগির ডিম' classify under dairy_bakery
        lower.contains("দুধ") || lower.contains("milk") ||
        lower.contains("ডিম") || (lower.contains("egg") && !lower.contains("eggplant")) ||
        lower.contains("দই") || lower.contains("yogurt") || lower.contains("curd") ||
        lower.contains("মাখন") || lower.contains("butter") ||
        lower.contains("ছানা") || lower.contains("পনির") || lower.contains("cheese") ||
        lower.contains("পাউরুটি") || lower.contains("বনরুটি") || lower.contains("রুটি") || lower.contains("bread") ||
        lower.contains("টোস্ট") || lower.contains("toast") ||
        lower.contains("বিস্কুট") || lower.contains("বিস্কিট") || lower.contains("biscuit") || lower.contains("cookie") ||
        lower.contains("কেক") || lower.contains("cake") -> "dairy_bakery"

        // Priority 4: Fish & Meat (মাছ ও মাংস)
        lower.contains("মাছ") || lower.contains("fish") ||
        lower.contains("মাংস") || lower.contains("meat") ||
        lower.contains("মুরগি") || lower.contains("মুরগী") || lower.contains("চিকেন") || lower.contains("chicken") ||
        lower.contains("গরু") || lower.contains("বিফ") || lower.contains("beef") ||
        lower.contains("খাসি") || lower.contains("মাটন") || lower.contains("mutton") ||
        lower.contains("হাঁস") || lower.contains("ছাগল") || lower.contains("duck") ||
        lower.contains("ইলিশ") || lower.contains("রুই") || lower.contains("কাতলা") ||
        lower.contains("চিংড়ি") || lower.contains("চিংড়ি") || lower.contains("চিংড়ী") || lower.contains("চিংড়ী") || lower.contains("prawn") || lower.contains("shrimp") ||
        lower.contains("পাঙ্গাস") || lower.contains("পাঙ্গাশ") ||
        lower.contains("তেলাপিয়া") || lower.contains("তেলাপিয়া") || lower.contains("tilapia") ||
        lower.contains("কৈ") || lower.contains("শিং") || lower.contains("মাগুর") || lower.contains("বোয়াল") || lower.contains("বোয়াল") -> "meat_fish"

        else -> "groceries"
    }
}

/**
 * Standard Market Departments enum with enhanced Bengali and English titles, icons, and ordering.
 */
enum class MarketDepartment(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val icon: String,
    val order: Int
) {
    VEGGIES("veggies", "কাঁচাবাজার ও সবজি", "Vegetables & Greens", "🥬", 0),
    MEAT_FISH("meat_fish", "মাছ ও মাংস", "Meat & Fish", "🐟", 1),
    DAIRY_BAKERY("dairy_bakery", "দুধ ও বেকারি", "Dairy & Bakery", "🥛", 2),
    GROCERIES("groceries", "মুদি ও মসলা", "Groceries & Spices", "🧂", 3);

    fun toDepartmentMeta(): DepartmentMeta = DepartmentMeta(id, titleBn, titleEn, icon)

    companion object {
        fun fromKey(key: String): MarketDepartment =
            entries.firstOrNull { it.id == key } ?: GROCERIES

        fun fromItemName(name: String): MarketDepartment =
            fromKey(getMarketItemCategory(name))
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
