package com.example.expensetracker.ui.components

internal sealed interface KeypadItem {
    data class Digit(val char: Char, val display: String) : KeypadItem
    data class Operator(val char: Char, val display: String) : KeypadItem
    data object Decimal : KeypadItem
    data object Backspace : KeypadItem
}
