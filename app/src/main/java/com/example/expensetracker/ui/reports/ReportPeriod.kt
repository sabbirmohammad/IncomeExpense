package com.example.expensetracker.ui.reports

enum class ReportPeriod(val titleEn: String, val titleBn: String) {
    THIS_MONTH("This Month", "চলতি মাস"),
    LAST_MONTH("Last Month", "গত মাস"),
    LAST_3_MONTHS("Last 3 Months", "গত ৩ মাস"),
    THIS_YEAR("This Year", "চলতি বছর"),
    ALL_TIME("All Time", "সব সময়"),
    CUSTOM("Custom Range", "কাস্টম সময়সীমা");

    fun getDisplayName(isBangla: Boolean): String = if (isBangla) titleBn else titleEn
}
