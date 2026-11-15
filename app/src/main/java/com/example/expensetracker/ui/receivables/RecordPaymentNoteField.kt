package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun RecordPaymentNoteField(
    note: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Text(strings.note, fontSize = 12.sp, color = appColors.textSecondary)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        placeholder = { Text(strings.optional, color = appColors.textMuted) },
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
        modifier = modifier.fillMaxWidth()
    )
}
