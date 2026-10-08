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
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionTypeSwitcher(
    selectedType: TransactionType,
    isExpense: Boolean,
    onTypeSelected: (TransactionType) -> Unit,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()
    val isIncome = selectedType == TransactionType.INCOME

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(LocalAppColors.current.surfaceVariant)
            .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Expense Tab
        Surface(
            shape = RoundedCornerShape(11.dp),
            color = if (isExpense) ColorDangerExpense else Color.Transparent,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    if (selectedType != TransactionType.EXPENSE) {
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
                    imageVector = Icons.Outlined.ArrowUpward,
                    contentDescription = null,
                    tint = if (isExpense) Color.White else ColorTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.expense,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isExpense) Color.White else ColorTextSecondary
                )
            }
        }

        // Income Tab
        Surface(
            shape = RoundedCornerShape(11.dp),
            color = if (isIncome) ColorSuccessIncome else Color.Transparent,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    if (selectedType != TransactionType.INCOME) {
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
                    imageVector = Icons.Outlined.ArrowDownward,
                    contentDescription = null,
                    tint = if (isIncome) Color.White else ColorTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.income,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isIncome) Color.White else ColorTextSecondary
                )
            }
        }
    }
}
