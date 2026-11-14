package com.example.expensetracker.data.local

import android.database.Cursor
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayableStatus

internal fun Cursor.toPayable(): Payable {
    val dueIndex = getColumnIndexOrThrow(COL_PAY_DUE_DATE)
    val dueDate = if (isNull(dueIndex)) null else getLong(dueIndex)
    val statusStr = getString(getColumnIndexOrThrow(COL_PAY_STATUS))
    val status = try {
        PayableStatus.valueOf(statusStr)
    } catch (_: Exception) {
        PayableStatus.PENDING
    }
    return Payable(
        id = getLong(getColumnIndexOrThrow(COL_PAY_ID)),
        personName = getString(getColumnIndexOrThrow(COL_PAY_PERSON_NAME)),
        phoneNumber = getString(getColumnIndexOrThrow(COL_PAY_PHONE)),
        amount = getDouble(getColumnIndexOrThrow(COL_PAY_AMOUNT)),
        amountPaid = getDouble(getColumnIndexOrThrow(COL_PAY_AMOUNT_PAID)),
        dueDateMillis = dueDate,
        createdDateMillis = getLong(getColumnIndexOrThrow(COL_PAY_CREATED_DATE)),
        notes = getString(getColumnIndexOrThrow(COL_PAY_NOTES)),
        status = status
    )
}
