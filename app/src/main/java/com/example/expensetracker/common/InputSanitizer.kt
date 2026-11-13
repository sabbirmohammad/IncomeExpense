package com.example.expensetracker.common

/**
 * Centralized sanitization utility for financial and mathematical input fields.
 * Ensures consistent character whitelist across all numerical, amount, and calculation text fields.
 */
object InputSanitizer {

    private val MATH_OPERATOR_CHARS = charArrayOf('+', '-', '×', '÷', '/', '*', '−', '—', ' ')

    /**
     * Sanitizes input for fields supporting numerical values and arithmetic calculations
     * (e.g. In-Place Calculator fields, amount keypads, transfers, bill splits).
     */
    fun sanitizeMathInput(input: String): String {
        val sb = java.lang.StringBuilder(input.length)
        for (i in 0 until input.length) {
            val c = input[i]
            if (c.isDigit() || c == '.' || c in MATH_OPERATOR_CHARS) {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    /**
     * Sanitizes input strictly for clean decimal numbers without operators.
     */
    fun sanitizeDecimalInput(input: String): String {
        val sb = java.lang.StringBuilder(input.length)
        for (i in 0 until input.length) {
            val c = input[i]
            if (c.isDigit() || c == '.') {
                sb.append(c)
            }
        }
        return sb.toString()
    }
}
