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
    accountId: Long? = null
): MarketCompletionResult = withContext(Dispatchers.IO) {
    val items = dbHelper.getMarketItemsForList(listId)
    val allLists = dbHelper.getAllMarketLists()
    val currentTrip = allLists.find { it.list.id == listId }
    val marketList = currentTrip?.list

    // 1. Mark any unpurchased items as purchased when finishing the market trip
    for (item in items) {
        if (!item.isPurchased) {
            dbHelper.updateMarketItemPurchased(
                itemId = item.id,
                isPurchased = true,
                amount = item.amount,
                transactionId = item.transactionId
            )
        }
    }

    // 2. Re-fetch the updated items
    val updatedItems = dbHelper.getMarketItemsForList(listId)
    val purchasedItems = updatedItems.filter { it.isPurchased && (it.amount ?: 0.0) > 0 }
    val totalSpent = purchasedItems.sumOf { it.amount ?: 0.0 }
    val purchasedCount = updatedItems.count { it.isPurchased }

    // 3. Remove any old individual transactions created during tick to consolidate into one clean market trip transaction
    val existingTxIds = updatedItems.mapNotNull { it.transactionId }.filter { it > 0 }.distinct()
    for (oldTxId in existingTxIds) {
        dbHelper.deleteTransaction(oldTxId)
    }

    var newTxId: Long? = null
    if (totalSpent > 0) {
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

        val tripDate = marketList?.createdAt ?: System.currentTimeMillis()
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

        val breakdownLines = updatedItems.filter { it.isPurchased }.joinToString("\n") { itm ->
            val amtStr = if (itm.amount != null && itm.amount > 0) {
                val formattedNum = if (itm.amount % 1.0 == 0.0) "${itm.amount.toInt()}" else "${itm.amount}"
                "৳$formattedNum"
            } else {
                if (isBn) "মূল্য ছাড়া" else "No price"
            }
            "• ${itm.name} (${itm.formatQuantity()}) - $amtStr"
        }
        val noteHeader = if (isBn) "বাজারের বিবরণ (${purchasedCount}টি পণ্য):" else "Market Trip Details ($purchasedCount items):"
        val txNotes = "$noteHeader\n$breakdownLines"

        val newTx = Transaction(
            title = txTitle,
            amount = totalSpent,
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
        for (item in updatedItems) {
            if (item.isPurchased) {
                dbHelper.updateMarketItemPurchased(
                    itemId = item.id,
                    isPurchased = true,
                    amount = item.amount,
                    transactionId = newTxId
                )
            }
        }
    }

    val success = dbHelper.completeMarketList(listId)

    refreshTransactions()
    refreshAccounts()
    refreshBudgets()
    refreshMarketList()

    MarketCompletionResult(
        success = success,
        totalSpent = totalSpent,
        purchasedCount = purchasedCount,
        transactionId = newTxId
    )
}
