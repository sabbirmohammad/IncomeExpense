package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.HairlineDivider

@Composable
fun TransactionGroupCard(
    itemsInGroup: List<Transaction>,
    marketLists: List<MarketListWithItems>,
    userProfile: UserProfile,
    isBangla: Boolean,
    onEditTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onDeleteMarketTrip: (MarketListWithItems) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        padding = 0.dp
    ) {
        Column {
            val visibleItems = remember(itemsInGroup, marketLists) {
                val seenTripIds = mutableSetOf<Long>()
                val list = mutableListOf<Pair<Transaction, MarketListWithItems?>>()
                for (tx in itemsInGroup) {
                    val trip = marketLists.find { t ->
                        t.items.any { it.transactionId == tx.id } ||
                        (tx.notes.contains("বাজারের বিবরণ") && t.list.createdAt == tx.dateMillis)
                    }
                    if (trip != null) {
                        if (seenTripIds.add(trip.list.id)) {
                            list.add(Pair(tx, trip))
                        }
                    } else {
                        list.add(Pair(tx, null))
                    }
                }
                list
            }

            visibleItems.forEachIndexed { index, (tx, associatedMarketTrip) ->
                if (associatedMarketTrip != null) {
                    MarketTripHistoryCard(
                        trip = associatedMarketTrip,
                        currencySymbol = userProfile.currencySymbol,
                        isBangla = isBangla,
                        useBengaliNumerals = userProfile.useBengaliNumerals || isBangla,
                        onDeleteClick = { onDeleteMarketTrip(associatedMarketTrip) },
                        isEmbedded = true
                    )
                } else {
                    TransactionSwipeRow(
                        transaction = tx,
                        currencySymbol = userProfile.currencySymbol,
                        isBangla = isBangla,
                        useBengaliNumerals = userProfile.useBengaliNumerals || isBangla,
                        onEdit = onEditTransaction,
                        onDelete = onDeleteTransaction
                    )
                }

                if (index < visibleItems.size - 1) {
                    HairlineDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}
