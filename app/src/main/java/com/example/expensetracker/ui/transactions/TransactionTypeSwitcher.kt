package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionTypeSwitcher(
    selectedType: TransactionType,
    isExpense: Boolean,
    onTypeSelected: (TransactionType) -> Unit,
    strings: Strings,
    modifier: Modifier = Modifier,
    isMarketActive: Boolean = false,
    onMarketClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val isExpenseActive = !isMarketActive && isExpense
    val isIncomeActive = !isMarketActive && selectedType == TransactionType.INCOME
    val marketTint = if (appColors.isDark) Color(0xFF00A8FF) else Color(0xFF0284C7)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(LocalAppColors.current.surfaceVariant)
            .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Expense Tab
        Surface(
            shape = RoundedCornerShape(11.dp),
            color = if (isExpenseActive) appColors.expense else Color.Transparent,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    if (!isExpenseActive) {
                        haptics.mediumClick()
                        onTypeSelected(TransactionType.EXPENSE)
                    }
                }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowDownward,
                    contentDescription = null,
                    tint = if (isExpenseActive) Color.White else appColors.textSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.expense,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = if (isExpenseActive) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isExpenseActive) Color.White else appColors.textSecondary
                )
            }
        }

        // Income Tab
        val incomeContentColor = if (appColors.isDark) Color(0xFF052B14) else Color.White
        Surface(
            shape = RoundedCornerShape(11.dp),
            color = if (isIncomeActive) appColors.income else Color.Transparent,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    if (!isIncomeActive) {
                        haptics.mediumClick()
                        onTypeSelected(TransactionType.INCOME)
                    }
                }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowUpward,
                    contentDescription = null,
                    tint = if (isIncomeActive) incomeContentColor else appColors.textSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.income,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = if (isIncomeActive) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isIncomeActive) incomeContentColor else appColors.textSecondary
                )
            }
        }

        // Market Tab
        if (onMarketClick != null) {
            Surface(
                shape = RoundedCornerShape(11.dp),
                color = if (isMarketActive) marketTint else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (!isMarketActive) {
                            haptics.mediumClick()
                            onMarketClick()
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        tint = if (isMarketActive) Color.White else marketTint,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (strings.isBangla) "বাজার" else "Market",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp,
                            fontWeight = if (isMarketActive) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        color = if (isMarketActive) Color.White else marketTint
                    )
                }
            }
        }
    }
}
