package com.example.expensetracker.ui.market

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.ProductPriceRecord

class MarketItemDetailState(
    val allHistoryRecords: List<ProductPriceRecord>,
    selectedRangeInit: PriceHistoryRange,
    val commodity: MarketCommodityUiModel
) {
    var selectedRange by mutableStateOf(selectedRangeInit)
    var showRenameDialog by mutableStateOf(false)
    var showMergeWithDialog by mutableStateOf(false)
    var isAddedToListFeedback by mutableStateOf(false)

    val filteredHistory: List<ProductPriceRecord>
        get() = if (selectedRange == PriceHistoryRange.ALL) {
            allHistoryRecords
        } else {
            val cutoff = System.currentTimeMillis() - (selectedRange.days.toLong() * 24 * 60 * 60 * 1000)
            allHistoryRecords.filter { it.timestamp >= cutoff }
        }

    val activeRecords: List<ProductPriceRecord>
        get() = if (filteredHistory.isNotEmpty()) filteredHistory else allHistoryRecords

    val minRate: Double
        get() = if (activeRecords.isNotEmpty()) activeRecords.minOf { it.unitPrice } else commodity.currentPrice

    val maxRate: Double
        get() = if (activeRecords.isNotEmpty()) activeRecords.maxOf { it.unitPrice } else commodity.currentPrice

    val avgRate: Double
        get() = if (activeRecords.isNotEmpty()) {
            val totalAmt = activeRecords.sumOf { it.totalAmount }
            val totalBase = activeRecords.sumOf { it.baseQuantity }
            if (totalBase > 0.0) totalAmt / totalBase else activeRecords.map { it.unitPrice }.average()
        } else commodity.benchmarkPrice
}

@Composable
fun rememberMarketItemDetailState(
    commodity: MarketCommodityUiModel,
    onFetchHistory: (String) -> List<ProductPriceRecord>
): MarketItemDetailState {
    val allHistoryRecords = remember(commodity.name) { onFetchHistory(commodity.name) }
    return remember(commodity.name, allHistoryRecords) {
        MarketItemDetailState(
            allHistoryRecords = allHistoryRecords,
            selectedRangeInit = PriceHistoryRange.ALL,
            commodity = commodity
        )
    }
}
