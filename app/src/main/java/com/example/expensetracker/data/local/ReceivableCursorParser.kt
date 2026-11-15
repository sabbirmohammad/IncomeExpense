package com.example.expensetracker.data.local

import android.database.Cursor
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivableStatus

internal fun Cursor.toReceivable(): Receivable {
    val dueIndex = getColumnIndexOrThrow(COL_REC_DUE_DATE)
    val dueDate = if (isNull(dueIndex)) null else getLong(dueIndex)
    val statusStr = getString(getColumnIndexOrThrow(COL_REC_STATUS))
    val status = try {
        ReceivableStatus.valueOf(statusStr)
    } catch (_: Exception) {
        ReceivableStatus.PENDING
    }

    return Receivable(
        id = getLong(getColumnIndexOrThrow(COL_REC_ID)),
        personName = getString(getColumnIndexOrThrow(COL_REC_PERSON_NAME)),
        phoneNumber = getString(getColumnIndexOrThrow(COL_REC_PHONE)),
        amount = getDouble(getColumnIndexOrThrow(COL_REC_AMOUNT)),
        amountReceived = getDouble(getColumnIndexOrThrow(COL_REC_AMOUNT_RECEIVED)),
        dueDateMillis = dueDate,
        createdDateMillis = getLong(getColumnIndexOrThrow(COL_REC_CREATED_DATE)),
        notes = getString(getColumnIndexOrThrow(COL_REC_NOTES)),
        status = status
    )
}
