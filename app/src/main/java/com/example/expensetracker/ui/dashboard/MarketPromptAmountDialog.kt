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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Clean & fast price entry popup for an item in the market checklist.
 * - Shows item name and quantity badge.
 * - Auto-focuses keyboard.
 * - Provides quick price increment chips (+50, +100, +200, +500).
 * - Supports free/zero price option or cancel.
 */
@Composable
fun MarketPromptAmountDialog(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double) -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val focusRequester = remember { FocusRequester() }

    var promptAmountText by remember {
        mutableStateOf(
            item.amount?.let { amt ->
                if (amt > 0) {
                    if (amt % 1.0 == 0.0) amt.toInt().toString() else amt.toString()
                } else ""
            } ?: ""
        )
    }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    val submitAmount = {
        val cleanNum = MarketListParser.convertBengaliDigitsToEnglish(promptAmountText)
        val amt = cleanNum.toDoubleOrNull() ?: 0.0
        haptics.mediumClick()
        onConfirm(amt)
    }

    val addQuickAmount = { addAmt: Int ->
        haptics.lightTick()
        val current = MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull() ?: 0.0
        val nextAmt = (current + addAmt).toInt()
        promptAmountText = nextAmt.toString()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = LocalAppColors.current.surface,
            border = BorderStroke(1.dp, LocalAppColors.current.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header: Item name + quantity pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = appColors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.accent.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = item.formatQuantity(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = appColors.accent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBangla) "পণ্যটির ক্রয়মূল্য লিখুন:" else "Enter actual purchase price:",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Price Input Field
                OutlinedTextField(
                    value = promptAmountText,
                    onValueChange = { promptAmountText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submitAmount() }),
                    leadingIcon = {
                        Text(
                            text = currencySymbol,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = appColors.accent
                            )
                        )
                    },
                    placeholder = {
                        Text(
                            text = if (isBangla) "০" else "0",
                            color = appColors.textSecondary.copy(alpha = 0.5f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = LocalAppColors.current.border,
                        focusedContainerColor = LocalAppColors.current.background,
                        unfocusedContainerColor = LocalAppColors.current.background
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick increment chips: +50, +100, +200, +500
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val chips = listOf(50, 100, 200, 500)
                    chips.forEach { chipValue ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LocalAppColors.current.background)
                                .clickable { addQuickAmount(chipValue) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val chipText = if (isBangla) "+${convertToBengaliDigits(chipValue.toString())}" else "+$chipValue"
                            Text(
                                text = chipText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = appColors.accent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action buttons: Cancel, Free, Confirm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, LocalAppColors.current.border)
                    ) {
                        Text(
                            text = if (isBangla) "বাতিল" else "Cancel",
                            color = appColors.textSecondary,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = submitAmount,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                    ) {
                        Text(
                            text = if (isBangla) "যুক্ত করুন" else "Save Price",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Subtle option: Mark without price
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = {
                            haptics.lightTick()
                            onConfirm(0.0)
                        }
                    ) {
                        Text(
                            text = if (isBangla) "মূল্য ছাড়াই কেনা চিহ্নিত করুন" else "Mark purchased without price",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = appColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
