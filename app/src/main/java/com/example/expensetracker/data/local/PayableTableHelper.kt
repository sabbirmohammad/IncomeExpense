package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.Cursor
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayableStatus
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType

internal class PayableTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun getAllPayables(): List<Payable> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Payable>()
        val cursor = db.query(TABLE_PAYABLES, null, null, null, null, null, "$COL_PAY_STATUS ASC, $COL_PAY_CREATED_DATE DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToPayable(c))
            }
        }
        return list
    }

    fun getPayableById(id: Long): Payable? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_PAYABLES, null, "$COL_PAY_ID = ?", arrayOf(id.toString()), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                return cursorToPayable(c)
            }
        }
        return null
    }

    fun insertPayable(payable: Payable, depositToAccountId: Long? = null): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val cv = ContentValues().apply {
                put(COL_PAY_PERSON_NAME, payable.personName)
                put(COL_PAY_PHONE, payable.phoneNumber)
                put(COL_PAY_AMOUNT, payable.amount)
                put(COL_PAY_AMOUNT_PAID, payable.amountPaid)
                if (payable.dueDateMillis != null) put(COL_PAY_DUE_DATE, payable.dueDateMillis) else putNull(COL_PAY_DUE_DATE)
                put(COL_PAY_CREATED_DATE, payable.createdDateMillis)
                put(COL_PAY_NOTES, payable.notes)
                put(COL_PAY_STATUS, payable.status.name)
            }
            val id = db.insert(TABLE_PAYABLES, null, cv)

            if (id > 0 && depositToAccountId != null) {
                val acc = dbHelper.getAccountById(depositToAccountId)
                val loanCat = dbHelper.getOrCreateCategoryByName("Loan & Debt Borrowed", TransactionType.INCOME, "Handshake", "#3B8FF0")
                val incomeTx = Transaction(
                    id = 0,
                    title = "Borrowed from ${payable.personName}",
                    amount = payable.amount,
                    type = TransactionType.INCOME,
                    categoryId = loanCat.id,
                    categoryName = loanCat.name,
                    categoryIcon = loanCat.icon,
                    categoryColor = loanCat.colorHex,
                    dateMillis = payable.createdDateMillis,
                    paymentMethod = when (acc?.type) {
                        AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                        AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                        else -> PaymentMethod.CASH
                    },
                    notes = "Loan borrowed from ${payable.personName}: ${payable.notes}",
                    accountId = depositToAccountId,
                    accountName = acc?.name
                )
                dbHelper.insertTransaction(incomeTx)
            }

            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    fun updatePayable(payable: Payable): Int {
        val db = dbHelper.writableDatabase
        val computedStatus = when {
            payable.amountPaid >= payable.amount - 0.001 -> PayableStatus.SETTLED
            payable.amountPaid > 0.0 -> PayableStatus.PARTIAL
            else -> PayableStatus.PENDING
        }
        val cv = ContentValues().apply {
            put(COL_PAY_PERSON_NAME, payable.personName)
            put(COL_PAY_PHONE, payable.phoneNumber)
            put(COL_PAY_AMOUNT, payable.amount)
            put(COL_PAY_AMOUNT_PAID, payable.amountPaid)
            if (payable.dueDateMillis != null) put(COL_PAY_DUE_DATE, payable.dueDateMillis) else putNull(COL_PAY_DUE_DATE)
            put(COL_PAY_NOTES, payable.notes)
            put(COL_PAY_STATUS, computedStatus.name)
        }
        return db.update(TABLE_PAYABLES, cv, "$COL_PAY_ID = ?", arrayOf(payable.id.toString()))
    }

    fun deletePayable(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_PAYABLES, "$COL_PAY_ID = ?", arrayOf(id.toString()))
    }

    fun recordPayablePayment(
        payableId: Long,
        amountPaid: Double,
        deductFromAccountId: Long? = null,
        note: String = ""
    ): Boolean {
        if (amountPaid <= 0.0) return false
        val pay = getPayableById(payableId) ?: return false

        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val newPaid = (pay.amountPaid + amountPaid).coerceAtMost(pay.amount)
            val isFullyPaid = newPaid >= pay.amount - 0.001
            val newStatus = if (isFullyPaid) PayableStatus.SETTLED else PayableStatus.PARTIAL

            val cv = ContentValues().apply {
                put(COL_PAY_AMOUNT_PAID, newPaid)
                put(COL_PAY_STATUS, newStatus.name)
            }
            db.update(TABLE_PAYABLES, cv, "$COL_PAY_ID = ?", arrayOf(payableId.toString()))

            val (sourceAccountId, sourceAccountName) = dbHelper.resolveAccountForTransaction(db, deductFromAccountId, PaymentMethod.CASH)
            val acc = dbHelper.getAccountById(sourceAccountId)
            val repaymentCat = dbHelper.getOrCreateCategoryByName("Debt Repayment", TransactionType.EXPENSE, "Handshake", "#E63946")
            val expenseTx = Transaction(
                id = 0,
                title = "Repaid to ${pay.personName}",
                amount = amountPaid,
                type = TransactionType.EXPENSE,
                categoryId = repaymentCat.id,
                categoryName = repaymentCat.name,
                categoryIcon = repaymentCat.icon,
                categoryColor = repaymentCat.colorHex,
                dateMillis = System.currentTimeMillis(),
                paymentMethod = when (acc?.type) {
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                    else -> PaymentMethod.CASH
                },
                notes = if (note.isNotBlank()) note else "Denar taka porishodh: ${pay.personName}",
                accountId = sourceAccountId,
                accountName = sourceAccountName
            )
            dbHelper.insertTransaction(expenseTx)

            db.setTransactionSuccessful()
            return true
        } catch (_: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }

    fun getPayablesSummary(): PayablesSummary {
        val list = getAllPayables()
        var totalBorrowed = 0.0
        var totalRepaid = 0.0
        var pendingCount = 0

        for (p in list) {
            totalBorrowed += p.amount
            totalRepaid += p.amountPaid
            if (!p.isSettled) {
                pendingCount++
            }
        }
        val remainingDue = (totalBorrowed - totalRepaid).coerceAtLeast(0.0)
        return PayablesSummary(
            totalBorrowed = totalBorrowed,
            totalRepaid = totalRepaid,
            remainingDue = remainingDue,
            pendingCount = pendingCount
        )
    }

    fun getDebtBookSummary(): DebtBookSummary {
        val recSummary = dbHelper.getReceivablesSummary()
        val paySummary = getPayablesSummary()
        return DebtBookSummary(
            totalReceivable = recSummary.remainingDue,
            totalPayable = paySummary.remainingDue
        )
    }

    private fun cursorToPayable(c: Cursor): Payable {
        val dueIndex = c.getColumnIndexOrThrow(COL_PAY_DUE_DATE)
        val dueDate = if (c.isNull(dueIndex)) null else c.getLong(dueIndex)
        val statusStr = c.getString(c.getColumnIndexOrThrow(COL_PAY_STATUS))
        val status = try {
            PayableStatus.valueOf(statusStr)
        } catch (_: Exception) {
            PayableStatus.PENDING
        }
        return Payable(
            id = c.getLong(c.getColumnIndexOrThrow(COL_PAY_ID)),
            personName = c.getString(c.getColumnIndexOrThrow(COL_PAY_PERSON_NAME)),
            phoneNumber = c.getString(c.getColumnIndexOrThrow(COL_PAY_PHONE)),
            amount = c.getDouble(c.getColumnIndexOrThrow(COL_PAY_AMOUNT)),
            amountPaid = c.getDouble(c.getColumnIndexOrThrow(COL_PAY_AMOUNT_PAID)),
            dueDateMillis = dueDate,
            createdDateMillis = c.getLong(c.getColumnIndexOrThrow(COL_PAY_CREATED_DATE)),
            notes = c.getString(c.getColumnIndexOrThrow(COL_PAY_NOTES)),
            status = status
        )
    }
}
