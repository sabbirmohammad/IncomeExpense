package com.example.expensetracker

import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.dashboard.MarketDepartment
import com.example.expensetracker.ui.dashboard.getDepartmentMeta
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Empirical Stress-Testing Harness for Milestone M3 (R3):
 * 1. Department categorization across edge-case Bengali commodity names, compound names, spelling variants, and transliterated items.
 * 2. Live progress counter logic: 0 items, all items, dynamic updates, and edge conditions.
 * 3. Collapsible department mechanics, ordering, and empty-set suppression.
 * 4. Accessibility compliance: 48dp minimum touch targets on headers, pills, and checkboxes.
 * 5. Theme hygiene and semantic color resolution.
 */
class ChallengerM3EmpiricalStressTest {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        val resolved = if (file.exists()) file else File("app/$basePath/$relativePath")
        assertTrue("Expected file to exist: ${resolved.absolutePath}", resolved.exists())
        return resolved.readText()
    }

    // =========================================================================
    // 1. DEPARTMENT CATEGORIZATION: BENGALI COMMODITY NAMES & SPELLING VARIANTS
    // =========================================================================

    @Test
    fun testDepartmentCategorizationBengaliCommoditiesAndVariants() {
        // --- 1.1 Fresh Produce (VEGGIES) ---
        val veggieItems = listOf(
            "আলু", "গোল আলু", "নতুন আলু", "লাল আলু", "বগুড়ার আলু",
            "পেঁয়াজ", "পেয়াজ", "পেঁয়াজ", "পেয়াজ", "দেশি পেঁয়াজ",
            "রসুন", "দেশি রসুন", "আদা", "কাঁচা মরিচ", "সবুজ মরিচ",
            "টমেটো", "পাকা টমেটো", "কাঁচা টমেটো",
            "বেগুন", "গোল বেগুন", "লম্বা বেগুন", "তালবেগুন",
            "পটল", "লাউ", "কচি লাউ", "মিষ্টি কুমড়া", "মিষ্টি কুমড়া", "চালকুমড়া", "চাল কুমড়া",
            "শসা", "গাজর", "লেবু", "কাগজি লেবু", "বাতাবি লেবু",
            "লাল শাক", "পালং শাক", "পুঁই শাক", "কলমি শাক", "ডাটা শাক", "হেলেঞ্চা শাক", "সরিষার শাক",
            "ফুলকপি", "ফুল কপি", "বাঁধাকপি", "বাঁধা কপি",
            "ঢেঁড়স", "ঢেঁড়শ", "ঢেড়স", "ঢেড়শ",
            "চিচিঙ্গা", "ঝিঙে", "ঝিঙ্গা", "করলা", "উচ্ছে",
            "পেঁপে", "পেপে", "কাঁচা পেঁপে", "পাকা পেঁপে",
            "সজনে", "সজনে ডাঁটা",
            "সিম", "শিম", "বরবটি", "মুলা", "মটরশুঁটি", "ক্যাপসিকাম", "ধনেপাতা"
        )
        for (item in veggieItems) {
            assertEquals("Item '$item' should categorize as veggies", "veggies", getMarketItemCategory(item))
            assertEquals(MarketDepartment.VEGGIES, MarketDepartment.fromItemName(item))
        }

        // --- 1.2 Fish & Meat (MEAT_FISH) ---
        val meatFishItems = listOf(
            "মাছ", "রুই মাছ", "কাতলা মাছ", "ইলিশ মাছ", "পদ্মার ইলিশ", "পাঙ্গাস", "পাঙ্গাস মাছ",
            "তেলাপিয়া", "তেলাপিয়া মাছ",
            "কৈ", "কৈ মাছ", "শিং", "শিং মাছ", "মাগুর", "বোয়াল",
            "মাংস", "গরুর মাংস", "খাসির মাংস", "খাসি", "মাটন", "বিফ",
            "মুরগি", "মুরগী", "ব্রয়লার মুরগি", "দেশি মুরগি", "সোনালী মুরগি", "লেয়ার মুরগি", "চিকেন"
        )
        for (item in meatFishItems) {
            assertEquals("Item '$item' should categorize as meat_fish", "meat_fish", getMarketItemCategory(item))
            assertEquals(MarketDepartment.MEAT_FISH, MarketDepartment.fromItemName(item))
        }

        // --- 1.3 Dairy & Bakery (DAIRY_BAKERY) ---
        val dairyBakeryItems = listOf(
            "দুধ", "তরল দুধ", "খাঁটি দুধ", "গাভীর দুধ", "গরুর দুধ", "গুঁড়া দুধ", "গুঁড়ো দুধ",
            "ডিম", "ফার্মের ডিম", "হাঁসের ডিম", "দেশি ডিম", "কোয়েল পাখির ডিম", "মুরগির ডিম",
            "দই", "বগুড়ার দই", "বগুড়া দই", "মিষ্টি দই", "টক দই",
            "মাখন", "পনির", "পাউরুটি", "রুটি", "টোস্ট", "টোস্ট বিস্কুট", "বিস্কুট", "কেক"
        )
        for (item in dairyBakeryItems) {
            assertEquals("Item '$item' should categorize as dairy_bakery", "dairy_bakery", getMarketItemCategory(item))
            assertEquals(MarketDepartment.DAIRY_BAKERY, MarketDepartment.fromItemName(item))
        }

        // --- 1.4 Groceries & Spices (GROCERIES) ---
        val groceryItems = listOf(
            "সয়াবিন তেল", "সয়াবিন তেল", "তীর সয়াবিন তেল", "রূপচাঁদা সয়াবিন তেল", "সরিষার তেল", "ঘি",
            "চাল", "নাজিরশাইল চাল", "মিনিকেট চাল", "বাসমতী চাল", "পোলাওয়ের চাল",
            "ডাল", "মসুর ডাল", "মুগ ডাল", "বুট ডাল", "ছোলা",
            "মসলা", "মশলা", "গরম মসলা", "হলুদ", "হলুদ গুঁড়া", "হলুদ গুড়া",
            "মরিচ গুঁড়া", "মরিচ গুড়া", "জিরার গুঁড়া", "জিরা", "ধনিয়া", "এলাচ", "দারুচিনি", "তেজপাতা",
            "লবণ", "আয়োডিনযুক্ত লবণ", "নুন", "চিনি",
            "আটা", "ময়দা", "ময়দা", "সুজি", "চা পাতা",
            "সস", "ভিনেগার", "নুডলস", "পাস্তা"
        )
        for (item in groceryItems) {
            assertEquals("Item '$item' should categorize as groceries", "groceries", getMarketItemCategory(item))
            assertEquals(MarketDepartment.GROCERIES, MarketDepartment.fromItemName(item))
        }
    }

    // =========================================================================
    // 2. COMPOUND COMMODITY DISAMBIGUATION & ADVERSARIAL EDGE CASES
    // =========================================================================

    @Test
    fun testCompoundCommodityDisambiguationAndPrecedence() {
        // Priority 1 precedence over Priority 2:
        // "মরিচ গুঁড়া" contains both "মরিচ" (veggies) and "গুঁড়া" (groceries).
        // Priority 1 groceries MUST win.
        assertEquals("groceries", getMarketItemCategory("মরিচ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("মরিচের গুড়া"))
        assertEquals("groceries", getMarketItemCategory("হলুদ গুঁড়া"))

        // Priority 1 precedence over Priority 2 for sauces:
        // "টমেটো সস" contains "টমেটো" (veggies) and "সস" (groceries).
        assertEquals("groceries", getMarketItemCategory("টমেটো সস"))
        assertEquals("groceries", getMarketItemCategory("চিলি সস"))

        // Bogra district disambiguation:
        // "বগুড়ার দই" must be dairy_bakery, not groceries (even though বগুড়া contains গুড়া)
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়ার দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়ার মিষ্টি দই"))
        // "বগুড়ার আলু" must be veggies, not groceries
        assertEquals("veggies", getMarketItemCategory("বগুড়ার আলু"))

        // Fish Roe ("মাছের ডিম") has "মাছ" (meat_fish) and "ডিম" (dairy_bakery).
        // Since fish roe is sold in fish markets, meat_fish precedence correctly classifies it.
        assertEquals("meat_fish", getMarketItemCategory("মাছের ডিম"))

        // Blank, whitespace, and unknown commodities default safely to groceries
        assertEquals("groceries", getMarketItemCategory(""))
        assertEquals("groceries", getMarketItemCategory("   "))
        assertEquals("groceries", getMarketItemCategory("সানসিল্ক শ্যাম্পু"))
        assertEquals("groceries", getMarketItemCategory("হুইল সাবান"))
    }

    @Test
    fun testAdversarialPriorityInversionFindings() {
        // CHALLENGE 1 (REMEDIATED): "গরুর দুধ" (Cow's Milk)
        // Dairy keywords ("দুধ") evaluate before generic meat prefixes ("গরু"),
        // so "গরুর দুধ" correctly classifies as dairy_bakery.
        val categoryCowMilk = getMarketItemCategory("গরুর দুধ")
        assertEquals("dairy_bakery", categoryCowMilk)

        // Contrast with "তরল দুধ" (which also has "দুধ" keyword) -> classifies as dairy_bakery
        assertEquals("dairy_bakery", getMarketItemCategory("তরল দুধ"))

        // CHALLENGE 2 (REMEDIATED): "মুরগির ডিম" (Chicken Egg)
        // Dairy/Egg keywords ("ডিম") evaluate before generic poultry prefixes ("মুরগি"),
        // so "মুরগির ডিম" correctly classifies as dairy_bakery.
        val categoryChickenEgg = getMarketItemCategory("মুরগির ডিম")
        assertEquals("dairy_bakery", categoryChickenEgg)

        // Contrast with "ফার্মের ডিম" and "হাঁসের ডিম" -> both classify as dairy_bakery
        assertEquals("dairy_bakery", getMarketItemCategory("ফার্মের ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("হাঁসের ডিম"))

        // CHALLENGE 3 (REMEDIATED): "সরিষা শাক" (Mustard Greens)
        // Leafy greens ("শাক") take precedence over grocery seeds ("সরিষা"),
        // so "সরিষার শাক" correctly classifies as veggies.
        val categoryMustardGreens = getMarketItemCategory("সরিষার শাক")
        assertEquals("veggies", categoryMustardGreens)

        // CHALLENGE 4 (REMEDIATED): Unicode Antastha Ja (য U+09AF) vs Antastha Ya (য় U+09DF) in Onion ("পেয়াজ")
        // Both precomposed and decomposed forms with either য or য় classify as veggies.
        val categoryPeyajWithAntasthaJa = getMarketItemCategory("পেয়াজ")
        assertEquals("veggies", categoryPeyajWithAntasthaJa)

        // CHALLENGE 5 (REMEDIATED): "চালকুমড়া" (Wax Gourd / Ash Gourd)
        // Guarded rice ("চাল") rule prevents masking wax gourd, so it classifies as veggies.
        val categoryChalkumra = getMarketItemCategory("চালকুমড়া")
        assertEquals("veggies", categoryChalkumra)

        // CHALLENGE 6 (REMEDIATED): Spaced Cauliflower ("ফুল কপি") & Spaced Cabbage ("বাঁধা কপি")
        // Both spaced and composite cabbage/cauliflower variants classify as veggies.
        assertEquals("veggies", getMarketItemCategory("ফুল কপি"))
        assertEquals("veggies", getMarketItemCategory("বাঁধা কপি"))

        // CHALLENGE 7 (REMEDIATED): Okra without Chandrabindu ("ঢেড়শ", "ঢেড়স")
        // Both with and without chandrabindu variants correctly classify as veggies.
        assertEquals("veggies", getMarketItemCategory("ঢেড়শ"))
        assertEquals("veggies", getMarketItemCategory("ঢেড়স"))

        // CHALLENGE 8 (REMEDIATED): Powder Milk spelling symmetry ("গুঁড়ো দুধ" and "গুঁড়া দুধ")
        // Powder milk variants are guarded from grocery spice powders and correctly classify as dairy_bakery.
        assertEquals("dairy_bakery", getMarketItemCategory("গুঁড়ো দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("গুঁড়া দুধ"))

        // CHALLENGE 9 (REMEDIATED): Tilapia Fish ("তেলাপিয়া") masked by Cooking Oil ("তেল")
        // Cooking oil rule guards against "তেলাপিয়া", allowing Tilapia fish to route to meat_fish.
        val categoryTilapia = getMarketItemCategory("তেলাপিয়া")
        assertEquals("meat_fish", categoryTilapia)
        val categoryTilapiaFish = getMarketItemCategory("তেলাপিয়া মাছ")
        assertEquals("meat_fish", categoryTilapiaFish)

        // CHALLENGE 10 (REMEDIATED): Unicode Normalization (NFC vs NFD Decomposed Nukta) in Shrimp ("চিংড়ি")
        // NFC normalization ensures precomposed "চিংড়ি" matches meat_fish.
        val precomposedChingri = "\u099a\u09bf\u0982\u09dc\u09bf"
        assertEquals("meat_fish", getMarketItemCategory(precomposedChingri))
    }

    @Test
    fun testChallengerRemediationExhaustiveEdgeCases() {
        // 1. Tilapia fish ("তেলাপিয়া" -> meat_fish) & counter-cases
        assertEquals("meat_fish", getMarketItemCategory("তেলাপিয়া"))
        assertEquals("meat_fish", getMarketItemCategory("তেলাপিয়া মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("দেশি তেলাপিয়া"))
        assertEquals("meat_fish", getMarketItemCategory("তেলাপিয়া"))
        assertEquals("meat_fish", getMarketItemCategory("তেলাপিয়া মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("tilapia"))
        assertEquals("meat_fish", getMarketItemCategory("tilapia fish"))
        // Counter-case: Oil must remain groceries
        assertEquals("groceries", getMarketItemCategory("তেল"))
        assertEquals("groceries", getMarketItemCategory("সয়াবিন তেল"))
        assertEquals("groceries", getMarketItemCategory("সরিষার তেল"))
        assertEquals("groceries", getMarketItemCategory("cooking oil"))

        // 2. Wax gourd ("চালকুমড়া" -> veggies) & counter-cases
        assertEquals("veggies", getMarketItemCategory("চালকুমড়া"))
        assertEquals("veggies", getMarketItemCategory("চালকুমড়া"))
        assertEquals("veggies", getMarketItemCategory("চাল কুমড়া"))
        assertEquals("veggies", getMarketItemCategory("চাল কুমড়া"))
        assertEquals("veggies", getMarketItemCategory("কচি চালকুমড়া"))
        // Counter-case: Rice must remain groceries
        assertEquals("groceries", getMarketItemCategory("চাল"))
        assertEquals("groceries", getMarketItemCategory("বাসমতী চাল"))
        assertEquals("groceries", getMarketItemCategory("পোলাও চাল"))
        assertEquals("groceries", getMarketItemCategory("rice"))

        // 3. Cow's milk ("গরুর দুধ" -> dairy_bakery) & counter-cases
        assertEquals("dairy_bakery", getMarketItemCategory("গরুর দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("গাভীর দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("খাঁটি দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("milk"))
        assertEquals("dairy_bakery", getMarketItemCategory("cow milk"))
        // Counter-case: Beef must remain meat_fish
        assertEquals("meat_fish", getMarketItemCategory("গরুর মাংস"))
        assertEquals("meat_fish", getMarketItemCategory("গরু"))
        assertEquals("meat_fish", getMarketItemCategory("beef"))

        // 4. Chicken egg ("মুরগির ডিম" -> dairy_bakery) & counter-cases
        assertEquals("dairy_bakery", getMarketItemCategory("মুরগির ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("ফার্মের ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("হাঁসের ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("egg"))
        assertEquals("dairy_bakery", getMarketItemCategory("chicken egg"))
        // Counter-case: Eggplant must be veggies
        assertEquals("veggies", getMarketItemCategory("eggplant"))
        // Counter-case: Fish roe must be meat_fish
        assertEquals("meat_fish", getMarketItemCategory("মাছের ডিম"))
        assertEquals("meat_fish", getMarketItemCategory("মাছের ডিম্ব"))
        // Counter-case: Chicken meat must remain meat_fish
        assertEquals("meat_fish", getMarketItemCategory("মুরগির মাংস"))
        assertEquals("meat_fish", getMarketItemCategory("মুরগি"))
        assertEquals("meat_fish", getMarketItemCategory("chicken"))

        // 5. Mustard greens ("সরিষার শাক" -> veggies) & counter-cases
        assertEquals("veggies", getMarketItemCategory("সরিষার শাক"))
        assertEquals("veggies", getMarketItemCategory("সরিষা শাক"))
        assertEquals("veggies", getMarketItemCategory("তাজা সরিষার শাক"))
        // Counter-case: Mustard seed/oil must be groceries
        assertEquals("groceries", getMarketItemCategory("সরিষা"))
        assertEquals("groceries", getMarketItemCategory("সরিষার তেল"))

        // 6. Onion orthography ("পেঁয়াজ"/"পেঁয়াজ"/"পেয়াজ"/"পেয়াজ" -> veggies)
        assertEquals("veggies", getMarketItemCategory("পেঁয়াজ"))
        assertEquals("veggies", getMarketItemCategory("পেঁয়াজ"))
        assertEquals("veggies", getMarketItemCategory("পেয়াজ"))
        assertEquals("veggies", getMarketItemCategory("পেয়াজ"))
        assertEquals("veggies", getMarketItemCategory("দেশি পেঁয়াজ"))
        assertEquals("veggies", getMarketItemCategory("দেশি পেঁয়াজ"))
        assertEquals("veggies", getMarketItemCategory("দেশি পেয়াজ"))
        assertEquals("veggies", getMarketItemCategory("দেশি পেয়াজ"))
        assertEquals("veggies", getMarketItemCategory("onion"))
        assertEquals("veggies", getMarketItemCategory("shallot"))

        // 7. Okra orthography ("ঢেঁড়শ", "ঢেড়শ", "ঢেঁড়স", "ঢেড়স" -> veggies)
        assertEquals("veggies", getMarketItemCategory("ঢেঁড়স"))
        assertEquals("veggies", getMarketItemCategory("ঢেঁড়শ"))
        assertEquals("veggies", getMarketItemCategory("ঢেড়স"))
        assertEquals("veggies", getMarketItemCategory("ঢেড়শ"))
        assertEquals("veggies", getMarketItemCategory("ঢেঁড়স"))
        assertEquals("veggies", getMarketItemCategory("ঢেঁড়শ"))
        assertEquals("veggies", getMarketItemCategory("ঢেড়স"))
        assertEquals("veggies", getMarketItemCategory("ঢেড়শ"))
        assertEquals("veggies", getMarketItemCategory("okra"))
        assertEquals("veggies", getMarketItemCategory("lady finger"))
        assertEquals("veggies", getMarketItemCategory("ladies finger"))
        assertEquals("veggies", getMarketItemCategory("ভেন্ডি"))

        // 8. Decomposed Unicode Nukta permutations
        // Decomposed Antastha Ya (\u09af\u09bc = য + nukta)
        val decomposedPeyaj1 = "\u09aa\u09c7\u0981\u09af\u09bc\u09be\u099c" // পেঁয়াজ
        val decomposedPeyaj2 = "\u09aa\u09c7\u09af\u09bc\u09be\u099c"       // পেয়াজ
        assertEquals("veggies", getMarketItemCategory(decomposedPeyaj1))
        assertEquals("veggies", getMarketItemCategory(decomposedPeyaj2))

        // Decomposed Rra (\u09a1\u09bc = ড + nukta)
        val decomposedDherosh1 = "\u09a2\u09c7\u0981\u09a1\u09bc\u09b8" // ঢেঁড়স
        val decomposedDherosh2 = "\u09a2\u09c7\u0981\u09a1\u09bc\u09b6" // ঢেঁড়শ
        val decomposedDherosh3 = "\u09a2\u09c7\u09a1\u09bc\u09b8"       // ঢেড়স
        val decomposedDherosh4 = "\u09a2\u09c7\u09a1\u09bc\u09b6"       // ঢেড়শ
        assertEquals("veggies", getMarketItemCategory(decomposedDherosh1))
        assertEquals("veggies", getMarketItemCategory(decomposedDherosh2))
        assertEquals("veggies", getMarketItemCategory(decomposedDherosh3))
        assertEquals("veggies", getMarketItemCategory(decomposedDherosh4))

        val decomposedChingri = "\u099a\u09bf\u0982\u09a1\u09bc\u09bf" // চিংড়ি
        assertEquals("meat_fish", getMarketItemCategory(decomposedChingri))

        val decomposedChalkumra = "\u099a\u09be\u09b2\u0995\u09c1\u09ae\u09a1\u09bc\u09be" // চালকুমড়া
        assertEquals("veggies", getMarketItemCategory(decomposedChalkumra))

        // 9. Powdered milk symmetry vs spice powder
        assertEquals("dairy_bakery", getMarketItemCategory("গুঁড়া দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("গুঁড়ো দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("গুড়া দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("গুড়ো দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("powder milk"))
        assertEquals("groceries", getMarketItemCategory("মরিচ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("হলুদ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("জিরার গুঁড়া"))
    }


    // =========================================================================
    // 3. TRANSLITERATED AND ENGLISH COMMODITY NAMES
    // =========================================================================

    @Test
    fun testTransliteratedAndEnglishCommodityKeywords() {
        // English keywords supported in Classifier:
        // Veggies:
        assertEquals("veggies", getMarketItemCategory("potato"))
        assertEquals("veggies", getMarketItemCategory("onion"))
        assertEquals("veggies", getMarketItemCategory("tomato"))
        assertEquals("veggies", getMarketItemCategory("chilli"))
        assertEquals("veggies", getMarketItemCategory("cabbage"))
        assertEquals("veggies", getMarketItemCategory("cauliflower"))
        assertEquals("veggies", getMarketItemCategory("carrot"))
        assertEquals("veggies", getMarketItemCategory("cucumber"))
        assertEquals("veggies", getMarketItemCategory("ginger"))
        assertEquals("veggies", getMarketItemCategory("garlic"))
        assertEquals("veggies", getMarketItemCategory("lemon"))
        assertEquals("veggies", getMarketItemCategory("spinach"))
        assertEquals("veggies", getMarketItemCategory("eggplant"))
        assertEquals("veggies", getMarketItemCategory("capsicum"))
        assertEquals("veggies", getMarketItemCategory("bell pepper"))

        // Meat & Fish:
        assertEquals("meat_fish", getMarketItemCategory("fish"))
        assertEquals("meat_fish", getMarketItemCategory("meat"))
        assertEquals("meat_fish", getMarketItemCategory("chicken"))
        assertEquals("meat_fish", getMarketItemCategory("beef"))
        assertEquals("meat_fish", getMarketItemCategory("mutton"))
        assertEquals("meat_fish", getMarketItemCategory("prawn"))
        assertEquals("meat_fish", getMarketItemCategory("shrimp"))

        // Dairy & Bakery:
        assertEquals("dairy_bakery", getMarketItemCategory("milk"))
        assertEquals("dairy_bakery", getMarketItemCategory("egg"))
        assertEquals("dairy_bakery", getMarketItemCategory("bread"))
        assertEquals("dairy_bakery", getMarketItemCategory("butter"))
        assertEquals("dairy_bakery", getMarketItemCategory("cheese"))
        assertEquals("dairy_bakery", getMarketItemCategory("yogurt"))
        assertEquals("dairy_bakery", getMarketItemCategory("curd"))
        assertEquals("dairy_bakery", getMarketItemCategory("biscuit"))
        assertEquals("dairy_bakery", getMarketItemCategory("toast"))

        // Groceries:
        assertEquals("groceries", getMarketItemCategory("oil"))
        assertEquals("groceries", getMarketItemCategory("rice"))
        assertEquals("groceries", getMarketItemCategory("lentil"))
        assertEquals("groceries", getMarketItemCategory("spice"))
        assertEquals("groceries", getMarketItemCategory("salt"))
        assertEquals("groceries", getMarketItemCategory("sugar"))
        assertEquals("groceries", getMarketItemCategory("flour"))
        assertEquals("groceries", getMarketItemCategory("tea"))
        assertEquals("groceries", getMarketItemCategory("sauce"))
        assertEquals("groceries", getMarketItemCategory("vinegar"))
        assertEquals("groceries", getMarketItemCategory("noodles"))
        assertEquals("groceries", getMarketItemCategory("pasta"))
    }

    // =========================================================================
    // 4. LIVE PROGRESS COUNTERS: ZERO ITEMS, ALL ITEMS, DYNAMIC TOGGLES
    // =========================================================================

    private fun computeHeaderProgressText(totalCount: Int, purchasedCount: Int, isBangla: Boolean): Pair<Boolean, String> {
        val isAllCompleted = totalCount > 0 && purchasedCount == totalCount
        val progressText = if (isAllCompleted) {
            if (isBangla) "✓ সম্পন্ন" else "✓ Done"
        } else {
            if (isBangla) "${convertToBengaliDigits(purchasedCount.toString())}/${convertToBengaliDigits(totalCount.toString())} টি সম্পন্ন"
            else "$purchasedCount/$totalCount Completed"
        }
        return Pair(isAllCompleted, progressText)
    }

    @Test
    fun testLiveProgressCounterBoundaryStates() {
        // 4.1 Zero Items (0/0)
        val (isAll0, text0Bn) = computeHeaderProgressText(0, 0, isBangla = true)
        assertFalse("0 total items cannot be marked as all completed", isAll0)
        assertEquals("০/০ টি সম্পন্ন", text0Bn)

        val (_, text0En) = computeHeaderProgressText(0, 0, isBangla = false)
        assertEquals("0/0 Completed", text0En)

        // 4.2 Initial unpurchased state: 0 of 5 items
        val (isAllInit, textInitBn) = computeHeaderProgressText(5, 0, isBangla = true)
        assertFalse(isAllInit)
        assertEquals("০/৫ টি সম্পন্ন", textInitBn)

        val (_, textInitEn) = computeHeaderProgressText(5, 0, isBangla = false)
        assertEquals("0/5 Completed", textInitEn)

        // 4.3 Partial progress: 3 of 5 items
        val (isAllPartial, textPartialBn) = computeHeaderProgressText(5, 3, isBangla = true)
        assertFalse(isAllPartial)
        assertEquals("৩/৫ টি সম্পন্ন", textPartialBn)

        val (_, textPartialEn) = computeHeaderProgressText(5, 3, isBangla = false)
        assertEquals("3/5 Completed", textPartialEn)

        // 4.4 All completed: 5 of 5 items
        val (isAllDone, textDoneBn) = computeHeaderProgressText(5, 5, isBangla = true)
        assertTrue("5 of 5 must trigger completion status", isAllDone)
        assertEquals("✓ সম্পন্ন", textDoneBn)

        val (_, textDoneEn) = computeHeaderProgressText(5, 5, isBangla = false)
        assertEquals("✓ Done", textDoneEn)

        // 4.5 Boundary edge case: 1 of 1 item
        val (isAllSingle, textSingleBn) = computeHeaderProgressText(1, 1, isBangla = true)
        assertTrue(isAllSingle)
        assertEquals("✓ সম্পন্ন", textSingleBn)
    }

    @Test
    fun testDynamicItemToggleSimulation() {
        // Simulate a department shopping session with 4 items:
        val items = mutableListOf(
            MarketItem(id = 1, listId = 1, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = false, amount = null),
            MarketItem(id = 2, listId = 1, name = "টমেটো", quantity = 1.0, unit = "কেজি", isPurchased = false, amount = null),
            MarketItem(id = 3, listId = 1, name = "কাঁচা মরিচ", quantity = 0.25, unit = "কেজি", isPurchased = false, amount = null),
            MarketItem(id = 4, listId = 1, name = "পেঁয়াজ", quantity = 1.5, unit = "কেজি", isPurchased = false, amount = null)
        )

        fun currentDeptState(): Pair<Boolean, String> {
            val total = items.size
            val purchased = items.count { it.isPurchased }
            return computeHeaderProgressText(total, purchased, isBangla = true)
        }

        // Initial: 0/4
        var state = currentDeptState()
        assertFalse(state.first)
        assertEquals("০/৪ টি সম্পন্ন", state.second)

        // Toggle 1: Buy Potato
        items[0] = items[0].copy(isPurchased = true, amount = 100.0)
        state = currentDeptState()
        assertFalse(state.first)
        assertEquals("১/৪ টি সম্পন্ন", state.second)

        // Toggle 2: Buy Tomato
        items[1] = items[1].copy(isPurchased = true, amount = 60.0)
        state = currentDeptState()
        assertFalse(state.first)
        assertEquals("২/৪ টি সম্পন্ন", state.second)

        // Toggle 3: Buy Chilli
        items[2] = items[2].copy(isPurchased = true, amount = 30.0)
        state = currentDeptState()
        assertFalse(state.first)
        assertEquals("৩/৪ টি সম্পন্ন", state.second)

        // Toggle 4: Buy Onion -> All 4 completed!
        items[3] = items[3].copy(isPurchased = true, amount = 120.0)
        state = currentDeptState()
        assertTrue("All items purchased must show completion status", state.first)
        assertEquals("✓ সম্পন্ন", state.second)

        // Rapid untoggle: User realizes Onion wasn't bought, untoggles it
        items[3] = items[3].copy(isPurchased = false, amount = null)
        state = currentDeptState()
        assertFalse("Untoggling must revoke completion status", state.first)
        assertEquals("৩/৪ টি সম্পন্ন", state.second)

        // Deletion: User removes unpurchased Onion from list
        items.removeAt(3)
        // Now 3 total, all 3 are purchased!
        state = currentDeptState()
        assertTrue("List should now be 100% complete", state.first)
        assertEquals("✓ সম্পন্ন", state.second)
    }

    // =========================================================================
    // 5. ACCESSIBILITY, TOUCH TARGETS & THEME HYGIENE (M3 EXPRESSIVE)
    // =========================================================================

    @Test
    fun testHeaderAndRowAccessibilityCompliance() {
        val headerContent = readFile("com/example/expensetracker/ui/dashboard/MarketDepartmentSectionHeader.kt")
        val itemRowContent = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")
        val dashboardRowContent = readFile("com/example/expensetracker/ui/components/DashboardMarketItemRow.kt")

        // 1. Department Section Header minimum 48dp height & minimumInteractiveComponentSize
        assertTrue(
            "MarketDepartmentSectionHeader must enforce minimumInteractiveComponentSize()",
            headerContent.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "MarketDepartmentSectionHeader must enforce heightIn(min = 48.dp)",
            headerContent.contains("heightIn(min = 48.dp)")
        )

        // 2. Animated Chevron Rotation
        assertTrue(
            "Header must use animateFloatAsState with 180f for smooth chevron rotation",
            headerContent.contains("animateFloatAsState(") &&
            headerContent.contains("180f") &&
            headerContent.contains("graphicsLayer { rotationZ = rotationAngle }")
        )

        // 3. Haptic feedback on header click
        assertTrue(
            "Header must trigger haptics.lightTick() on click",
            headerContent.contains("haptics.lightTick()")
        )

        // 4. Item Row 14dp rounded corners and 48dp interactive components
        assertTrue(
            "MarketItemRow must use RoundedCornerShape(14.dp)",
            itemRowContent.contains("RoundedCornerShape(14.dp)")
        )
        assertTrue(
            "MarketItemRow quantity pill must enforce minimumInteractiveComponentSize",
            itemRowContent.contains("minimumInteractiveComponentSize()") &&
            itemRowContent.contains("onUnitClick()")
        )
        assertTrue(
            "MarketItemRow substitute pill must enforce minimumInteractiveComponentSize",
            itemRowContent.contains("minimumInteractiveComponentSize()") &&
            itemRowContent.contains("onSubstituteClick()")
        )
        assertTrue(
            "MarketItemRow delete button in edit mode must enforce size(48.dp)",
            itemRowContent.contains("size(48.dp)")
        )

        // 5. Circular checkbox touch target in DashboardMarketItemRow
        assertTrue(
            "DashboardMarketItemRow checkbox must have 48dp touch target",
            dashboardRowContent.contains("sizeIn(minWidth = 48.dp, minHeight = 48.dp)") &&
            dashboardRowContent.contains("minimumInteractiveComponentSize()")
        )
    }

    @Test
    fun testThemeHygieneAndSemanticColors() {
        val headerContent = readFile("com/example/expensetracker/ui/dashboard/MarketDepartmentSectionHeader.kt")
        val finishButtonContent = readFile("com/example/expensetracker/ui/dashboard/MarketFinishButton.kt")
        val cardContent = readFile("com/example/expensetracker/ui/components/MarketChecklistCard.kt")

        // 1. LocalAppColors consumption
        assertTrue(headerContent.contains("val appColors = LocalAppColors.current"))
        assertTrue(finishButtonContent.contains("appColors = LocalAppColors.current"))

        // 2. No hardcoded 0xFF2EC4B6 in MarketChecklistCard
        assertFalse(cardContent.contains("0xFF2EC4B6"))

        // 3. MarketFinishButton executable code uses semantic appColors.background
        assertTrue(
            finishButtonContent.contains("val contentColor = if (appColors.isDark) appColors.background else Color.White")
        )

        // 4. Semantic color tokens in header
        assertTrue(headerContent.contains("color = appColors.textPrimary"))
        assertTrue(headerContent.contains("appColors.textSecondary"))
        assertTrue(headerContent.contains("appColors.income"))
        assertTrue(headerContent.contains("appColors.surfaceVariant"))
    }

    // =========================================================================
    // 6. COLLAPSIBLE BEHAVIOR AND EMPTY-SET SUPPRESSION
    // =========================================================================

    @Test
    fun testEmptyDepartmentSuppressionInScreens() {
        val marketScreenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")
        val marketSheetContent = readFile("com/example/expensetracker/ui/dashboard/MarketListSheet.kt")

        // 1. Both screens iterate over MarketDepartment.entries
        assertTrue(marketScreenContent.contains("for (dept in MarketDepartment.entries)"))
        assertTrue(marketSheetContent.contains("for (dept in MarketDepartment.entries)"))

        // 2. Both screens skip rendering headers when deptItems is null or empty
        assertTrue(
            "MarketScreen must skip empty departments",
            marketScreenContent.contains("val deptItems = itemsByDept[dept.id] ?: continue") &&
            marketScreenContent.contains("if (deptItems.isEmpty()) continue")
        )
        assertTrue(
            "MarketListSheet must skip empty departments",
            marketSheetContent.contains("val deptItems = itemsByDept[dept.id] ?: continue") &&
            marketSheetContent.contains("if (deptItems.isEmpty()) continue")
        )

        // 3. Both screens track collapsed state using rememberSaveable
        assertTrue(
            marketScreenContent.contains("var collapsedDepartments by rememberSaveable { mutableStateOf(setOf<String>()) }")
        )
        assertTrue(
            marketSheetContent.contains("var collapsedDepartments by rememberSaveable { mutableStateOf(setOf<String>()) }")
        )
    }
}
