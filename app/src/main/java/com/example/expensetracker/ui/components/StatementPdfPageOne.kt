package com.example.expensetracker.ui.components

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.StatementPdfTheme.CATEGORY_PALETTE
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_ACCENT_BLUE
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_BG_LIGHT
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_BORDER
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_BORDER_DARK
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_EXPENSE_RED
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_HEADER_BG
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_INCOME_GREEN
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_PRIMARY_NAVY
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_PURPLE
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_TEXT_MUTED
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_TEXT_PRIMARY
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_TEXT_SECONDARY
import com.example.expensetracker.ui.components.StatementPdfTheme.drawPageFooter
import com.example.expensetracker.ui.components.StatementPdfTheme.formatAmount
import com.example.expensetracker.ui.components.StatementPdfTheme.truncateToWidth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StatementPdfPageOne {

    fun renderPageOne(
        canvas: Canvas,
        paint: Paint,
        profile: UserProfile,
        summary: FinancialSummary,
        transactions: List<Transaction>,
        periodTitle: String,
        effectiveCategorySpends: List<CategorySpend>,
        totalPages: Int,
        pageLeft: Float,
        pageRight: Float,
        pageWidth: Float,
        rowHeight: Float,
        page1Capacity: Int
    ) {
        val df = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateDf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hasCategories = effectiveCategorySpends.isNotEmpty()

        // 1. Top Decorative Brand Accent Bar
        paint.color = COLOR_ACCENT_BLUE
        canvas.drawRect(0f, 0f, 595f, 4f, paint)

        // 2. Header Section
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 8.5f
        paint.color = COLOR_ACCENT_BLUE
        canvas.drawText("EXPENSE TRACKER", pageLeft, 28f, paint)

        paint.textSize = 18f
        paint.color = COLOR_PRIMARY_NAVY
        canvas.drawText("Financial Statement", pageLeft, 50f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 9f
        paint.color = COLOR_TEXT_PRIMARY
        val periodText = periodTitle
        val periodWidth = paint.measureText(periodText)
        canvas.drawText(periodText, pageRight - periodWidth, 32f, paint)

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 8f
        paint.color = COLOR_TEXT_MUTED
        val genDateText = "Generated: ${df.format(Date())}"
        val genDateWidth = paint.measureText(genDateText)
        canvas.drawText(genDateText, pageRight - genDateWidth, 46f, paint)

        // Metadata Row
        var yPos = 68f
        paint.color = COLOR_BORDER
        canvas.drawLine(pageLeft, yPos, pageRight, yPos, paint)

        yPos += 14f
        paint.textSize = 8.5f
        paint.color = COLOR_TEXT_SECONDARY
        canvas.drawText("Account: ${profile.displayName.ifBlank { profile.name }}", pageLeft, yPos, paint)

        val currencyLabel = "Currency: ${profile.currencyCode} (${profile.currencySymbol})"
        canvas.drawText(currencyLabel, pageLeft + 180f, yPos, paint)

        val recordsLabel = "Total Transactions: ${transactions.size}"
        val recWidth = paint.measureText(recordsLabel)
        canvas.drawText(recordsLabel, pageRight - recWidth, yPos, paint)

        yPos += 10f
        paint.color = COLOR_BORDER
        canvas.drawLine(pageLeft, yPos, pageRight, yPos, paint)

        // 3. KPI Metric Cards
        yPos += 12f
        val cardHeight = 56f
        val cardGap = 8f
        val cardWidth = (pageWidth - (cardGap * 3)) / 4

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
            paint.color = COLOR_BG_LIGHT
            canvas.drawRoundRect(RectF(cardX, yPos, cardX + cardWidth, yPos + cardHeight), 6f, 6f, paint)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            paint.color = COLOR_BORDER
            canvas.drawRoundRect(RectF(cardX, yPos, cardX + cardWidth, yPos + cardHeight), 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            paint.color = accentColor
            canvas.drawRoundRect(RectF(cardX, yPos, cardX + cardWidth, yPos + 3.5f), 3f, 3f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 7f
            paint.color = COLOR_TEXT_MUTED
            canvas.drawText(label, cardX + 8f, yPos + 18f, paint)

            paint.textSize = 10.5f
            paint.color = accentColor
            val safeVal = truncateToWidth(value, cardWidth - 14f, paint)
            canvas.drawText(safeVal, cardX + 8f, yPos + 38f, paint)

            cardX += cardWidth + cardGap
        }

        yPos += cardHeight + 14f

        // 4. Category Breakdown
        if (hasCategories) {
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 7.5f
            paint.color = COLOR_TEXT_SECONDARY
            canvas.drawText("EXPENSE BREAKDOWN BY CATEGORY", pageLeft, yPos, paint)

            yPos += 6f
            val barHeight = 7f
            val topCats = effectiveCategorySpends.take(6)
            var currentBarX = pageLeft

            for ((idx, cat) in topCats.withIndex()) {
                val sliceWidth = (cat.percentage * pageWidth).coerceAtLeast(2f)
                val catColor = CATEGORY_PALETTE[idx % CATEGORY_PALETTE.size]
                paint.color = catColor
                val rightEdge = (currentBarX + sliceWidth).coerceAtMost(pageRight)
                canvas.drawRect(currentBarX, yPos, rightEdge, yPos + barHeight, paint)
                currentBarX = rightEdge
                if (currentBarX >= pageRight) break
            }

            if (currentBarX < pageRight) {
                paint.color = COLOR_BORDER
                canvas.drawRect(currentBarX, yPos, pageRight, yPos + barHeight, paint)
            }

            yPos += barHeight + 10f
            var legendX = pageLeft
            paint.textSize = 7.5f
            for ((idx, cat) in topCats.take(4).withIndex()) {
                val catColor = CATEGORY_PALETTE[idx % CATEGORY_PALETTE.size]
                paint.color = catColor
                canvas.drawCircle(legendX + 3.5f, yPos - 2.5f, 3.5f, paint)
                paint.typeface = Typeface.DEFAULT
                paint.color = COLOR_TEXT_SECONDARY
                val catLabel = " ${cat.categoryName} (${String.format(Locale.US, "%.0f%%", cat.percentage * 100)})"
                canvas.drawText(catLabel, legendX + 9f, yPos, paint)
                legendX += paint.measureText(catLabel) + 20f
                if (legendX > pageRight - 60f) break
            }
            yPos += 14f
        }

        // 5. Table Header
        yPos += 4f
        paint.color = COLOR_HEADER_BG
        canvas.drawRect(pageLeft, yPos, pageRight, yPos + 22f, paint)
        paint.color = COLOR_BORDER_DARK
        paint.strokeWidth = 1f
        canvas.drawLine(pageLeft, yPos, pageRight, yPos, paint)
        canvas.drawLine(pageLeft, yPos + 22f, pageRight, yPos + 22f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 8f
        paint.color = COLOR_TEXT_PRIMARY
        canvas.drawText("DATE", pageLeft + 6f, yPos + 14f, paint)
        canvas.drawText("DESCRIPTION", pageLeft + 65f, yPos + 14f, paint)
        canvas.drawText("CATEGORY", pageLeft + 220f, yPos + 14f, paint)
        canvas.drawText("METHOD / ACCT", pageLeft + 325f, yPos + 14f, paint)
        val amtHeaderWidth = paint.measureText("AMOUNT")
        canvas.drawText("AMOUNT", pageRight - 6f - amtHeaderWidth, yPos + 14f, paint)

        yPos += 22f

        // 6. Table Rows
        val page1Rows = transactions.take(page1Capacity)
        for ((index, tx) in page1Rows.withIndex()) {
            if (index % 2 == 1) {
                paint.color = COLOR_BG_LIGHT
                canvas.drawRect(pageLeft, yPos, pageRight, yPos + rowHeight, paint)
            }
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 8f
            paint.color = COLOR_TEXT_MUTED
            canvas.drawText(dateDf.format(Date(tx.dateMillis)), pageLeft + 6f, yPos + 14f, paint)

            paint.color = COLOR_TEXT_PRIMARY
            canvas.drawText(truncateToWidth(tx.title, 145f, paint), pageLeft + 65f, yPos + 14f, paint)

            paint.color = COLOR_TEXT_SECONDARY
            canvas.drawText(truncateToWidth(tx.categoryName, 95f, paint), pageLeft + 220f, yPos + 14f, paint)

            val methodOrAccount = (tx.accountName ?: tx.paymentMethod.displayName)
            canvas.drawText(truncateToWidth(methodOrAccount, 85f, paint), pageLeft + 325f, yPos + 14f, paint)

            val isIncome = tx.type == TransactionType.INCOME
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = if (isIncome) COLOR_INCOME_GREEN else COLOR_EXPENSE_RED
            val amountText = "${if (isIncome) "+" else "-"} ${profile.currencySymbol} ${formatAmount(tx.amount)}"
            val amtWidth = paint.measureText(amountText)
            canvas.drawText(amountText, pageRight - 6f - amtWidth, yPos + 14f, paint)

            paint.color = COLOR_BORDER
            paint.strokeWidth = 0.5f
            canvas.drawLine(pageLeft, yPos + rowHeight, pageRight, yPos + rowHeight, paint)
            yPos += rowHeight
        }

        drawPageFooter(canvas, paint, 1, totalPages, pageLeft, pageRight)
    }
}
