package com.example.expensetracker.ui.components

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.model.PaymentMethod

fun getPaymentMethodColor(method: PaymentMethod, isDark: Boolean = true): Color {
    return if (isDark) {
        when (method) {
            PaymentMethod.BKASH -> Color(0xFFE2136E)
            PaymentMethod.NAGAD -> Color(0xFFF7941D)
            PaymentMethod.BANK_TRANSFER -> Color(0xFF3B8FF0)
            PaymentMethod.CASH -> Color(0xFF3DDC97)
            PaymentMethod.CREDIT_CARD -> Color(0xFF9D4EDD)
            PaymentMethod.DEBIT_CARD -> Color(0xFF5C7CFA)
            PaymentMethod.OTHER -> Color(0xFF8B90A0)
        }
    } else {
        when (method) {
            PaymentMethod.BKASH -> Color(0xFFBE123C)
            PaymentMethod.NAGAD -> Color(0xFFD97706)
            PaymentMethod.BANK_TRANSFER -> Color(0xFF1D4ED8)
            PaymentMethod.CASH -> Color(0xFF15803D)
            PaymentMethod.CREDIT_CARD -> Color(0xFF7E22CE)
            PaymentMethod.DEBIT_CARD -> Color(0xFF4338CA)
            PaymentMethod.OTHER -> Color(0xFF64748B)
        }
    }
}
