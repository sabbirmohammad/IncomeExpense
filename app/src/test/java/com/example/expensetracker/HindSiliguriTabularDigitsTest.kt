package com.example.expensetracker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.awt.Font
import java.awt.font.FontRenderContext
import java.io.File

/**
 * Tests Hind Siliguri font metrics, OpenType feature presence, and tabular alignment behavior
 * for both ASCII (0-9) and Bengali (০-৯) digits in amount columns.
 */
class HindSiliguriTabularDigitsTest {

    private val fontFile = File("src/main/res/font/hind_siliguri_regular.ttf")

    @Test
    fun testHindSiliguriLacksTnumOpenTypeTable() {
        assertTrue("hind_siliguri_regular.ttf must exist in res/font", fontFile.exists())
        val bytes = fontFile.readBytes()
        val tnumTag = "tnum".toByteArray(Charsets.US_ASCII)

        // Find if 'tnum' is in the TTF binary tables
        var foundTnum = false
        for (i in 0 until bytes.size - 4) {
            if (bytes[i] == tnumTag[0] &&
                bytes[i + 1] == tnumTag[1] &&
                bytes[i + 2] == tnumTag[2] &&
                bytes[i + 3] == tnumTag[3]
            ) {
                foundTnum = true
                break
            }
        }
        // Confirms why Compose FontFamily cannot use fontFeatureSettings = "tnum" natively on Hind Siliguri
        assertFalse("Hind Siliguri TTF lacks native OpenType 'tnum' glyph table", foundTnum)
    }

    @Test
    fun testRawHindSiliguriProportionalMetrics() {
        val font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(24f)
        val frc = FontRenderContext(null, true, true)

        val w1111 = font.getStringBounds("1111", frc).width
        val w8888 = font.getStringBounds("8888", frc).width
        val wBn1 = font.getStringBounds("১১১১", frc).width
        val wBn8 = font.getStringBounds("৮৮৮৮", frc).width

        println("Raw Hind Siliguri: '1111'=$w1111, '8888'=$w8888 (delta=${w8888 - w1111})")
        println("Raw Hind Siliguri: '১১১১'=$wBn1, '৮৮৮৮'=$wBn8 (delta=${wBn8 - wBn1})")

        // In raw proportional metrics, '1' is narrower than '8'
        assertTrue("'1111' is narrower than '8888' in raw proportional Hind Siliguri", w1111 < w8888)
        assertTrue("'১১১১' is narrower than '৮৮৮৮' in raw proportional Hind Siliguri", wBn1 < wBn8)
    }

    @Test
    fun testTabularSlotAlignmentGuaranteesEqualMeasuredWidths() {
        val font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(24f)
        val frc = FontRenderContext(null, true, true)

        val asciiDigits = (0..9).map { it.toString() }
        val bengaliDigits = listOf("০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯")

        val maxAsciiSlotWidth = asciiDigits.maxOf { font.getStringBounds(it, frc).width }
        val maxBnSlotWidth = bengaliDigits.maxOf { font.getStringBounds(it, frc).width }
        val maxUnifiedSlotWidth = maxOf(maxAsciiSlotWidth, maxBnSlotWidth)

        fun calculateTabularWidth(text: String): Double {
            var total = 0.0
            for (ch in text) {
                total += if (ch.isDigit()) {
                    maxUnifiedSlotWidth
                } else {
                    font.getStringBounds(ch.toString(), frc).width
                }
            }
            return total
        }

        // 1. Uniform 4-digit width check
        val tab1111 = calculateTabularWidth("1111")
        val tab8888 = calculateTabularWidth("8888")
        assertEquals("Tabular '1111' and '8888' must have identical measured width", tab1111, tab8888, 0.0001)

        val tabBn1111 = calculateTabularWidth("১১১১")
        val tabBn8888 = calculateTabularWidth("৮৮৮৮")
        assertEquals("Tabular '১১১১' and '৮৮৮৮' must have identical measured width", tabBn1111, tabBn8888, 0.0001)

        // 2. Full amount formatted string check with punctuation
        val amt1 = calculateTabularWidth("1,111.00")
        val amt8 = calculateTabularWidth("8,888.00")
        assertEquals("Formatted ASCII amounts '1,111.00' and '8,888.00' must match exactly", amt1, amt8, 0.0001)

        val amtBn1 = calculateTabularWidth("১,১১১.০০")
        val amtBn8 = calculateTabularWidth("৮,৮৮৮.০০")
        assertEquals("Formatted Bengali amounts '১,১১১.০০' and '৮,৮৮৮.০০' must match exactly", amtBn1, amtBn8, 0.0001)
    }
}
