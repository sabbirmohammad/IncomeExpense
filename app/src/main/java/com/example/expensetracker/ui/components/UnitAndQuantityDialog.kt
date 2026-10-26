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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun UnitAndQuantityDialog(
    initialQuantity: Double = 1.0,
    initialUnit: String = "কেজি",
    isBangla: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Double, unit: String) -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var selectedUnit by remember { mutableStateOf(initialUnit.ifBlank { "কেজি" }) }
    var quantityText by remember {
        mutableStateOf(if (initialQuantity % 1.0 == 0.0) initialQuantity.toInt().toString() else initialQuantity.toString())
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
                            .background(appColors.accent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Outlined.Scale, contentDescription = null, tint = appColors.accent, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = if (isBangla) "পরিমাণ ও একক নির্বাচন" else "Quantity & Unit",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = if (isBangla) "টাইপ (পিস/কেজি/লিটার) ও সংখ্যা নির্ধারণ করুন" else "Select unit type & specify amount",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = appColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Unit Type selection
                Text(
                    text = if (isBangla) "১. টাইপ / একক সিলেক্ট করুন" else "1. Select Unit Type",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp),
                    color = appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                UnitSelectorGrid(
                    availableUnits = availableUnits,
                    selectedUnit = selectedUnit,
                    onUnitSelected = { selectedUnit = it },
                    haptics = haptics
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Quantity Input with +/- Stepper
                Text(
                    text = if (isBangla) "২. পরিমাণ লিখুন" else "2. Enter Quantity",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp),
                    color = appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                QuantityStepperSection(
                    quantityText = quantityText,
                    onQuantityChange = { quantityText = it },
                    selectedUnit = selectedUnit,
                    quickPresets = quickPresets,
                    haptics = haptics
                )

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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = appColors.textSecondary),
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
                        colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                    ) {
                        Text(text = if (isBangla) "নিশ্চিত করুন" else "Confirm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
