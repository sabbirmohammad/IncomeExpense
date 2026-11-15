package com.example.expensetracker.ui.components

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.ui.components.StatementPdfTheme.CATEGORY_PALETTE
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_BORDER
import com.example.expensetracker.ui.components.StatementPdfTheme.COLOR_TEXT_SECONDARY
import java.util.Locale

internal object StatementPdfCategoryBreakdown {

    fun drawCategoryBreakdown(
        canvas: Canvas,
        paint: Paint,
        categorySpends: List<CategorySpend>,
        pageLeft: Float,
        pageRight: Float,
        pageWidth: Float,
        startY: Float
    ): Float {
        var yPos = startY
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 7.5f
        paint.color = COLOR_TEXT_SECONDARY
        canvas.drawText("EXPENSE BREAKDOWN BY CATEGORY", pageLeft, yPos, paint)

        yPos += 6f
        val barHeight = 7f
        val topCats = categorySpends.take(6)
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

        return yPos + 14f
    }
}
