package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun PayablePersonAndPhoneFields(
    personName: String,
    onPersonNameChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    OutlinedTextField(
        value = personName,
        onValueChange = onPersonNameChange,
        label = { Text(strings.lenderName) },
        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = appColors.accent) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = appColors.accent,
            unfocusedBorderColor = appColors.border
        )
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChange,
        label = { Text(strings.phoneNumber) },
        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = appColors.textSecondary) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = appColors.accent,
            unfocusedBorderColor = appColors.border
        )
    )
}
