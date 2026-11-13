package com.example.expensetracker.common

/**
 * High-performance, centralized converter between Latin (0-9) and Bengali (০-৯) digits.
 * Serves as the single source of truth for numeric numeral conversion across the application.
 */
object DigitConverter {

    private val BENGALI_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    /**
     * Converts any ASCII/English digits (0-9) in the input string to Bengali numerals (০-৯).
     * Non-digit characters (including punctuation, currency symbols, and letters) are preserved intact.
     */
    fun toBengaliDigits(input: String): String {
        val sb = java.lang.StringBuilder(input.length)
        for (i in 0 until input.length) {
            val ch = input[i]
            if (ch in '0'..'9') {
                sb.append(BENGALI_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Converts any Bengali digits (০-৯) in the input string to ASCII/English numerals (0-9).
     * Non-Bengali-digit characters are preserved intact.
     */
    fun toEnglishDigits(input: String): String {
        val sb = java.lang.StringBuilder(input.length)
        for (i in 0 until input.length) {
            val ch = input[i]
            when (ch) {
                '০' -> sb.append('0')
                '১' -> sb.append('1')
                '২' -> sb.append('2')
                '৩' -> sb.append('3')
                '৪' -> sb.append('4')
                '৫' -> sb.append('5')
                '৬' -> sb.append('6')
                '৭' -> sb.append('7')
                '৮' -> sb.append('8')
                '৯' -> sb.append('9')
                '।', '‚' -> sb.append('.')
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }
}
