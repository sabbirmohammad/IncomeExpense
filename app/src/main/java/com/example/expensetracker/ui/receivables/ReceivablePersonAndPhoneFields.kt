package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
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
fun ReceivablePersonAndPhoneFields(
    personName: String,
    onPersonNameChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Text(strings.personName, fontSize = 12.sp, color = appColors.textSecondary)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = personName,
        onValueChange = onPersonNameChange,
        placeholder = { Text(if (strings.isBangla) "যেমন: করিম আহমেদ, বন্ধু তানভীর" else "e.g. Karim Ahmed, Friend Tanvir", color = appColors.textMuted) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Outlined.Person, null, tint = appColors.textSecondary, modifier = Modifier.size(18.dp)) },
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

    Spacer(modifier = Modifier.height(12.dp))

    Text("${strings.phoneNumber} (${strings.optional})", fontSize = 12.sp, color = appColors.textSecondary)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChange,
        placeholder = { Text("017XXXXXXXX", color = appColors.textMuted) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = appColors.textSecondary, modifier = Modifier.size(18.dp)) },
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
