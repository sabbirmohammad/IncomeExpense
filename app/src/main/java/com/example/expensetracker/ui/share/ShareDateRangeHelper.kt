package com.example.expensetracker.ui.share

import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object ShareDateRangeHelper {

    fun computeActiveDateRange(
        period: SharePeriodType,
        initialTargetDateMillis: Long?,
        customStartDateMillis: Long,
        customEndDateMillis: Long
    ): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        return when (period) {
            SharePeriodType.TODAY -> {
                val target = initialTargetDateMillis ?: System.currentTimeMillis()
                cal.timeInMillis = target
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999)
                Pair(start, cal.timeInMillis)
            }
            SharePeriodType.YESTERDAY -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999)
                Pair(start, cal.timeInMillis)
            }
            SharePeriodType.THIS_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.add(Calendar.DAY_OF_YEAR, 6)
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999)
                Pair(start, cal.timeInMillis)
            }
            SharePeriodType.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999)
                Pair(start, cal.timeInMillis)
            }
            SharePeriodType.CUSTOM -> Pair(customStartDateMillis, customEndDateMillis)
        }
    }

    fun getPeriodTitle(period: SharePeriodType, strings: Strings): String {
        return when (period) {
            SharePeriodType.TODAY -> "${strings.today} (${strings.exportStatement})"
            SharePeriodType.YESTERDAY -> "${strings.yesterday} (${strings.exportStatement})"
            SharePeriodType.THIS_WEEK -> "${strings.thisWeek} (${strings.exportStatement})"
            SharePeriodType.THIS_MONTH -> "${strings.thisMonth} (${strings.exportStatement})"
            SharePeriodType.CUSTOM -> "${strings.customRange} (${strings.exportStatement})"
        }
    }

    fun getPeriodSubtitle(
        period: SharePeriodType,
        activeStart: Long,
        activeEnd: Long,
        dayOnlyFormat: SimpleDateFormat,
        rangeDateFormat: SimpleDateFormat,
        strings: Strings
    ): String {
        return when (period) {
            SharePeriodType.TODAY, SharePeriodType.YESTERDAY -> {
                val d = dayOnlyFormat.format(Date(activeStart))
                if (strings.isBangla) convertToBengaliDigits(d) else d
            }
            SharePeriodType.THIS_WEEK, SharePeriodType.THIS_MONTH, SharePeriodType.CUSTOM -> {
                val s = rangeDateFormat.format(Date(activeStart))
                val e = rangeDateFormat.format(Date(activeEnd))
                val sDisplay = if (strings.isBangla) convertToBengaliDigits(s) else s
                val eDisplay = if (strings.isBangla) convertToBengaliDigits(e) else e
                "$sDisplay - $eDisplay"
            }
        }
    }
}
