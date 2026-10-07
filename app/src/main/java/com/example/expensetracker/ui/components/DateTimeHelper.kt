package com.example.expensetracker.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeHelper {

    fun getLocale(isBangla: Boolean): Locale {
        return if (isBangla) Locale("bn", "BD") else Locale.ENGLISH
    }

    private fun formatBengaliPeriod(hour24: Int): String {
        return when (hour24) {
            in 4..11 -> "সকাল"
            in 12..15 -> "দুপুর"
            in 16..17 -> "বিকাল"
            in 18..19 -> "সন্ধ্যা"
            else -> "রাত"
        }
    }

    fun formatDateHeader(
        dateMillis: Long,
        isBangla: Boolean,
        useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled || isBangla
    ): String {
        val sdf = SimpleDateFormat("EEEE, d MMMM", getLocale(isBangla))
        val formatted = sdf.format(Date(dateMillis))
        return if (useBengaliNumerals) convertToBengaliDigits(formatted) else formatted
    }

    fun formatDayGroup(
        dateMillis: Long,
        isBangla: Boolean,
        useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled || isBangla
    ): String {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", getLocale(isBangla))
        val formatted = sdf.format(Date(dateMillis))
        return if (useBengaliNumerals) convertToBengaliDigits(formatted) else formatted
    }

    fun formatTimeOnly(
        dateMillis: Long,
        isBangla: Boolean,
        useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled || isBangla
    ): String {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = dateMillis }
        val hour12 = cal.get(java.util.Calendar.HOUR).let { if (it == 0) 12 else it }
        val minute = cal.get(java.util.Calendar.MINUTE)
        val minuteStr = String.format(Locale.ENGLISH, "%02d", minute)
        return if (isBangla) {
            val period = formatBengaliPeriod(cal.get(java.util.Calendar.HOUR_OF_DAY))
            val rawTime = "$period $hour12:$minuteStr"
            if (useBengaliNumerals || isBangla) convertToBengaliDigits(rawTime) else rawTime
        } else {
            val amPm = if (cal.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM) "AM" else "PM"
            String.format(Locale.ENGLISH, "%02d:%s %s", hour12, minuteStr, amPm)
        }
    }

    fun formatItemDateTime(
        dateMillis: Long,
        isBangla: Boolean,
        useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled || isBangla
    ): String {
        val datePart = SimpleDateFormat("d MMM", getLocale(isBangla)).format(Date(dateMillis))
        val timePart = formatTimeOnly(dateMillis, isBangla, useBengaliNumerals)
        val localizedDate = if (useBengaliNumerals || isBangla) convertToBengaliDigits(datePart) else datePart
        return "$localizedDate, $timePart"
    }

    fun formatFullDateTime(
        dateMillis: Long,
        isBangla: Boolean,
        useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled || isBangla
    ): String {
        val datePart = SimpleDateFormat("dd MMM yyyy", getLocale(isBangla)).format(Date(dateMillis))
        val timePart = formatTimeOnly(dateMillis, isBangla, useBengaliNumerals)
        val localizedDate = if (useBengaliNumerals || isBangla) convertToBengaliDigits(datePart) else datePart
        return "$localizedDate, $timePart"
    }

    fun formatCounter(
        count: Int,
        isBangla: Boolean,
        useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled || isBangla
    ): String {
        val raw = count.toString()
        return if (useBengaliNumerals || isBangla) convertToBengaliDigits(raw) else raw
    }

    fun formatMarketTripTitle(
        dateMillis: Long,
        isBangla: Boolean,
        useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled || isBangla
    ): String {
        val tripCal = java.util.Calendar.getInstance().apply { timeInMillis = dateMillis }
        val nowCal = java.util.Calendar.getInstance()
        val isSameYear = tripCal.get(java.util.Calendar.YEAR) == nowCal.get(java.util.Calendar.YEAR)

        val pattern = if (isSameYear) "d MMMM" else "d MMMM yyyy"
        val sdf = SimpleDateFormat(pattern, getLocale(isBangla))
        val formattedDate = sdf.format(Date(dateMillis))
        val localizedDate = if (useBengaliNumerals || isBangla) convertToBengaliDigits(formattedDate) else formattedDate

        return if (isBangla) "$localizedDate বাজার" else "$localizedDate Market"
    }
}
