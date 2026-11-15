package com.example.expensetracker.data.local

import android.database.Cursor
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType

internal fun Cursor.toCategory(): Category {
    return Category(
        id = getLong(getColumnIndexOrThrow(COL_CAT_ID)),
        name = getString(getColumnIndexOrThrow(COL_CAT_NAME)),
        type = TransactionType.valueOf(getString(getColumnIndexOrThrow(COL_CAT_TYPE))),
        icon = getString(getColumnIndexOrThrow(COL_CAT_ICON)),
        colorHex = getString(getColumnIndexOrThrow(COL_CAT_COLOR)),
        isDefault = getInt(getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
    )
}
