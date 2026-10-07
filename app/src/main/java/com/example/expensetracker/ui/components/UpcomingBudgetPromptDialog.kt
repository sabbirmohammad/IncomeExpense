package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun UpcomingBudgetPromptDialog(
    currentBudgetAmount: Double,
    categoryBudgetCount: Int,
    currencySymbol: String,
    isBangla: Boolean,
    onKeepPreviousBudget: () -> Unit,
    onSetNewBudget: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val appColors = LocalAppColors.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = appColors.surface,
            border = BorderStroke(1.dp, appColors.border),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(ColorAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = "Budget Icon",
                        tint = ColorAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = strings.upcomingBudgetPromptTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp, fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = strings.upcomingBudgetPromptDesc,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                    color = appColors.textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (currentBudgetAmount > 0) {
                    BudgetPromptPreviewCard(
                        amount = currentBudgetAmount,
                        categoryCount = categoryBudgetCount,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Action 1: Keep Previous Budget (Primary)
                Button(
                    onClick = {
                        haptics.mediumClick()
                        onKeepPreviousBudget()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorAccent, contentColor = Color.White)
                ) {
                    Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.keepPreviousBudget,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 2: Set New Budget (Outlined)
                OutlinedButton(
                    onClick = {
                        haptics.lightTick()
                        onSetNewBudget()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, ColorAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorAccent)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.setNewBudget,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Action 3: Not Now / Dismiss
                TextButton(
                    onClick = {
                        haptics.lightTick()
                        onDismiss()
                    }
                ) {
                    Text(text = strings.notNow, style = MaterialTheme.typography.bodyMedium.copy(color = appColors.textSecondary))
                }
            }
        }
    }
}

@Composable
private fun BudgetPromptPreviewCard(
    amount: Double,
    categoryCount: Int,
    currencySymbol: String,
    isBangla: Boolean
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = appColors.background,
        border = BorderStroke(1.dp, appColors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = strings.currentMonthBudgetLabel, style = MaterialTheme.typography.labelMedium, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = formatCurrency(amount, currencySymbol, isBangla),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = ColorAccent
                )
                if (categoryCount > 0) {
                    Text(
                        text = "${DateTimeHelper.formatCounter(categoryCount, isBangla)} ${strings.categoriesConfiguredCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = appColors.textSecondary
                    )
                }
            }
        }
    }
}
