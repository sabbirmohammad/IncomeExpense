package com.example.expensetracker.data.local

import android.content.ContentValues
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.BillSplitData
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.ReceivableStatus
import com.example.expensetracker.data.model.TransactionType

internal class BillSplitHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun executeBillSplit(splitData: BillSplitData): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val now = System.currentTimeMillis()
            val (payingAccId, payingAccName) = dbHelper.resolveAccountForTransaction(db, splitData.payingAccountId, PaymentMethod.CASH)
            val payingAcc = dbHelper.getAccountById(payingAccId)
            val payMethod = when (payingAcc?.type) {
                AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                AccountType.MFS -> if (payingAcc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                else -> PaymentMethod.CASH
            }

            // 1. Insert Expense Transaction for user's personal share
            val txCv = ContentValues().apply {
                put(COL_TX_TITLE, if (splitData.note.isNotBlank()) splitData.note else "Split Bill (My Share)")
                put(COL_TX_AMOUNT, splitData.myShare)
                put(COL_TX_TYPE, TransactionType.EXPENSE.name)
                put(COL_TX_CAT_ID, splitData.categoryId)
                put(COL_TX_CAT_NAME, splitData.categoryName)
                put(COL_TX_CAT_ICON, "Restaurant")
                put(COL_TX_CAT_COLOR, "#FF6B6B")
                put(COL_TX_DATE, now)
                put(COL_TX_PAYMENT_METHOD, payMethod.name)
                put(COL_TX_NOTES, "Bill Split: Total ৳${splitData.totalAmount}, My share ৳${splitData.myShare}")
                put(COL_TX_ACCOUNT_ID, payingAccId)
                put(COL_TX_ACCOUNT_NAME, payingAccName)
            }
            db.insert(TABLE_TRANSACTIONS, null, txCv)

            // 2. Insert Receivables for each friend and an expense for the lent amount if paying from account
            val friendLoanCat = dbHelper.getOrCreateCategoryByName("Loan & Debt Given", TransactionType.EXPENSE, "Handshake", "#FF758F")
            for (friend in splitData.friends) {
                if (friend.name.isNotBlank() && friend.amount > 0.0) {
                    val recCv = ContentValues().apply {
                        put(COL_REC_PERSON_NAME, friend.name.trim())
                        put(COL_REC_PHONE, friend.phoneNumber.trim())
                        put(COL_REC_AMOUNT, friend.amount)
                        put(COL_REC_AMOUNT_RECEIVED, 0.0)
                        put(COL_REC_CREATED_DATE, now)
                        put(COL_REC_NOTES, "Bill Split share: ${splitData.note.ifBlank { "Group Bill" }}")
                        put(COL_REC_STATUS, ReceivableStatus.PENDING.name)
                    }
                    db.insert(TABLE_RECEIVABLES, null, recCv)

                    val lentCv = ContentValues().apply {
                        put(COL_TX_TITLE, "Bill Split Lent to ${friend.name.trim()}")
                        put(COL_TX_AMOUNT, friend.amount)
                        put(COL_TX_TYPE, TransactionType.EXPENSE.name)
                        put(COL_TX_CAT_ID, friendLoanCat.id)
                        put(COL_TX_CAT_NAME, friendLoanCat.name)
                        put(COL_TX_CAT_ICON, friendLoanCat.icon)
                        put(COL_TX_CAT_COLOR, friendLoanCat.colorHex)
                        put(COL_TX_DATE, now)
                        put(COL_TX_PAYMENT_METHOD, payMethod.name)
                        put(COL_TX_NOTES, "Lent via Bill Split")
                        put(COL_TX_ACCOUNT_ID, payingAccId)
                        put(COL_TX_ACCOUNT_NAME, payingAccName)
                    }
                    db.insert(TABLE_TRANSACTIONS, null, lentCv)
                }
            }

            // 3. Recalculate paying account balance
            dbHelper.recalculateAccountBalance(db, payingAccId)

            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }
}
