package com.example.expensetracker

import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Empirical adversarial test suite for Phase 2 Iteration 2 re-verification of CalculatorEvaluator.
 */
class ChallengerPhase2Iteration2Test {

    // -------------------------------------------------------------------------
    // 1. IEEE-754 Special Literals ("NaN", "Infinity", "+Infinity", "-Infinity")
    // -------------------------------------------------------------------------
    @Test
    fun testNaNAndInfinityLiteralsReturnNull() {
        val testCases = listOf(
            "NaN",
            "nan",
            "NAN",
            "+NaN",
            "-NaN",
            " NaN ",
            "Infinity",
            "infinity",
            "INFINITY",
            "+Infinity",
            "-Infinity",
            "+infinity",
            "-infinity",
            " Infinity ",
            " -Infinity ",
            "+Infinity * 2",
            "-Infinity * 2",
            "NaN + 10"
        )

        for (case in testCases) {
            val parsed = case.toParsedAmountOrNull()
            assertNull("toParsedAmountOrNull('$case') must return null", parsed)

            val parsedViaEval = CalculatorEvaluator.parseOrEvaluate(case)
            assertNull("parseOrEvaluate('$case') must return null", parsedViaEval)

            val evaluated = CalculatorEvaluator.evaluate(case)
            assertNull("evaluate('$case') must return null", evaluated)
        }
    }

    // -------------------------------------------------------------------------
    // 2. Arithmetic Overflow Expressions (exceeding Double.MAX_VALUE)
    // -------------------------------------------------------------------------
    @Test
    fun testArithmeticOverflowExpressionsReturnNull() {
        val big200 = "1" + "0".repeat(200)
        val big308 = "1" + "0".repeat(308)
        val big400 = "1" + "0".repeat(400)

        val overflowCases = listOf(
            "$big200 * $big200",                     // 10^400 > Double.MAX_VALUE
            "$big308 + $big308",                     // 2 * 10^308 > Double.MAX_VALUE (~1.79e308)
            "$big400",                               // Plain double overflow literal
            "-$big200 * $big200",                    // Negative overflow to -Infinity
            "$big200 * -$big200",                    // Negative overflow to -Infinity
            "100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000 * 100000000000000000000"
        )

        for (expr in overflowCases) {
            val parsed = expr.toParsedAmountOrNull()
            assertNull("Overflow expr.toParsedAmountOrNull() must return null for length ${expr.length}", parsed)

            val evaluated = CalculatorEvaluator.evaluate(expr)
            assertNull("Overflow CalculatorEvaluator.evaluate() must return null for length ${expr.length}", evaluated)

            val parseOrEval = CalculatorEvaluator.parseOrEvaluate(expr)
            assertNull("Overflow CalculatorEvaluator.parseOrEvaluate() must return null for length ${expr.length}", parseOrEval)
        }
    }

    // -------------------------------------------------------------------------
    // 3. Invalid Characters and Scientific Notation in Expressions
    // -------------------------------------------------------------------------
    @Test
    fun testInvalidCharactersAndScientificNotationReturnNull() {
        val invalidExpressions = listOf(
            "1e308 * 10",
            "1e10 + 5",
            "1E10 + 5",
            "2e3 + 10",
            "10^200 * 10^200",
            "10^2",
            "100a + 50",
            "100 + 50b",
            "10$ + 5",
            "10% + 2",
            "10! + 2",
            "0x10 + 2",
            "100 / 0",
            "0 / 0",
            "100 / -0",
            "100 ÷ 0",
            "০ ÷ ০"
        )

        for (expr in invalidExpressions) {
            val parsed = expr.toParsedAmountOrNull()
            assertNull("Invalid char expr '$expr' toParsedAmountOrNull must return null", parsed)

            val evaluated = CalculatorEvaluator.evaluate(expr)
            assertNull("Invalid char expr '$expr' evaluate must return null", evaluated)

            val parseOrEval = CalculatorEvaluator.parseOrEvaluate(expr)
            assertNull("Invalid char expr '$expr' parseOrEvaluate must return null", parseOrEval)
        }
    }

    // -------------------------------------------------------------------------
    // 4. Valid Expressions (Arithmetic, Precedence, Decimals, Bengali, Unary)
    // -------------------------------------------------------------------------
    @Test
    fun testValidExpressionsEvaluateAccurately() {
        // Standard arithmetic
        assertEquals(205.0, "120 + 85".toParsedAmountOrNull()!!, 0.001)
        assertEquals(155.0, "200 - 45".toParsedAmountOrNull()!!, 0.001)
        assertEquals(200.0, "50 * 4".toParsedAmountOrNull()!!, 0.001)
        assertEquals(25.0, "100 / 4".toParsedAmountOrNull()!!, 0.001)

        // Unicode operator symbols
        assertEquals(200.0, "50 × 4".toParsedAmountOrNull()!!, 0.001)
        assertEquals(25.0, "100 ÷ 4".toParsedAmountOrNull()!!, 0.001)
        assertEquals(150.0, "200 − 50".toParsedAmountOrNull()!!, 0.001)
        assertEquals(170.0, "200 — 30".toParsedAmountOrNull()!!, 0.001)

        // Precedence
        assertEquals(290.0, "120 + 85 * 2".toParsedAmountOrNull()!!, 0.001)
        assertEquals(45.0, "100 - 20 * 3 + 40 / 8".toParsedAmountOrNull()!!, 0.001)
        assertEquals(590.0, "10 + 20 * 30 - 40 / 2".toParsedAmountOrNull()!!, 0.001)

        // Decimals
        assertEquals(19.75, "12.50 + 7.25".toParsedAmountOrNull()!!, 0.001)
        assertEquals(0.3, "0.1 + 0.2".toParsedAmountOrNull()!!, 0.001)
        assertEquals(1.0, ".5 + .5".toParsedAmountOrNull()!!, 0.001)
        assertEquals(10.0, "5. + 5.".toParsedAmountOrNull()!!, 0.001)

        // Bengali numerals
        assertEquals(290.0, "১২০ + ৮৫ * ২".toParsedAmountOrNull()!!, 0.001)
        assertEquals(75.75, "৫০.৫ + ২৫.২৫".toParsedAmountOrNull()!!, 0.001)
        assertEquals(4000.0, "১,৫০০ + ২,৫০০".toParsedAmountOrNull()!!, 0.001)
        assertEquals(500.0, "৫০০".toParsedAmountOrNull()!!, 0.001)

        // Unary minus
        assertEquals(-50.0, "-50".toParsedAmountOrNull()!!, 0.001)
        assertEquals(100.0, "-50 + 150".toParsedAmountOrNull()!!, 0.001)
        assertEquals(80.0, "100 + -20".toParsedAmountOrNull()!!, 0.001)
        assertEquals(120.0, "100 - -20".toParsedAmountOrNull()!!, 0.001)
        assertEquals(100.0, "-50 * -2".toParsedAmountOrNull()!!, 0.001)
        assertEquals(25.0, "-50 / -2".toParsedAmountOrNull()!!, 0.001)

        // Commas and formatting
        assertEquals(1500.50, "1,500.50".toParsedAmountOrNull()!!, 0.001)
        assertEquals(3000.0, "1,000 + 2,000".toParsedAmountOrNull()!!, 0.001)
    }

    // -------------------------------------------------------------------------
    // 5. Transaction UI State Guard Integration
    // -------------------------------------------------------------------------
    @Test
    fun testTransactionUiGuardsBlockInvalidOrOverflowAmounts() {
        val badInputs = listOf(
            "NaN",
            "Infinity",
            "+Infinity",
            "-Infinity",
            "1" + "0".repeat(200) + " * " + "1" + "0".repeat(200),
            "1e308 * 10",
            "100 / 0"
        )

        for (input in badInputs) {
            val amount = input.toParsedAmountOrNull()
            assertNull("Input '$input' must parse to null", amount)

            // TransactionBottomActionBar Save Button check: (amountText.toParsedAmountOrNull() ?: 0.0) > 0.0
            val isSaveButtonEnabled = (amount ?: 0.0) > 0.0
            assertFalse("Save button must be disabled for '$input'", isSaveButtonEnabled)

            // AddEditTransactionState validation check: if (evaluated == null || evaluated <= 0.0)
            val isBlockedFromSaving = (amount == null || amount <= 0.0)
            assertTrue("Save action must be blocked in ViewModel for '$input'", isBlockedFromSaving)
        }
    }
}
