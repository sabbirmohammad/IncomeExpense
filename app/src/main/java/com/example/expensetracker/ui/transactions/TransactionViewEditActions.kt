package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ShapePill
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionViewModeButton(
    isExpense: Boolean,
    actionContentColor: Color,
    strings: Strings,
    onEditClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    Button(
        onClick = {
            haptics.mediumClick()
            onEditClick()
        },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = ShapePill,
        colors = ButtonDefaults.buttonColors(
            containerColor = appColors.accent,
            contentColor = actionContentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = null,
            tint = actionContentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isExpense) "${strings.editTransaction} (${strings.expense})"
                   else "${strings.editTransaction} (${strings.income})",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
            color = actionContentColor
        )
    }
}

@Composable
fun TransactionEditModeRow(
    isExpense: Boolean,
    isAmountValid: Boolean,
    isSaving: Boolean,
    amountLabel: String,
    buttonContainerColor: Color,
    actionContentColor: Color,
    strings: Strings,
    onCancelEditClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
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
            shape = ShapePill,
            border = BorderStroke(HairlineInkStroke, appColors.border),
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
            shape = ShapePill,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonContainerColor,
                contentColor = if (isAmountValid) actionContentColor else actionContentColor.copy(alpha = 0.6f),
                disabledContainerColor = buttonContainerColor.copy(alpha = 0.45f),
                disabledContentColor = actionContentColor.copy(alpha = 0.7f)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
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
                val updateBtnText = if (isExpense) "${strings.updateExpense} •$amountLabel" else "${strings.updateIncome} •$amountLabel"
                Text(
                    text = updateBtnText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = actionContentColor
                )
            }
        }
    }
}
