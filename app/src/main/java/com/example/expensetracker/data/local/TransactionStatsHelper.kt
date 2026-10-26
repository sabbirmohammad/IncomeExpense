package com.example.expensetracker.data.local

import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.PaymentMethodSpend
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType

internal class TransactionStatsHelper(private val dbHelper: ExpenseDatabaseHelper) {

    companion object {
        const val EXCLUDED_CATEGORIES_SQL = """
            $COL_TX_CAT_NAME NOT IN (
                'Transfer', 'Account Transfer', 'অ্যাকাউন্ট স্থানান্তর', 'স্থানান্তর',
                'Loan & Debt Given', 'ঋণ বা ধার প্রদান', 'Debt Recovery', 'পাওনা টাকা আদায়',
                'Loan & Debt Borrowed', 'ধার বা ঋণ গ্রহণ', 'Debt Repayment', 'দেনা বা ঋণ পরিশোধ',
                'Adjustment', 'Balance Adjustment', 'সমন্বয়', 'ব্যালেন্স সমন্বয়'
            )
            AND $COL_TX_NOTES NOT LIKE '[Transfer]%'
            AND $COL_TX_NOTES NOT LIKE '[Reconciliation]%'
            AND $COL_TX_TITLE NOT LIKE 'Transfer to %'
            AND $COL_TX_TITLE NOT LIKE 'Transfer from %'
        """
    }

    fun getAggregatedCategorySpends(
        startMillis: Long,
        endMillis: Long,
        type: TransactionType = TransactionType.EXPENSE
    ): List<CategorySpend> {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT 
                $COL_TX_CAT_ID,
                $COL_TX_CAT_NAME,
                $COL_TX_CAT_ICON,
                $COL_TX_CAT_COLOR,
                SUM($COL_TX_AMOUNT) AS total_amount,
                COUNT(*) AS tx_count
            FROM $TABLE_TRANSACTIONS
            WHERE $COL_TX_DATE >= ? AND $COL_TX_DATE <= ?
              AND $COL_TX_TYPE = ?
              AND $EXCLUDED_CATEGORIES_SQL
            GROUP BY $COL_TX_CAT_ID, $COL_TX_CAT_NAME, $COL_TX_CAT_ICON, $COL_TX_CAT_COLOR
            ORDER BY total_amount DESC
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(startMillis.toString(), endMillis.toString(), type.name))
        val list = mutableListOf<CategorySpend>()
        var grandTotal = 0.0

        cursor.use { c ->
            while (c.moveToNext()) {
                val catId = c.getLong(0)
                val name = c.getString(1)
                val icon = c.getString(2)
                val color = c.getString(3)
                val amount = c.getDouble(4)
                val count = c.getInt(5)
                grandTotal += amount
                list.add(
                    CategorySpend(
                        categoryId = catId,
                        categoryName = name,
                        categoryIcon = icon,
                        categoryColor = color,
                        totalAmount = amount,
                        percentage = 0f,
                        transactionCount = count
                    )
                )
            }
        }

        return if (grandTotal > 0) {
            list.map { it.copy(percentage = (it.totalAmount / grandTotal).toFloat()) }
        } else {
            list
        }
    }

    fun getAggregatedCategorySpends(timeFilter: TimeFilter, type: TransactionType = TransactionType.EXPENSE): List<CategorySpend> {
        val (start, end) = dbHelper.getTimeRange(timeFilter)
        return getAggregatedCategorySpends(start, end, type)
    }

    fun getAggregatedFinancialSummary(startMillis: Long, endMillis: Long, overallBudget: Double = 0.0): FinancialSummary {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT
                SUM(CASE WHEN $COL_TX_TYPE = 'INCOME' THEN $COL_TX_AMOUNT ELSE 0 END) AS total_income,
                SUM(CASE WHEN $COL_TX_TYPE = 'EXPENSE' THEN $COL_TX_AMOUNT ELSE 0 END) AS total_expense
            FROM $TABLE_TRANSACTIONS
            WHERE $COL_TX_DATE >= ? AND $COL_TX_DATE <= ?
              AND $EXCLUDED_CATEGORIES_SQL
        """.trimIndent()

        var totalIncome = 0.0
        var totalExpense = 0.0
        val cursor = db.rawQuery(sql, arrayOf(startMillis.toString(), endMillis.toString()))
        cursor.use { c ->
            if (c.moveToFirst()) {
                if (!c.isNull(0)) totalIncome = c.getDouble(0)
                if (!c.isNull(1)) totalExpense = c.getDouble(1)
            }
        }

        val netBalance = totalIncome - totalExpense
        val budgetRemaining = if (overallBudget > 0) (overallBudget - totalExpense).coerceAtLeast(0.0) else 0.0
        val budgetOverspent = if (overallBudget > 0 && totalExpense > overallBudget) totalExpense - overallBudget else 0.0
        val budgetPercent = if (overallBudget > 0) ((totalExpense / overallBudget).toFloat()).coerceIn(0f, 2f) else 0f

        return FinancialSummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netBalance = netBalance,
            budgetAmount = overallBudget,
            budgetSpent = totalExpense,
            budgetRemaining = budgetRemaining,
            budgetPercent = budgetPercent,
            budgetOverspent = budgetOverspent
        )
    }

    fun getAggregatedFinancialSummary(timeFilter: TimeFilter, overallBudget: Double = 0.0): FinancialSummary {
        val (start, end) = dbHelper.getTimeRange(timeFilter)
        return getAggregatedFinancialSummary(start, end, overallBudget)
    }

    fun getAggregatedPaymentMethodSpends(
        startMillis: Long,
        endMillis: Long,
        type: TransactionType = TransactionType.EXPENSE
    ): List<PaymentMethodSpend> {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT 
                $COL_TX_PAYMENT_METHOD,
                SUM($COL_TX_AMOUNT) AS total_amount,
                COUNT(*) AS tx_count
            FROM $TABLE_TRANSACTIONS
            WHERE $COL_TX_DATE >= ? AND $COL_TX_DATE <= ?
              AND $COL_TX_TYPE = ?
              AND $EXCLUDED_CATEGORIES_SQL
            GROUP BY $COL_TX_PAYMENT_METHOD
            ORDER BY total_amount DESC
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(startMillis.toString(), endMillis.toString(), type.name))
        val list = mutableListOf<PaymentMethodSpend>()
        var grandTotal = 0.0

        cursor.use { c ->
            while (c.moveToNext()) {
                val pmStr = c.getString(0)
                val method = try { PaymentMethod.valueOf(pmStr) } catch (_: Exception) { PaymentMethod.CASH }
                val amount = c.getDouble(1)
                val count = c.getInt(2)
                grandTotal += amount
                list.add(
                    PaymentMethodSpend(
                        paymentMethod = method,
                        totalAmount = amount,
                        percentage = 0f,
                        transactionCount = count
                    )
                )
            }
        }

        return if (grandTotal > 0) {
            list.map { it.copy(percentage = (it.totalAmount / grandTotal).toFloat()) }
        } else {
            list
        }
    }

    fun getAggregatedPaymentMethodSpends(timeFilter: TimeFilter, type: TransactionType = TransactionType.EXPENSE): List<PaymentMethodSpend> {
        val (start, end) = dbHelper.getTimeRange(timeFilter)
        return getAggregatedPaymentMethodSpends(start, end, type)
    }
}
