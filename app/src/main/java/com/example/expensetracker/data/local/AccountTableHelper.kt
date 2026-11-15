package com.example.expensetracker.data.local

import android.content.ContentValues
import com.example.expensetracker.data.model.Account

internal class AccountTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun getAllAccounts(): List<Account> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Account>()
        val cursor = db.query(TABLE_ACCOUNTS, null, null, null, null, null, "$COL_ACC_IS_DEFAULT DESC, $COL_ACC_NAME ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(AccountCursorParser.cursorToAccount(c))
            }
        }
        return list
    }

    fun getAccountById(id: Long): Account? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_ACCOUNTS, null, "$COL_ACC_ID = ?", arrayOf(id.toString()), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                return AccountCursorParser.cursorToAccount(c)
            }
        }
        return null
    }

    fun insertAccount(acc: Account): Long {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_ACC_NAME, acc.name)
            put(COL_ACC_TYPE, acc.type.name)
            put(COL_ACC_NUMBER, acc.accountNumber)
            put(COL_ACC_INSTITUTION, acc.institutionName)
            put(COL_ACC_INITIAL_BALANCE, acc.initialBalance)
            put(COL_ACC_CURRENT_BALANCE, acc.initialBalance)
            put(COL_ACC_COLOR, acc.colorHex)
            put(COL_ACC_IS_DEFAULT, if (acc.isDefault) 1 else 0)
            put(COL_ACC_CREATED_AT, if (acc.createdAt > 0) acc.createdAt else System.currentTimeMillis())
        }
        val id = db.insert(TABLE_ACCOUNTS, null, cv)
        if (id > 0) {
            dbHelper.recalculateAccountBalance(db, id)
        }
        return id
    }

    fun updateAccount(acc: Account): Int {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_ACC_NAME, acc.name)
            put(COL_ACC_TYPE, acc.type.name)
            put(COL_ACC_NUMBER, acc.accountNumber)
            put(COL_ACC_INSTITUTION, acc.institutionName)
            put(COL_ACC_INITIAL_BALANCE, acc.initialBalance)
            put(COL_ACC_COLOR, acc.colorHex)
            put(COL_ACC_IS_DEFAULT, if (acc.isDefault) 1 else 0)
        }
        val rows = db.update(TABLE_ACCOUNTS, cv, "$COL_ACC_ID = ?", arrayOf(acc.id.toString()))
        if (rows > 0) {
            dbHelper.recalculateAccountBalance(db, acc.id)
            val txCv = ContentValues().apply {
                put(COL_TX_ACCOUNT_NAME, acc.name)
            }
            db.update(TABLE_TRANSACTIONS, txCv, "$COL_TX_ACCOUNT_ID = ?", arrayOf(acc.id.toString()))
            val billCv = ContentValues().apply {
                put(COL_BILL_ACCOUNT_NAME, acc.name)
            }
            db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_ACCOUNT_ID = ?", arrayOf(acc.id.toString()))
        }
        return rows
    }

    fun deleteAccount(id: Long): Int {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val countCursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_ACCOUNTS", null)
            val totalAccounts = countCursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
            if (totalAccounts <= 1) {
                return 0
            }

            var isDeletedAccountDefault = false
            var deletedAccountInitialBalance = 0.0
            val defCheckCur = db.query(
                TABLE_ACCOUNTS,
                arrayOf(COL_ACC_IS_DEFAULT, COL_ACC_INITIAL_BALANCE),
                "$COL_ACC_ID = ?",
                arrayOf(id.toString()),
                null, null, null
            )
            defCheckCur.use {
                if (it.moveToFirst()) {
                    isDeletedAccountDefault = it.getInt(0) == 1
                    deletedAccountInitialBalance = it.getDouble(1)
                }
            }

            var targetAccountId = 0L
            var targetAccountName = ""
            val cur = db.rawQuery(
                "SELECT $COL_ACC_ID, $COL_ACC_NAME FROM $TABLE_ACCOUNTS WHERE $COL_ACC_ID != ? ORDER BY $COL_ACC_IS_DEFAULT DESC, $COL_ACC_ID ASC LIMIT 1",
                arrayOf(id.toString())
            )
            cur.use {
                if (it.moveToFirst()) {
                    targetAccountId = it.getLong(0)
                    targetAccountName = it.getString(1)
                }
            }

            if (targetAccountId > 0) {
                if (isDeletedAccountDefault) {
                    val promoteCv = ContentValues().apply {
                        put(COL_ACC_IS_DEFAULT, 1)
                    }
                    db.update(TABLE_ACCOUNTS, promoteCv, "$COL_ACC_ID = ?", arrayOf(targetAccountId.toString()))
                }

                if (deletedAccountInitialBalance != 0.0) {
                    db.execSQL(
                        "UPDATE $TABLE_ACCOUNTS SET $COL_ACC_INITIAL_BALANCE = $COL_ACC_INITIAL_BALANCE + ? WHERE $COL_ACC_ID = ?",
                        arrayOf<Any>(deletedAccountInitialBalance, targetAccountId)
                    )
                }

                val txCv = ContentValues().apply {
                    put(COL_TX_ACCOUNT_ID, targetAccountId)
                    put(COL_TX_ACCOUNT_NAME, targetAccountName)
                }
                db.update(TABLE_TRANSACTIONS, txCv, "$COL_TX_ACCOUNT_ID = ?", arrayOf(id.toString()))

                val billCv = ContentValues().apply {
                    put(COL_BILL_ACCOUNT_ID, targetAccountId)
                    put(COL_BILL_ACCOUNT_NAME, targetAccountName)
                }
                db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_ACCOUNT_ID = ?", arrayOf(id.toString()))
            } else {
                val billCv = ContentValues().apply {
                    putNull(COL_BILL_ACCOUNT_ID)
                    putNull(COL_BILL_ACCOUNT_NAME)
                }
                db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_ACCOUNT_ID = ?", arrayOf(id.toString()))
            }

            val rows = db.delete(TABLE_ACCOUNTS, "$COL_ACC_ID = ?", arrayOf(id.toString()))
            if (targetAccountId > 0) {
                dbHelper.recalculateAccountBalance(db, targetAccountId)
            }
            db.setTransactionSuccessful()
            return rows
        } finally {
            db.endTransaction()
        }
    }

    fun transferBetweenAccounts(fromAccountId: Long, toAccountId: Long, amount: Double, note: String = ""): Boolean {
        return AccountTransferHelper.transferBetweenAccounts(
            dbHelper = dbHelper,
            getAccountById = { getAccountById(it) },
            fromAccountId = fromAccountId,
            toAccountId = toAccountId,
            amount = amount,
            note = note
        )
    }
}
