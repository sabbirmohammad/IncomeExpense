package com.example.expensetracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun EditProfileDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit
) {
    val strings = LocalAppStrings.current
    var firstNameInput by remember { mutableStateOf(userProfile.firstName) }
    var lastNameInput by remember { mutableStateOf(userProfile.lastName) }
    var selectedAvatar by remember { mutableIntStateOf(userProfile.avatarIndex) }

    val effectiveFirst = firstNameInput.trim()
    val isFirstValid = effectiveFirst.isNotEmpty()
    val dashboardNamePreview = if (isFirstValid) effectiveFirst else "..."

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = ColorSurface,
        titleContentColor = ColorTextPrimary,
        textContentColor = ColorTextSecondary,
        title = {
            Text(
                text = strings.editProfileTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = strings.firstNameLabel,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    color = ColorTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = firstNameInput,
                    onValueChange = { firstNameInput = it },
                    placeholder = { Text(strings.egFirstName, color = ColorTextMuted) },
                    shape = RoundedCornerShape(SmallElementRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ColorSurface,
                        unfocusedContainerColor = ColorSurface,
                        focusedBorderColor = ColorAccent,
                        unfocusedBorderColor = ColorBorder,
                        focusedTextColor = ColorTextPrimary,
                        unfocusedTextColor = ColorTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = strings.lastNameLabel,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    color = ColorTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = lastNameInput,
                    onValueChange = { lastNameInput = it },
                    placeholder = { Text(strings.egLastName, color = ColorTextMuted) },
                    shape = RoundedCornerShape(SmallElementRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ColorSurface,
                        unfocusedContainerColor = ColorSurface,
                        focusedBorderColor = ColorAccent,
                        unfocusedBorderColor = ColorBorder,
                        focusedTextColor = ColorTextPrimary,
                        unfocusedTextColor = ColorTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.chooseAvatarLabel,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    color = ColorTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                EditProfileAvatarPicker(
                    selectedAvatar = selectedAvatar,
                    firstNameInput = firstNameInput,
                    lastNameInput = lastNameInput,
                    onAvatarSelected = { selectedAvatar = it }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(SmallElementRadius))
                        .background(ColorAccent.copy(alpha = 0.1f))
                        .border(1.dp, ColorAccent.copy(alpha = 0.3f), RoundedCornerShape(SmallElementRadius))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = strings.dashboardPreviewLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ColorAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = strings.previewGreeting(dashboardNamePreview),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ColorTextPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isFirstValid) {
                        onSave(firstNameInput.trim(), lastNameInput.trim(), selectedAvatar)
                    }
                },
                enabled = isFirstValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorAccent,
                    disabledContainerColor = ColorBorder
                ),
                shape = RoundedCornerShape(SmallElementRadius)
            ) {
                Text(strings.save, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = ColorTextSecondary)
            }
        }
    )
}
