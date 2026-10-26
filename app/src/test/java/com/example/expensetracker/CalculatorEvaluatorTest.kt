package com.example.expensetracker

import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CalculatorEvaluatorTest {

    @Test
    fun testSimpleAddition() {
        val result = CalculatorEvaluator.evaluate("120 + 85")
        assertNotNull(result)
        assertEquals(205.0, result!!, 0.001)
    }

    @Test
    fun testSimpleSubtraction() {
        val result = CalculatorEvaluator.evaluate("200 - 45")
        assertNotNull(result)
        assertEquals(155.0, result!!, 0.001)
    }

    @Test
    fun testUnicodeMinusAndEmDash() {
        val result1 = CalculatorEvaluator.evaluate("200 − 50")
        assertNotNull(result1)
        assertEquals(150.0, result1!!, 0.001)

        val result2 = CalculatorEvaluator.evaluate("200 — 30")
        assertNotNull(result2)
        assertEquals(170.0, result2!!, 0.001)

        val compound = CalculatorEvaluator.evaluate("200 − 50 — 20")
        assertNotNull(compound)
        assertEquals(130.0, compound!!, 0.001)
    }

    @Test
    fun testMultiplicationAndPrecedence() {
        // Standard precedence: 85 * 2 = 170; 120 + 170 = 290
        val result = CalculatorEvaluator.evaluate("120 + 85 * 2")
        assertNotNull(result)
        assertEquals(290.0, result!!, 0.001)
    }

    @Test
    fun testMultiplicationSymbol() {
        val result = CalculatorEvaluator.evaluate("50 × 4")
        assertNotNull(result)
        assertEquals(200.0, result!!, 0.001)
    }

    @Test
    fun testDivisionSymbol() {
        val result = CalculatorEvaluator.evaluate("100 ÷ 4")
        assertNotNull(result)
        assertEquals(25.0, result!!, 0.001)
    }

    @Test
    fun testDivisionByZero() {
        val result = CalculatorEvaluator.evaluate("100 ÷ 0")
        assertNull("Division by zero should safely evaluate to null", result)
    }

    @Test
    fun testUnaryMinusLeading() {
        val result = CalculatorEvaluator.evaluate("-50 + 150")
        assertNotNull(result)
        assertEquals(100.0, result!!, 0.001)
    }

    @Test
    fun testUnaryMinusIntermediate() {
        val result = CalculatorEvaluator.evaluate("100 + -20")
        assertNotNull(result)
        assertEquals(80.0, result!!, 0.001)
    }

    @Test
    fun testDecimalArithmetic() {
        val result = CalculatorEvaluator.evaluate("12.50 + 7.25")
        assertNotNull(result)
        assertEquals(19.75, result!!, 0.001)
    }

    @Test
    fun testBengaliDigits() {
        // "১২০ + ৮৫ * ২" should translate to "120 + 85 * 2" = 290.0
        val result = CalculatorEvaluator.evaluate("১২০ + ৮৫ * ২")
        assertNotNull(result)
        assertEquals(290.0, result!!, 0.001)
    }

    @Test
    fun testBengaliDecimals() {
        // "৫০.৫ + ২৫" should evaluate to 75.5
        val result = CalculatorEvaluator.evaluate("৫০.৫ + ২৫")
        assertNotNull(result)
        assertEquals(75.5, result!!, 0.001)
    }

    @Test
    fun testIncompleteTrailingOperator() {
        val result = CalculatorEvaluator.evaluate("120 + ")
        assertNull("Trailing operator without operand should return null", result)
    }

    @Test
    fun testConsecutiveOperators() {
        val result = CalculatorEvaluator.evaluate("120 ++ 50")
        assertNull("Malformed consecutive operators should return null", result)
    }

    @Test
    fun testFormatResult() {
        val whole = CalculatorEvaluator.formatResult(290.0)
        assertEquals("290", whole)

        val decimal = CalculatorEvaluator.formatResult(75.5)
        assertEquals("75.50", decimal)

        val precise = CalculatorEvaluator.formatResult(19.75)
        assertEquals("19.75", precise)
    }

    @Test
    fun testToParsedAmountOrNull() {
        val plain = "250.75".toParsedAmountOrNull()
        assertNotNull(plain)
        assertEquals(250.75, plain!!, 0.001)

        val expression = "100 + 50".toParsedAmountOrNull()
        assertNotNull(expression)
        assertEquals(150.0, expression!!, 0.001)

        val bengali = "৫০০".toParsedAmountOrNull()
        assertNotNull(bengali)
        assertEquals(500.0, bengali!!, 0.001)

        val invalid = "abc".toParsedAmountOrNull()
        assertNull(invalid)
    }

    @Test
    fun testComplexExpression() {
        // 100 - (20 * 3) + (40 / 8) = 100 - 60 + 5 = 45.0
        val result = CalculatorEvaluator.evaluate("100 - 20 * 3 + 40 / 8")
        assertNotNull(result)
        assertEquals(45.0, result!!, 0.001)
    }

    @Test
    fun testParseOrEvaluateWithCommas() {
        val formattedNumber = CalculatorEvaluator.parseOrEvaluate("1,500.50")
        assertNotNull(formattedNumber)
        assertEquals(1500.50, formattedNumber!!, 0.001)
    }

    @Test
    fun testNaNAndInfinityRejection() {
        assertNull("'NaN' should return null", "NaN".toParsedAmountOrNull())
        assertNull("'Infinity' should return null", "Infinity".toParsedAmountOrNull())
        assertNull("'+Infinity' should return null", "+Infinity".toParsedAmountOrNull())
        assertNull("'-Infinity' should return null", "-Infinity".toParsedAmountOrNull())
        assertNull("evaluate('NaN') should return null", CalculatorEvaluator.evaluate("NaN"))
        assertNull("evaluate('Infinity') should return null", CalculatorEvaluator.evaluate("Infinity"))

        // Overflow arithmetic expressions (> 1.79e308)
        val bigNum = "1" + "0".repeat(200)
        val overflowExpr = "$bigNum * $bigNum"
        assertNull("Overflow expression should evaluate to null", CalculatorEvaluator.evaluate(overflowExpr))
        assertNull("Overflow expression toParsedAmountOrNull should return null", overflowExpr.toParsedAmountOrNull())

        val bigPlainNum = "1" + "0".repeat(400)
        assertNull("Overflow plain number should return null", bigPlainNum.toParsedAmountOrNull())
    }

    @Test
    fun testUnrecognizedCharactersRejection() {
        assertNull("Scientific notation 'e' in expression should return null", CalculatorEvaluator.evaluate("1e10 + 5"))
        assertNull("Arbitrary letters in expression should return null", CalculatorEvaluator.evaluate("100a + 50"))
    }
}
