package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency

/**
 * Modern, Ultra-Slim Market Summary Card.
 * Displays progress, total spent, target budget tracker, and quick sharing.
 */
@Composable
fun MarketSummarySection(
    totalCount: Int,
    purchasedCount: Int,
    totalSpent: Double,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier,
    selectedCategoryFilter: String? = null,
    onCategorySelected: ((String?) -> Unit)? = null,
    targetBudget: Double? = null,
    estimatedBudget: Double? = null,
    onSetTargetBudget: ((Double?) -> Unit)? = null,
    onShareClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    if (totalCount == 0) return

    val progress = if (totalCount > 0) purchasedCount.toFloat() / totalCount.toFloat() else 0f
    val percent = (progress * 100).toInt()
    var showBudgetDialog by remember { mutableStateOf(false) }
    var budgetInputText by remember(targetBudget) {
        mutableStateOf(targetBudget?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = LocalAppColors.current.surface,
        border = BorderStroke(1.dp, LocalAppColors.current.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (progress >= 1f) appColors.income else appColors.accent)
                    )
                    Text(
                        text = if (isBangla)
                            "${convertToBengaliDigits(purchasedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}টি কেনা সম্পন্ন (${convertToBengaliDigits(percent.toString())}%)"
                        else
                            "$purchasedCount/$totalCount items ($percent%)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = appColors.textPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isBangla) "মোট খরচ:" else "Spent:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = appColors.textSecondary
                    )
                    Text(
                        text = formatCurrency(totalSpent, currencySymbol),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = appColors.income
                    )

                    if (onShareClick != null) {
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = appColors.accent,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = appColors.income,
                trackColor = LocalAppColors.current.border.copy(alpha = 0.5f)
            )

            // Live Target Budget Row
            if (targetBudget != null && targetBudget > 0) {
                val budgetRemaining = targetBudget - totalSpent
                val isOverBudget = budgetRemaining < 0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clickable(enabled = onSetTargetBudget != null) { showBudgetDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = null,
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (isBangla) "বাজেট: ${formatCurrency(targetBudget, currencySymbol)}"
                                   else "Budget: ${formatCurrency(targetBudget, currencySymbol)}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = appColors.textSecondary
                        )
                    }

                    Text(
                        text = if (isOverBudget) {
                            if (isBangla) "বাজেট ছাড়িয়েছে (+${formatCurrency(-budgetRemaining, currencySymbol)})"
                            else "Over budget (+${formatCurrency(-budgetRemaining, currencySymbol)})"
                        } else {
                            if (isBangla) "বাকি: ${formatCurrency(budgetRemaining, currencySymbol)}"
                            else "Remaining: ${formatCurrency(budgetRemaining, currencySymbol)}"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isOverBudget) appColors.expense else appColors.income
                    )
                }
            } else if (estimatedBudget != null && estimatedBudget > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isBangla) "💡 আনুমানিক মোট বাজেট: ${formatCurrency(estimatedBudget, currencySymbol)}"
                                   else "💡 Est. Budget: ${formatCurrency(estimatedBudget, currencySymbol)}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold),
                            color = appColors.accent
                        )
                    }

                    if (onSetTargetBudget != null) {
                        Text(
                            text = if (isBangla) "+ কাস্টম বাজেট" else "+ Custom Budget",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.textSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { showBudgetDialog = true }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            } else if (onSetTargetBudget != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (isBangla) "+ টার্গেট বাজেট নির্ধারণ" else "+ Set Target Budget",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.accent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { showBudgetDialog = true }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }

    if (showBudgetDialog && onSetTargetBudget != null) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = {
                Text(
                    text = if (isBangla) "বাজারের টার্গেট বাজেট" else "Market Target Budget",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isBangla) "আজকের বাজারের জন্য আনুমানিক কত টাকার বাজেট রাখতে চান?"
                               else "Set an estimated target budget for today's market trip:",
                        style = MaterialTheme.typography.bodySmall,
                        color = appColors.textSecondary
                    )
                    OutlinedTextField(
                        value = budgetInputText,
                        onValueChange = { budgetInputText = it },
                        placeholder = { Text(if (isBangla) "যেমন: ২০০০" else "e.g. 2000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = budgetInputText.toDoubleOrNull()
                        onSetTargetBudget(parsed?.takeIf { it > 0 })
                        showBudgetDialog = false
                    }
                ) {
                    Text(if (isBangla) "সংরক্ষণ" else "Save")
                }
            },
            dismissButton = {
                Row {
                    if (targetBudget != null && targetBudget > 0) {
                        TextButton(
                            onClick = {
                                onSetTargetBudget(null)
                                budgetInputText = ""
                                showBudgetDialog = false
                            }
                        ) {
                            Text(if (isBangla) "বাজেট মুছুন" else "Clear", color = appColors.expense)
                        }
                    }
                    TextButton(onClick = { showBudgetDialog = false }) {
                        Text(if (isBangla) "বাতিল" else "Cancel")
                    }
                }
            }
        )
    }
}
