package com.example.expensetracker.ui.components

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StatementExportHelper {

    // Executive Color Palette
    private val COLOR_PRIMARY_NAVY = AndroidColor.rgb(15, 23, 42)     // #0F172A
    private val COLOR_SECONDARY_NAVY = AndroidColor.rgb(30, 41, 59)   // #1E293B
    private val COLOR_ACCENT_BLUE = AndroidColor.rgb(37, 99, 235)     // #2563EB
    private val COLOR_TEXT_PRIMARY = AndroidColor.rgb(15, 23, 42)    // #0F172A
    private val COLOR_TEXT_SECONDARY = AndroidColor.rgb(71, 85, 105)  // #475569
    private val COLOR_TEXT_MUTED = AndroidColor.rgb(148, 163, 184)    // #94A3B8
    private val COLOR_BG_LIGHT = AndroidColor.rgb(248, 250, 252)      // #F8FAFC
    private val COLOR_HEADER_BG = AndroidColor.rgb(241, 245, 249)     // #F1F5F9
    private val COLOR_BORDER = AndroidColor.rgb(226, 232, 240)        // #E2E8F0
    private val COLOR_BORDER_DARK = AndroidColor.rgb(203, 213, 225)   // #CBD5E1
    private val COLOR_INCOME_GREEN = AndroidColor.rgb(16, 185, 129)   // #10B981
    private val COLOR_EXPENSE_RED = AndroidColor.rgb(239, 68, 68)     // #EF4444
    private val COLOR_PURPLE = AndroidColor.rgb(139, 92, 246)         // #8B5CF6

    private val CATEGORY_PALETTE = intArrayOf(
        AndroidColor.rgb(37, 99, 235),   // Blue
        AndroidColor.rgb(16, 185, 129),  // Green
        AndroidColor.rgb(245, 158, 11),  // Amber
        AndroidColor.rgb(139, 92, 246),  // Purple
        AndroidColor.rgb(236, 72, 153),  // Pink
        AndroidColor.rgb(6, 182, 212),   // Cyan
        AndroidColor.rgb(100, 116, 139)  // Slate
    )

    /**
     * Generates a modern executive Android PDF financial statement and returns the File object.
     */
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
            val df = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val dateDf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Automatically compute category spends if not provided and expenses exist
            val effectiveCategorySpends: List<CategorySpend> = if (categorySpends.isNotEmpty()) {
                categorySpends
            } else {
                val totalExp = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                if (totalExp > 0.0) {
                    transactions.filter { it.type == TransactionType.EXPENSE }
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
                } else emptyList()
            }

            val hasCategories = effectiveCategorySpends.isNotEmpty()

            // Page 1 capacity: 20 rows if has category breakdown, 23 if not
            val page1Capacity = if (hasCategories) 20 else 23
            val subsequentCapacity = 30
            val totalTransactions = transactions.size
            val totalPages = if (totalTransactions <= page1Capacity) {
                1
            } else {
                1 + ((totalTransactions - page1Capacity + subsequentCapacity - 1) / subsequentCapacity)
            }

            // Margin & Layout Constants
            val pageLeft = 36f
            val pageRight = 559f
            val pageWidth = pageRight - pageLeft // 523f
            val rowHeight = 22f

            // =========================================================================
            // PAGE 1
            // =========================================================================
            val page1Info = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 at 72 DPI
            val page1 = pdfDocument.startPage(page1Info)
            val canvas1 = page1.canvas

            // 1. Top Decorative Brand Accent Bar
            paint.color = COLOR_ACCENT_BLUE
            canvas1.drawRect(0f, 0f, 595f, 4f, paint)

            // 2. Header Section
            // Subtitle tag
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 8.5f
            paint.color = COLOR_ACCENT_BLUE
            canvas1.drawText("EXPENSE TRACKER", pageLeft, 28f, paint)

            // Main Title
            paint.textSize = 18f
            paint.color = COLOR_PRIMARY_NAVY
            canvas1.drawText("Financial Statement", pageLeft, 50f, paint)

            // Right Header: Period & Generation Date
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 9f
            paint.color = COLOR_TEXT_PRIMARY
            val periodText = periodTitle
            val periodWidth = paint.measureText(periodText)
            canvas1.drawText(periodText, pageRight - periodWidth, 32f, paint)

            paint.typeface = Typeface.DEFAULT
            paint.textSize = 8f
            paint.color = COLOR_TEXT_MUTED
            val genDateText = "Generated: ${df.format(Date())}"
            val genDateWidth = paint.measureText(genDateText)
            canvas1.drawText(genDateText, pageRight - genDateWidth, 46f, paint)

            // Metadata Row: Account Name, Currency, Total Records
            var yPos = 68f
            paint.color = COLOR_BORDER
            canvas1.drawLine(pageLeft, yPos, pageRight, yPos, paint)

            yPos += 14f
            paint.textSize = 8.5f
            paint.color = COLOR_TEXT_SECONDARY
            val accountLabel = "Account: ${profile.displayName.ifBlank { profile.name }}"
            canvas1.drawText(accountLabel, pageLeft, yPos, paint)

            val currencyLabel = "Currency: ${profile.currencyCode} (${profile.currencySymbol})"
            val curWidth = paint.measureText(currencyLabel)
            canvas1.drawText(currencyLabel, pageLeft + 180f, yPos, paint)

            val recordsLabel = "Total Transactions: $totalTransactions"
            val recWidth = paint.measureText(recordsLabel)
            canvas1.drawText(recordsLabel, pageRight - recWidth, yPos, paint)

            yPos += 10f
            paint.color = COLOR_BORDER
            canvas1.drawLine(pageLeft, yPos, pageRight, yPos, paint)

            // 3. Executive KPI Metric Cards (4 Cards: Income, Expense, Net Balance, Savings Rate)
            yPos += 12f
            val cardHeight = 56f
            val cardGap = 8f
            val numCards = 4
            val cardWidth = (pageWidth - (cardGap * (numCards - 1))) / numCards

            val savingsRate = if (summary.totalIncome > 0) {
                ((summary.netBalance / summary.totalIncome) * 100).toInt().coerceIn(-999, 100)
            } else 0

            val cardData = listOf(
                Triple("TOTAL INCOME", "+${profile.currencySymbol} ${formatAmount(summary.totalIncome)}", COLOR_INCOME_GREEN),
                Triple("TOTAL EXPENSE", "-${profile.currencySymbol} ${formatAmount(summary.totalExpense)}", COLOR_EXPENSE_RED),
                Triple("NET BALANCE", "${if (summary.netBalance >= 0) "+" else ""}${profile.currencySymbol} ${formatAmount(summary.netBalance)}", if (summary.netBalance >= 0) COLOR_ACCENT_BLUE else COLOR_EXPENSE_RED),
                Triple("SAVINGS RATE", "$savingsRate%", if (savingsRate >= 0) COLOR_PURPLE else COLOR_EXPENSE_RED)
            )

            var cardX = pageLeft
            for ((label, value, accentColor) in cardData) {
                // Card Background
                paint.color = COLOR_BG_LIGHT
                canvas1.drawRoundRect(RectF(cardX, yPos, cardX + cardWidth, yPos + cardHeight), 6f, 6f, paint)

                // Card Border
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 1f
                paint.color = COLOR_BORDER
                canvas1.drawRoundRect(RectF(cardX, yPos, cardX + cardWidth, yPos + cardHeight), 6f, 6f, paint)
                paint.style = Paint.Style.FILL

                // Card Top Accent Indicator
                paint.color = accentColor
                canvas1.drawRoundRect(RectF(cardX, yPos, cardX + cardWidth, yPos + 3.5f), 3f, 3f, paint)

                // Card Label
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 7f
                paint.color = COLOR_TEXT_MUTED
                canvas1.drawText(label, cardX + 8f, yPos + 18f, paint)

                // Card Value
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 10.5f
                paint.color = accentColor
                val safeVal = truncateToWidth(value, cardWidth - 14f, paint)
                canvas1.drawText(safeVal, cardX + 8f, yPos + 38f, paint)

                cardX += cardWidth + cardGap
            }

            yPos += cardHeight + 14f

            // 4. Category Breakdown Progress Bar (if categories available)
            if (hasCategories) {
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 7.5f
                paint.color = COLOR_TEXT_SECONDARY
                canvas1.drawText("EXPENSE BREAKDOWN BY CATEGORY", pageLeft, yPos, paint)

                yPos += 6f
                val barHeight = 7f
                val topCats = effectiveCategorySpends.take(6)
                var currentBarX = pageLeft

                // Proportional stacked bar
                for ((idx, cat) in topCats.withIndex()) {
                    val sliceWidth = (cat.percentage * pageWidth).coerceAtLeast(2f)
                    val catColor = CATEGORY_PALETTE[idx % CATEGORY_PALETTE.size]
                    paint.color = catColor
                    val rightEdge = (currentBarX + sliceWidth).coerceAtMost(pageRight)
                    canvas1.drawRect(currentBarX, yPos, rightEdge, yPos + barHeight, paint)
                    currentBarX = rightEdge
                    if (currentBarX >= pageRight) break
                }

                // Fill remaining gap if any
                if (currentBarX < pageRight) {
                    paint.color = COLOR_BORDER
                    canvas1.drawRect(currentBarX, yPos, pageRight, yPos + barHeight, paint)
                }

                // Category Legend Tags below bar
                yPos += barHeight + 10f
                var legendX = pageLeft
                paint.textSize = 7.5f
                for ((idx, cat) in topCats.take(4).withIndex()) {
                    val catColor = CATEGORY_PALETTE[idx % CATEGORY_PALETTE.size]

                    // Dot indicator
                    paint.color = catColor
                    canvas1.drawCircle(legendX + 3.5f, yPos - 2.5f, 3.5f, paint)

                    // Text
                    paint.typeface = Typeface.DEFAULT
                    paint.color = COLOR_TEXT_SECONDARY
                    val catLabel = " ${cat.categoryName} (${String.format(Locale.US, "%.0f%%", cat.percentage * 100)})"
                    canvas1.drawText(catLabel, legendX + 9f, yPos, paint)

                    legendX += paint.measureText(catLabel) + 20f
                    if (legendX > pageRight - 60f) break
                }

                yPos += 14f
            }

            // 5. Transactions Table Header
            yPos += 4f
            paint.color = COLOR_HEADER_BG
            canvas1.drawRect(pageLeft, yPos, pageRight, yPos + 22f, paint)

            paint.color = COLOR_BORDER_DARK
            paint.strokeWidth = 1f
            canvas1.drawLine(pageLeft, yPos, pageRight, yPos, paint)
            canvas1.drawLine(pageLeft, yPos + 22f, pageRight, yPos + 22f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 8f
            paint.color = COLOR_TEXT_PRIMARY
            canvas1.drawText("DATE", pageLeft + 6f, yPos + 14f, paint)
            canvas1.drawText("DESCRIPTION", pageLeft + 65f, yPos + 14f, paint)
            canvas1.drawText("CATEGORY", pageLeft + 220f, yPos + 14f, paint)
            canvas1.drawText("METHOD / ACCT", pageLeft + 325f, yPos + 14f, paint)

            // AMOUNT right aligned
            val amountHeader = "AMOUNT"
            val amtHeaderWidth = paint.measureText(amountHeader)
            canvas1.drawText(amountHeader, pageRight - 6f - amtHeaderWidth, yPos + 14f, paint)

            yPos += 22f

            // 6. Page 1 Transactions Rows
            val page1Rows = transactions.take(page1Capacity)
            for ((index, tx) in page1Rows.withIndex()) {
                // Alternating row background
                if (index % 2 == 1) {
                    paint.color = COLOR_BG_LIGHT
                    canvas1.drawRect(pageLeft, yPos, pageRight, yPos + rowHeight, paint)
                }

                // Date
                paint.typeface = Typeface.DEFAULT
                paint.textSize = 8f
                paint.color = COLOR_TEXT_MUTED
                canvas1.drawText(dateDf.format(Date(tx.dateMillis)), pageLeft + 6f, yPos + 14f, paint)

                // Description
                paint.color = COLOR_TEXT_PRIMARY
                val safeTitle = truncateToWidth(tx.title, 145f, paint)
                canvas1.drawText(safeTitle, pageLeft + 65f, yPos + 14f, paint)

                // Category
                paint.color = COLOR_TEXT_SECONDARY
                val safeCat = truncateToWidth(tx.categoryName, 95f, paint)
                canvas1.drawText(safeCat, pageLeft + 220f, yPos + 14f, paint)

                // Account / Payment Method
                val methodOrAccount = (tx.accountName ?: tx.paymentMethod.displayName)
                val safeAcc = truncateToWidth(methodOrAccount, 85f, paint)
                canvas1.drawText(safeAcc, pageLeft + 325f, yPos + 14f, paint)

                // Amount (Right Aligned, Color Coded)
                val isIncome = tx.type == TransactionType.INCOME
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.color = if (isIncome) COLOR_INCOME_GREEN else COLOR_EXPENSE_RED
                val amountText = "${if (isIncome) "+" else "-"} ${profile.currencySymbol} ${formatAmount(tx.amount)}"
                val amtWidth = paint.measureText(amountText)
                canvas1.drawText(amountText, pageRight - 6f - amtWidth, yPos + 14f, paint)

                // Bottom hairline divider
                paint.color = COLOR_BORDER
                paint.strokeWidth = 0.5f
                canvas1.drawLine(pageLeft, yPos + rowHeight, pageRight, yPos + rowHeight, paint)

                yPos += rowHeight
            }

            // Page 1 Footer
            drawPageFooter(canvas1, paint, 1, totalPages, pageLeft, pageRight)
            pdfDocument.finishPage(page1)

            // =========================================================================
            // SUBSEQUENT PAGES
            // =========================================================================
            var remainingRows = transactions.drop(page1Capacity)
            var currentPage = 2
            while (remainingRows.isNotEmpty()) {
                val currentBatch = remainingRows.take(subsequentCapacity)
                remainingRows = remainingRows.drop(subsequentCapacity)

                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, currentPage).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                // Top Accent Bar
                paint.color = COLOR_ACCENT_BLUE
                canvas.drawRect(0f, 0f, 595f, 3f, paint)

                // Compact Running Header
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 9f
                paint.color = COLOR_PRIMARY_NAVY
                canvas.drawText("EXPENSE TRACKER — FINANCIAL STATEMENT (CONTD.)", pageLeft, 24f, paint)

                paint.typeface = Typeface.DEFAULT
                paint.textSize = 8f
                paint.color = COLOR_TEXT_MUTED
                val subPeriodText = "Period: $periodTitle"
                val subPeriodWidth = paint.measureText(subPeriodText)
                canvas.drawText(subPeriodText, pageRight - subPeriodWidth, 24f, paint)

                paint.color = COLOR_BORDER
                canvas.drawLine(pageLeft, 32f, pageRight, 32f, paint)

                // Table Header
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

                val amtHeaderSubWidth = paint.measureText(amountHeader)
                canvas.drawText(amountHeader, pageRight - 6f - amtHeaderSubWidth, subYPos + 14f, paint)

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
                    val safeTitle = truncateToWidth(tx.title, 145f, paint)
                    canvas.drawText(safeTitle, pageLeft + 65f, subYPos + 14f, paint)

                    paint.color = COLOR_TEXT_SECONDARY
                    val safeCat = truncateToWidth(tx.categoryName, 95f, paint)
                    canvas.drawText(safeCat, pageLeft + 220f, subYPos + 14f, paint)

                    val methodOrAccount = (tx.accountName ?: tx.paymentMethod.displayName)
                    val safeAcc = truncateToWidth(methodOrAccount, 85f, paint)
                    canvas.drawText(safeAcc, pageLeft + 325f, subYPos + 14f, paint)

                    val isIncome = tx.type == TransactionType.INCOME
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    paint.color = if (isIncome) COLOR_INCOME_GREEN else COLOR_EXPENSE_RED
                    val amountText = "${if (isIncome) "+" else "-"} ${profile.currencySymbol} ${formatAmount(tx.amount)}"
                    val amtWidth = paint.measureText(amountText)
                    canvas.drawText(amountText, pageRight - 6f - amtWidth, subYPos + 14f, paint)

                    paint.color = COLOR_BORDER
                    paint.strokeWidth = 0.5f
                    canvas.drawLine(pageLeft, subYPos + rowHeight, pageRight, subYPos + rowHeight, paint)

                    subYPos += rowHeight
                }

                // Page Footer
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

    private fun drawPageFooter(canvas: Canvas, paint: Paint, currentPage: Int, totalPages: Int, pageLeft: Float, pageRight: Float) {
        val footerY = 816f
        paint.color = COLOR_BORDER
        paint.strokeWidth = 0.5f
        canvas.drawLine(pageLeft, footerY - 12f, pageRight, footerY - 12f, paint)

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 7.5f
        paint.color = COLOR_TEXT_MUTED
        canvas.drawText("Expense Tracker • Confidential Financial Statement", pageLeft, footerY, paint)

        val pageStr = "Page $currentPage of $totalPages"
        val pageStrWidth = paint.measureText(pageStr)
        canvas.drawText(pageStr, pageRight - pageStrWidth, footerY, paint)
    }

    private fun truncateToWidth(text: String, maxWidth: Float, paint: Paint): String {
        if (paint.measureText(text) <= maxWidth) return text
        val ellipsis = "…"
        val ellipsisWidth = paint.measureText(ellipsis)
        if (ellipsisWidth >= maxWidth) return ""
        var low = 0
        var high = text.length
        var best = ""
        while (low <= high) {
            val mid = (low + high) / 2
            val candidate = text.substring(0, mid)
            if (paint.measureText(candidate) + ellipsisWidth <= maxWidth) {
                best = candidate + ellipsis
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        return best
    }

    private fun formatAmount(amount: Double): String {
        return String.format(Locale.US, "%,.2f", amount)
    }

    /**
     * Generates a native Android PDF financial statement and launches system share sheet.
     */
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

    /**
     * Opens generated PDF directly in an external PDF viewer.
     */
    fun openPdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                clipData = ClipData.newRawUri("Statement", uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                val chooser = Intent.createChooser(intent, "Open PDF").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "No PDF viewer found: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares generated PDF via Android share sheet.
     */
    fun sharePdf(context: Context, file: File, periodTitle: String = "All Time") {
        shareFile(context, file, "application/pdf", "Expense Tracker Statement ($periodTitle)")
    }

    /**
     * Generates an RFC-4180 CSV file with UTF-8 BOM (\uFEFF) for perfect Excel/Google Sheets rendering.
     */
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

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val sb = StringBuilder()
            // UTF-8 Byte Order Mark (BOM) so Excel respects UTF-8 encoding
            sb.append("\uFEFF")
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

            val fos = FileOutputStream(csvFile)
            fos.write(sb.toString().toByteArray(Charsets.UTF_8))
            fos.flush()
            fos.close()

            csvFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generates an RFC-4180 CSV file and launches system share sheet.
     */
    fun exportAndShareCsv(
        context: Context,
        profile: UserProfile,
        transactions: List<Transaction>,
        periodTitle: String = "All Time"
    ): Boolean {
        val file = generateCsvFile(context, profile, transactions, periodTitle) ?: return false
        shareCsv(context, file, periodTitle)
        return true
    }

    /**
     * Shares generated CSV file via Android share sheet.
     */
    fun shareCsv(context: Context, file: File, periodTitle: String = "All Time") {
        shareFile(context, file, "text/csv", "Expense Tracker CSV Statement ($periodTitle)")
    }

    /**
     * Generates a native JSON backup file and launches system share sheet via FileProvider.
     */
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

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                clipData = ClipData.newRawUri(title, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not share file: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
