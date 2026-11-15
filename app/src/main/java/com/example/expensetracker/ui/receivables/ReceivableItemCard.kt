package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivableStatus
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import java.util.Locale
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun ReceivableItemCard(
    receivable: Receivable,
    currencySymbol: String,
    onRecordPayment: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    var showMenu by remember { mutableStateOf(false) }

    val progress = if (receivable.amount > 0) {
        (receivable.amountReceived / receivable.amount).toFloat().coerceIn(0f, 1f)
    } else 0f

    val statusText = when (receivable.status) {
        ReceivableStatus.SETTLED -> strings.settledBadge
        ReceivableStatus.PARTIAL -> strings.partialBadge
        ReceivableStatus.PENDING -> strings.pendingBadge
    }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 16.dp
    ) {
        Column {
            ReceivableItemHeader(
                receivable = receivable,
                statusText = statusText,
                onRecordPayment = onRecordPayment,
                onEdit = onEdit,
                onDelete = onDelete
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(strings.remainingDue, fontSize = 11.sp, color = appColors.textMuted)
                    Text(
                        text = formatCurrency(receivable.remainingAmount, currencySymbol),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (receivable.isSettled) appColors.income else appColors.expense
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${strings.totalLent}: ${formatCurrency(receivable.amount, currencySymbol)}",
                        fontSize = 11.sp,
                        color = appColors.textSecondary,
                        style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum")
                    )
                    Text(
                        "${strings.totalCollected}: ${formatCurrency(receivable.amountReceived, currencySymbol)}",
                        fontSize = 11.sp,
                        color = appColors.income,
                        style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = appColors.income,
                trackColor = appColors.border
            )

            Spacer(modifier = Modifier.height(10.dp))

            ReceivableItemBottomBar(
                receivable = receivable,
                onRecordPayment = onRecordPayment,
                onCall = onCall
            )
        }
    }
}
