package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Smart Item Substitution Dialog.
 * Allows replacing an item that was unavailable at the bazaar (e.g. ইলিশ মাছ -> রুই মাছ)
 * with 1-tap suggestions or custom entry, preserving quantity and list state.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MarketSubstituteItemDialog(
    item: MarketItem,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConfirmSubstitute: (newTitle: String) -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var substituteName by remember { mutableStateOf("") }

    val category = remember(item.name) { getMarketItemCategory(item.name) }
    val dept = remember(category) { getDepartmentMeta(category) }

    val suggestions = remember(category, isBangla) {
        when (category) {
            "meat_fish" -> if (isBangla) listOf("রুই মাছ", "কাতলা মাছ", "তেলাপিয়া মাছ", "পাঙ্গাস মাছ", "ব্রয়লার মুরগি", "সোনালী মুরগি", "গরুর মাংস")
                           else listOf("Rui Fish", "Katla Fish", "Tilapia", "Chicken", "Beef")
            "veggies" -> if (isBangla) listOf("পটল", "ঢেঁড়স", "চিচিঙ্গা", "ঝিঙে", "করলা", "লাউ", "মিষ্টি কুমড়া", "পেঁপে")
                         else listOf("Pointed Gourd", "Okra", "Snake Gourd", "Bottle Gourd", "Pumpkin", "Papaya")
            "dairy_bakery" -> if (isBangla) listOf("ফার্মের ডিম", "দেশি ডিম", "তরল দুধ", "পাউরুটি", "দই")
                              else listOf("Farm Eggs", "Country Eggs", "Liquid Milk", "Bread", "Curd")
            else -> if (isBangla) listOf("সয়াবিন তেল", "সরিষার তেল", "মসুর ডাল", "মুগ ডাল", "মিনিকেট চাল", "নাজিরশাইল চাল")
                    else listOf("Soybean Oil", "Mustard Oil", "Lentils", "Miniket Rice")
        }
    }

    val submit = {
        if (substituteName.isNotBlank()) {
            haptics.mediumClick()
            onConfirmSubstitute(substituteName.trim())
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = LocalAppColors.current.surface,
            border = BorderStroke(1.dp, LocalAppColors.current.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) "বিকল্প পণ্য নির্বাচন" else "Substitute Item",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBangla)
                                "'${item.name}' বাজারে না পেলে এর বিকল্প বাছুন"
                            else
                                "Choose a substitute if '${item.name}' is unavailable",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = appColors.textSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.accent.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${dept.icon} ${item.formatQuantity()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = appColors.accent,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input
                OutlinedTextField(
                    value = substituteName,
                    onValueChange = { substituteName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = if (isBangla) "বিকল্প পণ্যের নাম লিখুন..." else "Enter substitute item name...",
                            color = appColors.textSecondary.copy(alpha = 0.5f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = LocalAppColors.current.border,
                        focusedContainerColor = LocalAppColors.current.background,
                        unfocusedContainerColor = LocalAppColors.current.background
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Suggestions Chips
                Text(
                    text = if (isBangla) "কুইক বিকল্প পরামর্শ:" else "Quick Suggestions:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                    color = appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestions.forEach { suggestion ->
                        val isSelected = substituteName.trim() == suggestion
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) appColors.accent else LocalAppColors.current.background)
                                .clickable {
                                    haptics.lightTick()
                                    substituteName = suggestion
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) androidx.compose.ui.graphics.Color.White else appColors.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action buttons
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
                        onClick = submit,
                        enabled = substituteName.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                    ) {
                        Text(
                            text = if (isBangla) "বদলান" else "Replace",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
