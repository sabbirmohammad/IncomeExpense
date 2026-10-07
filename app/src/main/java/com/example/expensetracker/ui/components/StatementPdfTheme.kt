package com.example.expensetracker.ui.components

import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import java.util.Locale

object StatementPdfTheme {
    val COLOR_PRIMARY_NAVY = AndroidColor.rgb(15, 23, 42)     // #0F172A
    val COLOR_SECONDARY_NAVY = AndroidColor.rgb(30, 41, 59)   // #1E293B
    val COLOR_ACCENT_BLUE = AndroidColor.rgb(37, 99, 235)     // #2563EB
    val COLOR_TEXT_PRIMARY = AndroidColor.rgb(15, 23, 42)    // #0F172A
    val COLOR_TEXT_SECONDARY = AndroidColor.rgb(71, 85, 105)  // #475569
    val COLOR_TEXT_MUTED = AndroidColor.rgb(148, 163, 184)    // #94A3B8
    val COLOR_BG_LIGHT = AndroidColor.rgb(248, 250, 252)      // #F8FAFC
    val COLOR_HEADER_BG = AndroidColor.rgb(241, 245, 249)     // #F1F5F9
    val COLOR_BORDER = AndroidColor.rgb(226, 232, 240)        // #E2E8F0
    val COLOR_BORDER_DARK = AndroidColor.rgb(203, 213, 225)   // #CBD5E1
    val COLOR_INCOME_GREEN = AndroidColor.rgb(16, 185, 129)   // #10B981
    val COLOR_EXPENSE_RED = AndroidColor.rgb(239, 68, 68)     // #EF4444
    val COLOR_PURPLE = AndroidColor.rgb(139, 92, 246)         // #8B5CF6

    val CATEGORY_PALETTE = intArrayOf(
        AndroidColor.rgb(37, 99, 235),   // Blue
        AndroidColor.rgb(16, 185, 129),  // Green
        AndroidColor.rgb(245, 158, 11),  // Amber
        AndroidColor.rgb(139, 92, 246),  // Purple
        AndroidColor.rgb(236, 72, 153),  // Pink
        AndroidColor.rgb(6, 182, 212),   // Cyan
        AndroidColor.rgb(100, 116, 139)  // Slate
    )

    fun drawPageFooter(canvas: Canvas, paint: Paint, currentPage: Int, totalPages: Int, pageLeft: Float, pageRight: Float) {
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

    fun truncateToWidth(text: String, maxWidth: Float, paint: Paint): String {
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

    fun formatAmount(amount: Double): String {
        return String.format(Locale.US, "%,.2f", amount)
    }
}
