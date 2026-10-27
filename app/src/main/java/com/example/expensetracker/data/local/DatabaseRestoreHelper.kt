package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.data.model.BackupPayload
import java.util.Calendar

internal object DatabaseRestoreHelper {

    fun restore(db: SQLiteDatabase, payload: BackupPayload, dbHelper: ExpenseDatabaseHelper): Boolean {
        db.beginTransaction()
        try {
            restoreProfile(payload, dbHelper)
            restoreCategories(db, payload, dbHelper)
            restoreAccounts(db, payload, dbHelper)
            restoreTransactions(db, payload)
            restoreBudgets(db, payload)
            restoreReceivables(db, payload)
            restoreRecurringBills(db, payload, dbHelper)
            restorePayables(db, payload)
            restoreSavingsGoals(db, payload)
            restoreMarketListsAndItems(db, payload)
            restorePriceHistoryAndMasterProducts(db, payload, dbHelper)

            dbHelper.recalculateAllAccountBalances(db)
            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }

    private fun restoreProfile(payload: BackupPayload, dbHelper: ExpenseDatabaseHelper) {
        val currentProfile = dbHelper.getUserProfile()
        val effectiveGoogleEmail = currentProfile.googleAccountEmail ?: payload.profile.googleAccountEmail

        val baseProfile = if (payload.profile.firstName.isBlank() && payload.profile.name.isNotBlank()) {
            val parts = payload.profile.name.trim().split(Regex("\\s+"), limit = 2)
            payload.profile.copy(
                firstName = parts.getOrNull(0) ?: "Sabbir",
                lastName = parts.getOrNull(1) ?: ""
            )
        } else {
            payload.profile
        }
        val profileToRestore = baseProfile.copy(
            googleAccountEmail = effectiveGoogleEmail,
            lastSyncMillis = System.currentTimeMillis()
        )
        dbHelper.updateUserProfile(profileToRestore)
    }

    private fun restoreCategories(db: SQLiteDatabase, payload: BackupPayload, dbHelper: ExpenseDatabaseHelper) {
        db.delete(TABLE_CATEGORIES, null, null)
        if (payload.categories.isNotEmpty()) {
            for (cat in payload.categories) {
                val cv = ContentValues().apply {
                    put(COL_CAT_ID, cat.id)
                    put(COL_CAT_NAME, cat.name)
                    put(COL_CAT_TYPE, cat.type.name)
                    put(COL_CAT_ICON, cat.icon)
                    put(COL_CAT_COLOR, cat.colorHex)
                    put(COL_CAT_IS_DEFAULT, if (cat.isDefault) 1 else 0)
                }
                db.insert(TABLE_CATEGORIES, null, cv)
            }
        } else {
            dbHelper.seedDefaultCategories(db)
        }
        dbHelper.reconcileAndDeduplicateCategoryColors(db)
    }

    private fun restoreAccounts(db: SQLiteDatabase, payload: BackupPayload, dbHelper: ExpenseDatabaseHelper) {
        db.delete(TABLE_ACCOUNTS, null, null)
        if (payload.accounts.isNotEmpty()) {
            for (acc in payload.accounts) {
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
        } else {
            dbHelper.seedDefaultAccountsIfEmpty(db)
        }
    }

    private fun restoreTransactions(db: SQLiteDatabase, payload: BackupPayload) {
        db.delete(TABLE_TRANSACTIONS, null, null)
        for (tx in payload.transactions) {
            val cv = ContentValues().apply {
                put(COL_TX_ID, tx.id)
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

    private fun restoreBudgets(db: SQLiteDatabase, payload: BackupPayload) {
        db.delete(TABLE_BUDGETS, null, null)
        if (payload.budgets.isNotEmpty()) {
            for (b in payload.budgets) {
                val cv = ContentValues().apply {
                    put(COL_BUDGET_ID, b.id)
                    if (b.categoryId != null) put(COL_BUDGET_CAT_ID, b.categoryId) else putNull(COL_BUDGET_CAT_ID)
                    put(COL_BUDGET_CAT_NAME, b.categoryName)
                    put(COL_BUDGET_AMOUNT, b.amount)
                    put(COL_BUDGET_MONTH, b.month)
                    put(COL_BUDGET_YEAR, b.year)
                }
                db.insert(TABLE_BUDGETS, null, cv)
            }
        } else {
            val cal = Calendar.getInstance()
            val budgetCv = ContentValues().apply {
                putNull(COL_BUDGET_CAT_ID)
                put(COL_BUDGET_CAT_NAME, "Overall Monthly")
                put(COL_BUDGET_AMOUNT, 35000.0)
                put(COL_BUDGET_MONTH, cal.get(Calendar.MONTH))
                put(COL_BUDGET_YEAR, cal.get(Calendar.YEAR))
            }
            db.insert(TABLE_BUDGETS, null, budgetCv)
        }
    }

    private fun restoreReceivables(db: SQLiteDatabase, payload: BackupPayload) {
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

    private fun restoreRecurringBills(db: SQLiteDatabase, payload: BackupPayload, dbHelper: ExpenseDatabaseHelper) {
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

    private fun restorePayables(db: SQLiteDatabase, payload: BackupPayload) {
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

    private fun restoreSavingsGoals(db: SQLiteDatabase, payload: BackupPayload) {
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

    private fun restoreMarketListsAndItems(db: SQLiteDatabase, payload: BackupPayload) {
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

    private fun restorePriceHistoryAndMasterProducts(db: SQLiteDatabase, payload: BackupPayload, dbHelper: ExpenseDatabaseHelper) {
        if (payload.productPriceHistory.isNotEmpty()) {
            db.delete(TABLE_PRODUCT_PRICE_HISTORY, null, null)
            for (pph in payload.productPriceHistory) {
                val cv = ContentValues().apply {
                    put(COL_PPH_ID, pph.id)
                    put(COL_PPH_PRODUCT_NAME, pph.productName)
                    put(COL_PPH_RAW_QUANTITY, pph.rawQuantity)
                    put(COL_PPH_RAW_UNIT, pph.rawUnit)
                    put(COL_PPH_BASE_QUANTITY, pph.baseQuantity)
                    put(COL_PPH_BASE_UNIT, pph.baseUnit)
                    put(COL_PPH_TOTAL_AMOUNT, pph.totalAmount)
                    put(COL_PPH_UNIT_PRICE, pph.unitPrice)
                    if (pph.marketItemId != null) put(COL_PPH_MARKET_ITEM_ID, pph.marketItemId) else putNull(COL_PPH_MARKET_ITEM_ID)
                    if (pph.transactionId != null) put(COL_PPH_TRANSACTION_ID, pph.transactionId) else putNull(COL_PPH_TRANSACTION_ID)
                    put(COL_PPH_TIMESTAMP, pph.timestamp)
                }
                db.insert(TABLE_PRODUCT_PRICE_HISTORY, null, cv)
            }
        }

        if (payload.masterProducts.isNotEmpty()) {
            db.delete(TABLE_MASTER_PRODUCTS, null, null)
            for (mp in payload.masterProducts) {
                val cv = ContentValues().apply {
                    put(COL_MP_ID, mp.id)
                    put(COL_MP_NAME, mp.name)
                    put(COL_MP_NORMALIZED_NAME, mp.normalizedName)
                    put(COL_MP_CATEGORY, mp.category)
                    put(COL_MP_DEFAULT_UNIT, mp.defaultUnit)
                    put(COL_MP_LATEST_UNIT_PRICE, mp.latestUnitPrice)
                    if (mp.previousUnitPrice != null) put(COL_MP_PREVIOUS_UNIT_PRICE, mp.previousUnitPrice) else putNull(COL_MP_PREVIOUS_UNIT_PRICE)
                    put(COL_MP_MIN_UNIT_PRICE, mp.minUnitPrice)
                    put(COL_MP_MAX_UNIT_PRICE, mp.maxUnitPrice)
                    put(COL_MP_AVG_UNIT_PRICE, mp.avgUnitPrice)
                    put(COL_MP_PURCHASE_COUNT, mp.purchaseCount)
                    put(COL_MP_IS_STAPLE, if (mp.isStaple) 1 else 0)
                    put(COL_MP_FIRST_PURCHASED_AT, mp.firstPurchasedAt)
                    put(COL_MP_LAST_PURCHASED_AT, mp.lastPurchasedAt)
                }
                db.insert(TABLE_MASTER_PRODUCTS, null, cv)
            }
        } else {
            dbHelper.masterProductHelper.backfillFromPriceHistory(db)
            dbHelper.masterProductHelper.seedDefaultStaplesIfEmpty(db)
        }
    }
}
