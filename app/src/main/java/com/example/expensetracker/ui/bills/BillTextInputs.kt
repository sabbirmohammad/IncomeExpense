package com.example.expensetracker.ui.bills

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun BillTitleInput(
    title: String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        placeholder = { Text(strings.billTitle, color = appColors.textMuted, fontSize = 13.sp) },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SmallElementRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = appColors.accent,
            unfocusedBorderColor = appColors.border,
            focusedTextColor = appColors.textPrimary,
            unfocusedTextColor = appColors.textPrimary,
            focusedContainerColor = appColors.surfaceVariant,
            unfocusedContainerColor = appColors.surfaceVariant
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
    )
}

@Composable
fun BillNotesInput(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        placeholder = { Text(strings.notesOptional, color = appColors.textMuted, fontSize = 13.sp) },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SmallElementRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = appColors.accent,
            unfocusedBorderColor = appColors.border,
            focusedTextColor = appColors.textPrimary,
            unfocusedTextColor = appColors.textPrimary,
            focusedContainerColor = appColors.surfaceVariant,
            unfocusedContainerColor = appColors.surfaceVariant
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
    )
}
