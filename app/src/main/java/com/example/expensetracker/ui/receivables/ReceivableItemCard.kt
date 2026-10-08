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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import java.util.Locale

@Composable
fun ReceivableItemCard(
    receivable: Receivable,
    currencySymbol: String,
    onRecordPayment: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit,
    onGenerateReminder: () -> Unit = {}
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ColorAccent.copy(alpha = 0.15f))
                            .border(1.dp, ColorAccent.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = receivable.personName.take(2).uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ColorAccent
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = receivable.personName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (receivable.phoneNumber.isNotBlank()) {
                            Text(
                                text = receivable.phoneNumber,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (receivable.status) {
                                    ReceivableStatus.SETTLED -> ColorSuccessIncome.copy(alpha = 0.15f)
                                    ReceivableStatus.PARTIAL -> Color(0xFFF0A63D).copy(alpha = 0.15f)
                                    ReceivableStatus.PENDING -> ColorDangerExpense.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (receivable.status) {
                                ReceivableStatus.SETTLED -> ColorSuccessIncome
                                ReceivableStatus.PARTIAL -> Color(0xFFF0A63D)
                                ReceivableStatus.PENDING -> ColorDangerExpense
                            }
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, null, tint = ColorTextSecondary, modifier = Modifier.size(18.dp))
                        }
                        ReceivableItemMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            isSettled = receivable.isSettled,
                            onGenerateReminder = onGenerateReminder,
                            onRecordPayment = onRecordPayment,
                            onEdit = onEdit,
                            onDelete = onDelete
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(strings.remainingDue, fontSize = 11.sp, color = ColorTextMuted)
                    Text(
                        text = formatCurrency(receivable.remainingAmount, currencySymbol),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (receivable.isSettled) ColorSuccessIncome else ColorDangerExpense
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("${strings.totalLent}: ${formatCurrency(receivable.amount, currencySymbol)}", fontSize = 11.sp, color = ColorTextSecondary)
                    Text("${strings.totalCollected}: ${formatCurrency(receivable.amountReceived, currencySymbol)}", fontSize = 11.sp, color = ColorSuccessIncome)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ColorSuccessIncome,
                trackColor = ColorBorder
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
