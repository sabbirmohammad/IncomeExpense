package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketListParserTest {

    @Test
    fun testBengaliDigitsConversion() {
        assertEquals("0123456789", MarketListParser.convertBengaliDigitsToEnglish("০১২৩৪৫৬৭৮৯"))
        assertEquals("2.5", MarketListParser.convertBengaliDigitsToEnglish("২.৫"))
    }

    @Test
    fun testParseSingleLineVariousFormats() {
        // Bengali format with quantity & unit
        val item1 = MarketListParser.parseSingleLine("আলু ২ কেজি")
        assertNotNull(item1)
        assertEquals("আলু", item1?.name)
        assertEquals(2.0, item1?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item1?.unit)

        // Oil with liters
        val item2 = MarketListParser.parseSingleLine("সয়াবিন তেল - ৫ লিটার")
        assertNotNull(item2)
        assertEquals("সয়াবিন তেল", item2?.name)
        assertEquals(5.0, item2?.quantity ?: 0.0, 0.01)
        assertEquals("লিটার", item2?.unit)

        // Eggs with dozen
        val item3 = MarketListParser.parseSingleLine("ডিম ১ ডজন")
        assertNotNull(item3)
        assertEquals("ডিম", item3?.name)
        assertEquals(1.0, item3?.quantity ?: 0.0, 0.01)
        assertEquals("ডজন", item3?.unit)

        // Soap with piece
        val item4 = MarketListParser.parseSingleLine("সাবান ২ পিস")
        assertNotNull(item4)
        assertEquals("সাবান", item4?.name)
        assertEquals(2.0, item4?.quantity ?: 0.0, 0.01)
        assertEquals("পিস", item4?.unit)

        // Bengali fractions: দেড় কেজি
        val item5 = MarketListParser.parseSingleLine("মুরগি দেড় কেজি")
        assertNotNull(item5)
        assertEquals("মুরগি", item5?.name)
        assertEquals(1.5, item5?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item5?.unit)

        // Bengali fractions: হাফ কেজি
        val item6 = MarketListParser.parseSingleLine("পেঁয়াজ হাফ কেজি")
        assertNotNull(item6)
        assertEquals("পেঁয়াজ", item6?.name)
        assertEquals(0.5, item6?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item6?.unit)

        // English format
        val item7 = MarketListParser.parseSingleLine("Potato 2kg")
        assertNotNull(item7)
        assertEquals("Potato", item7?.name)
        assertEquals(2.0, item7?.quantity ?: 0.0, 0.01)
        assertEquals("কেজি", item7?.unit)

        // Grams
        val item8 = MarketListParser.parseSingleLine("রসুন ২৫০ গ্রাম")
        assertNotNull(item8)
        assertEquals("রসুন", item8?.name)
        assertEquals(250.0, item8?.quantity ?: 0.0, 0.01)
        assertEquals("গ্রাম", item8?.unit)
    }

    @Test
    fun testParseBulkWhatsAppList() {
        val sampleWhatsAppText = """
            1. আলু ২ কেজি
            2. পেঁয়াজ ১ কেজি
            3. সয়াবিন তেল ২ লিটার
            4. ডিম ১ ডজন
            5. সাবান ২ পিস
            6. কাঁচামরিচ ২৫০ গ্রাম
        """.trimIndent()

        val parsed = MarketListParser.parseBulkText(sampleWhatsAppText)
        assertEquals(6, parsed.size)
        assertEquals("আলু", parsed[0].name)
        assertEquals(2.0, parsed[0].quantity, 0.01)
        assertEquals("কেজি", parsed[0].unit)

        assertEquals("সয়াবিন তেল", parsed[2].name)
        assertEquals(2.0, parsed[2].quantity, 0.01)
        assertEquals("লিটার", parsed[2].unit)

        assertEquals("সাবান", parsed[4].name)
        assertEquals(2.0, parsed[4].quantity, 0.01)
        assertEquals("পিস", parsed[4].unit)
    }

    @Test
    fun testParseCommaSeparatedBulkText() {
        val commaText = "আলু ২ কেজি, ডিম ১ ডজন, সয়াবিন তেল ১ লিটার, কাঁচামরিচ ২৫০ গ্রাম"
        val parsed = MarketListParser.parseBulkText(commaText)
        assertEquals(4, parsed.size)
        assertEquals("আলু", parsed[0].name)
        assertEquals(2.0, parsed[0].quantity, 0.01)
        assertEquals("কেজি", parsed[0].unit)

        assertEquals("ডিম", parsed[1].name)
        assertEquals(1.0, parsed[1].quantity, 0.01)
        assertEquals("ডজন", parsed[1].unit)

        assertEquals("সয়াবিন তেল", parsed[2].name)
        assertEquals(1.0, parsed[2].quantity, 0.01)
        assertEquals("লিটার", parsed[2].unit)

        assertEquals("কাঁচামরিচ", parsed[3].name)
        assertEquals(250.0, parsed[3].quantity, 0.01)
        assertEquals("গ্রাম", parsed[3].unit)
    }
}
