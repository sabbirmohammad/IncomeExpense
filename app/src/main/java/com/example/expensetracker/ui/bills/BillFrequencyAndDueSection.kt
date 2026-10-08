package com.example.expensetracker.ui.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.expensetracker.data.model.BillFrequency
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun BillFrequencySelector(
    frequency: BillFrequency,
    onFrequencyChange: (BillFrequency) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = strings.frequency,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = ColorTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(SmallElementRadius))
                .background(Color(0xFF0F131D))
                .border(1.dp, ColorBorder, RoundedCornerShape(SmallElementRadius))
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(BillFrequency.MONTHLY, BillFrequency.WEEKLY, BillFrequency.YEARLY).forEach { freq ->
                val isSelected = frequency == freq
                val label = if (strings.isBangla) freq.bnDisplayName else freq.displayName
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(SmallElementRadius - 2.dp))
                        .background(if (isSelected) ColorAccent else Color.Transparent)
                        .clickable {
                            haptics.lightTick()
                            onFrequencyChange(freq)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (isSelected) Color.White else ColorTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun BillDueDayInput(
    dueDayText: String,
    onDueDayChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "${strings.dueDay} (1 - 31)",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = ColorTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = dueDayText,
            onValueChange = {
                if (it.length <= 2) {
                    onDueDayChange(it.filter { c -> c.isDigit() })
                }
            },
            placeholder = { Text(if (strings.isBangla) "৫" else "5", color = ColorTextMuted, fontSize = 13.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(SmallElementRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorAccent,
                unfocusedBorderColor = ColorBorder,
                focusedTextColor = ColorTextPrimary,
                unfocusedTextColor = ColorTextPrimary,
                focusedContainerColor = Color(0xFF0F131D),
                unfocusedContainerColor = Color(0xFF0F131D)
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
        )
    }
}
