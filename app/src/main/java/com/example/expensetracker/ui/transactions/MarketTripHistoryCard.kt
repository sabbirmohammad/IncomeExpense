package com.example.expensetracker.ui.transactions

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun MarketTripHistoryCard(
    trip: MarketListWithItems,
    currencySymbol: String,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEmbedded: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    val haptics = rememberAppHaptics()
    val dateText = remember(trip.list.createdAt, isBangla, useBengaliNumerals) {
        val datePart = SimpleDateFormat("d MMMM yyyy", DateTimeHelper.getLocale(isBangla)).format(Date(trip.list.createdAt))
        val timePart = DateTimeHelper.formatTimeOnly(trip.list.createdAt, isBangla, useBengaliNumerals)
        val localizedDate = if (useBengaliNumerals || isBangla) convertToBengaliDigits(datePart) else datePart
        "$localizedDate, $timePart"
    }

    val displayTitle = remember(trip.list.title, trip.list.createdAt, isBangla, useBengaliNumerals) {
        val generatedTitle = DateTimeHelper.formatMarketTripTitle(trip.list.createdAt, isBangla, useBengaliNumerals)
        if (trip.list.title.isBlank() ||
            trip.list.title == "আজকের বাজার" ||
            trip.list.title == "বাজারের ফর্দ" ||
            trip.list.title == "Market Checklist" ||
            trip.list.title.endsWith("বাজার") ||
            trip.list.title.endsWith("Market")
        ) {
            generatedTitle
        } else {
            "$generatedTitle (${trip.list.title})"
        }
    }

    val cardContent = @Composable {
        Column(
            modifier = Modifier
                .padding(if (isEmbedded) 12.dp else 14.dp)
                .animateContentSize()
        ) {
            MarketTripCardHeader(
                displayTitle = displayTitle,
                dateText = dateText,
                totalSpent = trip.totalSpent,
                currencySymbol = currencySymbol,
                isBangla = isBangla
            )

            Spacer(modifier = Modifier.height(10.dp))

            MarketTripCardActionsRow(
                purchasedItems = trip.purchasedItems,
                totalItems = trip.totalItems,
                isBangla = isBangla,
                expanded = expanded,
                onToggleExpand = {
                    haptics.lightTick()
                    expanded = !expanded
                },
                showOptionsMenu = showOptionsMenu,
                onShowOptionsMenu = {
                    haptics.lightTick()
                    showOptionsMenu = it
                },
                onDeleteClick = onDeleteClick
            )

            if (expanded) {
                MarketTripItemsBreakdown(
                    items = trip.items,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla
                )
            }
        }
    }

    val appColors = LocalAppColors.current

    if (isEmbedded) {
        androidx.compose.foundation.layout.Box(
            modifier = modifier
                .fillMaxWidth()
                .background(appColors.surface)
        ) {
            cardContent()
        }
    } else {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = appColors.surface,
            border = BorderStroke(1.dp, appColors.border),
            modifier = modifier.fillMaxWidth()
        ) {
            cardContent()
        }
    }
}
