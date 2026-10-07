package com.example.expensetracker.ui.components

import android.content.Context
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import java.io.File

object StatementExportHelper {

    fun generatePdfFile(
        context: Context,
        profile: UserProfile,
        summary: FinancialSummary,
        transactions: List<Transaction>,
        periodTitle: String = "All Time",
        categorySpends: List<CategorySpend> = emptyList()
    ): File? {
        return StatementPdfBuilder.generatePdfFile(context, profile, summary, transactions, periodTitle, categorySpends)
    }

    fun exportAndSharePdf(
        context: Context,
        profile: UserProfile,
        summary: FinancialSummary,
        transactions: List<Transaction>,
        periodTitle: String = "All Time",
        categorySpends: List<CategorySpend> = emptyList()
    ): Boolean {
        val file = generatePdfFile(context, profile, summary, transactions, periodTitle, categorySpends) ?: return false
        sharePdf(context, file, periodTitle)
        return true
    }

    fun openPdf(context: Context, file: File) {
        StatementCsvBuilder.openPdf(context, file)
    }

    fun sharePdf(context: Context, file: File, periodTitle: String = "All Time") {
        StatementCsvBuilder.shareFile(context, file, "application/pdf", "Expense Tracker Statement ($periodTitle)")
    }

    fun generateCsvFile(
        context: Context,
        profile: UserProfile,
        transactions: List<Transaction>,
        periodTitle: String = "All Time"
    ): File? {
        return StatementCsvBuilder.generateCsvFile(context, profile, transactions, periodTitle)
    }

    fun exportAndShareCsv(
        context: Context,
        profile: UserProfile,
        transactions: List<Transaction>,
        periodTitle: String = "All Time"
    ): Boolean {
        return StatementCsvBuilder.exportAndShareCsv(context, profile, transactions, periodTitle)
    }

    fun shareCsv(context: Context, file: File, periodTitle: String = "All Time") {
        StatementCsvBuilder.shareFile(context, file, "text/csv", "Expense Tracker CSV Statement ($periodTitle)")
    }

    fun exportAndShareJsonBackup(
        context: Context,
        jsonPayload: String
    ): Boolean {
        return StatementCsvBuilder.exportAndShareJsonBackup(context, jsonPayload)
    }
}
