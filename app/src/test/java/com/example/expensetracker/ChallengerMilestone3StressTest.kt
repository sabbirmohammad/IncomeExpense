package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.random.Random

/**
 * Challenger Empirical Stress Test Suite for Milestone M3:
 * Rigorously stress-tests:
 * 1. Compound arithmetic expressions with operator precedence, division by zero, floating point decimals, rapid multi-operator sequences.
 * 2. Bulk WhatsApp paste stress testing: irregular whitespace, Bengali fractions ("দেড় কেজি", "আড়াই লিটার", "পোয়া"), multi-line mixed Bangla/English text.
 * 3. Rapid amount increment clicks (e.g. +50, +100, +200, +500 combinations) and state transitions.
 * 4. Unit converter base price calculations and edge-case resilience (zero/negative quantities).
 * 5. Touch target ergonomics (>= 48dp), typography constraints (>= 12sp), and zero AI sparkle tropes across all M3 components.
 */
class ChallengerMilestone3StressTest {

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
    // SECTION 1: COMPOUND ARITHMETIC EXPRESSIONS & OPERATOR PRECEDENCE
    // =========================================================================

    @Test
    fun testOperatorPrecedenceCompoundExpressions() {
        // Multiplicative precedence over addition and subtraction:
        // 120 + 85 * 2 = 120 + 170 = 290 (not (120+85)*2 = 410)
        assertEquals(290.0, CalculatorEvaluator.evaluate("120 + 85 * 2")!!, 0.001)

        // 100 - 20 * 3 + 10 / 2 = 100 - 60 + 5 = 45
        assertEquals(45.0, CalculatorEvaluator.evaluate("100 - 20 * 3 + 10 / 2")!!, 0.001)

        // Multiple multiplications and divisions in succession
        // 100 * 2 / 4 * 3 = 200 / 4 * 3 = 50 * 3 = 150
        assertEquals(150.0, CalculatorEvaluator.evaluate("100 * 2 / 4 * 3")!!, 0.001)

        // Decimals with precedence: 45 * 2.5 + 10.5 / 2 = 112.5 + 5.25 = 117.75
        assertEquals(117.75, CalculatorEvaluator.evaluate("45 * 2.5 + 10.5 / 2")!!, 0.001)

        // Subtraction associativity: 100 - 30 - 20 = 50 (not 100 - (30 - 20) = 90)
        assertEquals(50.0, CalculatorEvaluator.evaluate("100 - 30 - 20")!!, 0.001)

        // Division associativity: 120 / 4 / 2 = 30 / 2 = 15 (not 120 / (4 / 2) = 60)
        assertEquals(15.0, CalculatorEvaluator.evaluate("120 / 4 / 2")!!, 0.001)
    }

    @Test
    fun testBengaliAndUnicodeOperatorExpressions() {
        // Unicode operators: × and ÷
        assertEquals(290.0, CalculatorEvaluator.evaluate("120 + 85 × 2")!!, 0.001)
        assertEquals(45.0, CalculatorEvaluator.evaluate("100 − 20 × 3 + 10 ÷ 2")!!, 0.001)

        // Bengali digits with Unicode operators: ১২০ + ৮৫ × ২ = ২৯০
        assertEquals(290.0, CalculatorEvaluator.evaluate("১২০ + ৮৫ × ২")!!, 0.001)

        // Bengali decimals: ৫০.৫ × ২ + ১০০ ÷ ৪ = ১০১ + ২৫ = ১২৬
        assertEquals(126.0, CalculatorEvaluator.evaluate("৫০.৫ × ২ + ১০০ ÷ ৪")!!, 0.001)

        // Em-dash as minus: 150 — 50 = 100
        assertEquals(100.0, CalculatorEvaluator.evaluate("150 — 50")!!, 0.001)

        // Spaces and tabs inside expression
        assertEquals(200.0, CalculatorEvaluator.evaluate("  100   +   100  ")!!, 0.001)
    }

    @Test
    fun testDivisionByZeroResilienceAndOracles() {
        // Direct division by zero
        assertNull(CalculatorEvaluator.evaluate("100 / 0"))
        assertNull(CalculatorEvaluator.evaluate("100 ÷ 0"))
        assertNull(CalculatorEvaluator.evaluate("100 / 0.0"))
        assertNull(CalculatorEvaluator.evaluate("100 / -0.0"))
        assertNull(CalculatorEvaluator.evaluate("0 / 0"))
        assertNull(CalculatorEvaluator.evaluate("০ / ০"))

        // Division by zero in chained compound expressions
        assertNull(CalculatorEvaluator.evaluate("50 + 100 / 0 - 20"))
        assertNull(CalculatorEvaluator.evaluate("10 * 5 / 0"))
        assertNull(CalculatorEvaluator.evaluate("10 / 0 * 5"))
        assertNull(CalculatorEvaluator.evaluate("100 + 50 ÷ 0.0"))

        // Extension function toParsedAmountOrNull must also yield null
        assertNull("100 / 0".toParsedAmountOrNull())
        assertNull("100 ÷ 0".toParsedAmountOrNull())
        assertNull("০ ÷ ০".toParsedAmountOrNull())
    }

    @Test
    fun testFloatingPointDecimalsAndFormatting() {
        // Standard decimals
        val decResult = CalculatorEvaluator.evaluate("0.1 + 0.2")
        assertNotNull(decResult)
        assertEquals(0.3, decResult!!, 0.0001)

        // Leading and trailing dots
        assertEquals(3.5, CalculatorEvaluator.evaluate(".5 + 3")!!, 0.001)
        assertEquals(8.0, CalculatorEvaluator.evaluate("5. + 3.")!!, 0.001)

        // Bengali decimal digits
        assertEquals(15.75, CalculatorEvaluator.evaluate("১০.৫ + ৫.২৫")!!, 0.001)

        // formatResult verification
        assertEquals("200", CalculatorEvaluator.formatResult(200.0))
        assertEquals("200.50", CalculatorEvaluator.formatResult(200.5))
        assertEquals("0", CalculatorEvaluator.formatResult(0.0))
    }

    @Test
    fun testRapidMultiOperatorSequencesStressHarness() {
        // Stress harness: 1,000 synthetic random operator sequences.
        // None must throw an unhandled exception or crash the process.
        val rng = Random(1337)
        val validOps = listOf("+", "-", "*", "/", "×", "÷", "−")
        val invalidTokens = listOf("++", "---", "**", "//", "+*", "-/", "*/", "/*", "+++", "... ", "??", "##", "$$", "abc")

        for (i in 0 until 1000) {
            val num1 = rng.nextInt(1, 500)
            val num2 = rng.nextInt(1, 500)
            val isBroken = rng.nextBoolean()

            val expr = if (isBroken) {
                val badOp = invalidTokens[rng.nextInt(invalidTokens.size)]
                "$num1 $badOp $num2"
            } else {
                val goodOp = validOps[rng.nextInt(validOps.size)]
                "$num1 $goodOp $num2"
            }

            try {
                val result = CalculatorEvaluator.evaluate(expr)
                if (isBroken) {
                    assertNull("Broken expression '$expr' should evaluate to null", result)
                } else {
                    assertNotNull("Valid expression '$expr' should evaluate", result)
                    assertTrue("Result must be finite", result!!.isFinite())
                }
            } catch (t: Throwable) {
                org.junit.Assert.fail("CalculatorEvaluator.evaluate threw unhandled exception on: $expr, cause: ${t.message}")
            }
        }
    }

    @Test
    fun testKeypadIncompleteExpressionStateSafety() {
        // When user types operator in keypad, expression is temporarily incomplete.
        // Evaluate must safely return null without crashing.
        val incompleteInputs = listOf("100+", "100-", "100*", "100/", "100×", "100÷", "+", "-", "*", "/", ".")
        for (input in incompleteInputs) {
            val result = CalculatorEvaluator.evaluate(input)
            assertNull("Incomplete input '$input' must evaluate to null", result)
        }
    }

    // =========================================================================
    // SECTION 2: BULK WHATSAPP PASTE STRESS TESTING
    // =========================================================================

    @Test
    fun testIrregularWhitespaceAndListBullets() {
        val messyPastedList = """
            
            1.   আলু    ২ কেজি   
            2)  পেঁয়াজ   ১   কেজি   
            *   সয়াবিন   তেল   ২   লিটার  
            •    ডিম   ১   ডজন   
            -   মুরগি   ১.৫   কেজি  
            [ ]  চিনি    ১    কেজি   
            (৭)   রসুন   ২৫০   গ্রাম  
            >   আদা   ২৫০   গ্রাম   
            ✔   কাঁচামরিচ   ১০০   গ্রাম   
            ১০.  লবণ   ১   কেজি   
            
        """.trimIndent()

        val items = MarketListParser.parseBulkText(messyPastedList)
        assertEquals("Should parse all 10 items despite irregular whitespace and bullets", 10, items.size)

        assertEquals("আলু", items[0].name)
        assertEquals(2.0, items[0].quantity, 0.01)
        assertEquals("কেজি", items[0].unit)

        assertEquals("পেঁয়াজ", items[1].name)
        assertEquals(1.0, items[1].quantity, 0.01)
        assertEquals("কেজি", items[1].unit)

        assertEquals("সয়াবিন তেল", items[2].name)
        assertEquals(2.0, items[2].quantity, 0.01)
        assertEquals("লিটার", items[2].unit)

        assertEquals("ডিম", items[3].name)
        assertEquals(1.0, items[3].quantity, 0.01)
        assertEquals("ডজন", items[3].unit)

        assertEquals("মুরগি", items[4].name)
        assertEquals(1.5, items[4].quantity, 0.01)
        assertEquals("কেজি", items[4].unit)

        assertEquals("চিনি", items[5].name)
        assertEquals(1.0, items[5].quantity, 0.01)
        assertEquals("কেজি", items[5].unit)

        assertEquals("রসুন", items[6].name)
        assertEquals(250.0, items[6].quantity, 0.01)
        assertEquals("গ্রাম", items[6].unit)

        assertEquals("আদা", items[7].name)
        assertEquals(250.0, items[7].quantity, 0.01)
        assertEquals("গ্রাম", items[7].unit)

        assertEquals("কাঁচামরিচ", items[8].name)
        assertEquals(100.0, items[8].quantity, 0.01)
        assertEquals("গ্রাম", items[8].unit)

        assertEquals("লবণ", items[9].name)
        assertEquals(1.0, items[9].quantity, 0.01)
        assertEquals("কেজি", items[9].unit)
    }

    @Test
    fun testBengaliFractionsParsing() {
        // 1. "দেড় কেজি" (1.5 kg) and alternative spelling "দেড়"
        val item1 = MarketListParser.parseSingleLine("মুরগি দেড় কেজি")
        assertNotNull(item1)
        assertEquals("মুরগি", item1?.name)
        assertEquals(1.5, item1?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item1?.unit)

        val item1b = MarketListParser.parseSingleLine("গরুর মাংস দেড় কেজি")
        assertNotNull(item1b)
        assertEquals("গরুর মাংস", item1b?.name)
        assertEquals(1.5, item1b?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item1b?.unit)

        // 2. "আড়াই লিটার" (2.5 L) and alternative spelling "আড়াই"
        val item2 = MarketListParser.parseSingleLine("সয়াবিন তেল আড়াই লিটার")
        assertNotNull(item2)
        assertEquals("সয়াবিন তেল", item2?.name)
        assertEquals(2.5, item2?.quantity ?: 0.0, 0.01)
        assertEquals("লিটার", item2?.unit)

        val item2b = MarketListParser.parseSingleLine("দুধ আড়াই লিটার")
        assertNotNull(item2b)
        assertEquals("দুধ", item2b?.name)
        assertEquals(2.5, item2b?.quantity ?: 0.0, 0.01)
        assertEquals("লিটার", item2b?.unit)

        // 3. "হাফ" / "আধা" (0.5)
        val item3 = MarketListParser.parseSingleLine("পেঁয়াজ হাফ কেজি")
        assertNotNull(item3)
        assertEquals("পেঁয়াজ", item3?.name)
        assertEquals(0.5, item3?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item3?.unit)

        val item3b = MarketListParser.parseSingleLine("আধা কেজি মসুর ডাল")
        assertNotNull(item3b)
        assertEquals("মসুর ডাল", item3b?.name)
        assertEquals(0.5, item3b?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item3b?.unit)

        // 4. "পোয়া" (powa) variations
        // When unit is unspecified, "পোয়া" defaults to 250 গ্রাম
        val item4 = MarketListParser.parseSingleLine("পোয়া কাঁচামরিচ")
        assertNotNull(item4)
        assertEquals("কাঁচামরিচ", item4?.name)
        assertEquals(250.0, item4?.quantity ?: 0.0, 0.01)
        assertEquals("গ্রাম", item4?.unit)

        val item4b = MarketListParser.parseSingleLine("রসুন এক পোয়া")
        assertNotNull(item4b)
        assertEquals("রসুন", item4b?.name)
        assertEquals(250.0, item4b?.quantity ?: 0.0, 0.01)
        assertEquals("গ্রাম", item4b?.unit)

        val item4c = MarketListParser.parseSingleLine("আদা ১ পোয়া")
        assertNotNull(item4c)
        assertEquals("আদা", item4c?.name)
        assertEquals(250.0, item4c?.quantity ?: 0.0, 0.01)
        assertEquals("গ্রাম", item4c?.unit)

        // "পোয়া" with explicit unit "কেজি": 1/4 kg
        val item4d = MarketListParser.parseSingleLine("পোয়া কেজি ঘি")
        assertNotNull(item4d)
        assertEquals("ঘি", item4d?.name)
        assertEquals(0.25, item4d?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item4d?.unit)

        // 5. Fraction notations: "১/২", "1/2", "১/৪", "৩/৪"
        val item5a = MarketListParser.parseSingleLine("১/২ কেজি মিষ্টি")
        assertNotNull(item5a)
        assertEquals("মিষ্টি", item5a?.name)
        assertEquals(0.5, item5a?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item5a?.unit)

        val item5b = MarketListParser.parseSingleLine("৩/৪ কেজি পনির")
        assertNotNull(item5b)
        assertEquals("পনির", item5b?.name)
        assertEquals(0.75, item5b?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item5b?.unit)
    }

    @Test
    fun testMultiLineMixedBanglaEnglishText() {
        val mixedWhatsAppExport = """
            Bazaar List for Tomorrow:
            1. Potato 2.5 kg
            2. Onion - দেড় কেজি
            3. Soybean Oil 5 L
            4. Farm Eggs 1 dozen
            5. Broiler Chicken 2kg
            6. Milk - আড়াই লিটার
            7. Green Chili - এক পোয়া
            8. Garlic 250g
            9. Bread 1 pack
            10. Salt 1 kg
        """.trimIndent()

        val parsed = MarketListParser.parseBulkText(mixedWhatsAppExport)
        // 10 items (the header line without quantity will parse with default quantity 1.0 or get filtered)
        assertTrue("Parsed list should have at least 10 items", parsed.size >= 10)

        // Find items by name
        val potato = parsed.find { it.name.contains("Potato", ignoreCase = true) }
        assertNotNull(potato)
        assertEquals(2.5, potato!!.quantity, 0.01)
        assertEquals("কেজি", potato.unit)

        val onion = parsed.find { it.name.contains("Onion", ignoreCase = true) }
        assertNotNull(onion)
        assertEquals(1.5, onion!!.quantity, 0.01)
        assertEquals("কেজি", onion.unit)

        val oil = parsed.find { it.name.contains("Oil", ignoreCase = true) }
        assertNotNull(oil)
        assertEquals(5.0, oil!!.quantity, 0.01)
        assertEquals("লিটার", oil.unit)

        val eggs = parsed.find { it.name.contains("Eggs", ignoreCase = true) }
        assertNotNull(eggs)
        assertEquals(1.0, eggs!!.quantity, 0.01)
        assertEquals("ডজন", eggs.unit)

        val chili = parsed.find { it.name.contains("Chili", ignoreCase = true) }
        assertNotNull(chili)
        assertEquals(250.0, chili!!.quantity, 0.01)
        assertEquals("গ্রাম", chili.unit)

        val garlic = parsed.find { it.name.contains("Garlic", ignoreCase = true) }
        assertNotNull(garlic)
        assertEquals(250.0, garlic!!.quantity, 0.01)
        assertEquals("গ্রাম", garlic.unit)
    }

    @Test
    fun testChaoticDelimitersAndPunctuation() {
        // Multi-item lines separated by commas, semicolons, and Bengali dari (।)
        val chaoticInput = "আলু ১ কেজি, পেঁয়াজ ২ কেজি; ডিম ১ ডজন। তেল ৫ লিটার"
        val parsed = MarketListParser.parseBulkText(chaoticInput)
        assertEquals(4, parsed.size)

        assertEquals("আলু", parsed[0].name)
        assertEquals(1.0, parsed[0].quantity, 0.01)
        assertEquals("কেজি", parsed[0].unit)

        assertEquals("পেঁয়াজ", parsed[1].name)
        assertEquals(2.0, parsed[1].quantity, 0.01)
        assertEquals("কেজি", parsed[1].unit)

        assertEquals("ডিম", parsed[2].name)
        assertEquals(1.0, parsed[2].quantity, 0.01)
        assertEquals("ডজন", parsed[2].unit)

        assertEquals("তেল", parsed[3].name)
        assertEquals(5.0, parsed[3].quantity, 0.01)
        assertEquals("লিটার", parsed[3].unit)
    }

    // =========================================================================
    // SECTION 3: RAPID AMOUNT INCREMENT CLICKS & KEYPAD SIMULATION
    // =========================================================================

    @Test
    fun testRapidAmountIncrementSimulationHarness() {
        // Simulate MarketPromptAmountDialog's addQuickAmount lambda:
        // val addQuickAmount = { addAmt: Int ->
        //     val current = evaluatedAmount ?: MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull() ?: 0.0
        //     val nextAmt = (current + addAmt).toInt()
        //     promptAmountText = nextAmt.toString()
        // }

        val chips = listOf(50, 100, 200, 500)
        val rng = Random(42)

        // Test 1: 10,000 rapid randomized clicks starting from empty string
        var promptText = ""
        var expectedSum = 0L

        for (i in 0 until 10000) {
            val chip = chips[rng.nextInt(chips.size)]
            val evaluated = CalculatorEvaluator.evaluate(promptText)
                ?: MarketListParser.convertBengaliDigitsToEnglish(promptText).toDoubleOrNull()
                ?: 0.0
            val nextAmt = (evaluated + chip).toLong()
            promptText = nextAmt.toString()
            expectedSum += chip

            // Check every 1000 iterations
            if (i % 1000 == 0) {
                assertEquals("Accumulator mismatch at iteration $i", expectedSum, promptText.toLong())
            }
        }
        assertEquals(expectedSum, promptText.toLong())

        // Test 2: Starting from existing decimal amount (e.g. "120.50")
        promptText = "120.50"
        val eval1 = CalculatorEvaluator.evaluate(promptText)
            ?: MarketListParser.convertBengaliDigitsToEnglish(promptText).toDoubleOrNull()
            ?: 0.0
        val nextAmt1 = (eval1 + 50).toInt()
        promptText = nextAmt1.toString()
        assertEquals("170", promptText)

        // Test 3: Starting from compound expression (e.g. "45 * 2.5")
        promptText = "45 * 2.5"
        val eval2 = CalculatorEvaluator.evaluate(promptText)
            ?: MarketListParser.convertBengaliDigitsToEnglish(promptText).toDoubleOrNull()
            ?: 0.0
        assertEquals(112.5, eval2, 0.001)
        val nextAmt2 = (eval2 + 50).toInt()
        promptText = nextAmt2.toString()
        assertEquals("162", promptText)

        // Test 4: Starting from Bengali digits (e.g. "২৫০")
        promptText = "২৫০"
        val eval3 = CalculatorEvaluator.evaluate(promptText)
            ?: MarketListParser.convertBengaliDigitsToEnglish(promptText).toDoubleOrNull()
            ?: 0.0
        assertEquals(250.0, eval3, 0.001)
        val nextAmt3 = (eval3 + 100).toInt()
        promptText = nextAmt3.toString()
        assertEquals("350", promptText)

        // Test 5: Starting from incomplete trailing operator (e.g. "100+")
        promptText = "100+"
        val eval4 = CalculatorEvaluator.evaluate(promptText)
            ?: MarketListParser.convertBengaliDigitsToEnglish(promptText).toDoubleOrNull()
            ?: 0.0
        assertEquals(0.0, eval4, 0.001) // Incomplete expression safely yields 0.0 fallback
        val nextAmt4 = (eval4 + 50).toInt()
        promptText = nextAmt4.toString()
        assertEquals("50", promptText) // Gracefully resets to chip amount without error
    }

    @Test
    fun testKeypadEvaluationAndSubmissionConsistency() {
        // Test submitAmount contract:
        // val amt = evaluatedAmount ?: MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull() ?: 0.0
        fun getSubmitAmount(input: String): Double {
            val eval = CalculatorEvaluator.evaluate(input)
                ?: MarketListParser.convertBengaliDigitsToEnglish(input).toDoubleOrNull()
            return eval ?: MarketListParser.convertBengaliDigitsToEnglish(input).toDoubleOrNull() ?: 0.0
        }

        assertEquals(205.0, getSubmitAmount("120 + 85"), 0.001)
        assertEquals(240.0, getSubmitAmount("120 × 2"), 0.001)
        assertEquals(500.0, getSubmitAmount("৫০০"), 0.001)
        assertEquals(125.5, getSubmitAmount("১২৫.৫"), 0.001)
        assertEquals(0.0, getSubmitAmount(""), 0.001)
        assertEquals(0.0, getSubmitAmount("invalid text"), 0.001)
        assertEquals(0.0, getSubmitAmount("100 / 0"), 0.001) // Division by zero falls back to 0.0
    }

    // =========================================================================
    // SECTION 4: UNIT CONVERTER ENGINE & PRICING ORACLES
    // =========================================================================

    @Test
    fun testUnitConverterBasePriceAndCostEstimation() {
        // 5 kg for 300 tk -> 60 tk/kg
        val (rate1, unit1) = UnitConverterEngine.calculateBaseUnitPrice(300.0, 5.0, "কেজি")
        assertEquals(60.0, rate1, 0.001)
        assertEquals("কেজি", unit1)

        // 500 g for 35 tk -> baseQty = 0.5 kg -> 70 tk/kg
        val (rate2, unit2) = UnitConverterEngine.calculateBaseUnitPrice(35.0, 500.0, "গ্রাম")
        assertEquals(70.0, rate2, 0.001)
        assertEquals("কেজি", unit2)

        // 1 hali eggs for 48 tk -> baseQty = 4 pcs -> 12 tk/pc
        val (rate3, unit3) = UnitConverterEngine.calculateBaseUnitPrice(48.0, 1.0, "হালি")
        assertEquals(12.0, rate3, 0.001)
        assertEquals("পিস", unit3)

        // Cost estimation: base rate 60 tk/kg, target 2.5 kg -> 150 tk
        val estCost1 = UnitConverterEngine.estimateCost(60.0, 2.5, "কেজি")
        assertEquals(150.0, estCost1, 0.001)

        // Target 250 g -> 0.25 * 60 = 15 tk
        val estCost2 = UnitConverterEngine.estimateCost(60.0, 250.0, "গ্রাম")
        assertEquals(15.0, estCost2, 0.001)

        // Resilience under zero or negative quantity: must never throw or divide by zero
        val (zeroRate, zeroUnit) = UnitConverterEngine.calculateBaseUnitPrice(100.0, 0.0, "কেজি")
        assertTrue("Zero quantity must not produce NaN or Infinity", zeroRate.isFinite())
        assertEquals("কেজি", zeroUnit)

        val (negRate, negUnit) = UnitConverterEngine.calculateBaseUnitPrice(100.0, -5.0, "কেজি")
        assertTrue("Negative quantity must not produce NaN or Infinity", negRate.isFinite())
        assertEquals("কেজি", negUnit)
    }

    // =========================================================================
    // SECTION 5: TOUCH TARGET ERGONOMICS, TYPOGRAPHY & ZERO AI TROPE AUDIT
    // =========================================================================

    @Test
    fun testTouchTargetErgonomicsAcrossM3Components() {
        // 1. Audit MarketPromptAmountDialog.kt
        val keypadContent = readSourceFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")
        assertTrue(
            "MarketPromptAmountDialog must declare minimumInteractiveComponentSize()",
            keypadContent.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "Operator chips in keypad must satisfy minHeight 48dp",
            keypadContent.contains("defaultMinSize(minHeight = 48.dp)")
        )
        assertTrue(
            "Action buttons must satisfy height 48dp",
            keypadContent.contains("height(48.dp)")
        )

        // 2. Audit CreateMarketListSection.kt
        val createContent = readSourceFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")
        assertTrue(
            "CreateMarketListSection must declare minimumInteractiveComponentSize()",
            createContent.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "Quick suggestions must satisfy minHeight 48dp",
            createContent.contains("defaultMinSize(minHeight = 48.dp)")
        )

        // 3. Audit MarketParsedTagChip.kt
        val chipContent = readSourceFile("com/example/expensetracker/ui/market/MarketParsedTagChip.kt")
        assertTrue(
            "MarketParsedTagChip delete action must have minimumInteractiveComponentSize()",
            chipContent.contains("minimumInteractiveComponentSize()")
        )

        // 4. Audit MarketTripCompletionSheet.kt
        val sheetContent = readSourceFile("com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt")
        assertTrue(
            "MarketTripCompletionSheet payment selector must have minimumInteractiveComponentSize()",
            sheetContent.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "MarketTripCompletionSheet payment selector must have minHeight 48dp",
            sheetContent.contains("defaultMinSize(minHeight = 48.dp)")
        )
    }

    @Test
    fun testBengaliTypographyConstraintsAcrossM3() {
        val filesToCheck = listOf(
            "com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt",
            "com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt",
            "com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt",
            "com/example/expensetracker/ui/market/MarketParsedTagChip.kt"
        )

        val forbiddenSpTokens = listOf("9.sp", "9.5.sp", "10.sp", "10.5.sp", "11.sp", "11.5.sp")

        for (filePath in filesToCheck) {
            val content = readSourceFile(filePath)
            for (forbidden in forbiddenSpTokens) {
                val forbiddenRegex = Regex("""(?<![0-9])${Regex.escape(forbidden)}""")
                assertFalse(
                    "File $filePath violates >= 12sp constraint by containing $forbidden",
                    forbiddenRegex.containsMatchIn(content)
                )
            }
        }
    }

    @Test
    fun testZeroAiSparklesAcrossAllM3Components() {
        val filesToCheck = listOf(
            "com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt",
            "com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt",
            "com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt",
            "com/example/expensetracker/ui/market/MarketParsedTagChip.kt"
        )

        for (filePath in filesToCheck) {
            val content = readSourceFile(filePath)
            assertFalse(
                "File $filePath must not contain AutoAwesome generic AI icon",
                content.contains("AutoAwesome")
            )
            assertFalse(
                "File $filePath must not contain sparkle emoji ✨",
                content.contains("✨")
            )
        }
    }
}
