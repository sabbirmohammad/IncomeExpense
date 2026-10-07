package com.example.expensetracker.ui.share

enum class ShareFormatType {
    PDF,
    IMAGE,
    TEXT
}

enum class SharePeriodType(val label: String) {
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    CUSTOM("Custom Range")
}
