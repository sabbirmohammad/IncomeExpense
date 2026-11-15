package com.example.expensetracker.ui.bills

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun PayBillSummaryCard(
    bill: RecurringBill,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    AppCard(
        modifier = modifier.fillMaxWidth(),
        padding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bill.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = appColors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = getLocalizedCategoryName(bill.categoryName, isBangla),
                    fontSize = 12.sp,
                    color = appColors.textMuted
                )
            }
            Text(
                text = formatCurrency(bill.amount, currencySymbol),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = appColors.expense
            )
        }
    }
}
