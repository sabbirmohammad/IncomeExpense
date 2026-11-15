package com.example.expensetracker.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.expensetracker.data.model.CategorySpend
import kotlin.math.atan2

object DonutChartTouchHelper {
    fun calculateTappedIndex(tapOffset: Offset, size: IntSize, categorySpends: List<CategorySpend>): Int? {
        val center = Offset(size.width / 2f, size.height / 2f)
        val dx = tapOffset.x - center.x
        val dy = tapOffset.y - center.y
        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        if (angle < 0) angle += 360f
        val normalizedAngle = (angle + 90f) % 360f

        var accumulated = 0f
        for ((idx, spend) in categorySpends.withIndex()) {
            val sweep = spend.percentage * 360f
            if (normalizedAngle >= accumulated && normalizedAngle < accumulated + sweep) {
                return idx
            }
            accumulated += sweep
        }
        return null
    }
}
