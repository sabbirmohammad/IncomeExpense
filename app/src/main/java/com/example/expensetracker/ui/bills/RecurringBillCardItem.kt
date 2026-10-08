package com.example.expensetracker.ui.bills

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun RecurringBillCardItem(
    bill: RecurringBill,
    currencySymbol: String,
    onPayNow: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val strings = LocalAppStrings.current
    val categoryColor = CategoryIconHelper.parseColor(bill.categoryColor)

    AppCard(
        modifier = modifier.fillMaxWidth(),
        padding = 14.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                            .size(42.dp)
                            .clip(RoundedCornerShape(SmallElementRadius))
                            .background(categoryColor.copy(alpha = 0.16f))
                            .border(1.dp, categoryColor.copy(alpha = 0.35f), RoundedCornerShape(SmallElementRadius)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = CategoryIconHelper.getIcon(bill.categoryIcon),
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bill.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            color = ColorTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val freqText = if (strings.isBangla) bill.frequency.bnDisplayName else bill.frequency.displayName
                            Text(
                                text = "$freqText • ${if (strings.isBangla) "${bill.dueDay} তারিখ" else "Day ${bill.dueDay}"}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ColorTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (!bill.accountName.isNullOrBlank()) {
                                Text(
                                    text = "• ${bill.accountName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = ColorAccent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatCurrency(bill.amount, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = ColorTextPrimary,
                        maxLines = 1,
                        softWrap = false
                    )

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = ColorTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF0F131D))
                        ) {
                            DropdownMenuItem(
                                text = { Text(strings.editBill, color = ColorTextPrimary) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = ColorAccent) },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(strings.delete, color = ColorDangerExpense) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = ColorDangerExpense) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HairlineDivider()
            Spacer(modifier = Modifier.height(10.dp))

            BillPaymentStatusRow(
                bill = bill,
                onPayNow = onPayNow
            )
        }
    }
}
