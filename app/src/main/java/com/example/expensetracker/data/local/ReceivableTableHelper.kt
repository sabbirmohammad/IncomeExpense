package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.Cursor
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivableStatus
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType

internal class ReceivableTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun getAllReceivables(): List<Receivable> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Receivable>()
        val cursor = db.query(TABLE_RECEIVABLES, null, null, null, null, null, "$COL_REC_STATUS ASC, $COL_REC_CREATED_DATE DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToReceivable(c))
            }
        }
        return list
    }

    fun getReceivableById(id: Long): Receivable? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_RECEIVABLES, null, "$COL_REC_ID = ?", arrayOf(id.toString()), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                return cursorToReceivable(c)
            }
        }
        return null
    }

    fun insertReceivable(receivable: Receivable, deductFromAccountId: Long? = null): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val cv = ContentValues().apply {
                put(COL_REC_PERSON_NAME, receivable.personName)
                put(COL_REC_PHONE, receivable.phoneNumber)
                put(COL_REC_AMOUNT, receivable.amount)
                put(COL_REC_AMOUNT_RECEIVED, receivable.amountReceived)
                if (receivable.dueDateMillis != null) put(COL_REC_DUE_DATE, receivable.dueDateMillis) else putNull(COL_REC_DUE_DATE)
                put(COL_REC_CREATED_DATE, receivable.createdDateMillis)
                put(COL_REC_NOTES, receivable.notes)
                put(COL_REC_STATUS, receivable.status.name)
            }
            val id = db.insert(TABLE_RECEIVABLES, null, cv)

            if (id > 0 && deductFromAccountId != null) {
                val acc = dbHelper.getAccountById(deductFromAccountId)
                val loanCat = dbHelper.getOrCreateCategoryByName("Loan & Debt Given", TransactionType.EXPENSE, "Handshake", "#FF758F")
                val expenseTx = Transaction(
                    id = 0,
                    title = "Lent to ${receivable.personName}",
                    amount = receivable.amount,
                    type = TransactionType.EXPENSE,
                    categoryId = loanCat.id,
                    categoryName = loanCat.name,
                    categoryIcon = loanCat.icon,
                    categoryColor = loanCat.colorHex,
                    dateMillis = receivable.createdDateMillis,
                    paymentMethod = when (acc?.type) {
                        AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                        AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                        else -> PaymentMethod.CASH
                    },
                    notes = "Money owed by ${receivable.personName}: ${receivable.notes}",
                    accountId = deductFromAccountId,
                    accountName = acc?.name
                )
                dbHelper.insertTransaction(expenseTx)
            }

            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    fun updateReceivable(receivable: Receivable): Int {
        val db = dbHelper.writableDatabase
        val computedStatus = when {
            receivable.amountReceived >= receivable.amount - 0.001 -> ReceivableStatus.SETTLED
            receivable.amountReceived > 0.0 -> ReceivableStatus.PARTIAL
            else -> ReceivableStatus.PENDING
        }
        val cv = ContentValues().apply {
            put(COL_REC_PERSON_NAME, receivable.personName)
            put(COL_REC_PHONE, receivable.phoneNumber)
            put(COL_REC_AMOUNT, receivable.amount)
            put(COL_REC_AMOUNT_RECEIVED, receivable.amountReceived)
            if (receivable.dueDateMillis != null) put(COL_REC_DUE_DATE, receivable.dueDateMillis) else putNull(COL_REC_DUE_DATE)
            put(COL_REC_NOTES, receivable.notes)
            put(COL_REC_STATUS, computedStatus.name)
        }
        return db.update(TABLE_RECEIVABLES, cv, "$COL_REC_ID = ?", arrayOf(receivable.id.toString()))
    }

    fun deleteReceivable(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_RECEIVABLES, "$COL_REC_ID = ?", arrayOf(id.toString()))
    }

    fun recordReceivablePayment(
        receivableId: Long,
        amountPaid: Double,
        depositToAccountId: Long? = null,
        note: String = ""
    ): Boolean {
        if (amountPaid <= 0.0) return false
        val rec = getReceivableById(receivableId) ?: return false

        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val remaining = (rec.amount - rec.amountReceived).coerceAtLeast(0.0)
            val actualPaid = amountPaid.coerceAtMost(remaining)
            if (actualPaid <= 0.0) return false

            val newReceived = (rec.amountReceived + actualPaid).coerceAtMost(rec.amount)
            val isFullyPaid = newReceived >= rec.amount - 0.001
            val newStatus = if (isFullyPaid) ReceivableStatus.SETTLED else ReceivableStatus.PARTIAL

            val cv = ContentValues().apply {
                put(COL_REC_AMOUNT_RECEIVED, newReceived)
                put(COL_REC_STATUS, newStatus.name)
            }
            db.update(TABLE_RECEIVABLES, cv, "$COL_REC_ID = ?", arrayOf(receivableId.toString()))

            val (targetAccountId, targetAccountName) = dbHelper.resolveAccountForTransaction(db, depositToAccountId, PaymentMethod.CASH)
            val acc = dbHelper.getAccountById(targetAccountId)
            val recoveryCat = dbHelper.getOrCreateCategoryByName("Debt Recovery", TransactionType.INCOME, "Handshake", "#2EC4B6")
            val incomeTx = Transaction(
                id = 0,
                title = "Repayment from ${rec.personName}",
                amount = actualPaid,
                type = TransactionType.INCOME,
                categoryId = recoveryCat.id,
                categoryName = recoveryCat.name,
                categoryIcon = recoveryCat.icon,
                categoryColor = recoveryCat.colorHex,
                dateMillis = System.currentTimeMillis(),
                paymentMethod = when (acc?.type) {
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                    else -> PaymentMethod.CASH
                },
                notes = if (note.isNotBlank()) note else "Pawnar taka ferot: ${rec.personName}",
                accountId = targetAccountId,
                accountName = targetAccountName
            )
            dbHelper.insertTransaction(incomeTx)

            db.setTransactionSuccessful()
            return true
        } catch (_: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }

    fun getReceivablesSummary(): ReceivablesSummary {
        val list = getAllReceivables()
        var totalLent = 0.0
        var totalCollected = 0.0
        var pendingCount = 0

        for (r in list) {
            totalLent += r.amount
            totalCollected += r.amountReceived
            if (!r.isSettled) {
                pendingCount++
            }
        }
        val remainingDue = (totalLent - totalCollected).coerceAtLeast(0.0)
        return ReceivablesSummary(
            totalLent = totalLent,
            totalCollected = totalCollected,
            remainingDue = remainingDue,
            pendingCount = pendingCount
        )
    }

    private fun cursorToReceivable(c: Cursor): Receivable {
        val dueIndex = c.getColumnIndexOrThrow(COL_REC_DUE_DATE)
        val dueDate = if (c.isNull(dueIndex)) null else c.getLong(dueIndex)
        val statusStr = c.getString(c.getColumnIndexOrThrow(COL_REC_STATUS))
        val status = try {
            ReceivableStatus.valueOf(statusStr)
        } catch (_: Exception) {
            ReceivableStatus.PENDING
        }

        return Receivable(
            id = c.getLong(c.getColumnIndexOrThrow(COL_REC_ID)),
            personName = c.getString(c.getColumnIndexOrThrow(COL_REC_PERSON_NAME)),
            phoneNumber = c.getString(c.getColumnIndexOrThrow(COL_REC_PHONE)),
            amount = c.getDouble(c.getColumnIndexOrThrow(COL_REC_AMOUNT)),
            amountReceived = c.getDouble(c.getColumnIndexOrThrow(COL_REC_AMOUNT_RECEIVED)),
            dueDateMillis = dueDate,
            createdDateMillis = c.getLong(c.getColumnIndexOrThrow(COL_REC_CREATED_DATE)),
            notes = c.getString(c.getColumnIndexOrThrow(COL_REC_NOTES)),
            status = status
        )
    }
}
