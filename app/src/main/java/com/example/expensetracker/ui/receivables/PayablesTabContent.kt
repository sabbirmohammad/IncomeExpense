package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.Strings
import java.util.Locale

fun LazyListScope.payablesTabContent(
    summary: PayablesSummary,
    payables: List<Payable>,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    strings: Strings,
    onAddPayable: () -> Unit,
    onRecordPayment: (Payable) -> Unit,
    onEdit: (Payable) -> Unit,
    onDelete: (Payable) -> Unit
) {
    item {
        Spacer(modifier = Modifier.height(4.dp))
        PayablesSummaryCard(
            summary = summary,
            currencySymbol = currencySymbol,
            isPrivacyMode = isPrivacyMode
        )
    }

    item {
        Spacer(modifier = Modifier.height(CardGap))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${strings.payablesTitle.uppercase(Locale.getDefault())} (${payables.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                color = ColorTextSecondary,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "+ ${strings.addPayable}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorAccent,
                modifier = Modifier.clickable { onAddPayable() }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    if (payables.isEmpty()) {
        item {
            Spacer(modifier = Modifier.height(CardGap))
            AppCard(modifier = Modifier.fillMaxWidth(), padding = 32.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Outlined.Handshake, null, tint = ColorTextSecondary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(strings.noPayablesFound, style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold), color = ColorTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(strings.noPayablesHint, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = ColorTextSecondary)
                }
            }
        }
    } else {
        item { Spacer(modifier = Modifier.height(CardGap)) }
        items(payables, key = { it.id }) { pay ->
            PayableRowItem(
                payable = pay,
                currencySymbol = currencySymbol,
                isPrivacyMode = isPrivacyMode,
                onRecordPayment = { onRecordPayment(pay) },
                onEdit = { onEdit(pay) },
                onDelete = { onDelete(pay) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
