package com.example.expensetracker.ui.components

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StatementCsvBuilder {

    fun buildCsvContent(
        profile: UserProfile,
        transactions: List<Transaction>,
        periodTitle: String = "All Time"
    ): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("\uFEFF") // UTF-8 BOM
        sb.append("EXPENSE TRACKER FINANCIAL STATEMENT\n")
        sb.append("Period: $periodTitle\n")
        sb.append("Name: ${profile.name}\n\n")
        sb.append("ID,Date,Title,Type,Category,Amount,Currency,Account,Payment Method,Notes,Has Receipt\n")

        for (tx in transactions) {
            val dateStr = sdf.format(Date(tx.dateMillis))
            val safeTitle = tx.title.replace("\"", "\"\"")
            val safeCategory = tx.categoryName.replace("\"", "\"\"")
            val safeAccount = (tx.accountName ?: "").replace("\"", "\"\"")
            val safeNotes = tx.notes.replace("\"", "\"\"")
            val hasReceipt = if (tx.receiptImagePath != null) "Yes" else "No"

            sb.append("${tx.id},\"$dateStr\",\"$safeTitle\",${tx.type.name},\"$safeCategory\",${tx.amount},\"${profile.currencySymbol}\",\"$safeAccount\",${tx.paymentMethod.name},\"$safeNotes\",$hasReceipt\n")
        }
        return sb.toString()
    }

    fun generateCsvFile(
        context: Context,
        profile: UserProfile,
        transactions: List<Transaction>,
        periodTitle: String = "All Time"
    ): File? {
        return try {
            val reportsDir = File(context.cacheDir, "reports").apply { if (!exists()) mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val csvFile = File(reportsDir, "Statement_${timeStamp}.csv")

            val content = buildCsvContent(profile, transactions, periodTitle)

            val fos = FileOutputStream(csvFile)
            fos.write(content.toByteArray(Charsets.UTF_8))
            fos.flush()
            fos.close()

            csvFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportAndShareCsv(
        context: Context,
        profile: UserProfile,
        transactions: List<Transaction>,
        periodTitle: String = "All Time"
    ): Boolean {
        val file = generateCsvFile(context, profile, transactions, periodTitle) ?: return false
        shareFile(context, file, "text/csv", "Expense Tracker CSV Statement ($periodTitle)")
        return true
    }

    fun exportAndShareJsonBackup(
        context: Context,
        jsonPayload: String
    ): Boolean {
        return try {
            val reportsDir = File(context.cacheDir, "reports").apply { if (!exists()) mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val jsonFile = File(reportsDir, "ExpenseTracker_Backup_${timeStamp}.json")

            val fos = FileOutputStream(jsonFile)
            fos.write(jsonPayload.toByteArray(Charsets.UTF_8))
            fos.flush()
            fos.close()

            shareFile(context, jsonFile, "application/json", "Expense Tracker Backup (${timeStamp})")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun openPdf(context: Context, file: File) {
        com.example.expensetracker.common.FileShareHelper.openPdf(context, file)
    }

    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        com.example.expensetracker.common.FileShareHelper.shareFile(context, file, mimeType, title)
    }
}
