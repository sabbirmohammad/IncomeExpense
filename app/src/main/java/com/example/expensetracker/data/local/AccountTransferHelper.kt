package com.example.expensetracker.data.local

import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType

internal object AccountTransferHelper {

    fun transferBetweenAccounts(
        dbHelper: ExpenseDatabaseHelper,
        getAccountById: (Long) -> Account?,
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        note: String = ""
    ): Boolean {
        if (amount <= 0.0 || fromAccountId == toAccountId) return false
        val fromAcc = getAccountById(fromAccountId) ?: return false
        val toAcc = getAccountById(toAccountId) ?: return false

        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val now = System.currentTimeMillis()
            val transferNote = if (note.isNotBlank()) "[Transfer] $note" else "[Transfer] Fund Transfer"
            val outCategory = dbHelper.getOrCreateCategoryByName("Transfer", TransactionType.EXPENSE, "SwapHoriz", "#6C5CE7")
            val inCategory = dbHelper.getOrCreateCategoryByName("Transfer", TransactionType.INCOME, "SwapHoriz", "#6C5CE7")

            val expenseTx = Transaction(
                id = 0,
                title = "Transfer to ${toAcc.name}",
                amount = amount,
                type = TransactionType.EXPENSE,
                categoryId = outCategory.id,
                categoryName = outCategory.name,
                categoryIcon = outCategory.icon,
                categoryColor = outCategory.colorHex,
                dateMillis = now,
                paymentMethod = when (fromAcc.type) {
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> if (fromAcc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                    AccountType.CASH -> PaymentMethod.CASH
                },
                notes = transferNote,
                accountId = fromAccountId,
                accountName = fromAcc.name
            )
            dbHelper.insertTransaction(expenseTx)

            val incomeTx = Transaction(
                id = 0,
                title = "Transfer from ${fromAcc.name}",
                amount = amount,
                type = TransactionType.INCOME,
                categoryId = inCategory.id,
                categoryName = inCategory.name,
                categoryIcon = inCategory.icon,
                categoryColor = inCategory.colorHex,
                dateMillis = now,
                paymentMethod = when (toAcc.type) {
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> if (toAcc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                    AccountType.CASH -> PaymentMethod.CASH
                },
                notes = transferNote,
                accountId = toAccountId,
                accountName = toAcc.name
            )
            dbHelper.insertTransaction(incomeTx)

            dbHelper.recalculateAccountBalance(db, fromAccountId)
            dbHelper.recalculateAccountBalance(db, toAccountId)

            db.setTransactionSuccessful()
            return true
        } catch (_: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }
}
