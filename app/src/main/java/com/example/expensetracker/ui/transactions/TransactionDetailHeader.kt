package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.Strings

@Composable
fun TransactionDetailHeader(
    transaction: Transaction,
    isIncome: Boolean,
    amountColor: Color,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(CategoryIconHelper.parseColor(transaction.categoryColor).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = CategoryIconHelper.getIcon(transaction.categoryIcon),
                contentDescription = null,
                tint = CategoryIconHelper.parseColor(transaction.categoryColor),
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = appColors.textPrimary
            )
            Text(
                text = if (isIncome) strings.income else strings.expense,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = amountColor
            )
        }
    }
}
