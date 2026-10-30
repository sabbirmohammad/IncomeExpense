package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable

enum class PriceTrend {
    UP,      // 🔺 Price Increased (Inflation)
    DOWN,    // 🔻 Price Decreased (Savings)
    STABLE   // ➖ No meaningful change (< 1%)
}

@Serializable
data class ProductPriceRecord(
    val id: Long = 0,
    val productName: String,
    val rawQuantity: Double,
    val rawUnit: String,
    val baseQuantity: Double,
    val baseUnit: String,
    val totalAmount: Double,
    val unitPrice: Double, // Price per base unit (e.g. ৳ per kg)
    val marketItemId: Long? = null,
    val transactionId: Long? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ProductPriceInsight(
    val productName: String,
    val currentUnitPrice: Double,       // Latest unit price per base unit
    val previousUnitPrice: Double? = null, // Immediately prior unit price
    val baseUnit: String,              // "কেজি", "লিটার", "পিস", etc.
    val priceDiff: Double = 0.0,        // current - previous
    val percentDiff: Double = 0.0,      // ((current - prev) / prev) * 100
    val oneMonthAgoUnitPrice: Double? = null, // Price ~30 days ago
    val oneMonthDiff: Double? = null,
    val oneMonthPercentDiff: Double? = null,
    val minUnitPrice: Double = currentUnitPrice,
    val maxUnitPrice: Double = currentUnitPrice,
    val avgUnitPrice: Double = currentUnitPrice,
    val purchaseCount: Int = 1,
    val lastPurchasedTimestamp: Long = System.currentTimeMillis(),
    val priceTrend: PriceTrend = PriceTrend.STABLE
) {
    val isPriceUp: Boolean get() = priceTrend == PriceTrend.UP
    val isPriceDown: Boolean get() = priceTrend == PriceTrend.DOWN
    val isPriceStable: Boolean get() = priceTrend == PriceTrend.STABLE

    fun formatUnitRate(): String {
        val rateFormatted = if (currentUnitPrice % 1.0 == 0.0) {
            currentUnitPrice.toInt().toString()
        } else {
            "%.1f".format(currentUnitPrice)
        }
        return "৳$rateFormatted/$baseUnit"
    }

    fun formatAvgRate(): String {
        val rateFormatted = if (avgUnitPrice % 1.0 == 0.0) {
            avgUnitPrice.toInt().toString()
        } else {
            "%.1f".format(avgUnitPrice)
        }
        return "৳$rateFormatted/$baseUnit"
    }

    fun formatDiff(): String {
        val diffAbs = kotlin.math.abs(priceDiff)
        val diffFormatted = if (diffAbs % 1.0 == 0.0) {
            diffAbs.toInt().toString()
        } else {
            "%.1f".format(diffAbs)
        }
        val pctFormatted = "%.1f".format(kotlin.math.abs(percentDiff))
        return if (priceDiff > 0) {
            "+৳$diffFormatted ($pctFormatted%)"
        } else if (priceDiff < 0) {
            "-৳$diffFormatted ($pctFormatted%)"
        } else {
            "০%"
        }
    }
}

@Serializable
data class MarketListEstimateSummary(
    val totalEstimated: Double = 0.0,
    val totalActualSpent: Double = 0.0,
    val estimatedItemsCount: Int = 0,
    val actualItemsCount: Int = 0,
    val totalItemsCount: Int = 0
) {
    val variance: Double get() = totalActualSpent - totalEstimated
    val hasEstimation: Boolean get() = totalEstimated > 0.0
}
