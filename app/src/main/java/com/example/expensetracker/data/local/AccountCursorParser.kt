package com.example.expensetracker.data.local

import android.database.Cursor
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType

internal object AccountCursorParser {
    fun cursorToAccount(c: Cursor): Account {
        return Account(
            id = c.getLong(c.getColumnIndexOrThrow(COL_ACC_ID)),
            name = c.getString(c.getColumnIndexOrThrow(COL_ACC_NAME)),
            type = AccountType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_ACC_TYPE))),
            accountNumber = c.getString(c.getColumnIndexOrThrow(COL_ACC_NUMBER)),
            institutionName = c.getString(c.getColumnIndexOrThrow(COL_ACC_INSTITUTION)),
            initialBalance = c.getDouble(c.getColumnIndexOrThrow(COL_ACC_INITIAL_BALANCE)),
            currentBalance = c.getDouble(c.getColumnIndexOrThrow(COL_ACC_CURRENT_BALANCE)),
            colorHex = c.getString(c.getColumnIndexOrThrow(COL_ACC_COLOR)),
            isDefault = c.getInt(c.getColumnIndexOrThrow(COL_ACC_IS_DEFAULT)) == 1,
            createdAt = c.getLong(c.getColumnIndexOrThrow(COL_ACC_CREATED_AT))
        )
    }
}
