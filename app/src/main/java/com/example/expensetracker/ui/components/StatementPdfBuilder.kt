package com.example.expensetracker.ui.components

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_ACCENT_BLUE
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_BG_LIGHT
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_BORDER
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_BORDER_DARK
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_EXPENSE_RED
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_HEADER_BG
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_INCOME_GREEN
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_PRIMARY_NAVY
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_TEXT_MUTED
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_TEXT_PRIMARY
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_TEXT_SECONDARY
import com.example.expensetracker.ui.components.StatementPdfTheme.drawPageFooter
import com.example.expensetracker.ui.components.StatementPdfTheme.formatAmount
import com.example.expensetracker.ui.components.StatementPdfTheme.truncateToWidth
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
            val rowHeight = 22f

            // PAGE 1
            val page1Info = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page1 = pdfDocument.startPage(page1Info)
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

            // SUBSEQUENT PAGES
            var remainingRows = transactions.drop(page1Capacity)
            var currentPage = 2
            while (remainingRows.isNotEmpty()) {
                val currentBatch = remainingRows.take(subsequentCapacity)
                remainingRows = remainingRows.drop(subsequentCapacity)

                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, currentPage).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                paint.color = COLOR_ACCENT_BLUE
                canvas.drawRect(0f, 0f, 595f, 3f, paint)

                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 9f
                paint.color = COLOR_PRIMARY_NAVY
                canvas.drawText("EXPENSE TRACKER — FINANCIAL STATEMENT (CONTD.)", pageLeft, 24f, paint)

                paint.typeface = Typeface.DEFAULT
                paint.textSize = 8f
                paint.color = COLOR_TEXT_MUTED
                val subPeriodText = "Period: $periodTitle"
                canvas.drawText(subPeriodText, pageRight - paint.measureText(subPeriodText), 24f, paint)

                paint.color = COLOR_BORDER
                canvas.drawLine(pageLeft, 32f, pageRight, 32f, paint)

                var subYPos = 40f
                paint.color = COLOR_HEADER_BG
                canvas.drawRect(pageLeft, subYPos, pageRight, subYPos + 22f, paint)

                paint.color = COLOR_BORDER_DARK
                paint.strokeWidth = 1f
                canvas.drawLine(pageLeft, subYPos, pageRight, subYPos, paint)
                canvas.drawLine(pageLeft, subYPos + 22f, pageRight, subYPos + 22f, paint)

                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 8f
                paint.color = COLOR_TEXT_PRIMARY
                canvas.drawText("DATE", pageLeft + 6f, subYPos + 14f, paint)
                canvas.drawText("DESCRIPTION", pageLeft + 65f, subYPos + 14f, paint)
                canvas.drawText("CATEGORY", pageLeft + 220f, subYPos + 14f, paint)
                canvas.drawText("METHOD / ACCT", pageLeft + 325f, subYPos + 14f, paint)
                val amtHeader = "AMOUNT"
                canvas.drawText(amtHeader, pageRight - 6f - paint.measureText(amtHeader), subYPos + 14f, paint)

                subYPos += 22f

                for ((index, tx) in currentBatch.withIndex()) {
                    if (index % 2 == 1) {
                        paint.color = COLOR_BG_LIGHT
                        canvas.drawRect(pageLeft, subYPos, pageRight, subYPos + rowHeight, paint)
                    }
                    paint.typeface = Typeface.DEFAULT
                    paint.textSize = 8f
                    paint.color = COLOR_TEXT_MUTED
                    canvas.drawText(dateDf.format(Date(tx.dateMillis)), pageLeft + 6f, subYPos + 14f, paint)

                    paint.color = COLOR_TEXT_PRIMARY
                    canvas.drawText(truncateToWidth(tx.title, 145f, paint), pageLeft + 65f, subYPos + 14f, paint)

                    paint.color = COLOR_TEXT_SECONDARY
                    canvas.drawText(truncateToWidth(tx.categoryName, 95f, paint), pageLeft + 220f, subYPos + 14f, paint)

                    val methodOrAccount = (tx.accountName ?: tx.paymentMethod.displayName)
                    canvas.drawText(truncateToWidth(methodOrAccount, 85f, paint), pageLeft + 325f, subYPos + 14f, paint)

                    val isIncome = tx.type == TransactionType.INCOME
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    paint.color = if (isIncome) COLOR_INCOME_GREEN else COLOR_EXPENSE_RED
                    val amountText = "${if (isIncome) "+" else "-"} ${profile.currencySymbol} ${formatAmount(tx.amount)}"
                    canvas.drawText(amountText, pageRight - 6f - paint.measureText(amountText), subYPos + 14f, paint)

                    paint.color = COLOR_BORDER
                    paint.strokeWidth = 0.5f
                    canvas.drawLine(pageLeft, subYPos + rowHeight, pageRight, subYPos + rowHeight, paint)
                    subYPos += rowHeight
                }

                drawPageFooter(canvas, paint, currentPage, totalPages, pageLeft, pageRight)
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
