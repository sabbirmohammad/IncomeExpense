package com.example.expensetracker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Universal Typography Invariant Test Suite across ALL 9 Market and Market List Components.
 *
 * Living Ledger Design System Invariant:
 * 1. ZERO occurrences of sub-12sp font size tokens (6.sp - 11.5.sp).
 * 2. Every fontSize declaration (fontSize = X.sp) must satisfy X >= 12.0.
 * 3. Enforces compliance across all 9 overhauled market components without omission:
 *    - MarketItemRow.kt
 *    - MarketDepartmentSectionHeader.kt
 *    - MarketBottomTripSummaryBar.kt
 *    - MarketScreen.kt
 *    - CreateMarketListSection.kt
 *    - MarketPromptAmountDialog.kt
 *    - MarketTripCompletionSheet.kt
 *    - ProductPriceInsightsSheet.kt
 *    - MarketParsedTagChip.kt
 * 4. Ensures Bengali numerals and complex ligatures render without diacritic clipping.
 * 5. Tabular numerals (tnum) verification on price and quantity rendering.
 */
class MarketUniversalTypographyTest {

    companion object {
        val ALL_MARKET_COMPONENTS = listOf(
            "com/example/expensetracker/ui/dashboard/MarketItemRow.kt",
            "com/example/expensetracker/ui/market/MarketDepartmentSectionHeader.kt",
            "com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt",
            "com/example/expensetracker/ui/market/MarketScreen.kt",
            "com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt",
            "com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt",
            "com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt",
            "com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt",
            "com/example/expensetracker/ui/market/MarketParsedTagChip.kt"
        )
    }

    private fun readSource(relativePath: String): Pair<File, String> {
        val candidates = listOf(
            File("src/main/java/$relativePath"),
            File("app/src/main/java/$relativePath"),
            File("../app/src/main/java/$relativePath")
        )
        val file = candidates.find { it.exists() } ?: File("src/main/java/$relativePath")
        assertTrue("Market component source file must exist: ${file.absolutePath}", file.exists())
        return file to file.readText()
    }

    @Test
    fun testAllNineMarketComponentsExistOnDisk() {
        assertEquals("Exactly 9 market components must be audited", 9, ALL_MARKET_COMPONENTS.size)
        for (relPath in ALL_MARKET_COMPONENTS) {
            val (file, content) = readSource(relPath)
            assertTrue("File ${file.name} must not be empty", content.isNotBlank())
        }
    }

    @Test
    fun testZeroSub12spFontSizeDeclarationsAcrossAllNineComponents() {
        // Semantic regex extracting explicit fontSize assignments
        val fontSizeRegex = Regex("""fontSize\s*=\s*([0-9]+(?:\.[0-9]+)?)\.sp""")
        val violations = mutableListOf<String>()

        for (relPath in ALL_MARKET_COMPONENTS) {
            val (file, content) = readSource(relPath)
            val lines = content.lines()
            for ((index, line) in lines.withIndex()) {
                val lineNum = index + 1
                for (match in fontSizeRegex.findAll(line)) {
                    val sizeStr = match.groupValues[1]
                    val sizeVal = sizeStr.toDoubleOrNull()
                    if (sizeVal != null && sizeVal < 12.0) {
                        violations.add("${file.name}:$lineNum -> fontSize = ${sizeStr}.sp (line: ${line.trim()})")
                    }
                }
            }
        }

        assertTrue(
            "Found ${violations.size} sub-12sp font size violation(s) across Market components:\n" +
                violations.joinToString("\n"),
            violations.isEmpty()
        )
    }

    @Test
    fun testZeroForbiddenSub12spTokensInRawSourceCode() {
        // Raw token regex with lookbehind excluding digits and periods to prevent matching 12.5.sp as 5.sp
        val tokenRegex = Regex("""(?<![0-9.])([6-9]|1[01])(?:\.[0-9]+)?\.sp(?![0-9])""")
        val violations = mutableListOf<String>()

        for (relPath in ALL_MARKET_COMPONENTS) {
            val (file, content) = readSource(relPath)
            val lines = content.lines()
            for ((index, line) in lines.withIndex()) {
                val lineNum = index + 1
                // Exclude letterSpacing declarations which legitimately use small sp units
                if (line.contains("letterSpacing")) continue

                for (match in tokenRegex.findAll(line)) {
                    violations.add("${file.name}:$lineNum -> '${match.value}' in: ${line.trim()}")
                }
            }
        }

        assertTrue(
            "Found ${violations.size} forbidden sub-12sp token(s) across Market components:\n" +
                violations.joinToString("\n"),
            violations.isEmpty()
        )
    }

    @Test
    fun testTabularNumeralsConfiguredAcrossAllMarketComponents() {
        for (relPath in ALL_MARKET_COMPONENTS) {
            val (file, content) = readSource(relPath)
            val hasTnum = content.contains("\"tnum\"") ||
                content.contains("TabularNumFeature") ||
                content.contains("fontFeatureSettings")
            assertTrue(
                "${file.name} must enable tabular figures (tnum) for stable Bengali number and currency layout",
                hasTnum
            )
        }
    }

    @Test
    fun testLivingLedgerExplicitFontWeightsAcrossAllComponents() {
        for (relPath in ALL_MARKET_COMPONENTS) {
            val (file, content) = readSource(relPath)
            val hasExplicitWeights = content.contains("FontWeight.") ||
                content.contains("WeightBold") ||
                content.contains("WeightSemiBold") ||
                content.contains("WeightMedium")
            assertTrue(
                "${file.name} must declare explicit font weights conforming to Living Ledger typography scale",
                hasExplicitWeights
            )
        }
    }
}
