package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.data.model.PaymentMethod

internal object AccountBalanceHelper {

    fun resolveAccountForTransaction(
        db: SQLiteDatabase,
        accountId: Long?,
        paymentMethod: PaymentMethod?,
        dbHelper: ExpenseDatabaseHelper
    ): Pair<Long, String> {
        // 1. If accountId is provided and exists in TABLE_ACCOUNTS, use it
        if (accountId != null && accountId > 0) {
            val cur = db.query(TABLE_ACCOUNTS, arrayOf(COL_ACC_ID, COL_ACC_NAME), "$COL_ACC_ID = ?", arrayOf(accountId.toString()), null, null, null)
            cur.use {
                if (it.moveToFirst()) {
                    return Pair(it.getLong(0), it.getString(1))
                }
            }
        }

        // 2. Try to match by payment method
        if (paymentMethod != null) {
            val (typeMatch, nameKeyword) = when (paymentMethod) {
                PaymentMethod.CASH -> Pair("CASH", null)
                PaymentMethod.BKASH -> Pair("MFS", "bkash")
                PaymentMethod.NAGAD -> Pair("MFS", "nagad")
                PaymentMethod.BANK_TRANSFER, PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD -> Pair("BANK", null)
                PaymentMethod.OTHER -> Pair(null, null)
            }

            if (nameKeyword != null) {
                val cur = db.rawQuery(
                    "SELECT $COL_ACC_ID, $COL_ACC_NAME FROM $TABLE_ACCOUNTS WHERE $COL_ACC_TYPE = ? AND (LOWER($COL_ACC_NAME) LIKE ? OR LOWER($COL_ACC_INSTITUTION) LIKE ?) LIMIT 1",
                    arrayOf(typeMatch, "%$nameKeyword%", "%$nameKeyword%")
                )
                cur.use {
                    if (it.moveToFirst()) {
                        return Pair(it.getLong(0), it.getString(1))
                    }
                }
            }

            if (typeMatch != null) {
                val cur = db.rawQuery(
                    "SELECT $COL_ACC_ID, $COL_ACC_NAME FROM $TABLE_ACCOUNTS WHERE $COL_ACC_TYPE = ? ORDER BY $COL_ACC_IS_DEFAULT DESC, $COL_ACC_ID ASC LIMIT 1",
                    arrayOf(typeMatch)
                )
                cur.use {
                    if (it.moveToFirst()) {
                        return Pair(it.getLong(0), it.getString(1))
                    }
                }
            }
        }

        // 3. Fallback to default account
        val defCur = db.rawQuery(
            "SELECT $COL_ACC_ID, $COL_ACC_NAME FROM $TABLE_ACCOUNTS WHERE $COL_ACC_IS_DEFAULT = 1 LIMIT 1",
            null
        )
        defCur.use {
            if (it.moveToFirst()) {
                return Pair(it.getLong(0), it.getString(1))
            }
        }

        // 4. Fallback to first available account
        val firstCur = db.rawQuery(
            "SELECT $COL_ACC_ID, $COL_ACC_NAME FROM $TABLE_ACCOUNTS ORDER BY $COL_ACC_ID ASC LIMIT 1",
            null
        )
        firstCur.use {
            if (it.moveToFirst()) {
                return Pair(it.getLong(0), it.getString(1))
            }
        }

        // 5. If no accounts exist at all, seed default starter accounts and return Cash in Hand (id=1)
        dbHelper.seedDefaultAccountsIfEmpty(db)
        return Pair(1L, "Cash in Hand")
    }

    fun recalculateAccountBalance(db: SQLiteDatabase, accountId: Long) {
        var initial = 0.0
        val accCursor = db.query(TABLE_ACCOUNTS, arrayOf(COL_ACC_INITIAL_BALANCE), "$COL_ACC_ID = ?", arrayOf(accountId.toString()), null, null, null)
        accCursor.use {
            if (it.moveToFirst()) {
                initial = it.getDouble(0)
            }
        }

        var income = 0.0
        val incCursor = db.rawQuery("SELECT SUM($COL_TX_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COL_TX_ACCOUNT_ID = ? AND $COL_TX_TYPE = 'INCOME'", arrayOf(accountId.toString()))
        incCursor.use {
            if (it.moveToFirst() && !it.isNull(0)) {
                income = it.getDouble(0)
            }
        }

        var expense = 0.0
        val expCursor = db.rawQuery("SELECT SUM($COL_TX_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COL_TX_ACCOUNT_ID = ? AND $COL_TX_TYPE = 'EXPENSE'", arrayOf(accountId.toString()))
        expCursor.use {
            if (it.moveToFirst() && !it.isNull(0)) {
                expense = it.getDouble(0)
            }
        }

        val rawCurrent = initial + income - expense
        val current = kotlin.math.round(rawCurrent * 100.0) / 100.0
        val cv = ContentValues().apply {
            put(COL_ACC_CURRENT_BALANCE, current)
        }
        db.update(TABLE_ACCOUNTS, cv, "$COL_ACC_ID = ?", arrayOf(accountId.toString()))
    }

    fun recalculateAllAccountBalances(db: SQLiteDatabase, dbHelper: ExpenseDatabaseHelper) {
        // Auto-repair: Link any legacy orphaned transactions that have no valid account_id
        val orphanedCursor = db.query(
            TABLE_TRANSACTIONS,
            arrayOf(COL_TX_ID, COL_TX_PAYMENT_METHOD),
            "$COL_TX_ACCOUNT_ID IS NULL OR $COL_TX_ACCOUNT_ID NOT IN (SELECT $COL_ACC_ID FROM $TABLE_ACCOUNTS)",
            null,
            null,
            null,
            null
        )
        val orphanedTxs = mutableListOf<Pair<Long, PaymentMethod>>()
        orphanedCursor.use { c ->
            val idCol = c.getColumnIndexOrThrow(COL_TX_ID)
            val pmCol = c.getColumnIndexOrThrow(COL_TX_PAYMENT_METHOD)
            while (c.moveToNext()) {
                val txId = c.getLong(idCol)
                val pmStr = c.getString(pmCol)
                val pm = try { PaymentMethod.valueOf(pmStr) } catch (_: Exception) { PaymentMethod.CASH }
                orphanedTxs.add(Pair(txId, pm))
            }
        }
        for ((txId, pm) in orphanedTxs) {
            val (resolvedId, resolvedName) = resolveAccountForTransaction(db, null, pm, dbHelper)
            val repairCv = ContentValues().apply {
                put(COL_TX_ACCOUNT_ID, resolvedId)
                put(COL_TX_ACCOUNT_NAME, resolvedName)
            }
            db.update(TABLE_TRANSACTIONS, repairCv, "$COL_TX_ID = ?", arrayOf(txId.toString()))
        }

        val sql = """
            SELECT 
                a.$COL_ACC_ID,
                a.$COL_ACC_INITIAL_BALANCE,
                COALESCE(SUM(CASE WHEN t.$COL_TX_TYPE = 'INCOME' THEN t.$COL_TX_AMOUNT ELSE 0 END), 0) AS inc,
                COALESCE(SUM(CASE WHEN t.$COL_TX_TYPE = 'EXPENSE' THEN t.$COL_TX_AMOUNT ELSE 0 END), 0) AS exp
            FROM $TABLE_ACCOUNTS a
            LEFT JOIN $TABLE_TRANSACTIONS t ON a.$COL_ACC_ID = t.$COL_TX_ACCOUNT_ID
            GROUP BY a.$COL_ACC_ID, a.$COL_ACC_INITIAL_BALANCE
        """.trimIndent()

        val updates = mutableListOf<Pair<Long, Double>>()
        val cursor = db.rawQuery(sql, null)
        cursor.use { c ->
            while (c.moveToNext()) {
                val accId = c.getLong(0)
                val initBal = c.getDouble(1)
                val inc = c.getDouble(2)
                val exp = c.getDouble(3)
                val current = kotlin.math.round((initBal + inc - exp) * 100.0) / 100.0
                updates.add(Pair(accId, current))
            }
        }

        for ((accId, balance) in updates) {
            val cv = ContentValues().apply {
                put(COL_ACC_CURRENT_BALANCE, balance)
            }
            db.update(TABLE_ACCOUNTS, cv, "$COL_ACC_ID = ?", arrayOf(accId.toString()))
        }
    }
}
