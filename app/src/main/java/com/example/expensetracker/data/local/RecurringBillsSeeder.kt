package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

internal object RecurringBillsSeeder {
    fun seedSampleRecurringBillsIfEmpty(db: SQLiteDatabase) {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_RECURRING_BILLS", null)
        var count = 0
        cursor.use { if (it.moveToFirst()) count = it.getInt(0) }
        if (count == 0) {
            val now = System.currentTimeMillis()
            val cvWifi = ContentValues().apply {
                put(COL_BILL_TITLE, "WiFi Internet Bill")
                put(COL_BILL_AMOUNT, 1000.0)
                put(COL_BILL_CAT_ID, 4L)
                put(COL_BILL_CAT_NAME, "Bills & Utilities")
                put(COL_BILL_CAT_ICON, "Receipt")
                put(COL_BILL_CAT_COLOR, "#F0A63D")
                put(COL_BILL_ACCOUNT_ID, 3L)
                put(COL_BILL_ACCOUNT_NAME, "bKash Personal")
                put(COL_BILL_FREQUENCY, "MONTHLY")
                put(COL_BILL_DUE_DAY, 5)
                put(COL_BILL_NEXT_DUE, now + (3L * 24 * 3600 * 1000))
                put(COL_BILL_IS_ACTIVE, 1)
                put(COL_BILL_NOTES, "Dot Internet Fiber Monthly")
            }
            db.insert(TABLE_RECURRING_BILLS, null, cvWifi)

            val cvRent = ContentValues().apply {
                put(COL_BILL_TITLE, "House Rent / বাড়ি ভাড়া")
                put(COL_BILL_AMOUNT, 15000.0)
                put(COL_BILL_CAT_ID, 4L)
                put(COL_BILL_CAT_NAME, "Bills & Utilities")
                put(COL_BILL_CAT_ICON, "Receipt")
                put(COL_BILL_CAT_COLOR, "#3B8FF0")
                put(COL_BILL_ACCOUNT_ID, 2L)
                put(COL_BILL_ACCOUNT_NAME, "Bank Account")
                put(COL_BILL_FREQUENCY, "MONTHLY")
                put(COL_BILL_DUE_DAY, 7)
                put(COL_BILL_NEXT_DUE, now + (5L * 24 * 3600 * 1000))
                put(COL_BILL_IS_ACTIVE, 1)
                put(COL_BILL_NOTES, "Apartment Monthly Rent")
            }
            db.insert(TABLE_RECURRING_BILLS, null, cvRent)
        }
    }
}
