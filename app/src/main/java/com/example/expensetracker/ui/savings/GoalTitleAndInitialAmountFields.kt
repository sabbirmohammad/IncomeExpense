package com.example.expensetracker.ui.savings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun GoalTitleField(
    title: String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(strings.goalTitle, fontSize = 12.sp, color = appColors.textSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = {
                Text(
                    if (strings.isBangla) "যেমন: নতুন বাইক, জরুরি তহবিল, হজ্ব"
                    else "e.g. New Bike, Emergency Fund, Laptop",
                    color = appColors.textMuted
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(SmallElementRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = appColors.surfaceVariant,
                unfocusedContainerColor = appColors.surfaceVariant,
                focusedBorderColor = appColors.accent,
                unfocusedBorderColor = appColors.border,
                focusedTextColor = appColors.textPrimary,
                unfocusedTextColor = appColors.textPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun GoalInitialSavedField(
    initialSavedText: String,
    onInitialSavedChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text("${strings.savedAmount} (${strings.optional})", fontSize = 12.sp, color = appColors.textSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = initialSavedText,
            onValueChange = { onInitialSavedChange(it.filter { c -> c.isDigit() || c == '.' }) },
            placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = appColors.textMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(SmallElementRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = appColors.surfaceVariant,
                unfocusedContainerColor = appColors.surfaceVariant,
                focusedBorderColor = appColors.accent,
                unfocusedBorderColor = appColors.border,
                focusedTextColor = appColors.textPrimary,
                unfocusedTextColor = appColors.textPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
