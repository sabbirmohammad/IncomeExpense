package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun MarketShopModeItemList(
    filteredPending: List<MarketItem>,
    filteredBought: List<MarketItem>,
    isBoughtCollapsed: Boolean,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    currencySymbol: String,
    isBangla: Boolean,
    onItemClick: (MarketItem) -> Unit,
    onQuickMarkPurchased: (MarketItem) -> Unit,
    onUnmarkPurchased: (MarketItem) -> Unit,
    onDeleteItem: (MarketItem) -> Unit,
    onToggleBoughtCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Pending items
        itemsIndexed(filteredPending, key = { _, item -> item.id }) { _, item ->
            val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
            val lastRateText = if (lastRate != null && lastRate.first > 0) {
                val rateStr = if (lastRate.first % 1.0 == 0.0) lastRate.first.toInt().toString() else "%.1f".format(lastRate.first)
                val rateBengali = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
                if (isBangla) "গতবার ৳$rateBengali/${lastRate.second}" else "Last ৳$rateBengali/${lastRate.second}"
            } else null

            MarketShopItemRow(
                item = item,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                lastRateText = lastRateText,
                onRowClick = { onItemClick(item) },
                onTickClick = { onQuickMarkPurchased(item) },
                onDelete = { onDeleteItem(item) }
            )
        }

        // Bought items
        if (filteredBought.isNotEmpty()) {
            item(key = "shop_bought_header") {
                MarketShopModeBoughtHeader(
                    boughtCount = filteredBought.size,
                    isBangla = isBangla,
                    isBoughtCollapsed = isBoughtCollapsed,
                    onToggleCollapse = onToggleBoughtCollapse
                )
            }

            if (!isBoughtCollapsed) {
                itemsIndexed(filteredBought, key = { _, item -> item.id }) { _, item ->
                    val lastRate = lastPurchasedRates[item.name.lowercase().trim()]
                    val lastRateText = if (lastRate != null && lastRate.first > 0) {
                        val rateStr = if (lastRate.first % 1.0 == 0.0) lastRate.first.toInt().toString() else "%.1f".format(lastRate.first)
                        val rateBengali = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
                        if (isBangla) "গতবার ৳$rateBengali/${lastRate.second}" else "Last ৳$rateBengali/${lastRate.second}"
                    } else null

                    MarketShopItemRow(
                        item = item,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla,
                        lastRateText = lastRateText,
                        onRowClick = { onItemClick(item) },
                        onTickClick = { onUnmarkPurchased(item) },
                        onDelete = { onDeleteItem(item) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}
