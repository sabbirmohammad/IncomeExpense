package com.example.expensetracker.ui.dashboard

import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.ui.components.convertToBengaliDigits

fun generateMarketShareText(
    listTitle: String,
    items: List<MarketItem>,
    isBangla: Boolean,
    targetBudget: Double? = null
): String {
    val sb = StringBuilder()
    sb.append("🛒 ")
    val title = if (listTitle.isNotBlank() &&
        listTitle != "আজকের বাজার" &&
        listTitle != "বাজারের ফর্দ" &&
        listTitle != "Market Checklist"
    ) listTitle else if (isBangla) "আজকের বাজার ফর্দ" else "Market Checklist"
    sb.append(title)
    sb.append(":\n\n")

    val purchased = items.filter { it.isPurchased }
    val remaining = items.filter { !it.isPurchased }
    val totalSpent = purchased.sumOf { it.amount ?: 0.0 }

    val grouped = items.groupBy { getMarketItemCategory(it.name) }
    for (dept in ALL_DEPARTMENTS) {
        val deptItems = grouped[dept.key] ?: continue
        if (deptItems.isEmpty()) continue
        sb.append("${dept.icon} ${if (isBangla) dept.titleBn else dept.titleEn}:\n")
        for (item in deptItems) {
            val check = if (item.isPurchased) "☑" else "☐"
            sb.append("$check ${item.name} (${item.formatQuantity()})")
            if (item.amount != null && item.amount > 0) {
                val amtStr = if (item.amount % 1.0 == 0.0) item.amount.toInt().toString() else item.amount.toString()
                sb.append(" - ৳$amtStr")
            }
            sb.append("\n")
        }
        sb.append("\n")
    }

    if (purchased.isNotEmpty()) {
        val spentFormatted = if (totalSpent % 1.0 == 0.0) totalSpent.toInt().toString() else "%.1f".format(totalSpent)
        sb.append(if (isBangla) "💰 মোট খরচ: ৳${convertToBengaliDigits(spentFormatted)} (${convertToBengaliDigits(purchased.size.toString())}টি কেনা)\n"
                  else "💰 Total Spent: ৳$spentFormatted (${purchased.size} bought)\n")
    }

    if (remaining.isNotEmpty()) {
        sb.append(if (isBangla) "⏳ বাকি আছে: ${convertToBengaliDigits(remaining.size.toString())}টি পণ্য\n"
                  else "⏳ Remaining: ${remaining.size} items\n")
    }

    if (targetBudget != null && targetBudget > 0) {
        val remainingBudget = targetBudget - totalSpent
        val remFormatted = if (remainingBudget % 1.0 == 0.0) remainingBudget.toInt().toString() else "%.1f".format(remainingBudget)
        val budFormatted = if (targetBudget % 1.0 == 0.0) targetBudget.toInt().toString() else "%.1f".format(targetBudget)
        val remText = if (isBangla) convertToBengaliDigits(remFormatted) else remFormatted
        val budText = if (isBangla) convertToBengaliDigits(budFormatted) else budFormatted
        sb.append(if (isBangla) "🎯 বাজেট: ৳$budText (অবশিষ্ট: ৳$remText)\n" else "🎯 Budget: ৳$budText (Remaining: ৳$remText)\n")
    }

    return sb.toString().trim()
}
