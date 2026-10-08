package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.theme.ColorWarningBills
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun AccountBalanceWarning(
    account: Account?,
    amount: Double,
    isExpense: Boolean,
    currencySymbol: String,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    if (isExpense && account != null && amount > account.currentBalance && account.currentBalance > 0.0) {
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = ColorWarningBills.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, ColorWarningBills.copy(alpha = 0.3f)),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = ColorWarningBills,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (strings.isBangla)
                        "${account.getLocalizedName(true)}-এ ব্যালেন্স ${formatCurrency(account.currentBalance, currencySymbol, true)}। খরচ করার পর ব্যালেন্স ঋণাত্মক হবে।"
                    else
                        "Balance in ${account.getLocalizedName(false)} is ${formatCurrency(account.currentBalance, currencySymbol, false)}. Balance will become negative after this expense.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = ColorWarningBills
                )
            }
        }
    }
}
