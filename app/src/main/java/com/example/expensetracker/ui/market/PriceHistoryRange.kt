package com.example.expensetracker.ui.market

/**
 * Historical time-range filter window for commodity price dynamics.
 */
enum class PriceHistoryRange(val titleBn: String, val titleEn: String, val days: Int) {
    SEVEN_DAYS("৭ দিন", "7d", 7),
    THIRTY_DAYS("৩০ দিন", "30d", 30),
    NINETY_DAYS("৯০ দিন", "90d", 90),
    ALL("সকল", "All", 3650)
}
