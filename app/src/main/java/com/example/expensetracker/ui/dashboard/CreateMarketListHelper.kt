package com.example.expensetracker.ui.dashboard

import com.example.expensetracker.common.MarketListParser
import java.util.Calendar

object CreateMarketListHelper {
    fun resolveDefaultTitle(isBangla: Boolean): String {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val isWeekend = cal.get(Calendar.DAY_OF_WEEK) in listOf(Calendar.FRIDAY, Calendar.SATURDAY)
        return when {
            isWeekend -> if (isBangla) "সাপ্তাহিক বাজার" else "Weekly Market"
            hour < 12 -> if (isBangla) "সকালের বাজার" else "Morning Market"
            else -> if (isBangla) "বিকেলের বাজার" else "Evening Market"
        }
    }

    fun parseNameAndQty(trimmed: String, qtyStr: String): Triple<String, Double, String> {
        val parsed = MarketListParser.parseSingleLine(trimmed)
        val name = parsed?.name ?: trimmed
        val q = qtyStr.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 1.0
        val u = qtyStr.filter { !it.isDigit() && it != '.' }.trim().ifEmpty { "কেজি" }
        val qty = if (parsed != null && parsed.quantity > 0 && parsed.name != trimmed) parsed.quantity else q
        val unit = if (parsed != null && parsed.unit.isNotBlank() && parsed.name != trimmed) parsed.unit else u
        return Triple(name, qty, unit)
    }
}
