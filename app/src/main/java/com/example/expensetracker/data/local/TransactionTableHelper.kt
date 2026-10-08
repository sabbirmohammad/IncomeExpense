package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.Cursor
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType

internal class TransactionTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun insertTransaction(tx: Transaction): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val (resolvedAccountId, resolvedAccountName) = dbHelper.resolveAccountForTransaction(db, tx.accountId, tx.paymentMethod)
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
                put(COL_TX_ACCOUNT_ID, resolvedAccountId)
                put(COL_TX_ACCOUNT_NAME, resolvedAccountName)
                if (tx.receiptImagePath != null) put(COL_TX_RECEIPT_PATH, tx.receiptImagePath) else putNull(COL_TX_RECEIPT_PATH)
            }
            val id = db.insert(TABLE_TRANSACTIONS, null, cv)
            if (id > 0) {
                dbHelper.recalculateAccountBalance(db, resolvedAccountId)
            }
            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    fun updateTransaction(tx: Transaction): Int {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            var priorAccountId: Long? = null
            val cur = db.query(TABLE_TRANSACTIONS, arrayOf(COL_TX_ACCOUNT_ID), "$COL_TX_ID = ?", arrayOf(tx.id.toString()), null, null, null)
            cur.use {
                if (it.moveToFirst() && !it.isNull(0)) {
                    priorAccountId = it.getLong(0)
                }
            }

            val (resolvedAccountId, resolvedAccountName) = dbHelper.resolveAccountForTransaction(db, tx.accountId, tx.paymentMethod)
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
                put(COL_TX_ACCOUNT_ID, resolvedAccountId)
                put(COL_TX_ACCOUNT_NAME, resolvedAccountName)
                if (tx.receiptImagePath != null) put(COL_TX_RECEIPT_PATH, tx.receiptImagePath) else putNull(COL_TX_RECEIPT_PATH)
            }
            val rows = db.update(TABLE_TRANSACTIONS, cv, "$COL_TX_ID = ?", arrayOf(tx.id.toString()))
            if (rows > 0) {
                if (priorAccountId != null) dbHelper.recalculateAccountBalance(db, priorAccountId)
                if (resolvedAccountId != priorAccountId) dbHelper.recalculateAccountBalance(db, resolvedAccountId)
            }
            db.setTransactionSuccessful()
            return rows
        } finally {
            db.endTransaction()
        }
    }

    fun deleteTransaction(id: Long): Int {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            var accountId: Long? = null
            val cur = db.query(TABLE_TRANSACTIONS, arrayOf(COL_TX_ACCOUNT_ID), "$COL_TX_ID = ?", arrayOf(id.toString()), null, null, null)
            cur.use {
                if (it.moveToFirst() && !it.isNull(0)) {
                    accountId = it.getLong(0)
                }
            }
            val rows = db.delete(TABLE_TRANSACTIONS, "$COL_TX_ID = ?", arrayOf(id.toString()))
            if (rows > 0) {
                val cvMarket = ContentValues().apply { putNull(COL_MI_TRANSACTION_ID) }
                db.update(TABLE_MARKET_ITEMS, cvMarket, "$COL_MI_TRANSACTION_ID = ?", arrayOf(id.toString()))
                if (accountId != null) {
                    dbHelper.recalculateAccountBalance(db, accountId)
                }
            }
            db.setTransactionSuccessful()
            return rows
        } finally {
            db.endTransaction()
        }
    }

    fun getAllTransactions(): List<Transaction> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Transaction>()
        val cursor = db.query(TABLE_TRANSACTIONS, null, null, null, null, null, "$COL_TX_DATE DESC, $COL_TX_ID DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToTransaction(c))
            }
        }
        return list
    }

    fun getTransactionsForAccount(accountId: Long): List<Transaction> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Transaction>()
        val cursor = db.query(TABLE_TRANSACTIONS, null, "$COL_TX_ACCOUNT_ID = ?", arrayOf(accountId.toString()), null, null, "$COL_TX_DATE DESC, $COL_TX_ID DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToTransaction(c))
            }
        }
        return list
    }

    fun cursorToTransaction(c: Cursor): Transaction {
        val paymentMethodStr = c.getString(c.getColumnIndexOrThrow(COL_TX_PAYMENT_METHOD))
        val paymentMethod = try {
            PaymentMethod.valueOf(paymentMethodStr)
        } catch (_: Exception) {
            PaymentMethod.CASH
        }

        val accIdIndex = c.getColumnIndex(COL_TX_ACCOUNT_ID)
        val accountId = if (accIdIndex != -1 && !c.isNull(accIdIndex)) c.getLong(accIdIndex) else null
        val accNameIndex = c.getColumnIndex(COL_TX_ACCOUNT_NAME)
        val accountName = if (accNameIndex != -1 && !c.isNull(accNameIndex)) c.getString(accNameIndex) else null

        val receiptIndex = c.getColumnIndex(COL_TX_RECEIPT_PATH)
        val receiptImagePath = if (receiptIndex != -1 && !c.isNull(receiptIndex)) c.getString(receiptIndex) else null

        return Transaction(
            id = c.getLong(c.getColumnIndexOrThrow(COL_TX_ID)),
            title = c.getString(c.getColumnIndexOrThrow(COL_TX_TITLE)),
            amount = c.getDouble(c.getColumnIndexOrThrow(COL_TX_AMOUNT)),
            type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_TX_TYPE))),
            categoryId = c.getLong(c.getColumnIndexOrThrow(COL_TX_CAT_ID)),
            categoryName = c.getString(c.getColumnIndexOrThrow(COL_TX_CAT_NAME)),
            categoryIcon = c.getString(c.getColumnIndexOrThrow(COL_TX_CAT_ICON)),
            categoryColor = c.getString(c.getColumnIndexOrThrow(COL_TX_CAT_COLOR)),
            dateMillis = c.getLong(c.getColumnIndexOrThrow(COL_TX_DATE)),
            paymentMethod = paymentMethod,
            notes = c.getString(c.getColumnIndexOrThrow(COL_TX_NOTES)),
            accountId = accountId,
            accountName = accountName,
            receiptImagePath = receiptImagePath
        )
    }
}
