package com.example.expensetracker.ui.market

/**
 * Strategy for unpurchased leftover items when finishing a market trip.
 */
enum class LeftoverHandlingOption {
    MOVE_TO_NEXT,
    KEEP_IN_LIST,
    DISCARD
}

/**
 * Detected commodity price shift between the trip rate and historical benchmark.
 */
data class PriceChangeInsight(
    val itemName: String,
    val diffAmount: Double,
    val diffPercentage: Double,
    val isIncrease: Boolean
)
