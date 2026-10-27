package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MasterProduct(
    val id: Long = 0,
    val name: String,
    val normalizedName: String,
    val category: String = "groceries", // "veggies", "meat_fish", "dairy_bakery", "groceries"
    val defaultUnit: String = "কেজি",
    val latestUnitPrice: Double = 0.0,
    val previousUnitPrice: Double? = null,
    val minUnitPrice: Double = latestUnitPrice,
    val maxUnitPrice: Double = latestUnitPrice,
    val avgUnitPrice: Double = latestUnitPrice,
    val purchaseCount: Int = 1,
    val isStaple: Boolean = false, // true = commonly/regularly purchased (>= 3 times or staple default)
    val firstPurchasedAt: Long = System.currentTimeMillis(),
    val lastPurchasedAt: Long = System.currentTimeMillis()
) {
    val priceDiff: Double?
        get() = if (previousUnitPrice != null) latestUnitPrice - previousUnitPrice!! else null

    val percentDiff: Double?
        get() = if (previousUnitPrice != null && previousUnitPrice!! > 0) {
            ((latestUnitPrice - previousUnitPrice!!) / previousUnitPrice!!) * 100.0
        } else null

    val isPriceSpike: Boolean
        get() = (percentDiff ?: 0.0) >= 20.0 // 20% or more price increase

    val isNewProduct: Boolean
        get() = purchaseCount <= 1
}
