package com.example.expensetracker.ui.components

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_ACCENT_BLUE
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_BG_LIGHT
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_BORDER
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_EXPENSE_RED
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_INCOME_GREEN
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_PURPLE
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_TEXT_MUTED
import com.example.expensetracker.ui.components.StatementPdfTheme.formatAmount
import com.example.expensetracker.ui.components.StatementPdfTheme.truncateToWidth

internal object StatementPdfKpiCards {

    fun drawKpiCards(
        canvas: Canvas,
        paint: Paint,
        profile: UserProfile,
        summary: FinancialSummary,
        pageLeft: Float,
        pageWidth: Float,
        startY: Float
    ): Float {
        var yPos = startY + 12f
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

        return yPos + cardHeight + 14f
    }
}
