package com.example.expensetracker.ui.receivables

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.Strings

fun LazyListScope.receivablesTabContent(
    summary: ReceivablesSummary,
    receivables: List<Receivable>,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    strings: Strings,
    context: Context,
    onAddReceivable: () -> Unit,
    onRecordPayment: (Receivable) -> Unit,
    onEdit: (Receivable) -> Unit,
    onDelete: (Receivable) -> Unit,
    onGenerateReminder: (Receivable) -> Unit
) {
    item {
        Spacer(modifier = Modifier.height(4.dp))
        ReceivablesSummaryHeroCard(
            summary = summary,
            currencySymbol = currencySymbol,
            isPrivacyMode = isPrivacyMode
        )
    }

    item {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${strings.pendingDebtors} (${receivables.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextMuted,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "+ ${strings.addReceivable}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorAccent,
                modifier = Modifier.clickable { onAddReceivable() }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    if (receivables.isEmpty()) {
        item {
            AppCard(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                padding = 24.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Outlined.Handshake, null, tint = ColorTextMuted, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(strings.noReceivablesFound, style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp), color = ColorTextPrimary)
                    Text(strings.noReceivablesHint, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = ColorTextSecondary)
                }
            }
        }
    } else {
        items(receivables, key = { it.id }) { rec ->
            ReceivableItemCard(
                receivable = rec,
                currencySymbol = currencySymbol,
                onRecordPayment = { onRecordPayment(rec) },
                onEdit = { onEdit(rec) },
                onDelete = { onDelete(rec) },
                onCall = {
                    if (rec.phoneNumber.isNotBlank()) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${rec.phoneNumber}"))
                        context.startActivity(intent)
                    }
                },
                onGenerateReminder = { onGenerateReminder(rec) }
            )
        }
    }
}
