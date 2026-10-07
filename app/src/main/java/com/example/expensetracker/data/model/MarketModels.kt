package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MarketItem(
    val id: Long = 0,
    val listId: Long = 0,
    val name: String,
    val quantity: Double = 1.0,
    val unit: String = "কেজি",
    val amount: Double? = null,
    val isPurchased: Boolean = false,
    val transactionId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun formatQuantity(): String {
        return if (quantity % 1.0 == 0.0) {
            "${quantity.toInt()} $unit"
        } else {
            "$quantity $unit"
        }
    }
}

@Serializable
data class MarketList(
    val id: Long = 0,
    val title: String = "বাজারের ফর্দ",
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)

data class MarketListWithItems(
    val list: MarketList,
    val items: List<MarketItem>
) {
    val totalItems: Int get() = items.size
    val purchasedItems: Int get() = items.count { it.isPurchased }
    val remainingItems: Int get() = items.count { !it.isPurchased }
    val totalSpent: Double get() = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
    val progress: Float get() = if (totalItems > 0) purchasedItems.toFloat() / totalItems.toFloat() else 0f
    val isAllPurchased: Boolean get() = items.isNotEmpty() && items.all { it.isPurchased }
}

@Serializable
data class MarketCompletionResult(
    val success: Boolean = false,
    val totalSpent: Double = 0.0,
    val purchasedCount: Int = 0,
    val transactionId: Long? = null
)

@Serializable
data class MarketCostEstimationResult(
    val estimatedTotalMin: Double = 0.0,
    val estimatedTotalMax: Double = 0.0,
    val itemPrices: Map<String, Double> = emptyMap(),
    val advice: String = ""
)
