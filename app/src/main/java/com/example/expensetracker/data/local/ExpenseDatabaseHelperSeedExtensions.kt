package com.example.expensetracker.data.local

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction

// --- Internal Seed & Balance Operations ---
internal fun ExpenseDatabaseHelper.seedDefaultAccountsIfEmpty(db: SQLiteDatabase) = DatabaseSeedData.seedDefaultAccountsIfEmpty(db)
internal fun ExpenseDatabaseHelper.backfillTransactionAccounts(db: SQLiteDatabase) = DatabaseSeedData.backfillTransactionAccounts(db)
internal fun ExpenseDatabaseHelper.seedSampleRecurringBillsIfEmpty(db: SQLiteDatabase) = DatabaseSeedData.seedSampleRecurringBillsIfEmpty(db)
fun ExpenseDatabaseHelper.seedDefaultCategories(db: SQLiteDatabase) = DatabaseSeedData.seedDefaultCategories(db)
fun ExpenseDatabaseHelper.reconcileAndDeduplicateCategoryColors(db: SQLiteDatabase) = CategoryColorReconciler.reconcileAndDeduplicateCategoryColors(db)
fun ExpenseDatabaseHelper.recalculateAccountBalance(db: SQLiteDatabase, accountId: Long) = AccountBalanceHelper.recalculateAccountBalance(db, accountId)
fun ExpenseDatabaseHelper.recalculateAllAccountBalances(db: SQLiteDatabase) = AccountBalanceHelper.recalculateAllAccountBalances(db, this)
fun ExpenseDatabaseHelper.resolveAccountForTransaction(db: SQLiteDatabase, accountId: Long?, paymentMethod: PaymentMethod?): Pair<Long, String> =
    AccountBalanceHelper.resolveAccountForTransaction(db, accountId, paymentMethod, this)
internal fun ExpenseDatabaseHelper.cursorToTransaction(c: Cursor): Transaction = transactionHelper.cursorToTransaction(c)
