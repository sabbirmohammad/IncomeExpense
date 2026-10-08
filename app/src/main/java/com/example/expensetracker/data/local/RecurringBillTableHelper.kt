package com.example.expensetracker.data.local

import android.content.ContentValues
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.BillFrequency
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.TransactionType
import java.util.Calendar

internal class RecurringBillTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun insertRecurringBill(bill: RecurringBill): Long {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
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
        return db.insert(TABLE_RECURRING_BILLS, null, cv)
    }

    fun updateRecurringBill(bill: RecurringBill): Int {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
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
        return db.update(TABLE_RECURRING_BILLS, cv, "$COL_BILL_ID = ?", arrayOf(bill.id.toString()))
    }

    fun deleteRecurringBill(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_RECURRING_BILLS, "$COL_BILL_ID = ?", arrayOf(id.toString()))
    }

    fun getAllRecurringBills(): List<RecurringBill> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<RecurringBill>()
        val cursor = db.query(TABLE_RECURRING_BILLS, null, null, null, null, null, "$COL_BILL_NEXT_DUE ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                val accIdCol = c.getColumnIndex(COL_BILL_ACCOUNT_ID)
                val accNameCol = c.getColumnIndex(COL_BILL_ACCOUNT_NAME)
                val lastPaidCol = c.getColumnIndex(COL_BILL_LAST_PAID)

                val freqStr = c.getString(c.getColumnIndexOrThrow(COL_BILL_FREQUENCY))
                val frequency = try {
                    BillFrequency.valueOf(freqStr)
                } catch (_: Exception) {
                    BillFrequency.MONTHLY
                }

                list.add(
                    RecurringBill(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_BILL_ID)),
                        title = c.getString(c.getColumnIndexOrThrow(COL_BILL_TITLE)),
                        amount = c.getDouble(c.getColumnIndexOrThrow(COL_BILL_AMOUNT)),
                        categoryId = c.getLong(c.getColumnIndexOrThrow(COL_BILL_CAT_ID)),
                        categoryName = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_NAME)),
                        categoryIcon = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_ICON)),
                        categoryColor = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_COLOR)),
                        accountId = if (accIdCol != -1 && !c.isNull(accIdCol)) c.getLong(accIdCol) else null,
                        accountName = if (accNameCol != -1 && !c.isNull(accNameCol)) c.getString(accNameCol) else null,
                        frequency = frequency,
                        dueDay = c.getInt(c.getColumnIndexOrThrow(COL_BILL_DUE_DAY)),
                        nextDueDateMillis = c.getLong(c.getColumnIndexOrThrow(COL_BILL_NEXT_DUE)),
                        lastPaidDateMillis = if (lastPaidCol != -1 && !c.isNull(lastPaidCol)) c.getLong(lastPaidCol) else null,
                        isActive = c.getInt(c.getColumnIndexOrThrow(COL_BILL_IS_ACTIVE)) == 1,
                        notes = c.getString(c.getColumnIndexOrThrow(COL_BILL_NOTES))
                    )
                )
            }
        }
        return list
    }

    fun markBillAsPaid(billId: Long, paymentAccountId: Long?, paymentAccountName: String?): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            var bill: RecurringBill? = null
            val cursor = db.query(TABLE_RECURRING_BILLS, null, "$COL_BILL_ID = ?", arrayOf(billId.toString()), null, null, null)
            cursor.use { c ->
                if (c.moveToFirst()) {
                    val accIdCol = c.getColumnIndex(COL_BILL_ACCOUNT_ID)
                    val accNameCol = c.getColumnIndex(COL_BILL_ACCOUNT_NAME)
                    val lastPaidCol = c.getColumnIndex(COL_BILL_LAST_PAID)
                    val freqStr = c.getString(c.getColumnIndexOrThrow(COL_BILL_FREQUENCY))
                    val frequency = try { BillFrequency.valueOf(freqStr) } catch (_: Exception) { BillFrequency.MONTHLY }
                    bill = RecurringBill(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_BILL_ID)),
                        title = c.getString(c.getColumnIndexOrThrow(COL_BILL_TITLE)),
                        amount = c.getDouble(c.getColumnIndexOrThrow(COL_BILL_AMOUNT)),
                        categoryId = c.getLong(c.getColumnIndexOrThrow(COL_BILL_CAT_ID)),
                        categoryName = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_NAME)),
                        categoryIcon = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_ICON)),
                        categoryColor = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_COLOR)),
                        accountId = if (accIdCol != -1 && !c.isNull(accIdCol)) c.getLong(accIdCol) else null,
                        accountName = if (accNameCol != -1 && !c.isNull(accNameCol)) c.getString(accNameCol) else null,
                        frequency = frequency,
                        dueDay = c.getInt(c.getColumnIndexOrThrow(COL_BILL_DUE_DAY)),
                        nextDueDateMillis = c.getLong(c.getColumnIndexOrThrow(COL_BILL_NEXT_DUE)),
                        lastPaidDateMillis = if (lastPaidCol != -1 && !c.isNull(lastPaidCol)) c.getLong(lastPaidCol) else null,
                        isActive = c.getInt(c.getColumnIndexOrThrow(COL_BILL_IS_ACTIVE)) == 1,
                        notes = c.getString(c.getColumnIndexOrThrow(COL_BILL_NOTES))
                    )
                }
            }

            val b = bill ?: return -1L

            val (effectiveAccountId, effectiveAccountName) = dbHelper.resolveAccountForTransaction(db, paymentAccountId ?: b.accountId, null)
            val acc = dbHelper.getAccountById(effectiveAccountId)
            val payMethod = when (acc?.type) {
                AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                else -> PaymentMethod.CASH
            }

            val txCv = ContentValues().apply {
                put(COL_TX_TITLE, "Bill: ${b.title}")
                put(COL_TX_AMOUNT, b.amount)
                put(COL_TX_TYPE, TransactionType.EXPENSE.name)
                put(COL_TX_CAT_ID, b.categoryId)
                put(COL_TX_CAT_NAME, b.categoryName)
                put(COL_TX_CAT_ICON, b.categoryIcon)
                put(COL_TX_CAT_COLOR, b.categoryColor)
                put(COL_TX_DATE, System.currentTimeMillis())
                put(COL_TX_PAYMENT_METHOD, payMethod.name)
                put(COL_TX_NOTES, "Paid via Recurring Bills tracker")
                put(COL_TX_ACCOUNT_ID, effectiveAccountId)
                put(COL_TX_ACCOUNT_NAME, effectiveAccountName)
            }
            val txId = db.insert(TABLE_TRANSACTIONS, null, txCv)

            val cal = Calendar.getInstance()
            val baseTime = if (b.nextDueDateMillis > 0) b.nextDueDateMillis else System.currentTimeMillis()
            cal.timeInMillis = baseTime

            when (b.frequency) {
                BillFrequency.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, 1)
                BillFrequency.YEARLY -> {
                    cal.add(Calendar.YEAR, 1)
                    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    cal.set(Calendar.DAY_OF_MONTH, b.dueDay.coerceIn(1, maxDay))
                }
                BillFrequency.MONTHLY -> {
                    cal.add(Calendar.MONTH, 1)
                    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    cal.set(Calendar.DAY_OF_MONTH, b.dueDay.coerceIn(1, maxDay))
                }
            }
            cal.set(Calendar.HOUR_OF_DAY, 9)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            val now = System.currentTimeMillis()
            while (cal.timeInMillis <= now && b.frequency == BillFrequency.MONTHLY) {
                cal.add(Calendar.MONTH, 1)
                val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                cal.set(Calendar.DAY_OF_MONTH, b.dueDay.coerceIn(1, maxDay))
            }

            val billCv = ContentValues().apply {
                put(COL_BILL_LAST_PAID, System.currentTimeMillis())
                put(COL_BILL_NEXT_DUE, cal.timeInMillis)
            }
            db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_ID = ?", arrayOf(billId.toString()))

            dbHelper.recalculateAccountBalance(db, effectiveAccountId)

            db.setTransactionSuccessful()
            return txId
        } finally {
            db.endTransaction()
        }
    }
}
