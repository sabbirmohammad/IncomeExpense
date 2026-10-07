package com.example.expensetracker.ui.components

import java.text.NumberFormat
import java.util.Locale

@Volatile
var globalBengaliNumeralsEnabled: Boolean = false

private val currencyFormatterThreadLocal = object : ThreadLocal<NumberFormat>() {
    override fun initialValue(): NumberFormat {
        return NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
    }
}

fun convertToBengaliDigits(input: String): String {
    val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val sb = StringBuilder(input.length)
    for (ch in input) {
        if (ch in '0'..'9') {
            sb.append(bengaliDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

fun formatCurrency(
    amount: Double,
    symbol: String,
    useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled
): String {
    val formatter = currencyFormatterThreadLocal.get()!!
    val formatted = formatter.format(amount)
    val displayAmount = if (useBengaliNumerals) convertToBengaliDigits(formatted) else formatted
    return "$symbol $displayAmount"
}
