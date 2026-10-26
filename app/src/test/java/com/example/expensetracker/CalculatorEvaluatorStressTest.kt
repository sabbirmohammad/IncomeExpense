package com.example.expensetracker

import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorEvaluatorStressTest {

    // ---------------------------------------------------------
    // 1. Division by Zero Edge Cases
    // ---------------------------------------------------------
    @Test
    fun testDivisionByZeroVariants() {
        assertNull("Standard slash division by zero", CalculatorEvaluator.evaluate("100 / 0"))
        assertNull("Unicode division by zero", CalculatorEvaluator.evaluate("100 ÷ 0"))
        assertNull("Division by decimal zero 0.0", CalculatorEvaluator.evaluate("100 / 0.0"))
        assertNull("Division by negative zero -0", CalculatorEvaluator.evaluate("100 / -0"))
        assertNull("Zero divided by zero", CalculatorEvaluator.evaluate("0 / 0"))
        assertNull("Zero divided by zero unicode", CalculatorEvaluator.evaluate("0 ÷ 0"))
        assertNull("Bengali zero divided by Bengali zero", CalculatorEvaluator.evaluate("০ / ০"))
        assertNull("Chained division with trailing zero divisor", CalculatorEvaluator.evaluate("10 / 2 / 0"))
        assertNull("Chained division with intermediate zero divisor", CalculatorEvaluator.evaluate("10 / 0 / 2"))
    }

    // ---------------------------------------------------------
    // 2. Malformed Expressions & Stray Characters
    // ---------------------------------------------------------
    @Test
    fun testMalformedExpressionsReturnNull() {
        assertNull("Empty string", CalculatorEvaluator.evaluate(""))
        assertNull("Only spaces", CalculatorEvaluator.evaluate("    "))
        assertNull("Lone plus", CalculatorEvaluator.evaluate("+"))
        assertNull("Lone minus", CalculatorEvaluator.evaluate("-"))
        assertNull("Lone multiply", CalculatorEvaluator.evaluate("*"))
        assertNull("Lone divide", CalculatorEvaluator.evaluate("/"))
        assertNull("Lone unicode multiply", CalculatorEvaluator.evaluate("×"))
        assertNull("Lone unicode divide", CalculatorEvaluator.evaluate("÷"))
        assertNull("Lone dot", CalculatorEvaluator.evaluate("."))
        assertNull("Double dot", CalculatorEvaluator.evaluate(".."))
        assertNull("Multiple dots in number", CalculatorEvaluator.evaluate("1..2"))
        assertNull("Three dots in number", CalculatorEvaluator.evaluate("1.2.3"))
        assertNull("Trailing plus", CalculatorEvaluator.evaluate("100 +"))
        assertNull("Trailing minus", CalculatorEvaluator.evaluate("100 -"))
        assertNull("Trailing multiply", CalculatorEvaluator.evaluate("100 *"))
        assertNull("Trailing divide", CalculatorEvaluator.evaluate("100 /"))
        assertNull("Leading multiply", CalculatorEvaluator.evaluate("* 100"))
        assertNull("Leading divide", CalculatorEvaluator.evaluate("/ 100"))
        assertNull("Double plus", CalculatorEvaluator.evaluate("100 ++ 50"))
        assertNull("Plus multiply", CalculatorEvaluator.evaluate("100 +* 50"))
        assertNull("Plus divide", CalculatorEvaluator.evaluate("100 +/ 50"))
        assertNull("Minus plus", CalculatorEvaluator.evaluate("100 -+ 50"))
        assertNull("Minus multiply", CalculatorEvaluator.evaluate("100 -* 50"))
        assertNull("Minus divide", CalculatorEvaluator.evaluate("100 -/ 50"))
        assertNull("Multiply plus", CalculatorEvaluator.evaluate("100 *+ 50"))
        assertNull("Multiply multiply", CalculatorEvaluator.evaluate("100 ** 50"))
        assertNull("Multiply divide", CalculatorEvaluator.evaluate("100 */ 50"))
        assertNull("Divide plus", CalculatorEvaluator.evaluate("100 /+ 50"))
        assertNull("Divide multiply", CalculatorEvaluator.evaluate("100 /* 50"))
        assertNull("Divide divide", CalculatorEvaluator.evaluate("100 // 50"))
        assertNull("Triple minus", CalculatorEvaluator.evaluate("---50"))
        assertNull("Double minus standalone", CalculatorEvaluator.evaluate("--50"))
        assertNull("Triple minus intermediate", CalculatorEvaluator.evaluate("100 - - - 50"))
    }

    // ---------------------------------------------------------
    // 3. Bengali Numerals Conversion (০-৯)
    // ---------------------------------------------------------
    @Test
    fun testBengaliNumeralsFullDigitRange() {
        // Individual digits 0 to 9
        val bengaliDigits = listOf("০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯")
        bengaliDigits.forEachIndexed { index, bnDigit ->
            val result = CalculatorEvaluator.evaluate(bnDigit)
            assertNotNull("Bengali digit $bnDigit must evaluate", result)
            assertEquals(index.toDouble(), result!!, 0.001)
        }

        // Full sequence: 0123456789
        val seqResult = CalculatorEvaluator.evaluate("০১২৩৪৫৬৭৮৯")
        assertNotNull(seqResult)
        assertEquals(123456789.0, seqResult!!, 0.001)
    }

    @Test
    fun testBengaliArithmeticOperations() {
        // Addition
        val add = CalculatorEvaluator.evaluate("১২০ + ৮৫")
        assertNotNull(add)
        assertEquals(205.0, add!!, 0.001)

        // Subtraction
        val sub = CalculatorEvaluator.evaluate("১২০ - ৪৫")
        assertNotNull(sub)
        assertEquals(75.0, sub!!, 0.001)

        // Multiplication with Unicode symbol
        val mul = CalculatorEvaluator.evaluate("১২০ × ২")
        assertNotNull(mul)
        assertEquals(240.0, mul!!, 0.001)

        // Division with Unicode symbol
        val div = CalculatorEvaluator.evaluate("১২০ ÷ ৪")
        assertNotNull(div)
        assertEquals(30.0, div!!, 0.001)

        // Bengali decimals
        val dec = CalculatorEvaluator.evaluate("৫০.৫ + ২৫.২৫")
        assertNotNull(dec)
        assertEquals(75.75, dec!!, 0.001)

        // Mixed Bengali and English digits
        val mixed = CalculatorEvaluator.evaluate("৫০ + 50")
        assertNotNull(mixed)
        assertEquals(100.0, mixed!!, 0.001)
    }

    // ---------------------------------------------------------
    // 4. Unary Minus Combinations
    // ---------------------------------------------------------
    @Test
    fun testUnaryMinusCombinations() {
        val leading = CalculatorEvaluator.evaluate("-50")
        assertNotNull(leading)
        assertEquals(-50.0, leading!!, 0.001)

        val leadingWithOp = CalculatorEvaluator.evaluate("-50 + 100")
        assertNotNull(leadingWithOp)
        assertEquals(50.0, leadingWithOp!!, 0.001)

        val intermediateAddMinus = CalculatorEvaluator.evaluate("100 + -50")
        assertNotNull(intermediateAddMinus)
        assertEquals(50.0, intermediateAddMinus!!, 0.001)

        val intermediateSubMinus = CalculatorEvaluator.evaluate("100 - -50")
        assertNotNull(intermediateSubMinus)
        assertEquals(150.0, intermediateSubMinus!!, 0.001)

        val intermediateMulMinus = CalculatorEvaluator.evaluate("100 * -2")
        assertNotNull(intermediateMulMinus)
        assertEquals(-200.0, intermediateMulMinus!!, 0.001)

        val intermediateDivMinus = CalculatorEvaluator.evaluate("100 / -2")
        assertNotNull(intermediateDivMinus)
        assertEquals(-50.0, intermediateDivMinus!!, 0.001)

        val mulBothNegative = CalculatorEvaluator.evaluate("-50 * -2")
        assertNotNull(mulBothNegative)
        assertEquals(100.0, mulBothNegative!!, 0.001)

        val divBothNegative = CalculatorEvaluator.evaluate("-50 / -2")
        assertNotNull(divBothNegative)
        assertEquals(25.0, divBothNegative!!, 0.001)
    }

    // ---------------------------------------------------------
    // 5. Decimal Arithmetic & Precision
    // ---------------------------------------------------------
    @Test
    fun testDecimalNumbers() {
        val simple = CalculatorEvaluator.evaluate("0.1 + 0.2")
        assertNotNull(simple)
        assertEquals(0.3, simple!!, 0.001)

        val multiDec = CalculatorEvaluator.evaluate("100.50 + 200.25 - 50.125")
        assertNotNull(multiDec)
        assertEquals(250.625, multiDec!!, 0.001)

        val leadingDot = CalculatorEvaluator.evaluate(".5 + .5")
        assertNotNull(".5 should parse as 0.5", leadingDot)
        assertEquals(1.0, leadingDot!!, 0.001)

        val trailingDot = CalculatorEvaluator.evaluate("5. + 5.")
        assertNotNull("5. should parse as 5.0", trailingDot)
        assertEquals(10.0, trailingDot!!, 0.001)
    }

    // ---------------------------------------------------------
    // 6. Large Numbers & Chained Operations
    // ---------------------------------------------------------
    @Test
    fun testLargeNumbers() {
        val largeSum = CalculatorEvaluator.evaluate("999999999 + 1")
        assertNotNull(largeSum)
        assertEquals(1000000000.0, largeSum!!, 0.001)

        val largeMul = CalculatorEvaluator.evaluate("1000000 * 1000000")
        assertNotNull(largeMul)
        assertEquals(1000000000000.0, largeMul!!, 0.001)
    }

    @Test
    fun testLongChainedOperations() {
        // 10 + 20 + 30 + 40 = 100
        val sumChain = CalculatorEvaluator.evaluate("10 + 20 + 30 + 40")
        assertNotNull(sumChain)
        assertEquals(100.0, sumChain!!, 0.001)

        // 100 - 20 - 30 - 10 = 40
        val subChain = CalculatorEvaluator.evaluate("100 - 20 - 30 - 10")
        assertNotNull(subChain)
        assertEquals(40.0, subChain!!, 0.001)

        // 2 * 3 * 4 * 5 = 120
        val mulChain = CalculatorEvaluator.evaluate("2 * 3 * 4 * 5")
        assertNotNull(mulChain)
        assertEquals(120.0, mulChain!!, 0.001)

        // 120 / 2 / 3 / 4 = 5
        val divChain = CalculatorEvaluator.evaluate("120 / 2 / 3 / 4")
        assertNotNull(divChain)
        assertEquals(5.0, divChain!!, 0.001)

        // Mixed precedence: 10 + 20 * 30 - 40 / 2 = 10 + 600 - 20 = 590
        val mixed = CalculatorEvaluator.evaluate("10 + 20 * 30 - 40 / 2")
        assertNotNull(mixed)
        assertEquals(590.0, mixed!!, 0.001)
    }

    // ---------------------------------------------------------
    // 7. toParsedAmountOrNull & NaN / Infinity Integrity
    // ---------------------------------------------------------
    @Test
    fun testToParsedAmountOrNullReliability() {
        // Normal expressions
        assertEquals(205.0, "120 + 85".toParsedAmountOrNull()!!, 0.001)
        assertEquals(500.0, "৫০০".toParsedAmountOrNull()!!, 0.001)
        assertEquals(1500.50, "1,500.50".toParsedAmountOrNull()!!, 0.001)

        // Bengali with commas
        assertEquals(4000.0, "১,৫০০ + ২,৫০০".toParsedAmountOrNull()!!, 0.001)

        // Division by zero via toParsedAmountOrNull
        assertNull("100 / 0 must yield null", "100 / 0".toParsedAmountOrNull())
        assertNull("100 ÷ 0 must yield null", "100 ÷ 0".toParsedAmountOrNull())

        // Malformed inputs
        assertNull("Empty string", "".toParsedAmountOrNull())
        assertNull("Whitespace", "   ".toParsedAmountOrNull())
        assertNull("Alphabetic garbage", "invalid".toParsedAmountOrNull())
        assertNull("Trailing plus", "120 + ".toParsedAmountOrNull())
    }

    @Test
    fun testNaNAndInfinityRejection() {
        // String literal NaN and Infinity must not yield valid amounts
        val nanResult = "NaN".toParsedAmountOrNull()
        val infResult = "Infinity".toParsedAmountOrNull()
        val posInfResult = "+Infinity".toParsedAmountOrNull()
        val negInfResult = "-Infinity".toParsedAmountOrNull()

        assertNull("Literal 'NaN' must yield null", nanResult)
        assertNull("Literal 'Infinity' must yield null", infResult)
        assertNull("Literal '+Infinity' must yield null", posInfResult)
        assertNull("Literal '-Infinity' must yield null", negInfResult)

        // Real arithmetic overflow without scientific notation:
        // 10^200 * 10^200 overflows Double (> 1.79e308) to Double.POSITIVE_INFINITY
        val bigNum = "1" + "0".repeat(200)
        val overflowExpr = "$bigNum * $bigNum"
        val realOverflowResult = CalculatorEvaluator.evaluate(overflowExpr)
        val parsedRealOverflow = overflowExpr.toParsedAmountOrNull()

        assertNull("Overflow evaluate must yield null", realOverflowResult)
        assertNull("Overflow toParsedAmountOrNull must yield null", parsedRealOverflow)

        // TransactionBottomActionBar check:
        val isAmountValid = (parsedRealOverflow ?: 0.0) > 0.0
        assertFalse("TransactionBottomActionBar rejects overflow as invalid", isAmountValid)

        // AddEditTransactionState check:
        val passesSaveValidation = !(parsedRealOverflow == null || parsedRealOverflow <= 0.0)
        assertFalse("AddEditTransactionState blocks overflow from being saved", passesSaveValidation)
    }
}
