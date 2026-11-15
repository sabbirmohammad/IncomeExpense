package com.example.expensetracker.ui.components

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
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
import java.text.SimpleDateFormat
import java.util.Date

internal object StatementPdfSubsequentPage {

    fun renderSubsequentPage(
        canvas: Canvas,
        paint: Paint,
        profile: UserProfile,
        batch: List<Transaction>,
        periodTitle: String,
        currentPage: Int,
        totalPages: Int,
        pageLeft: Float,
        pageRight: Float,
        rowHeight: Float,
        dateDf: SimpleDateFormat
    ) {
        paint.color = COLOR_ACCENT_BLUE
        canvas.drawRect(0f, 0f, 595f, 4f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        paint.color = COLOR_PRIMARY_NAVY
        canvas.drawText("Financial Statement (Cont.)", pageLeft, 34f, paint)

        paint.textSize = 8.5f
        paint.color = COLOR_TEXT_MUTED
        val pagePeriodText = "$periodTitle • Page $currentPage of $totalPages"
        val pagePeriodWidth = paint.measureText(pagePeriodText)
        canvas.drawText(pagePeriodText, pageRight - pagePeriodWidth, 34f, paint)

        var subYPos = 48f
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

        for ((index, tx) in batch.withIndex()) {
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
    }
}
