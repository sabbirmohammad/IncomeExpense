package com.example.expensetracker.ui.reports

import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object ReportPeriodFormatter {
    fun formatPeriodTitle(
        period: ReportPeriod,
        customStartMillis: Long,
        customEndMillis: Long,
        isBangla: Boolean
    ): String {
        val locale = DateTimeHelper.getLocale(isBangla)
        val df = SimpleDateFormat("dd MMM yyyy", locale)
        val raw = when (period) {
            ReportPeriod.THIS_MONTH -> {
                val cal = Calendar.getInstance()
                SimpleDateFormat("MMMM yyyy", locale).format(cal.time)
            }
            ReportPeriod.LAST_MONTH -> {
                val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
                SimpleDateFormat("MMMM yyyy", locale).format(cal.time)
            }
            ReportPeriod.LAST_3_MONTHS -> if (isBangla) "গত ৩ মাস" else "Last 3 Months"
            ReportPeriod.THIS_YEAR -> {
                val cal = Calendar.getInstance()
                SimpleDateFormat("yyyy", locale).format(cal.time)
            }
            ReportPeriod.ALL_TIME -> if (isBangla) "সর্বমোট হিসাব" else "All Time"
            ReportPeriod.CUSTOM -> "${df.format(Date(customStartMillis))} - ${df.format(Date(customEndMillis))}"
        }
        return if (isBangla) convertToBengaliDigits(raw) else raw
    }
}
