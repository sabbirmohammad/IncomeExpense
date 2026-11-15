package com.example.expensetracker.ui.bills

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.BillFrequency
import com.example.expensetracker.data.model.BillPaymentStatus
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppChip
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.LocalAppStrings

data class BillsSummaryStats(
    val totalMonthlyCommitments: Double,
    val overdueCount: Int,
    val dueSoonCount: Int
)

fun calculateBillsSummary(bills: List<RecurringBill>): BillsSummaryStats {
    val total = bills.filter { it.isActive }.sumOf {
        when (it.frequency) {
            BillFrequency.MONTHLY -> it.amount
            BillFrequency.WEEKLY -> it.amount * 4.33
            BillFrequency.YEARLY -> it.amount / 12.0
        }
    }
    val overdue = bills.count { it.isActive && it.getPaymentStatus() == BillPaymentStatus.OVERDUE }
    val dueSoon = bills.count { it.isActive && it.getPaymentStatus() == BillPaymentStatus.DUE_SOON }
    return BillsSummaryStats(total, overdue, dueSoon)
}

fun filterBillsBySegment(bills: List<RecurringBill>, selectedSegment: Int): List<RecurringBill> {
    return when (selectedSegment) {
        0 -> bills.filter { it.getPaymentStatus() != BillPaymentStatus.PAID }
        1 -> bills.filter { it.getPaymentStatus() == BillPaymentStatus.PAID }
        else -> bills
    }
}

@Composable
fun BillsEmptyStateCard(onAddClick: () -> Unit) {
    val strings = LocalAppStrings.current
    EmptyStateCard(
        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
        headline = strings.noRecurringBillsHint,
        explanation = if (strings.isBangla)
            "আপনার নিয়মিত ইউটিলিটি, ইন্টারনেট ও সাবস্ক্রিপশন বিল যোগ করুন।"
        else
            "Track all your recurring subscriptions and utility bills in one place.",
        buttonText = strings.addBill,
        onButtonClick = onAddClick
    )
}

@Composable
fun BillsTemplateRow(
    templates: List<Pair<String, String>>,
    onSelect: (String, String) -> Unit
) {
    val strings = LocalAppStrings.current
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(
            text = if (strings.isBangla) "বিল টেমপ্লেট নির্বাচন করুন:" else "Quick bill templates:",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.Medium),
            color = LocalAppColors.current.textMuted
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            templates.forEach { (label, defaultTitle) ->
                AppChip(
                    label = label,
                    selected = false,
                    onClick = { onSelect(label, defaultTitle) }
                )
            }
        }
    }
}
