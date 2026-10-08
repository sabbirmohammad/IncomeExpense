package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionAmountInputField(
    amountText: String,
    onAmountChange: (String) -> Unit,
    currencySymbol: String,
    typeAccentColor: Color,
    amountFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = typeAccentColor.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, typeAccentColor.copy(alpha = 0.3f))
        ) {
            Text(
                text = currencySymbol,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.Black),
                color = typeAccentColor,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = amountText,
            onValueChange = { input ->
                val sanitized = input.filter { it.isDigit() || it == '.' || it in "+-×÷/* " }
                onAmountChange(sanitized)
            },
            placeholder = {
                Text(
                    text = "0.00",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = ColorTextMuted.copy(alpha = 0.4f)
                )
            },
            textStyle = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = ColorTextPrimary,
                textAlign = TextAlign.Start
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = typeAccentColor
            ),
            modifier = Modifier
                .weight(1f, fill = false)
                .focusRequester(amountFocusRequester),
            singleLine = true
        )

        if (amountText.isNotEmpty()) {
            IconButton(
                onClick = {
                    haptics.lightTick()
                    onAmountChange("")
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = "Clear",
                    tint = ColorTextMuted,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}
