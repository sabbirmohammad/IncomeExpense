package com.example.expensetracker.ui.accounts

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
fun TransferNoteField(
    note: String,
    onNoteChange: (String) -> Unit
) {
    val colors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Text(strings.note, fontSize = 12.sp, color = colors.textSecondary)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        placeholder = { Text(if (strings.isBangla) "যেমন: ব্যাংক থেকে বিকাশ, এটিএম উত্তোলন" else "e.g. Bank to bKash add money, ATM withdrawal", color = colors.textMuted) },
        singleLine = true,
        shape = RoundedCornerShape(SmallElementRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant,
            focusedBorderColor = colors.accent,
            unfocusedBorderColor = colors.border,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
