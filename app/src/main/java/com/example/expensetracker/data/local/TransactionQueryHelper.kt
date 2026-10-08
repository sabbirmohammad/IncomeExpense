package com.example.expensetracker.data.local

import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import java.util.Calendar

internal class TransactionQueryHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun getTimeRange(filter: TimeFilter): Pair<Long, Long> {
        return when (filter) {
            TimeFilter.TODAY -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.THIS_WEEK -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                cal.add(Calendar.DAY_OF_WEEK, 6)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.THIS_MONTH -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.THIS_YEAR -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.ALL, TimeFilter.CUSTOM -> Pair(0L, Long.MAX_VALUE)
        }
    }

    fun getFilteredTransactions(
        timeFilter: TimeFilter = TimeFilter.ALL,
        type: TransactionType? = null,
        categoryId: Long? = null,
        query: String? = null
    ): List<Transaction> {
        val db = dbHelper.readableDatabase
        val whereClauses = mutableListOf<String>()
        val args = mutableListOf<String>()

        if (type != null) {
            whereClauses.add("$COL_TX_TYPE = ?")
            args.add(type.name)
        }
        if (categoryId != null && categoryId > 0) {
            whereClauses.add("$COL_TX_CAT_ID = ?")
            args.add(categoryId.toString())
        }
        if (!query.isNullOrBlank()) {
            whereClauses.add("($COL_TX_TITLE LIKE ? OR $COL_TX_NOTES LIKE ? OR $COL_TX_CAT_NAME LIKE ?)")
            val wildcard = "%$query%"
            args.add(wildcard)
            args.add(wildcard)
            args.add(wildcard)
        }

        val (startMillis, endMillis) = getTimeRange(timeFilter)
        if (startMillis > 0) {
            whereClauses.add("$COL_TX_DATE >= ? AND $COL_TX_DATE <= ?")
            args.add(startMillis.toString())
            args.add(endMillis.toString())
        }

        val where = if (whereClauses.isNotEmpty()) whereClauses.joinToString(" AND ") else null
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            where,
            if (args.isNotEmpty()) args.toTypedArray() else null,
            null,
            null,
            "$COL_TX_DATE DESC, $COL_TX_ID DESC"
        )
        val list = mutableListOf<Transaction>()
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(dbHelper.cursorToTransaction(c))
            }
        }
        return list
    }

    fun getTransactionsBetween(
        startMillis: Long,
        endMillis: Long,
        type: TransactionType? = null,
        categoryId: Long? = null,
        query: String? = null
    ): List<Transaction> {
        val db = dbHelper.readableDatabase
        val whereClauses = mutableListOf<String>()
        val args = mutableListOf<String>()

        if (type != null) {
            whereClauses.add("$COL_TX_TYPE = ?")
            args.add(type.name)
        }
        if (categoryId != null && categoryId > 0) {
            whereClauses.add("$COL_TX_CAT_ID = ?")
            args.add(categoryId.toString())
        }
        if (!query.isNullOrBlank()) {
            whereClauses.add("($COL_TX_TITLE LIKE ? OR $COL_TX_NOTES LIKE ? OR $COL_TX_CAT_NAME LIKE ?)")
            val wildcard = "%$query%"
            args.add(wildcard)
            args.add(wildcard)
            args.add(wildcard)
        }

        whereClauses.add("$COL_TX_DATE >= ? AND $COL_TX_DATE <= ?")
        args.add(startMillis.toString())
        args.add(endMillis.toString())

        val where = whereClauses.joinToString(" AND ")
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            where,
            args.toTypedArray(),
            null,
            null,
            "$COL_TX_DATE DESC, $COL_TX_ID DESC"
        )
        val list = mutableListOf<Transaction>()
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(dbHelper.cursorToTransaction(c))
            }
        }
        return list
    }
}
