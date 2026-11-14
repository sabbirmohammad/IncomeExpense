package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType

internal object DemoTransactionsSeeder {
    fun seedDemoTransactions(db: SQLiteDatabase) {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L
        val demoTransactions = listOf(
            Transaction(0, "Monthly Salary", 65000.0, TransactionType.INCOME, 13, "Salary", "Work", "#3DDC97", now - 2 * oneDay, PaymentMethod.BANK_TRANSFER, "Direct company deposit", accountId = 2, accountName = "Bank Account"),
            Transaction(0, "Freelance Web Project", 18000.0, TransactionType.INCOME, 14, "Freelance", "Laptop", "#3B8FF0", now - 1 * oneDay, PaymentMethod.BKASH, "Milestone payout", accountId = 3, accountName = "bKash Personal"),
            Transaction(0, "Supermarket Groceries", 4200.0, TransactionType.EXPENSE, 2, "Groceries", "ShoppingCart", "#2EC4B6", now - 1 * oneDay, PaymentMethod.DEBIT_CARD, "Weekly essentials", accountId = 2, accountName = "Bank Account"),
            Transaction(0, "Dinner with Family", 2100.0, TransactionType.EXPENSE, 1, "Food & Dining", "Restaurant", "#F0716B", now - oneDay / 2, PaymentMethod.BKASH, "Weekend dinner", accountId = 3, accountName = "bKash Personal"),
            Transaction(0, "Electricity & Internet Bill", 3450.0, TransactionType.EXPENSE, 6, "Bills & Utilities", "Bolt", "#F0A63D", now - oneDay / 4, PaymentMethod.NAGAD, "Monthly utility bills", accountId = 4, accountName = "Nagad Personal")
        )
        for (tx in demoTransactions) {
            val cv = ContentValues().apply {
                put(COL_TX_TITLE, tx.title)
                put(COL_TX_AMOUNT, tx.amount)
                put(COL_TX_TYPE, tx.type.name)
                put(COL_TX_CAT_ID, tx.categoryId)
                put(COL_TX_CAT_NAME, tx.categoryName)
                put(COL_TX_CAT_ICON, tx.categoryIcon)
                put(COL_TX_CAT_COLOR, tx.categoryColor)
                put(COL_TX_DATE, tx.dateMillis)
                put(COL_TX_PAYMENT_METHOD, tx.paymentMethod.name)
                put(COL_TX_NOTES, tx.notes)
                if (tx.accountId != null) put(COL_TX_ACCOUNT_ID, tx.accountId) else putNull(COL_TX_ACCOUNT_ID)
                if (tx.accountName != null) put(COL_TX_ACCOUNT_NAME, tx.accountName) else putNull(COL_TX_ACCOUNT_NAME)
            }
            db.insert(TABLE_TRANSACTIONS, null, cv)
        }
    }
}
