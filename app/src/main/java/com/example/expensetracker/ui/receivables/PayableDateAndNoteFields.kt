package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun PayableDateAndNoteFields(
    dueDateMillis: Long?,
    onDueDateClick: () -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    dateFormat: SimpleDateFormat
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    OutlinedTextField(
        value = dueDateMillis?.let {
            val d = dateFormat.format(Date(it))
            if (strings.isBangla) convertToBengaliDigits(d) else d
        } ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(strings.dueDate) },
        leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = appColors.accent) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDueDateClick() },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = appColors.textPrimary,
            disabledBorderColor = appColors.border,
            disabledLabelColor = appColors.textSecondary,
            disabledLeadingIconColor = appColors.accent
        )
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text(strings.note) },
        singleLine = false,
        maxLines = 3,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = appColors.accent,
            unfocusedBorderColor = appColors.border
        )
    )
}
