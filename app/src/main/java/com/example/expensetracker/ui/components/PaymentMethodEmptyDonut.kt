package com.example.expensetracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun PaymentMethodEmptyDonut(
    chartDiameter: Dp,
    baseStrokeWidthDp: Dp,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val emptyBorderColor = appColors.border

    Canvas(modifier = modifier.size(chartDiameter - 10.dp)) {
        val stroke = baseStrokeWidthDp.toPx()
        val radius = (size.minDimension - stroke) / 2
        val topLeft = Offset((size.width - 2 * radius) / 2, (size.height - 2 * radius) / 2)
        drawArc(
            color = emptyBorderColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = stroke)
        )
    }

    Text(
        text = if (strings.isBangla) "এই সময়ে কোনো তথ্য নেই" else "No data for this period",
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        ),
        color = appColors.textMuted
    )
}
