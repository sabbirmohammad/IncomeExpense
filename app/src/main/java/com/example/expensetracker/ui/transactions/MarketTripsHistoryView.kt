package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.Strings

@Composable
fun MarketTripsHistoryView(
    marketLists: List<MarketListWithItems>,
    currencySymbol: String,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    strings: Strings,
    onDeleteTrip: (MarketListWithItems) -> Unit,
    onBackToTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (marketLists.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateCard(
                icon = Icons.Outlined.ShoppingCart,
                headline = strings.noMarketTrips,
                explanation = strings.noMarketTripsHint,
                buttonText = if (isBangla) "সব লেনদেন দেখুন" else "View All Transactions",
                onButtonClick = onBackToTransactions
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(marketLists, key = { it.list.id }) { trip ->
                MarketTripHistoryCard(
                    trip = trip,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla,
                    useBengaliNumerals = useBengaliNumerals,
                    onDeleteClick = { onDeleteTrip(trip) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}
