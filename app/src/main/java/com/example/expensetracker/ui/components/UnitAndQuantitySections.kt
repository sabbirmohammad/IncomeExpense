package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UnitSelectorGrid(
    availableUnits: List<Pair<String, String>>,
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        availableUnits.forEach { (unitKey, unitLabel) ->
            val isSelected = selectedUnit == unitKey
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) ColorAccent else LocalAppColors.current.background)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) ColorAccent else LocalAppColors.current.border,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        haptics.lightTick()
                        onUnitSelected(unitKey)
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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuantityStepperSection(
    quantityText: String,
    onQuantityChange: (String) -> Unit,
    selectedUnit: String,
    quickPresets: List<Pair<Double, String>>,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = {
                haptics.lightTick()
                val cur = MarketListParser.convertBengaliDigitsToEnglish(quantityText).toDoubleOrNull() ?: 1.0
                val step = if (cur <= 1.0) 0.5 else 1.0
                val next = (cur - step).coerceAtLeast(0.5)
                onQuantityChange(if (next % 1.0 == 0.0) next.toInt().toString() else next.toString())
            },
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(LocalAppColors.current.background)
                .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(10.dp))
        ) {
            Icon(imageVector = Icons.Filled.Remove, contentDescription = "Decrease", tint = ColorTextPrimary)
        }

        OutlinedTextField(
            value = quantityText,
            onValueChange = onQuantityChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold, color = ColorTextPrimary),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorAccent,
                unfocusedBorderColor = LocalAppColors.current.border,
                focusedContainerColor = LocalAppColors.current.background,
                unfocusedContainerColor = LocalAppColors.current.background
            ),
            trailingIcon = {
                Text(
                    text = selectedUnit,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                    color = ColorAccent,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        )

        IconButton(
            onClick = {
                haptics.lightTick()
                val cur = MarketListParser.convertBengaliDigitsToEnglish(quantityText).toDoubleOrNull() ?: 1.0
                val step = if (cur < 1.0) 0.5 else 1.0
                val next = cur + step
                onQuantityChange(if (next % 1.0 == 0.0) next.toInt().toString() else next.toString())
            },
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(LocalAppColors.current.background)
                .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(10.dp))
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Increase", tint = ColorTextPrimary)
        }
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
    ) {
        quickPresets.forEach { (presetVal, presetLabel) ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(LocalAppColors.current.background)
                    .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(6.dp))
                    .clickable {
                        haptics.lightTick()
                        onQuantityChange(if (presetVal % 1.0 == 0.0) presetVal.toInt().toString() else presetVal.toString())
                    }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(text = presetLabel, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = ColorTextSecondary)
            }
        }
    }
}
