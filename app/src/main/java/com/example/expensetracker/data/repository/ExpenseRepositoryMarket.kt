package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.DateTimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ExpenseRepository.createMarketListWithItems(
    title: String,
    items: List<MarketItem>,
    createdAt: Long = System.currentTimeMillis()
): Long = withContext(Dispatchers.IO) {
    val finalTitle = if (title.isBlank() || title == "আজকের বাজার" || title == "বাজারের ফর্দ") {
        DateTimeHelper.formatMarketTripTitle(createdAt, isBangla = true)
    } else {
        title
    }
    val listId = dbHelper.createMarketList(finalTitle, createdAt)
    if (listId > 0 && items.isNotEmpty()) {
        dbHelper.addMarketItems(listId, items)
    }
    refreshMarketList()
    listId
}

suspend fun ExpenseRepository.addMarketItem(listId: Long, item: MarketItem): Long = withContext(Dispatchers.IO) {
    val itemId = dbHelper.addMarketItem(item.copy(listId = listId))
    refreshMarketList()
    itemId
}

suspend fun ExpenseRepository.updateMarketItem(item: MarketItem): Boolean = withContext(Dispatchers.IO) {
    val success = dbHelper.updateMarketItem(item)
    refreshMarketList()
    success
}

suspend fun ExpenseRepository.updateMarketListTitle(listId: Long, title: String): Boolean = withContext(Dispatchers.IO) {
    val success = dbHelper.updateMarketListTitle(listId, title)
    refreshMarketList()
    success
}

suspend fun ExpenseRepository.deleteMarketItem(itemId: Long): Boolean = withContext(Dispatchers.IO) {
    val success = dbHelper.deleteMarketItem(itemId)
    refreshMarketList()
    success
}

suspend fun ExpenseRepository.markMarketItemPurchased(
    item: MarketItem,
    amount: Double,
    accountId: Long? = null
): Long = withContext(Dispatchers.IO) {
    val categories = dbHelper.getAllCategories()
    val groceryCat = categories.find { 
        it.name.equals("Groceries", ignoreCase = true) || 
        it.name.equals("বাজার ও সদাই", ignoreCase = true) 
    }
    val catId = groceryCat?.id ?: 2L
    val catName = groceryCat?.name ?: "Groceries"
    val catIcon = groceryCat?.icon ?: "ShoppingCart"
    val catColor = groceryCat?.colorHex ?: "#2EC4B6"

    val accounts = dbHelper.getAllAccounts()
    val targetAccount = if (accountId != null) {
        accounts.find { it.id == accountId }
    } else {
        accounts.find { it.isDefault } ?: accounts.firstOrNull()
    }

    val qtyText = item.formatQuantity()
    val txTitle = "${item.name} ($qtyText)"
    val noteText = "বাজারের ফর্দ থেকে কেনা ($qtyText)"

    val newTx = Transaction(
        title = txTitle,
        amount = amount,
        type = TransactionType.EXPENSE,
        categoryId = catId,
        categoryName = catName,
        categoryIcon = catIcon,
        categoryColor = catColor,
        dateMillis = System.currentTimeMillis(),
        paymentMethod = when (targetAccount?.type) {
            com.example.expensetracker.data.model.AccountType.BANK -> PaymentMethod.BANK_TRANSFER
            com.example.expensetracker.data.model.AccountType.MFS -> PaymentMethod.BKASH
            else -> PaymentMethod.CASH
        },
        notes = noteText,
        accountId = targetAccount?.id,
        accountName = targetAccount?.name
    )

    val txId = dbHelper.insertTransaction(newTx)
    dbHelper.updateMarketItem(item.copy(isPurchased = true, amount = amount, transactionId = txId))
    dbHelper.updateMarketItemPurchased(item.id, isPurchased = true, amount = amount, transactionId = txId)
    dbHelper.recordProductPrice(
        productName = item.name,
        rawQuantity = item.quantity,
        rawUnit = item.unit,
        totalAmount = amount,
        marketItemId = item.id,
        transactionId = txId,
        timestamp = System.currentTimeMillis()
    )

    refreshTransactions()
    refreshAccounts()
    refreshBudgets()
    refreshMarketList()
    txId
}

suspend fun ExpenseRepository.unmarkMarketItemPurchased(item: MarketItem): Boolean = withContext(Dispatchers.IO) {
    if (item.transactionId != null && item.transactionId > 0) {
        dbHelper.deleteTransaction(item.transactionId)
    }
    val success = dbHelper.updateMarketItemPurchased(item.id, isPurchased = false, amount = null, transactionId = null)
    refreshTransactions()
    refreshAccounts()
    refreshBudgets()
    refreshMarketList()
    success
}

suspend fun ExpenseRepository.deleteActiveMarketList(listId: Long): Boolean = withContext(Dispatchers.IO) {
    deleteMarketList(listId)
}

suspend fun ExpenseRepository.deleteMarketList(listId: Long): Boolean = withContext(Dispatchers.IO) {
    val items = dbHelper.getMarketItemsForList(listId)
    val txIds = items.mapNotNull { it.transactionId }.filter { it > 0 }.distinct()
    for (txId in txIds) {
        dbHelper.deleteTransaction(txId)
    }
    val success = dbHelper.deleteMarketList(listId)
    refreshTransactions()
    refreshAccounts()
    refreshBudgets()
    refreshMarketList()
    success
}
