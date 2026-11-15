package com.example.expensetracker

import com.example.expensetracker.ui.components.TabularRun
import com.example.expensetracker.ui.components.segmentIntoTabularRuns
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PulseTabularTextRunTest {

    @Test
    fun testSegmentEmptyString() {
        val runs = segmentIntoTabularRuns("")
        assertTrue("Empty string produces empty runs", runs.isEmpty())
    }

    @Test
    fun testTwoKgBengaliUnitNeverSplitsBaseConsonantFromVowelSigns() {
        val runs = segmentIntoTabularRuns("২ কেজি")
        // "২" is digit, " কেজি" is non-digit containing ক + ে + জ + ি
        assertEquals(2, runs.size)
        assertEquals(TabularRun.Digits("২"), runs[0])
        assertEquals(TabularRun.NonDigits(" কেজি"), runs[1])

        // Verify that the non-digit run retains unbroken " কেজি" with kar vowel signs intact
        val textContent = (runs[1] as TabularRun.NonDigits).content
        assertTrue("Contains space and base consonants", textContent.contains("ক") && textContent.contains("জ"))
        assertTrue("Contains dependent vowel signs (e-kar and i-kar)", textContent.contains("ে") && textContent.contains("ি"))
    }

    @Test
    fun testTotalSpentBengaliLabelNeverSplitsBaseConsonantFromVowelSigns() {
        val runs = segmentIntoTabularRuns("মোট খরচ: ৳১৪৫")
        // "মোট খরচ: ৳" is non-digit, "১৪৫" is digits
        assertEquals(2, runs.size)
        assertEquals(TabularRun.NonDigits("মোট খরচ: ৳"), runs[0])
        assertEquals(TabularRun.Digits("১৪৫"), runs[1])

        val label = (runs[0] as TabularRun.NonDigits).content
        assertTrue("Contains o-kar in মোট", label.contains("ম") && label.contains("ো") && label.contains("ট"))
        assertTrue("Contains currency symbol ৳", label.contains("৳"))
    }

    @Test
    fun testOneSlashOneKeepsDigitsAndSeparator() {
        val runs = segmentIntoTabularRuns("১/১")
        assertEquals(3, runs.size)
        assertEquals(TabularRun.Digits("১"), runs[0])
        assertEquals(TabularRun.NonDigits("/"), runs[1])
        assertEquals(TabularRun.Digits("১"), runs[2])
    }

    @Test
    fun testZeroSlashZeroWithParensKeepsDigitsAndDelimiters() {
        val runs = segmentIntoTabularRuns("(০/০)")
        assertEquals(5, runs.size)
        assertEquals(TabularRun.NonDigits("("), runs[0])
        assertEquals(TabularRun.Digits("০"), runs[1])
        assertEquals(TabularRun.NonDigits("/"), runs[2])
        assertEquals(TabularRun.Digits("০"), runs[3])
        assertEquals(TabularRun.NonDigits(")"), runs[4])
    }

    @Test
    fun testFormattedCurrencyKeepsDigitRunsTogether() {
        val runs = segmentIntoTabularRuns("৳১,২৩৪.৫০")
        assertEquals(6, runs.size)
        assertEquals(TabularRun.NonDigits("৳"), runs[0])
        assertEquals(TabularRun.Digits("১"), runs[1])
        assertEquals(TabularRun.NonDigits(","), runs[2])
        assertEquals(TabularRun.Digits("২৩৪"), runs[3])
        assertEquals(TabularRun.NonDigits("."), runs[4])
        assertEquals(TabularRun.Digits("৫০"), runs[5])
    }

    @Test
    fun testHasantaAndConjunctsRemainUnbrokenInNonDigitRun() {
        // Bengali conjunct: রক্ত (র + ক + ্ত), মিষ্টি (ম + ি + ষ + ্ট + ি)
        val runs = segmentIntoTabularRuns("রক্ত ২৫ মিষ্টি")
        assertEquals(3, runs.size)
        assertEquals(TabularRun.NonDigits("রক্ত "), runs[0])
        assertEquals(TabularRun.Digits("২৫"), runs[1])
        assertEquals(TabularRun.NonDigits(" মিষ্টি"), runs[2])
    }
}
