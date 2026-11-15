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
fun ReceivableNoteField(
    notes: String,
    onNotesChange: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Text("${strings.note} (${strings.optional})", fontSize = 12.sp, color = appColors.textSecondary)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        placeholder = { Text(if (strings.isBangla) "যেমন: প্রোজেক্টের অগ্রিম, জরুরি ধার" else "e.g. Project advance, Emergency loan", color = appColors.textMuted) },
        maxLines = 2,
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
