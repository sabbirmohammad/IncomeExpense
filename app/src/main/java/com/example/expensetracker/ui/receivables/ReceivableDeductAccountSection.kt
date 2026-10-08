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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun ReceivableDeductAccountSection(
    deductFromAccount: Boolean,
    onDeductToggle: (Boolean) -> Unit,
    accounts: List<Account>,
    selectedAccountId: Long?,
    currencySymbol: String,
    onAccountSelected: (Long) -> Unit
) {
    val strings = LocalAppStrings.current
    Surface(
        shape = RoundedCornerShape(SmallElementRadius),
        color = ColorBackground,
        border = BorderStroke(1.dp, ColorBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(strings.deductFromAccount, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ColorTextPrimary)
                    Text(
                        if (strings.isBangla) "ওয়ালেট থেকে ব্যয়ের হিসাব হিসেবে যুক্ত করুন" else "Log as an expense from one of your accounts",
                        fontSize = 11.sp,
                        color = ColorTextSecondary
                    )
                }
                Switch(
                    checked = deductFromAccount,
                    onCheckedChange = onDeductToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ColorAccent)
                )
            }

            if (deductFromAccount) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("${strings.fromAccount}:", fontSize = 11.sp, color = ColorTextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    accounts.forEach { acc ->
                        val isSelected = selectedAccountId == acc.id
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ColorAccent else ColorSurface,
                            border = BorderStroke(1.dp, if (isSelected) ColorAccent else ColorBorder),
                            modifier = Modifier.clickable { onAccountSelected(acc.id) }
                        ) {
                            Text(
                                text = "${acc.name} (${formatCurrency(acc.currentBalance, currencySymbol)})",
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else ColorTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
