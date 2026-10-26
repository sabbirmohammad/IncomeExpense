package com.example.expensetracker.ui.savings

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
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun ContributionAccountSelector(
    accounts: List<Account>,
    deductFromAccount: Boolean,
    selectedAccountId: Long?,
    goalColor: Color,
    currencySymbol: String,
    onDeductChanged: (Boolean) -> Unit,
    onAccountSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(SmallElementRadius),
        color = appColors.surfaceVariant,
        border = BorderStroke(1.dp, appColors.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (strings.isBangla) "ওয়ালেট থেকে কেটে নিন" else "Deduct from Account",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = appColors.textPrimary
                    )
                    Text(
                        text = if (strings.isBangla) "টাকাটি নির্বাচিত অ্যাকাউন্ট থেকে বিয়োগ হবে" else "Balance will be deducted from chosen account",
                        fontSize = 11.sp,
                        color = appColors.textSecondary
                    )
                }
                Switch(
                    checked = deductFromAccount,
                    onCheckedChange = onDeductChanged,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = goalColor)
                )
            }

            if (deductFromAccount) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("${strings.fromAccount}:", fontSize = 11.sp, color = appColors.textMuted)
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
                            color = if (isSelected) goalColor else appColors.surface,
                            border = BorderStroke(1.dp, if (isSelected) goalColor else appColors.border),
                            modifier = Modifier.clickable { onAccountSelected(acc.id) }
                        ) {
                            Text(
                                text = "${acc.getLocalizedName(strings.isBangla)} (${formatCurrency(acc.currentBalance, currencySymbol)})",
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else appColors.textPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
