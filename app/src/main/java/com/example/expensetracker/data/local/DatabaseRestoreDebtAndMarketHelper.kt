package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.data.model.BackupPayload

internal object DatabaseRestoreDebtAndMarketHelper {

    fun restoreReceivables(db: SQLiteDatabase, payload: BackupPayload) {
        db.delete(TABLE_RECEIVABLES, null, null)
        for (r in payload.receivables) {
            val cv = ContentValues().apply {
                put(COL_REC_ID, r.id)
                put(COL_REC_PERSON_NAME, r.personName)
                put(COL_REC_PHONE, r.phoneNumber)
                put(COL_REC_AMOUNT, r.amount)
                put(COL_REC_AMOUNT_RECEIVED, r.amountReceived)
                if (r.dueDateMillis != null) put(COL_REC_DUE_DATE, r.dueDateMillis) else putNull(COL_REC_DUE_DATE)
                put(COL_REC_CREATED_DATE, r.createdDateMillis)
                put(COL_REC_NOTES, r.notes)
                put(COL_REC_STATUS, r.status.name)
            }
            db.insert(TABLE_RECEIVABLES, null, cv)
        }
    }

    fun restoreRecurringBills(db: SQLiteDatabase, payload: BackupPayload, dbHelper: ExpenseDatabaseHelper) {
        db.delete(TABLE_RECURRING_BILLS, null, null)
        if (payload.recurringBills.isNotEmpty()) {
            for (bill in payload.recurringBills) {
                val cv = ContentValues().apply {
                    put(COL_BILL_ID, bill.id)
                    put(COL_BILL_TITLE, bill.title)
                    put(COL_BILL_AMOUNT, bill.amount)
                    put(COL_BILL_CAT_ID, bill.categoryId)
                    put(COL_BILL_CAT_NAME, bill.categoryName)
                    put(COL_BILL_CAT_ICON, bill.categoryIcon)
                    put(COL_BILL_CAT_COLOR, bill.categoryColor)
                    if (bill.accountId != null) put(COL_BILL_ACCOUNT_ID, bill.accountId) else putNull(COL_BILL_ACCOUNT_ID)
                    if (bill.accountName != null) put(COL_BILL_ACCOUNT_NAME, bill.accountName) else putNull(COL_BILL_ACCOUNT_NAME)
                    put(COL_BILL_FREQUENCY, bill.frequency.name)
                    put(COL_BILL_DUE_DAY, bill.dueDay)
                    put(COL_BILL_NEXT_DUE, bill.nextDueDateMillis)
                    if (bill.lastPaidDateMillis != null) put(COL_BILL_LAST_PAID, bill.lastPaidDateMillis) else putNull(COL_BILL_LAST_PAID)
                    put(COL_BILL_IS_ACTIVE, if (bill.isActive) 1 else 0)
                    put(COL_BILL_NOTES, bill.notes)
                }
                db.insert(TABLE_RECURRING_BILLS, null, cv)
            }
        } else if (payload.version < 4) {
            dbHelper.seedSampleRecurringBillsIfEmpty(db)
        }
    }

    fun restorePayables(db: SQLiteDatabase, payload: BackupPayload) {
        db.delete(TABLE_PAYABLES, null, null)
        for (p in payload.payables) {
            val cv = ContentValues().apply {
                put(COL_PAY_ID, p.id)
                put(COL_PAY_PERSON_NAME, p.personName)
                put(COL_PAY_PHONE, p.phoneNumber)
                put(COL_PAY_AMOUNT, p.amount)
                put(COL_PAY_AMOUNT_PAID, p.amountPaid)
                if (p.dueDateMillis != null) put(COL_PAY_DUE_DATE, p.dueDateMillis) else putNull(COL_PAY_DUE_DATE)
                put(COL_PAY_CREATED_DATE, p.createdDateMillis)
                put(COL_PAY_NOTES, p.notes)
                put(COL_PAY_STATUS, p.status.name)
            }
            db.insert(TABLE_PAYABLES, null, cv)
        }
    }

    fun restoreSavingsGoals(db: SQLiteDatabase, payload: BackupPayload) {
        db.delete(TABLE_SAVINGS_GOALS, null, null)
        for (g in payload.savingsGoals) {
            val cv = ContentValues().apply {
                put(COL_GOAL_ID, g.id)
                put(COL_GOAL_TITLE, g.title)
                put(COL_GOAL_TARGET_AMOUNT, g.targetAmount)
                put(COL_GOAL_SAVED_AMOUNT, g.savedAmount)
                if (g.targetDateMillis != null) put(COL_GOAL_TARGET_DATE, g.targetDateMillis) else putNull(COL_GOAL_TARGET_DATE)
                put(COL_GOAL_CREATED_DATE, g.createdDateMillis)
                put(COL_GOAL_ICON, g.icon)
                put(COL_GOAL_COLOR, g.colorHex)
                put(COL_GOAL_NOTES, g.notes)
                put(COL_GOAL_IS_COMPLETED, if (g.isCompleted) 1 else 0)
            }
            db.insert(TABLE_SAVINGS_GOALS, null, cv)
        }
    }

    fun restoreMarketListsAndItems(db: SQLiteDatabase, payload: BackupPayload) {
        if (payload.marketLists.isNotEmpty()) {
            db.delete(TABLE_MARKET_LISTS, null, null)
            for (ml in payload.marketLists) {
                val cv = ContentValues().apply {
                    put(COL_ML_ID, ml.id)
                    put(COL_ML_TITLE, ml.title)
                    put(COL_ML_CREATED_AT, ml.createdAt)
                    put(COL_ML_IS_COMPLETED, if (ml.isCompleted) 1 else 0)
                }
                db.insert(TABLE_MARKET_LISTS, null, cv)
            }
        }
        if (payload.marketItems.isNotEmpty()) {
            db.delete(TABLE_MARKET_ITEMS, null, null)
            for (item in payload.marketItems) {
                val cv = ContentValues().apply {
                    put(COL_MI_ID, item.id)
                    put(COL_MI_LIST_ID, item.listId)
                    put(COL_MI_NAME, item.name)
                    put(COL_MI_QUANTITY, item.quantity)
                    put(COL_MI_UNIT, item.unit)
                    if (item.amount != null) put(COL_MI_AMOUNT, item.amount) else putNull(COL_MI_AMOUNT)
                    put(COL_MI_IS_PURCHASED, if (item.isPurchased) 1 else 0)
                    if (item.transactionId != null) put(COL_MI_TRANSACTION_ID, item.transactionId) else putNull(COL_MI_TRANSACTION_ID)
                    put(COL_MI_CREATED_AT, item.createdAt)
                }
                db.insert(TABLE_MARKET_ITEMS, null, cv)
            }
        }
    }
}
