package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.ReceivableStatus
import com.example.expensetracker.data.model.TransactionType
import java.util.Calendar

internal object DatabaseSeedData {

    fun seedSampleRecurringBillsIfEmpty(db: SQLiteDatabase) {
        RecurringBillsSeeder.seedSampleRecurringBillsIfEmpty(db)
    }

    fun seedDefaultAccountsIfEmpty(db: SQLiteDatabase) {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_ACCOUNTS", null)
        var count = 0
        cursor.use { if (it.moveToFirst()) count = it.getInt(0) }
        if (count == 0) {
            val now = System.currentTimeMillis()
            val accounts = listOf(
                Account(1, "Cash in Hand", AccountType.CASH, "", "Cash", 5000.0, 5000.0, "#2EC4B6", true, now),
                Account(2, "Bank Account", AccountType.BANK, "**** 4589", "City Bank", 50000.0, 50000.0, "#3B8FF0", false, now),
                Account(3, "bKash Personal", AccountType.MFS, "017XXXXXXXX", "bKash", 10000.0, 10000.0, "#E056FD", false, now),
                Account(4, "Nagad Personal", AccountType.MFS, "018XXXXXXXX", "Nagad", 4000.0, 4000.0, "#F0A63D", false, now)
            )
            for (acc in accounts) {
                val cv = ContentValues().apply {
                    put(COL_ACC_ID, acc.id)
                    put(COL_ACC_NAME, acc.name)
                    put(COL_ACC_TYPE, acc.type.name)
                    put(COL_ACC_NUMBER, acc.accountNumber)
                    put(COL_ACC_INSTITUTION, acc.institutionName)
                    put(COL_ACC_INITIAL_BALANCE, acc.initialBalance)
                    put(COL_ACC_CURRENT_BALANCE, acc.currentBalance)
                    put(COL_ACC_COLOR, acc.colorHex)
                    put(COL_ACC_IS_DEFAULT, if (acc.isDefault) 1 else 0)
                    put(COL_ACC_CREATED_AT, acc.createdAt)
                }
                db.insert(TABLE_ACCOUNTS, null, cv)
            }
        }
    }

    fun backfillTransactionAccounts(db: SQLiteDatabase) {
        db.execSQL("UPDATE $TABLE_TRANSACTIONS SET $COL_TX_ACCOUNT_ID = 3, $COL_TX_ACCOUNT_NAME = 'bKash Personal' WHERE $COL_TX_PAYMENT_METHOD = 'BKASH' AND $COL_TX_ACCOUNT_ID IS NULL")
        db.execSQL("UPDATE $TABLE_TRANSACTIONS SET $COL_TX_ACCOUNT_ID = 4, $COL_TX_ACCOUNT_NAME = 'Nagad Personal' WHERE $COL_TX_PAYMENT_METHOD = 'NAGAD' AND $COL_TX_ACCOUNT_ID IS NULL")
        db.execSQL("UPDATE $TABLE_TRANSACTIONS SET $COL_TX_ACCOUNT_ID = 2, $COL_TX_ACCOUNT_NAME = 'Bank Account' WHERE $COL_TX_PAYMENT_METHOD IN ('BANK_TRANSFER', 'CREDIT_CARD', 'DEBIT_CARD') AND $COL_TX_ACCOUNT_ID IS NULL")
        db.execSQL("UPDATE $TABLE_TRANSACTIONS SET $COL_TX_ACCOUNT_ID = 1, $COL_TX_ACCOUNT_NAME = 'Cash in Hand' WHERE $COL_TX_ACCOUNT_ID IS NULL")
    }

    fun seedDefaultCategories(db: SQLiteDatabase) {
        val defaultCategories = listOf(
            Category(0, "Food & Dining", TransactionType.EXPENSE, "Restaurant", "#EF4444", true),
            Category(0, "Groceries", TransactionType.EXPENSE, "ShoppingCart", "#10B981", true),
            Category(0, "Transportation", TransactionType.EXPENSE, "DirectionsCar", "#0284C7", true),
            Category(0, "Shopping", TransactionType.EXPENSE, "ShoppingBag", "#DB2777", true),
            Category(0, "Housing & Rent", TransactionType.EXPENSE, "Home", "#7C3AED", true),
            Category(0, "Bills & Utilities", TransactionType.EXPENSE, "Bolt", "#F59E0B", true),
            Category(0, "Entertainment", TransactionType.EXPENSE, "Movie", "#EC4899", true),
            Category(0, "Health & Care", TransactionType.EXPENSE, "MedicalServices", "#06B6D4", true),
            Category(0, "Education", TransactionType.EXPENSE, "School", "#4F46E5", true),
            Category(0, "Travel", TransactionType.EXPENSE, "Flight", "#14B8A6", true),
            Category(0, "Loan & Debt Given", TransactionType.EXPENSE, "Handshake", "#E11D48", true),
            Category(0, "Other Expense", TransactionType.EXPENSE, "MoreHoriz", "#64748B", true),
            Category(0, "Salary", TransactionType.INCOME, "Work", "#22C55E", true),
            Category(0, "Freelance", TransactionType.INCOME, "Laptop", "#2563EB", true),
            Category(0, "Business", TransactionType.INCOME, "Store", "#D97706", true),
            Category(0, "Investment", TransactionType.INCOME, "TrendingUp", "#84CC16", true),
            Category(0, "Debt Recovery", TransactionType.INCOME, "Handshake", "#0D9488", true),
            Category(0, "Bonus", TransactionType.INCOME, "CardGiftcard", "#9333EA", true),
            Category(0, "Other Income", TransactionType.INCOME, "AttachMoney", "#475569", true)
        )
        for (cat in defaultCategories) {
            val cv = ContentValues().apply {
                put(COL_CAT_NAME, cat.name)
                put(COL_CAT_TYPE, cat.type.name)
                put(COL_CAT_ICON, cat.icon)
                put(COL_CAT_COLOR, cat.colorHex)
                put(COL_CAT_IS_DEFAULT, if (cat.isDefault) 1 else 0)
            }
            db.insert(TABLE_CATEGORIES, null, cv)
        }
    }

    fun seedInitialData(db: SQLiteDatabase, dbHelper: ExpenseDatabaseHelper) {
        seedDefaultCategories(db)

        val profileCv = ContentValues().apply {
            put(COL_PROFILE_ID, 1)
            put(COL_PROFILE_NAME, "Sabbir")
            put(COL_PROFILE_FIRST_NAME, "Sabbir")
            put(COL_PROFILE_LAST_NAME, "")
            put(COL_PROFILE_CURRENCY_CODE, "BDT")
            put(COL_PROFILE_CURRENCY_SYMBOL, "৳")
            put(COL_PROFILE_AVATAR, 0)
            putNull(COL_PROFILE_GOOGLE_EMAIL)
            put(COL_PROFILE_AUTO_SYNC, 1)
            put(COL_PROFILE_LAST_SYNC, 0L)
            put(COL_PROFILE_THEME, "dark")
            put(COL_PROFILE_BENGALI_NUMERALS, 1)
        }
        db.insert(TABLE_PROFILE, null, profileCv)

        val cal = Calendar.getInstance()
        val budgetCv = ContentValues().apply {
            putNull(COL_BUDGET_CAT_ID)
            put(COL_BUDGET_CAT_NAME, "Overall Monthly")
            put(COL_BUDGET_AMOUNT, 35000.0)
            put(COL_BUDGET_MONTH, cal.get(Calendar.MONTH))
            put(COL_BUDGET_YEAR, cal.get(Calendar.YEAR))
        }
        db.insert(TABLE_BUDGETS, null, budgetCv)

        seedDefaultAccountsIfEmpty(db)
        dbHelper.masterProductHelper.seedDefaultStaplesIfEmpty(db)

        val recCv = ContentValues().apply {
            put(COL_REC_PERSON_NAME, "Karim Ahmed")
            put(COL_REC_PHONE, "01712345678")
            put(COL_REC_AMOUNT, 5000.0)
            put(COL_REC_AMOUNT_RECEIVED, 0.0)
            put(COL_REC_DUE_DATE, System.currentTimeMillis() + 7 * 86400000L)
            put(COL_REC_CREATED_DATE, System.currentTimeMillis() - 86400000L)
            put(COL_REC_NOTES, "Equipment purchase advance")
            put(COL_REC_STATUS, ReceivableStatus.PENDING.name)
        }
        db.insert(TABLE_RECEIVABLES, null, recCv)

        DemoTransactionsSeeder.seedDemoTransactions(db)
        dbHelper.recalculateAllAccountBalances(db)
    }
}
