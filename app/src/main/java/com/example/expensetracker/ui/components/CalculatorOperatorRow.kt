package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun RowScope.CalculatorOperatorButton(
    op: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = LocalAppColors.current.surface,
        border = BorderStroke(1.dp, LocalAppColors.current.border),
        modifier = modifier
            .weight(1f)
            .height(48.dp)
            .padding(horizontal = 2.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = op,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
fun RowScope.CalculatorClearButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = appColors.expense.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, appColors.expense.copy(alpha = 0.25f)),
        modifier = modifier
            .weight(1f)
            .height(48.dp)
            .padding(horizontal = 2.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "C",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = appColors.expense
            )
        }
    }
}
