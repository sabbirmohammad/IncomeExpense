package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun RecordPaymentAccountSelector(
    accounts: List<Account>,
    selectedAccountId: Long?,
    currencySymbol: String,
    onAccountSelected: (Long) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(strings.depositToAccount, fontSize = 12.sp, color = appColors.textSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            accounts.forEach { acc ->
                val isSelected = selectedAccountId == acc.id
                Surface(
                    shape = RoundedCornerShape(SmallElementRadius),
                    color = if (isSelected) appColors.income else appColors.surfaceVariant,
                    border = if (isSelected) null else BorderStroke(1.dp, appColors.border),
                    modifier = Modifier.clickable { onAccountSelected(acc.id) }
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = acc.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color.White else appColors.textPrimary
                        )
                        Text(
                            text = formatCurrency(acc.currentBalance, currencySymbol),
                            fontSize = 10.sp,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else appColors.textMuted
                        )
                    }
                }
            }
        }
    }
}
