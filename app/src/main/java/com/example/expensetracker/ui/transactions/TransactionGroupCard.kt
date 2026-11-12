package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.ui.components.HairlineDivider

/**
 * V7 TransactionGroupCard per UX_FLOWS section 4 & Screenshot Patch C5:
 * - Grouped day surface with dividers without heavy outlines
 * - Dispatches tap -> detail sheet, swipe left -> delete, swipe right -> edit
 */
@Composable
fun TransactionGroupCard(
    itemsInGroup: List<Transaction>,
    marketLists: List<MarketListWithItems>,
    userProfile: UserProfile,
    isBangla: Boolean,
    onEditTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onDeleteMarketTrip: (MarketListWithItems) -> Unit,
    modifier: Modifier = Modifier,
    onSelectTransaction: ((Transaction) -> Unit)? = null,
    hasMultipleAccounts: Boolean = false
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(RadiusM),
        color = appColors.surfaceRaised,
        border = if (appColors.isDark) null else BorderStroke(0.5.dp, appColors.border.copy(alpha = 0.6f)),
        modifier = modifier
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
                        onDelete = onDeleteTransaction,
                        onRowClick = {
                            if (onSelectTransaction != null) {
                                onSelectTransaction(tx)
                            } else {
                                onEditTransaction(tx)
                            }
                        },
                        showAccountPill = hasMultipleAccounts
                    )
                }

                if (index < visibleItems.size - 1) {
                    HairlineDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}
