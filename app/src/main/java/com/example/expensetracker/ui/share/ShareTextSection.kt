package com.example.expensetracker.ui.share

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun ShareTextSection(
    periodTitle: String,
    periodSubtitle: String,
    totalIncome: Double,
    totalExpense: Double,
    transactions: List<Transaction>,
    userProfile: UserProfile,
    strings: Strings,
    context: Context,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val textSummary = remember(periodTitle, periodSubtitle, totalExpense, totalIncome, transactions, userProfile) {
        buildString {
            appendLine("📊 *${periodTitle}*")
            appendLine("📅 ${periodSubtitle}")
            appendLine("💰 ${strings.expense}: ${formatCurrency(totalExpense, userProfile.currencySymbol)}")
            if (totalIncome > 0) {
                appendLine("📈 ${strings.income}: ${formatCurrency(totalIncome, userProfile.currencySymbol)}")
            }
            appendLine("-------------------------")
            transactions.forEach { tx ->
                val sign = if (tx.type == TransactionType.INCOME) "+" else "-"
                appendLine("• ${tx.title}: $sign${formatCurrency(tx.amount, userProfile.currencySymbol)} (${tx.categoryName})")
            }
            appendLine("-------------------------")
            appendLine("Shared via ${strings.appName}")
        }
    }

    Column(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = appColors.surfaceVariant,
            border = BorderStroke(1.dp, appColors.border),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Text(
                text = textSummary,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = appColors.textPrimary,
                modifier = Modifier.padding(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                ShareExpenseHelper.shareText(context, textSummary, "Share Expense Text Summary")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (appColors.isDark) Color(0xFF2EC4B6) else Color(0xFF0F766E),
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = strings.copyTextSummary,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                fontSize = 14.sp
            )
        }
    }
}
