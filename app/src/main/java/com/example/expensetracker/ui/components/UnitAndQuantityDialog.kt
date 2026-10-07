package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UnitAndQuantityDialog(
    initialQuantity: Double = 1.0,
    initialUnit: String = "কেজি",
    isBangla: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Double, unit: String) -> Unit
) {
    val haptics = rememberAppHaptics()
    var selectedUnit by remember { mutableStateOf(initialUnit.ifBlank { "কেজি" }) }
    var quantityText by remember {
        mutableStateOf(
            if (initialQuantity % 1.0 == 0.0) initialQuantity.toInt().toString() else initialQuantity.toString()
        )
    }

    val availableUnits = listOf(
        "কেজি" to if (isBangla) "কেজি (Kg)" else "Kg (কেজি)",
        "পিস" to if (isBangla) "পিস (Pcs)" else "Pcs (পিস)",
        "লিটার" to if (isBangla) "লিটার (Litre)" else "Litre (লিটার)",
        "গ্রাম" to if (isBangla) "গ্রাম (Gram)" else "Gram (গ্রাম)",
        "ডজন" to if (isBangla) "ডজন (Dozen)" else "Dozen (ডজন)",
        "প্যাকেট" to if (isBangla) "প্যাকেট (Pack)" else "Pack (প্যাকেট)",
        "হালি" to if (isBangla) "হালি (4 pcs)" else "Hali (হালি)",
        "আঁটি" to if (isBangla) "আঁটি (Bunch)" else "Bunch (আঁটি)",
        "বোতল" to if (isBangla) "বোতল (Bottle)" else "Bottle (বোতল)"
    )

    val quickPresets = listOf(
        0.5 to if (isBangla) "০.৫ (হাফ)" else "0.5 (Half)",
        1.0 to if (isBangla) "১" else "1",
        1.5 to if (isBangla) "১.৫ (দেড়)" else "1.5",
        2.0 to if (isBangla) "২" else "2",
        2.5 to if (isBangla) "২.৫ (আড়াই)" else "2.5",
        5.0 to if (isBangla) "৫" else "5"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = LocalAppColors.current.surface,
            border = BorderStroke(1.dp, LocalAppColors.current.border),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ColorAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Scale,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isBangla) "পরিমাণ ও একক নির্বাচন" else "Quantity & Unit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = if (isBangla) "টাইপ (পিস/কেজি/লিটার) ও সংখ্যা নির্ধারণ করুন" else "Select unit type & specify amount",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = ColorTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Unit Type selection
                Text(
                    text = if (isBangla) "১. টাইপ / একক সিলেক্ট করুন" else "1. Select Unit Type",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    ),
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableUnits.forEach { (unitKey, unitLabel) ->
                        val isSelected = selectedUnit == unitKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) ColorAccent else LocalAppColors.current.background
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ColorAccent else LocalAppColors.current.border,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    haptics.lightTick()
                                    selectedUnit = unitKey
                                }
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = unitLabel,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else ColorTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Quantity Input with +/- Stepper
                Text(
                    text = if (isBangla) "২. পরিমাণ লিখুন" else "2. Enter Quantity",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    ),
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Decrement button
                    IconButton(
                        onClick = {
                            haptics.lightTick()
                            val cur = MarketListParser.convertBengaliDigitsToEnglish(quantityText).toDoubleOrNull() ?: 1.0
                            val step = if (cur <= 1.0) 0.5 else 1.0
                            val next = (cur - step).coerceAtLeast(0.5)
                            quantityText = if (next % 1.0 == 0.0) next.toInt().toString() else next.toString()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LocalAppColors.current.background)
                            .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Decrease",
                            tint = ColorTextPrimary
                        )
                    }

                    // Quantity TextField
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorAccent,
                            unfocusedBorderColor = LocalAppColors.current.border,
                            focusedContainerColor = LocalAppColors.current.background,
                            unfocusedContainerColor = LocalAppColors.current.background
                        ),
                        trailingIcon = {
                            Text(
                                text = selectedUnit,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                ),
                                color = ColorAccent,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    )

                    // Increment button
                    IconButton(
                        onClick = {
                            haptics.lightTick()
                            val cur = MarketListParser.convertBengaliDigitsToEnglish(quantityText).toDoubleOrNull() ?: 1.0
                            val step = if (cur < 1.0) 0.5 else 1.0
                            val next = cur + step
                            quantityText = if (next % 1.0 == 0.0) next.toInt().toString() else next.toString()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LocalAppColors.current.background)
                            .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Increase",
                            tint = ColorTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Preset Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    quickPresets.forEach { (presetVal, presetLabel) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LocalAppColors.current.background)
                                .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(6.dp))
                                .clickable {
                                    haptics.lightTick()
                                    quantityText = if (presetVal % 1.0 == 0.0) presetVal.toInt().toString() else presetVal.toString()
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = presetLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            haptics.lightTick()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTextSecondary),
                        border = BorderStroke(1.dp, LocalAppColors.current.border)
                    ) {
                        Text(text = if (isBangla) "বাতিল" else "Cancel")
                    }

                    Button(
                        onClick = {
                            haptics.mediumClick()
                            val cleanQtyStr = MarketListParser.convertBengaliDigitsToEnglish(quantityText)
                            val finalQty = cleanQtyStr.toDoubleOrNull() ?: 1.0
                            onConfirm(finalQty, selectedUnit)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
                    ) {
                        Text(text = if (isBangla) "নিশ্চিত করুন" else "Confirm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
