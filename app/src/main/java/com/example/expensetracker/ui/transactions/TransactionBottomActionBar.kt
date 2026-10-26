package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.toParsedAmountOrNull

@Composable
fun TransactionBottomActionBar(
    isExistingTransaction: Boolean,
    isEditMode: Boolean,
    isExpense: Boolean,
    typeAccentColor: Color,
    amountText: String,
    currencySymbol: String,
    strings: Strings,
    isSaving: Boolean = false,
    onEditClick: () -> Unit,
    onCancelEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val isAmountValid = (amountText.toParsedAmountOrNull() ?: 0.0) > 0.0
    val currentAmountVal = amountText.toParsedAmountOrNull()
    val amountLabel = if (currentAmountVal != null && currentAmountVal > 0.0) {
        " • " + formatCurrency(currentAmountVal, currencySymbol)
    } else ""
    val isIncome = !isExpense || typeAccentColor == appColors.income
    val actionContentColor = if (isIncome && appColors.isDark) Color(0xFF052B14) else Color.White

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = appColors.surface,
        border = BorderStroke(1.dp, appColors.border)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            if (isExistingTransaction && !isEditMode) {
                // View Mode: Primary "Edit Transaction" button
                Button(
                    onClick = {
                        haptics.mediumClick()
                        onEditClick()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.accent,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isExpense) "${strings.editTransaction} (${strings.expense})"
                               else "${strings.editTransaction} (${strings.income})",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            } else if (isExistingTransaction && isEditMode) {
                // Edit Mode: Cancel + Update buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            haptics.lightTick()
                            onCancelEditClick()
                        },
                        modifier = Modifier.weight(0.35f).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, appColors.border),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = appColors.textSecondary)
                    ) {
                        Text(
                            text = strings.cancel,
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Button(
                        onClick = { onSaveClick() },
                        enabled = isAmountValid && !isSaving,
                        modifier = Modifier.weight(0.65f).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAmountValid) typeAccentColor else typeAccentColor.copy(alpha = 0.4f),
                            contentColor = if (isAmountValid) actionContentColor else actionContentColor.copy(alpha = 0.6f),
                            disabledContainerColor = typeAccentColor.copy(alpha = 0.5f),
                            disabledContentColor = actionContentColor.copy(alpha = 0.7f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isAmountValid && !isSaving) 2.dp else 0.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = actionContentColor,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.saving,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.5.sp, fontWeight = FontWeight.Bold),
                                color = actionContentColor
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = actionContentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val updateBtnText = if (isExpense) "${strings.updateExpense}$amountLabel" else "${strings.updateIncome}$amountLabel"
                            Text(
                                text = updateBtnText,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.5.sp, fontWeight = FontWeight.Bold),
                                color = actionContentColor
                            )
                        }
                    }
                }
            } else {
                // New Transaction: Single Save button
                Button(
                    onClick = { onSaveClick() },
                    enabled = isAmountValid && !isSaving,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAmountValid) typeAccentColor else typeAccentColor.copy(alpha = 0.4f),
                        contentColor = if (isAmountValid) actionContentColor else actionContentColor.copy(alpha = 0.6f),
                        disabledContainerColor = typeAccentColor.copy(alpha = 0.5f),
                        disabledContentColor = actionContentColor.copy(alpha = 0.7f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isAmountValid && !isSaving) 2.dp else 0.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = actionContentColor,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.saving,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                            color = actionContentColor
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = actionContentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val saveBtnText = if (isExpense) "${strings.saveExpense}$amountLabel" else "${strings.saveIncome}$amountLabel"
                        Text(
                            text = saveBtnText,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                            color = actionContentColor
                        )
                    }
                }
            }
        }
    }
}
