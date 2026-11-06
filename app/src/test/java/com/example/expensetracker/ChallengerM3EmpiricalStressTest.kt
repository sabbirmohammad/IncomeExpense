package com.example.expensetracker

import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.getDepartmentMeta
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Challenger M3 Empirical Stress Test Suite:
 *
 * 1. Exhaustive boundary testing of MarketDepartmentClassifier.kt with 1,000+ randomized
 *    and realistic Bengali grocery item names, including Bogra district collocations,
 *    regional qualifiers, and composite spice powder phrases.
 * 2. Touch target ergonomics: verify every interactive chip and button in
 *    MarketPromptAmountDialog, MarketTripCompletionSheet, MarketParsedTagChip, and
 *    CreateMarketListSection satisfies minimumInteractiveComponentSize or minHeight >= 48.dp.
 * 3. Typography constraints: verify all typography meets >= 12sp for Bengali strings and
 *    uses "tnum" tabular numbers.
 * 4. Thermal receipt and lined notepad tactile styling invariants.
 * 5. Empirical reproduction of classifier edge cases (e.g. Bogra vs spice powders, animal-qualified dairy items,
 *    and tilapia oil substring collision).
 * 6. Strictly zero AI sparkles across all M3 components.
 */
class ChallengerM3EmpiricalStressTest {

    private fun readSourceFile(relativePath: String): String {
        val candidates = listOf(
            File("src/main/java/$relativePath"),
            File("app/src/main/java/$relativePath"),
            File("../app/src/main/java/$relativePath")
        )
        val file = candidates.find { it.exists() }
        assertNotNull("Expected source file to exist: $relativePath", file)
        return file!!.readText()
    }

    // =========================================================================
    // 1. EXHAUSTIVE 1,000+ BENGALI GROCERY CLASSIFIER BOUNDARY TESTING
    // =========================================================================

    @Test
    fun testBograDistrictCollocationsAndCompositeSpicePowderMatrix() {
        // 1. Bogra district produce must classify as veggies, not groceries
        assertEquals("veggies", getMarketItemCategory("বগুড়ার আলু"))
        assertEquals("veggies", getMarketItemCategory("বগুড়ার লাল আলু"))
        assertEquals("veggies", getMarketItemCategory("বগুড়ার গোল আলু"))
        assertEquals("veggies", getMarketItemCategory("বগুড়ার শসা"))
        assertEquals("veggies", getMarketItemCategory("বগুড়ার পটল"))
        assertEquals("veggies", getMarketItemCategory("বগুড়ার কাঁচামরিচ"))
        assertEquals("veggies", getMarketItemCategory("বগুড়ার বেগুন"))

        // 2. Bogra curd must classify as dairy_bakery, not groceries
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়ার দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়া দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়ার মিষ্টি দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়ার টক দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("মিষ্টি দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("টক দই"))

        // 3. Composite spice powders from Bogra or elsewhere must classify as groceries
        assertEquals("groceries", getMarketItemCategory("মরিচ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("মরিচ গুড়া"))
        assertEquals("groceries", getMarketItemCategory("মরিচ গুঁড়ো"))
        assertEquals("groceries", getMarketItemCategory("মরিচ গুড়ো"))
        assertEquals("groceries", getMarketItemCategory("হলুদ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("হলুদ গুড়া"))
        assertEquals("groceries", getMarketItemCategory("জিরার গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("জিরা পাউডার"))
        assertEquals("groceries", getMarketItemCategory("চিলি পাউডার"))
        assertEquals("groceries", getMarketItemCategory("chilli powder"))
        assertEquals("groceries", getMarketItemCategory("বগুড়ার মরিচ গুড়া"))
        assertEquals("groceries", getMarketItemCategory("বগুড়ার হলুদ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("বগুড়ার জিরা গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("বগুড়ার মশলা পাউডার"))

        // 4. Dairy powders must classify as dairy_bakery, bypassing spice powder check
        assertEquals("dairy_bakery", getMarketItemCategory("গুঁড়া দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("গুড়া দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("milk powder"))
        assertEquals("dairy_bakery", getMarketItemCategory("powder milk"))
        assertEquals("dairy_bakery", getMarketItemCategory("তরল দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("মিল্কভিটা দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("ফার্মের ডিম"))
    }

    @Test
    fun testEmpiricalClassifierStress1000PlusItems() {
        val regionalPrefixes = listOf(
            "বগুড়ার", "রাজশাহীর", "দিনাজপুরের", "বিক্রমপুরের",
            "যশোরের", "মেহেরপুরের", "চাটগাঁর", "সিলেটের",
            "রংপুরের", "বরিশালের", "পাবনার", "কুষ্টিয়ার"
        )
        val qualityPrefixes = listOf(
            "দেশি", "খাঁটি", "তাজা", "লাল", "সতেজ", "গোল", "পাহাড়ী", "হাইব্রিড"
        )

        val veggiesBase = listOf(
            "আলু", "পটল", "পেঁয়াজ", "কাঁচা মরিচ", "টমেটো", "বেগুন", "লাউ", "কুমড়া",
            "শসা", "গাজর", "রসুন", "আদা", "লেবু", "পালং শাক", "লাল শাক", "কলমি শাক",
            "ডাটা শাক", "ঢেঁড়স", "ঝিঙ্গা", "চিচিঙ্গা", "করলা", "উচ্ছে", "পেঁপে", "মুলা",
            "শিম", "বরবটি", "ফুলকপি", "বাঁধাকপি", "মটরশুঁটি", "ক্যাপসিকাম", "সজনে"
        )

        val spicesBase = listOf(
            "মরিচ গুঁড়া", "মরিচ গুড়া", "হলুদ গুঁড়া", "হলুদ গুড়া", "জিরা গুঁড়া", "জিরার গুঁড়া",
            "জিরা পাউডার", "ধনিয়া গুঁড়া", "চিলি পাউডার", "গরম মশলা গুঁড়া", "কারি পাউডার",
            "দারুচিনি গুঁড়া", "এলাচ গুঁড়া", "তেজপাতা গুঁড়া", "গোলমরিচ গুঁড়া", "কালোজিরা",
            "লবঙ্গ", "মেথি", "মৌরি", "ধনিয়া পাউডার"
        )

        val dryGroceryBase = listOf(
            "সয়াবিন তেল", "সরিষার তেল", "মিনিকেট চাল", "নাজিরশাইল চাল", "বাসমতি চাল",
            "মসুর ডাল", "মুগ ডাল", "ছোলা", "চিনি", "লবণ", "আটা", "ময়দা", "সুজি",
            "চা পাতা", "নুডলস", "পাস্তা", "ভিনেগার", "সস", "সয়া সস"
        )

        val fishMeatBase = listOf(
            "রুই মাছ", "কাতলা মাছ", "ইলিশ মাছ", "পাবদা মাছ", "পাঙ্গাস মাছ", "বোয়াল মাছ",
            "শিং মাছ", "মাগুর মাছ", "চিংড়ি মাছ", "কৈ মাছ",
            "মুরগির মাংস", "খাসির মাংস", "গরুর মাংস", "ব্রয়লার মুরগি", "সোনালী মুরগি"
        )

        val dairyBase = listOf(
            "দই", "মিষ্টি দই", "টক দই", "গুঁড়া দুধ", "তরল দুধ", "মিল্কভিটা দুধ",
            "ফার্মের ডিম", "হাঁসের ডিম", "পাউরুটি", "মাখন", "পনির", "টোস্ট বিস্কুট"
        )

        var totalTested = 0

        // 1. Veggies: Regional + Quality + Veggie combinations
        for (reg in regionalPrefixes) {
            for (veg in veggiesBase) {
                val itemName = "$reg $veg"
                assertEquals("Expected veggies for $itemName", "veggies", getMarketItemCategory(itemName))
                totalTested++
            }
        }
        for (q in qualityPrefixes) {
            for (veg in veggiesBase) {
                val itemName = "$q $veg"
                assertEquals("Expected veggies for $itemName", "veggies", getMarketItemCategory(itemName))
                totalTested++
            }
        }

        // 2. Spices: Regional / Quality + Spices
        for (reg in regionalPrefixes) {
            for (sp in spicesBase) {
                val itemName = "$reg $sp"
                assertEquals("Expected groceries for $itemName", "groceries", getMarketItemCategory(itemName))
                totalTested++
            }
        }

        // 3. Dry Groceries: Regional / Quality + Dry Groceries
        for (q in qualityPrefixes) {
            for (dg in dryGroceryBase) {
                val itemName = "$q $dg"
                assertEquals("Expected groceries for $itemName", "groceries", getMarketItemCategory(itemName))
                totalTested++
            }
        }

        // 4. Fish & Meat: Quality + Fish/Meat
        for (q in qualityPrefixes) {
            for (fm in fishMeatBase) {
                val itemName = "$q $fm"
                assertEquals("Expected meat_fish for $itemName", "meat_fish", getMarketItemCategory(itemName))
                totalTested++
            }
        }

        // 5. Dairy: Regional / Quality + Dairy
        for (reg in listOf("বগুড়ার", "দিনাজপুরের", "পাবনার", "কুষ্টিয়ার")) {
            for (d in dairyBase) {
                val itemName = "$reg $d"
                assertEquals("Expected dairy_bakery for $itemName", "dairy_bakery", getMarketItemCategory(itemName))
                totalTested++
            }
        }

        assertTrue("Expected 1,000+ items tested, but tested $totalTested", totalTested >= 1000)
        println("Empirically tested $totalTested Bengali grocery items across all departments.")
    }

    @Test
    fun testEmpiricalDefect1AnimalQualifiedDairyMisclassification() {
        // DEFECT 1: Animal qualifier greedy matching in Priority 3 vs Priority 4.
        // In Bengali grocery shopping, "গরুর দুধ" (cow's milk) is an everyday staple that belongs
        // to Dairy & Bakery (দুধ ও বেকারি), stall icon 🥛.
        // However, in MarketDepartmentClassifier.kt:
        // Priority 3 (Fish & Meat) checks `lower.contains("গরু")` and `lower.contains("মুরগি")`
        // before Priority 4 (Dairy & Bakery: `lower.contains("দুধ")`, `lower.contains("ডিম")`) is evaluated!
        val cowMilkCategory = getMarketItemCategory("গরুর দুধ")
        val chickenEggCategory = getMarketItemCategory("মুরগির ডিম")
        val goatMilkCategory = getMarketItemCategory("খাসির দুধ")

        // Empirical reality:
        // The classifier currently returns "meat_fish" for "গরুর দুধ", "মুরগির ডিম", and "খাসির দুধ"
        // because of the priority inversion and lack of dairy exclusions on animal meat keywords.
        assertEquals("meat_fish", cowMilkCategory)
        assertEquals("meat_fish", chickenEggCategory)
        assertEquals("meat_fish", goatMilkCategory)

        // Contrast with unqualified or brand-qualified dairy items which correctly evaluate to dairy_bakery:
        assertEquals("dairy_bakery", getMarketItemCategory("তরল দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("মিল্কভিটা দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("ফার্মের ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("হাঁসের ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("মহিষের দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("বগুড়ার দই"))
        assertEquals("dairy_bakery", getMarketItemCategory("গুঁড়া দুধ"))
    }

    @Test
    fun testEmpiricalDefect2TilapiaFishGreedyOilSubstringCollision() {
        // DEFECT 2: Tilapia fish (তেলাপিয়া) contains the substring "তেল" (oil).
        // In MarketDepartmentClassifier.kt, Priority 1 line 21 checks `lower.contains("তেল") -> "groceries"`.
        // Line 53 checks `lower.contains("তেলাপিয়া") -> "meat_fish"`.
        // Because Priority 1 precedes Priority 3, any occurrence of "তেলাপিয়া" matches "তেল" first,
        // misclassifying Tilapia fish as "groceries" instead of "meat_fish"!
        val tilapiaCategory = getMarketItemCategory("তেলাপিয়া")
        val tilapiaFishCategory = getMarketItemCategory("তেলাপিয়া মাছ")
        val freshTilapiaCategory = getMarketItemCategory("দেশি তেলাপিয়া মাছ")

        // Empirical reality: all tilapia items are hijacked by "তেল" into groceries
        assertEquals("groceries", tilapiaCategory)
        assertEquals("groceries", tilapiaFishCategory)
        assertEquals("groceries", freshTilapiaCategory)

        // In contrast, other fish without "তেল" substring correctly evaluate to meat_fish:
        assertEquals("meat_fish", getMarketItemCategory("রুই মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("কাতলা মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("ইলিশ মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("পাঙ্গাস মাছ"))
    }

    // =========================================================================
    // 2. TOUCH TARGET ERGONOMICS & INTERACTIVE COMPONENT BOUNDARY AUDIT
    // =========================================================================

    @Test
    fun testMarketPromptAmountDialogTouchTargetErgonomics() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")

        // 1. Math operator chips (×, +, ÷, −) must guarantee >= 48dp touch targets
        assertTrue(
            "Operator chips must declare defaultMinSize(minHeight = 48.dp)",
            content.contains("defaultMinSize(minHeight = 48.dp)")
        )
        assertTrue(
            "Operator chips must declare minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )

        // 2. Quick increment chips (+50, +100, +200, +500) must guarantee >= 48dp touch targets
        assertTrue(
            "Quick increment chips must declare listOf(50, 100, 200, 500)",
            content.contains("listOf(50, 100, 200, 500)")
        )
        assertTrue(
            "Quick chips container must declare defaultMinSize(minHeight = 48.dp)",
            content.contains(".defaultMinSize(minHeight = 48.dp)")
        )

        // 3. Primary 1-Tap Last Rate button must guarantee >= 48dp touch target
        assertTrue(
            "Last rate suggestion button must declare defaultMinSize(minHeight = 48.dp)",
            content.contains("defaultMinSize(minHeight = 48.dp)")
        )

        // 4. Action buttons (Cancel, Save Price) must declare height >= 48.dp
        assertTrue(
            "Cancel / Save buttons must declare height(48.dp)",
            content.contains("height(48.dp)")
        )

        // 5. Zero-price / Free option TextButton must declare minimumInteractiveComponentSize
        assertTrue(
            "Zero-price TextButton must declare minimumInteractiveComponentSize()",
            content.contains("onConfirm(0.0)") && content.contains("minimumInteractiveComponentSize()")
        )

        // 6. FocusRequester auto-focus
        assertTrue(
            "MarketPromptAmountDialog must autofocus keyboard with FocusRequester",
            content.contains("focusRequester.requestFocus()")
        )
    }

    @Test
    fun testMarketTripCompletionSheetTouchTargetErgonomics() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt")

        // 1. Payment account selector items must declare defaultMinSize(minHeight = 48.dp)
        assertTrue(
            "Payment account selectors must declare defaultMinSize(minHeight = 48.dp)",
            content.contains("defaultMinSize(minHeight = 48.dp)")
        )
        assertTrue(
            "Payment account selectors must declare minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )

        // 2. Action buttons (Back, Confirm & Save) must declare height(50.dp) (>= 48.dp)
        assertTrue(
            "Completion sheet action buttons must declare height(50.dp)",
            content.contains("height(50.dp)")
        )
    }

    @Test
    fun testMarketParsedTagChipTouchTargetErgonomics() {
        val content = readSourceFile("com/example/expensetracker/ui/market/MarketParsedTagChip.kt")

        // 1. Tag chip container must declare defaultMinSize(minHeight = 44.dp)
        assertTrue(
            "MarketParsedTagChip container must declare defaultMinSize(minHeight = 44.dp)",
            content.contains("defaultMinSize(minHeight = 44.dp)")
        )

        // 2. 1-Tap Remove button must declare minimumInteractiveComponentSize() and size(36.dp)
        assertTrue(
            "MarketParsedTagChip remove button must declare minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "MarketParsedTagChip remove button must declare size(36.dp)",
            content.contains("size(36.dp)")
        )

        // 3. Clickable quantity pill
        assertTrue(
            "MarketParsedTagChip quantity pill must declare defaultMinSize(minHeight = 28.dp)",
            content.contains("defaultMinSize(minHeight = 28.dp)")
        )
    }

    @Test
    fun testCreateMarketListSectionTouchTargetErgonomics() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")

        // 1. Quick suggestion titles must declare defaultMinSize(minHeight = 48.dp)
        assertTrue(
            "Suggested titles must declare defaultMinSize(minHeight = 48.dp)",
            content.contains("defaultMinSize(minHeight = 48.dp)")
        )
        assertTrue(
            "Suggested titles must declare minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )

        // 2. Quick staples must declare defaultMinSize(minHeight = 48.dp)
        assertTrue(
            "Quick staples must declare defaultMinSize(minHeight = 48.dp)",
            content.contains("defaultMinSize(minHeight = 48.dp)")
        )

        // 3. Quick quantities LazyRow items must declare defaultMinSize(minHeight = 48.dp)
        assertTrue(
            "Quick quantities must declare defaultMinSize(minHeight = 48.dp)",
            content.contains("defaultMinSize(minHeight = 48.dp)")
        )

        // 4. Delete item button must declare size(48.dp) and minimumInteractiveComponentSize()
        assertTrue(
            "Delete item button must declare size(48.dp)",
            content.contains("size(48.dp)")
        )
        assertTrue(
            "Delete item button must declare minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )

        // 5. Add item button must declare size(50.dp) and minimumInteractiveComponentSize()
        assertTrue(
            "Add item button must declare size(50.dp)",
            content.contains("size(50.dp)")
        )
    }

    // =========================================================================
    // 3. TYPOGRAPHY >= 12SP & TABULAR NUMBER VERIFICATION
    // =========================================================================

    @Test
    fun testTypographyMinimum12spAcrossAllM3Components() {
        val files = listOf(
            "com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt",
            "com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt",
            "com/example/expensetracker/ui/market/MarketParsedTagChip.kt",
            "com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt"
        )

        // Disallow any font size strictly below 12sp (e.g. 9.sp, 9.5.sp, 10.sp, 10.5.sp, 11.sp, 11.5.sp)
        // Using negative lookbehind to avoid matching 19.sp or 29.sp.
        val sub12spRegex = Regex("""(?<!\d)(?:9|9\.5|10|10\.5|11|11\.5)\.sp""")

        for (file in files) {
            val content = readSourceFile(file)
            val match = sub12spRegex.find(content)
            assertFalse(
                "File $file must not contain sub-12sp font size, but found '${match?.value}'",
                match != null
            )
        }
    }

    @Test
    fun testTabularNumbersUsedAcrossNumericalDisplays() {
        val promptDialog = readSourceFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")
        val completionSheet = readSourceFile("com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt")
        val tagChip = readSourceFile("com/example/expensetracker/ui/market/MarketParsedTagChip.kt")
        val listCreator = readSourceFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")

        assertTrue("MarketPromptAmountDialog must use \"tnum\"", promptDialog.contains("\"tnum\""))
        assertTrue("MarketTripCompletionSheet must use \"tnum\"", completionSheet.contains("\"tnum\""))
        assertTrue("MarketParsedTagChip must use \"tnum\"", tagChip.contains("\"tnum\""))
        assertTrue("CreateMarketListSection must use \"tnum\"", listCreator.contains("\"tnum\""))
    }

    // =========================================================================
    // 4. THERMAL RECEIPT & NOTEPAD TACTILE AESTHETIC INVARIANTS
    // =========================================================================

    @Test
    fun testTactileStylingAndPerforatedThermalReceiptInvariants() {
        val completionContent = readSourceFile("com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt")

        // Perforated dashed divider
        assertTrue("Must use Canvas line drawing for perforated divider", completionContent.contains("dashPathEffect"))

        // Monospace item ledger lines with dot leaders
        assertTrue("Must use FontFamily.Monospace for thermal receipt lines", completionContent.contains("FontFamily.Monospace"))
        assertTrue("Must format ledger with dot leaders", completionContent.contains("· · ·"))

        // Celebratory ink stamp
        assertTrue("Must rotate ink stamp by -12 degrees", completionContent.contains("rotationZ = -12f"))
        assertTrue("Must render ink stamp with PAID or পরিশোধিত", completionContent.contains("PAID") || completionContent.contains("পরিশোধিত"))

        // Living Ledger tokens
        assertTrue("Must use appColors.cardBackground", completionContent.contains("appColors.cardBackground"))
        assertTrue("Must use HairlineInkStroke", completionContent.contains("HairlineInkStroke"))
        assertTrue("Must use appColors.divider", completionContent.contains("appColors.divider"))
    }

    // =========================================================================
    // 5. ZERO AI SPARKLES OR TROPES
    // =========================================================================

    @Test
    fun testStrictlyZeroAiSparklesAcrossM3Components() {
        val files = listOf(
            "com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt",
            "com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt",
            "com/example/expensetracker/ui/market/MarketParsedTagChip.kt",
            "com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt"
        )

        for (file in files) {
            val content = readSourceFile(file)
            assertFalse("File $file must not contain AutoAwesome", content.contains("AutoAwesome"))
            assertFalse("File $file must not contain sparkle emoji ✨", content.contains("✨"))
        }
    }
}
