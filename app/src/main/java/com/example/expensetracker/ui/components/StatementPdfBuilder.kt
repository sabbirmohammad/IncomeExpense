package com.example.expensetracker.ui.components

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StatementPdfBuilder {

    fun generatePdfFile(
        context: Context,
        profile: UserProfile,
        summary: FinancialSummary,
        transactions: List<Transaction>,
        periodTitle: String = "All Time",
        categorySpends: List<CategorySpend> = emptyList()
    ): File? {
        return try {
            val reportsDir = File(context.cacheDir, "reports").apply { if (!exists()) mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val pdfFile = File(reportsDir, "Statement_${timeStamp}.pdf")

            val pdfDocument = PdfDocument()
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val dateDf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val effectiveCategorySpends = computeCategorySpends(categorySpends, transactions)
            val hasCategories = effectiveCategorySpends.isNotEmpty()

            val page1Capacity = if (hasCategories) 20 else 23
            val subsequentCapacity = 30
            val totalTransactions = transactions.size
            val totalPages = if (totalTransactions <= page1Capacity) 1
            else 1 + ((totalTransactions - page1Capacity + subsequentCapacity - 1) / subsequentCapacity)

            val pageLeft = 36f
            val pageRight = 559f
            val pageWidth = pageRight - pageLeft
            val rowHeight = 18f

            // --- PAGE 1 ---
            val pageInfo1 = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page1 = pdfDocument.startPage(pageInfo1)
            StatementPdfPageOne.renderPageOne(
                canvas = page1.canvas,
                paint = paint,
                profile = profile,
                summary = summary,
                transactions = transactions,
                periodTitle = periodTitle,
                effectiveCategorySpends = effectiveCategorySpends,
                totalPages = totalPages,
                pageLeft = pageLeft,
                pageRight = pageRight,
                pageWidth = pageWidth,
                rowHeight = rowHeight,
                page1Capacity = page1Capacity
            )
            pdfDocument.finishPage(page1)

            // --- SUBSEQUENT PAGES ---
            var remainingTransactions = transactions.drop(page1Capacity)
            var currentPage = 2

            while (remainingTransactions.isNotEmpty()) {
                val currentBatch = remainingTransactions.take(subsequentCapacity)
                remainingTransactions = remainingTransactions.drop(subsequentCapacity)

                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, currentPage).create()
                val page = pdfDocument.startPage(pageInfo)

                StatementPdfSubsequentPage.renderSubsequentPage(
                    canvas = page.canvas,
                    paint = paint,
                    profile = profile,
                    batch = currentBatch,
                    periodTitle = periodTitle,
                    currentPage = currentPage,
                    totalPages = totalPages,
                    pageLeft = pageLeft,
                    pageRight = pageRight,
                    rowHeight = rowHeight,
                    dateDf = dateDf
                )

                pdfDocument.finishPage(page)
                currentPage++
            }

            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun computeCategorySpends(categorySpends: List<CategorySpend>, transactions: List<Transaction>): List<CategorySpend> {
        if (categorySpends.isNotEmpty()) return categorySpends
        val totalExp = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        if (totalExp <= 0.0) return emptyList()
        return transactions.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .map { (_, list) ->
                val sample = list.first()
                val catAmount = list.sumOf { it.amount }
                CategorySpend(
                    categoryId = sample.categoryId,
                    categoryName = sample.categoryName,
                    categoryIcon = sample.categoryIcon,
                    categoryColor = sample.categoryColor,
                    totalAmount = catAmount,
                    percentage = (catAmount / totalExp).toFloat().coerceIn(0f, 1f),
                    transactionCount = list.size
                )
            }.sortedByDescending { it.totalAmount }
    }
}
