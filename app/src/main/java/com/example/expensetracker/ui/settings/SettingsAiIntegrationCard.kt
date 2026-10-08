package com.example.expensetracker.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.ApiConnectionTestResult
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun SettingsAiIntegrationCard(
    userProfile: UserProfile,
    isTestingApi: Boolean,
    apiConnectionTest: ApiConnectionTestResult?,
    onSaveKey: (String) -> Unit,
    onTestKey: (String) -> Unit,
    onClearKey: () -> Unit,
    onSuccessSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val focusManager = LocalFocusManager.current
    var apiKeyInput by remember(userProfile.geminiApiKey) { mutableStateOf(userProfile.geminiApiKey ?: "") }
    var isKeyVisible by remember { mutableStateOf(false) }
    val isConfigured = !userProfile.geminiApiKey.isNullOrBlank()

    AppCard(
        modifier = modifier,
        backgroundColor = Color(0xFF131525),
        borderColor = ColorAccent.copy(alpha = 0.35f)
    ) {
        Column {
            SettingsAiCardHeader(
                isBangla = userProfile.appLanguage == "bn",
                isTestingApi = isTestingApi,
                isConfigured = isConfigured,
                apiConnectionTest = apiConnectionTest
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = apiKeyInput,
                onValueChange = { apiKeyInput = it },
                placeholder = {
                    Text(
                        text = if (userProfile.appLanguage == "bn") "এপিআই কি পেস্ট করুন (AIzaSy...)" else "Paste API key (AIzaSy...)",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextMuted
                    )
                },
                singleLine = true,
                visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    val trimmed = apiKeyInput.trim()
                    if (trimmed.isNotBlank()) {
                        onSaveKey(trimmed)
                    }
                }),
                trailingIcon = {
                    IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                        Icon(
                            imageVector = if (isKeyVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (isKeyVisible) "Hide key" else "Show key",
                            tint = if (isKeyVisible) ColorAccent else ColorTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedContainerColor = Color(0xFF0F111E),
                    unfocusedContainerColor = Color(0xFF0F111E),
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsAiKeyActionsRow(
                apiKeyInput = apiKeyInput,
                isConfigured = isConfigured,
                isTestingApi = isTestingApi,
                apiConnectionTest = apiConnectionTest,
                isBangla = userProfile.appLanguage == "bn",
                onClear = {
                    focusManager.clearFocus()
                    haptics.warningFeedback()
                    apiKeyInput = ""
                    onClearKey()
                },
                onTest = {
                    focusManager.clearFocus()
                    haptics.mediumClick()
                    val keyToTest = apiKeyInput.trim().ifEmpty { userProfile.geminiApiKey ?: "" }
                    if (keyToTest.isNotBlank()) {
                        onTestKey(keyToTest)
                    }
                },
                onSave = {
                    focusManager.clearFocus()
                    val trimmed = apiKeyInput.trim()
                    if (trimmed.isNotBlank()) {
                        onSaveKey(trimmed)
                        onTestKey(trimmed)
                        haptics.successFeedback()
                        onSuccessSaved()
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = strings.apiKeyPrivacyNotice,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    lineHeight = 14.sp
                ),
                color = ColorTextMuted
            )
        }
    }
}
