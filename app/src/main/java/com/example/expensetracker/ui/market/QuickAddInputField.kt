package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun QuickAddInputField(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onOpenPasteSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val isBangla = strings.isBangla

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.surface,
        border = BorderStroke(HairlineInkStroke, appColors.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                placeholder = {
                    Text(
                        text = strings.marketAddItemPlaceholder,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = appColors.textMuted
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (inputText.isNotBlank()) {
                            onSubmit()
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = appColors.accent
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = appColors.textPrimary
                ),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSubmit()
                    }
                },
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .size(44.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank()) appColors.accent
                            else appColors.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = if (isBangla) "যোগ করুন" else "Add",
                        tint = if (inputText.isNotBlank()) Color.White else appColors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = {
                    haptics.lightTick()
                    onOpenPasteSheet()
                },
                modifier = Modifier
                    .size(44.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(appColors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentPaste,
                        contentDescription = strings.marketPasteFromWhatsApp,
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}
