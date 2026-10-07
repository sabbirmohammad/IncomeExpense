package com.example.expensetracker.ui.components

import java.util.Locale

object CalculatorEvaluator {

    /**
     * Evaluates a math string like "120+85*2" into a Double.
     * Supports +, -, *, /, decimals. Respects operator precedence.
     */
    fun evaluate(expression: String): Double? {
        val converted = com.example.expensetracker.common.MarketListParser.convertBengaliDigitsToEnglish(expression)
        val sanitized = converted
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("—", "-")
            .replace(" ", "")
            .trim()
        if (sanitized.isEmpty()) return null

        return try {
            parseExpression(sanitized)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseExpression(expr: String): Double {
        // Tokenize into numbers and operators (+, -, *, /)
        val tokens = mutableListOf<String>()
        var currentNumber = StringBuilder()

        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            if (c.isDigit() || c == '.') {
                currentNumber.append(c)
            } else if (c in listOf('+', '-', '*', '/')) {
                if (currentNumber.isNotEmpty()) {
                    tokens.add(currentNumber.toString())
                    currentNumber = StringBuilder()
                } else if (c == '-' && (tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/"))) {
                    // Unary minus
                    currentNumber.append('-')
                    i++
                    continue
                }
                tokens.add(c.toString())
            }
            i++
        }
        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber.toString())
        }

        if (tokens.isEmpty()) throw IllegalArgumentException("Empty expression")

        // First pass: resolve * and /
        val pass1 = mutableListOf<String>()
        var idx = 0
        while (idx < tokens.size) {
            val token = tokens[idx]
            if (token == "*" || token == "/") {
                if (pass1.isEmpty() || idx + 1 >= tokens.size) throw IllegalArgumentException("Malformed expression")
                val left = pass1.removeAt(pass1.size - 1).toDouble()
                val right = tokens[idx + 1].toDouble()
                val res = if (token == "*") left * right else {
                    if (right == 0.0) throw ArithmeticException("Division by zero")
                    left / right
                }
                pass1.add(res.toString())
                idx += 2
            } else {
                pass1.add(token)
            }
            idx++
        }

        // Second pass: resolve + and -
        var result = pass1[0].toDouble()
        var j = 1
        while (j < pass1.size) {
            val op = pass1[j]
            val nextVal = pass1[j + 1].toDouble()
            result = if (op == "+") result + nextVal else result - nextVal
            j += 2
        }

        return result
    }

    fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            String.format(Locale.US, "%.2f", value)
        }
    }

    /**
     * Safely parses a number or evaluates a mathematical expression from user input.
     * Automatically strips commas and whitespace.
     */
    fun parseOrEvaluate(input: String): Double? {
        val converted = com.example.expensetracker.common.MarketListParser.convertBengaliDigitsToEnglish(input)
        val clean = converted.trim().replace(",", "")
        if (clean.isEmpty()) return null
        return clean.toDoubleOrNull() ?: evaluate(clean)
    }
}

/**
 * Extension helper to parse plain numbers or evaluate arithmetic expressions in user input.
 */
fun String.toParsedAmountOrNull(): Double? = CalculatorEvaluator.parseOrEvaluate(this)
