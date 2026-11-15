package com.example.expensetracker.ui.components

object CalculatorExpressionHelper {
    private val OPERATOR_CHARS = listOf('+', '-', '*', '/', '×', '÷', '−', '—')

    fun hasOperators(expression: String): Boolean =
        expression.any { it in OPERATOR_CHARS }

    fun appendOperator(currentExpression: String, op: String): String {
        val trimmed = currentExpression.trimEnd()
        if (trimmed.isEmpty()) {
            return if (op == "−") "- " else ""
        }
        return if (trimmed.last() in OPERATOR_CHARS) {
            val base = trimmed.dropLast(1).trimEnd()
            if (base.isEmpty() && op == "−") "- " else "$base $op "
        } else if (trimmed.last() == '.') {
            val base = trimmed.dropLast(1)
            "$base $op "
        } else {
            "$trimmed $op "
        }
    }
}
