package com.example.expensetracker.ui.components

/**
 * Pure evaluation and input transformation engine for AmountKeypad.
 * 100% testable without Compose runtime.
 */
object AmountKeypadLogic {

    private val OPERATORS = listOf('+', '−', '-', '×', '*', '÷', '/')

    fun isOperator(c: Char): Boolean = c in OPERATORS

    /**
     * Appends a digit ('0'..'9') to the expression.
     * Replaces lone "0" with the digit (unless digit is '0').
     */
    fun appendDigit(current: String, digit: Char): String {
        if (!digit.isDigit()) return current
        if (current == "0") return digit.toString()
        // If current ends with an operator followed by "0", replace that "0"
        val lastOpIndex = current.indexOfLast { isOperator(it) }
        if (lastOpIndex >= 0 && lastOpIndex == current.length - 2 && current.endsWith("0")) {
            return current.dropLast(1) + digit
        }
        return current + digit
    }

    /**
     * Appends a decimal point ('.') safely.
     * Prevents multiple decimals in the current number segment.
     */
    fun appendDecimal(current: String): String {
        if (current.isEmpty()) return "0."
        val lastOpIndex = current.indexOfLast { isOperator(it) }
        val currentSegment = if (lastOpIndex >= 0) current.substring(lastOpIndex + 1) else current
        return when {
            currentSegment.contains('.') -> current
            currentSegment.isEmpty() -> current + "0."
            else -> current + "."
        }
    }

    /**
     * Appends an operator ('+', '−', '×', '÷').
     * If the expression is empty, only '−' is allowed (for negative numbers).
     * If the expression already ends with an operator, it replaces it.
     */
    fun appendOperator(current: String, op: Char): String {
        if (!isOperator(op)) return current
        val normalizedOp = when (op) {
            '-' -> '−'
            '*' -> '×'
            '/' -> '÷'
            else -> op
        }
        if (current.isEmpty()) {
            return if (normalizedOp == '−') "−" else ""
        }
        // If it ends with decimal, drop it before appending operator
        val trimmed = if (current.endsWith('.')) current.dropLast(1) else current
        if (trimmed.isEmpty()) return ""

        return if (isOperator(trimmed.last())) {
            // Replace previous operator
            trimmed.dropLast(1) + normalizedOp
        } else {
            trimmed + normalizedOp
        }
    }

    /**
     * Deletes the last character. If length is 1 or empty, returns "".
     */
    fun backspace(current: String): String {
        return if (current.isNotEmpty()) current.dropLast(1) else ""
    }

    /**
     * Clears the entire expression.
     */
    fun clear(): String = ""

    /**
     * Evaluates the expression using CalculatorEvaluator.
     */
    fun evaluate(expression: String): Double? {
        if (expression.isBlank()) return null
        return CalculatorEvaluator.evaluate(expression)
    }

    /**
     * Formats evaluated double value cleanly.
     */
    fun formatEvaluated(value: Double, isBangla: Boolean = false): String {
        val formatted = CalculatorEvaluator.formatResult(value)
        return if (isBangla) convertToBengaliDigits(formatted) else formatted
    }

    /**
     * Checks if the expression contains any math operators.
     */
    fun hasOperators(expression: String): Boolean {
        return expression.any { isOperator(it) }
    }
}
