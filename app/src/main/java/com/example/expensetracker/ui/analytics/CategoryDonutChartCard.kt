package com.example.expensetracker.ui.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.DonutChart
import com.example.expensetracker.ui.components.Strings

@Composable
fun CategoryDonutChartCard(
    categorySpends: List<CategorySpend>,
    currencySymbol: String,
    totalAmount: Double,
    transactionType: TransactionType,
    strings: Strings,
    onCategoryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    AppCard(modifier = modifier) {
        Column {
            Text(
                text = if (transactionType == TransactionType.EXPENSE) {
                    if (strings.isBangla) "ক্যাটাগরি ভিত্তিক খরচ" else "Expense by category"
                } else {
                    if (strings.isBangla) "ক্যাটাগরি ভিত্তিক আয়" else "Income by category"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            DonutChart(
                categorySpends = categorySpends,
                currencySymbol = currencySymbol,
                totalAmount = totalAmount,
                centerLabel = if (transactionType == TransactionType.EXPENSE) {
                    if (strings.isBangla) "মোট খরচ" else "Total spent"
                } else {
                    if (strings.isBangla) "মোট আয়" else "Total income"
                },
                onCategoryClick = onCategoryClick
            )
        }
    }
}
