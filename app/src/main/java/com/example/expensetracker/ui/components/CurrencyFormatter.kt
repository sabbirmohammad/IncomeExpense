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

fun convertToBengaliDigits(input: String): String = com.example.expensetracker.common.DigitConverter.toBengaliDigits(input)

fun convertToEnglishDigits(input: String): String = com.example.expensetracker.common.DigitConverter.toEnglishDigits(input)

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
