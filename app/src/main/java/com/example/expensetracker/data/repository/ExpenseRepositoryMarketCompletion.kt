package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.MarketCompletionResult
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.DateTimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ExpenseRepository.completeActiveMarketList(
    listId: Long,
    accountId: Long? = null,
    customTotalAmount: Double? = null,
    otherCosts: Double = 0.0,
    customDateMillis: Long? = null,
    customNote: String? = null
): MarketCompletionResult = withContext(Dispatchers.IO) {
    val db = dbHelper.writableDatabase
    db.beginTransaction()
    val result = try {
        val items = dbHelper.getMarketItemsForList(listId)
        val allLists = dbHelper.getAllMarketLists()
        val currentTrip = allLists.find { it.list.id == listId }
        val marketList = currentTrip?.list

        // 1. Invariant: Unpurchased items remain unpurchased (isPurchased = 0, amount = null)
        val purchasedItems = items.filter { it.isPurchased }
        val purchasedItemsWithAmount = purchasedItems.filter { (it.amount ?: 0.0) > 0.0 }
        val itemSum = purchasedItemsWithAmount.sumOf { it.amount ?: 0.0 }
        val purchasedCount = purchasedItems.size

        val editedTotal = customTotalAmount ?: itemSum
        val safeOtherCosts = otherCosts.coerceAtLeast(0.0)
        val finalExpenseAmount = (editedTotal + safeOtherCosts).coerceAtLeast(0.0)
        val adjustment = editedTotal - itemSum

        // 2. Remove any old individual transactions created during tick
        val existingTxIds = items.mapNotNull { it.transactionId }.filter { it > 0 }.distinct()
        for (oldTxId in existingTxIds) {
            dbHelper.deleteTransaction(oldTxId)
        }

        var newTxId: Long? = null
        if (finalExpenseAmount > 0.0) {
            // Find Groceries category
            val categories = dbHelper.getAllCategories()
            val groceryCat = categories.find {
                it.name.equals("Groceries", ignoreCase = true) ||
                it.name.equals("বাজার ও সদাই", ignoreCase = true)
            }
            val catId = groceryCat?.id ?: 2L
            val catName = groceryCat?.name ?: "Groceries"
            val catIcon = groceryCat?.icon ?: "ShoppingCart"
            val catColor = groceryCat?.colorHex ?: "#2EC4B6"

            // Target account
            val accounts = dbHelper.getAllAccounts()
            val targetAccount = if (accountId != null) {
                accounts.find { it.id == accountId }
            } else {
                accounts.find { it.isDefault } ?: accounts.firstOrNull()
            }

            val tripDate = customDateMillis ?: marketList?.createdAt ?: System.currentTimeMillis()
            val isBn = userProfile.value.appLanguage != "en"
            val generatedTitle = DateTimeHelper.formatMarketTripTitle(tripDate, isBn, userProfile.value.useBengaliNumerals || isBn)
            val txTitle = if (marketList != null && marketList.title.isNotBlank() &&
                marketList.title != "আজকের বাজার" &&
                marketList.title != "বাজারের ফর্দ" &&
                marketList.title != "Market Checklist" &&
                !marketList.title.endsWith("বাজার") &&
                !marketList.title.endsWith("Market")
            ) {
                "$generatedTitle (${marketList.title})"
            } else {
                generatedTitle
            }

            val breakdownLines = purchasedItems.joinToString("\n") { itm ->
                val amtStr = if (itm.amount != null && itm.amount > 0) {
                    val formattedNum = if (itm.amount % 1.0 == 0.0) "${itm.amount.toInt()}" else "${itm.amount}"
                    "৳$formattedNum"
                } else {
                    if (isBn) "মূল্য ছাড়া" else "No price"
                }
                "• ${itm.name} (${itm.formatQuantity()}) - $amtStr"
            }

            val extraLines = buildList {
                if (safeOtherCosts > 0.0) {
                    val otherCostFormatted = if (safeOtherCosts % 1.0 == 0.0) "${safeOtherCosts.toInt()}" else "$safeOtherCosts"
                    add("• " + (if (isBn) "রিকশা/ব্যাগ/অন্যান্য খরচ: ৳$otherCostFormatted" else "Other costs (rickshaw/bag): ৳$otherCostFormatted"))
                }
                if (adjustment != 0.0) {
                    val absAdj = kotlin.math.abs(adjustment)
                    val adjFormatted = if (absAdj % 1.0 == 0.0) "${absAdj.toInt()}" else "$absAdj"
                    if (adjustment < 0.0) {
                        add("• " + (if (isBn) "সমন্বয় / ছাড়: -৳$adjFormatted" else "Adjustment / Discount: -৳$adjFormatted"))
                    } else {
                        add("• " + (if (isBn) "অতিরিক্ত সমন্বয়: +৳$adjFormatted" else "Extra adjustment: +৳$adjFormatted"))
                    }
                }
                if (!customNote.isNullOrBlank()) {
                    add((if (isBn) "নোট: " else "Note: ") + customNote.trim())
                }
            }

            val noteHeader = if (isBn) "বাজারের বিবরণ (${purchasedCount}টি পণ্য):" else "Market Trip Details ($purchasedCount items):"
            val txNotes = if (extraLines.isNotEmpty()) {
                "$noteHeader\n$breakdownLines\n${extraLines.joinToString("\n")}"
            } else {
                "$noteHeader\n$breakdownLines"
            }

            val newTx = Transaction(
                title = txTitle,
                amount = finalExpenseAmount,
                type = TransactionType.EXPENSE,
                categoryId = catId,
                categoryName = catName,
                categoryIcon = catIcon,
                categoryColor = catColor,
                dateMillis = tripDate,
                paymentMethod = when (targetAccount?.type) {
                    com.example.expensetracker.data.model.AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    com.example.expensetracker.data.model.AccountType.MFS -> PaymentMethod.BKASH
                    else -> PaymentMethod.CASH
                },
                notes = txNotes,
                accountId = targetAccount?.id,
                accountName = targetAccount?.name
            )

            newTxId = dbHelper.insertTransaction(newTx)

            // Update all purchased items in this list to point to newTxId
            for (item in purchasedItems) {
                dbHelper.updateMarketItemPurchased(
                    itemId = item.id,
                    isPurchased = true,
                    amount = item.amount,
                    transactionId = newTxId
                )
            }

            // Record price intelligence into product_price_history!
            dbHelper.recordPricesForMarketItems(purchasedItemsWithAmount, newTxId, tripDate)
        }

        val success = dbHelper.completeMarketList(listId)
        db.setTransactionSuccessful()

        MarketCompletionResult(
            success = success,
            totalSpent = finalExpenseAmount,
            purchasedCount = purchasedCount,
            transactionId = newTxId
        )
    } finally {
        db.endTransaction()
    }

    if (result.success) {
        refreshTransactions()
        refreshAccounts()
        refreshBudgets()
        refreshMarketList()
    }

    result
}
