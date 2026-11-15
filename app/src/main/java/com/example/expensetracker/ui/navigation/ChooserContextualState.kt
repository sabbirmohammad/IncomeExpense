package com.example.expensetracker.ui.navigation

import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.ui.components.convertToBengaliDigits

data class ChooserContextualState(
    val isMorningBazaarHours: Boolean,
    val hasActiveTrip: Boolean,
    val isMarketContextual: Boolean,
    val isExpenseSuggested: Boolean,
    val timeGreeting: String,
    val marketSubtitle: String,
    val marketBadge: String?
)

fun calculateChooserContextualState(
    currentHour: Int,
    activeMarketList: MarketListWithItems?,
    isBangla: Boolean
): ChooserContextualState {
    val isMorningBazaarHours = currentHour in 6..11
    val totalItems = activeMarketList?.totalItems ?: 0
    val remainingItems = activeMarketList?.remainingItems ?: 0
    val isCompleted = activeMarketList?.list?.isCompleted ?: false
    val hasActiveTrip = totalItems > 0 && !isCompleted
    val isMarketContextual = hasActiveTrip || isMorningBazaarHours
    val isExpenseSuggested = !isMarketContextual

    val timeGreeting = when (currentHour) {
        in 5..11 -> if (isBangla) "শুভ সকাল · দ্রুত হিসাব যোগ করুন" else "Good Morning · Quick Add"
        in 12..16 -> if (isBangla) "শুভ দুপুর · দৈনন্দিন হিসাব" else "Good Afternoon · Daily Log"
        in 17..21 -> if (isBangla) "শুভ সন্ধ্যা · সারাদিনের হিসাব" else "Good Evening · Day's Summary"
        else -> if (isBangla) "শুভ রাত্রি · নতুন এন্ট্রি" else "Good Night · Quick Entry"
    }

    val marketSubtitle = when {
        hasActiveTrip && remainingItems > 0 -> {
            val remStr = if (isBangla) convertToBengaliDigits(remainingItems.toString()) else remainingItems.toString()
            val totStr = if (isBangla) convertToBengaliDigits(totalItems.toString()) else totalItems.toString()
            if (isBangla) "${remStr}টি কেনা বাকি (${totStr}টির মধ্যে)" else "$remStr items remaining (of $totStr)"
        }
        hasActiveTrip && remainingItems == 0 -> {
            val totStr = if (isBangla) convertToBengaliDigits(totalItems.toString()) else totalItems.toString()
            if (isBangla) "সবগুলো পণ্য কেনা সম্পন্ন (${totStr}টি) · হিসাব চূড়ান্ত করুন" else "All $totStr items purchased · Finalize trip"
        }
        isMorningBazaarHours -> {
            if (isBangla) "সকালের বাজার ফর্দ তৈরি বা কেনাকাটা" else "Create morning bazaar list & shopping"
        }
        else -> {
            if (isBangla) "বাজারের ফর্দ তৈরি করুন বা কেনাকাটার হিসাব" else "Create market checklist or track shopping"
        }
    }

    val marketBadge = when {
        hasActiveTrip && remainingItems == 0 -> if (isBangla) "হিসাব চূড়ান্ত করুন" else "Finalize Trip"
        hasActiveTrip -> if (isBangla) "চলমান ফর্দ" else "Active Trip"
        isMorningBazaarHours -> if (isBangla) "প্রস্তাবিত" else "Suggested"
        else -> null
    }

    return ChooserContextualState(
        isMorningBazaarHours = isMorningBazaarHours,
        hasActiveTrip = hasActiveTrip,
        isMarketContextual = isMarketContextual,
        isExpenseSuggested = isExpenseSuggested,
        timeGreeting = timeGreeting,
        marketSubtitle = marketSubtitle,
        marketBadge = marketBadge
    )
}
