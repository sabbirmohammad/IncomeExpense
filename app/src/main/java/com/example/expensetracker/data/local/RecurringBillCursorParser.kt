package com.example.expensetracker.data.local

import android.database.Cursor
import com.example.expensetracker.data.model.BillFrequency
import com.example.expensetracker.data.model.RecurringBill

internal fun Cursor.toRecurringBill(): RecurringBill {
    val accIdCol = getColumnIndex(COL_BILL_ACCOUNT_ID)
    val accNameCol = getColumnIndex(COL_BILL_ACCOUNT_NAME)
    val lastPaidCol = getColumnIndex(COL_BILL_LAST_PAID)

    val freqStr = getString(getColumnIndexOrThrow(COL_BILL_FREQUENCY))
    val frequency = try {
        BillFrequency.valueOf(freqStr)
    } catch (_: Exception) {
        BillFrequency.MONTHLY
    }

    return RecurringBill(
        id = getLong(getColumnIndexOrThrow(COL_BILL_ID)),
        title = getString(getColumnIndexOrThrow(COL_BILL_TITLE)),
        amount = getDouble(getColumnIndexOrThrow(COL_BILL_AMOUNT)),
        categoryId = getLong(getColumnIndexOrThrow(COL_BILL_CAT_ID)),
        categoryName = getString(getColumnIndexOrThrow(COL_BILL_CAT_NAME)),
        categoryIcon = getString(getColumnIndexOrThrow(COL_BILL_CAT_ICON)),
        categoryColor = getString(getColumnIndexOrThrow(COL_BILL_CAT_COLOR)),
        accountId = if (accIdCol != -1 && !isNull(accIdCol)) getLong(accIdCol) else null,
        accountName = if (accNameCol != -1 && !isNull(accNameCol)) getString(accNameCol) else null,
        frequency = frequency,
        dueDay = getInt(getColumnIndexOrThrow(COL_BILL_DUE_DAY)),
        nextDueDateMillis = getLong(getColumnIndexOrThrow(COL_BILL_NEXT_DUE)),
        lastPaidDateMillis = if (lastPaidCol != -1 && !isNull(lastPaidCol)) getLong(lastPaidCol) else null,
        isActive = getInt(getColumnIndexOrThrow(COL_BILL_IS_ACTIVE)) == 1,
        notes = getString(getColumnIndexOrThrow(COL_BILL_NOTES))
    )
}
