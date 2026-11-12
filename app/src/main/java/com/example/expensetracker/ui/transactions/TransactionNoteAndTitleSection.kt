package com.example.expensetracker.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * V7 Merged Title & Notes Section:
 * C4 rule: One "Title" field + a "+ Add note" text button that expands a second field.
 */
@Composable
fun TransactionNoteAndTitleSection(
    title: String,
    onTitleChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    typeAccentColor: Color,
    strings: Strings,
    modifier: Modifier = Modifier,
    onFocusChange: ((Boolean) -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var isNoteExpanded by remember(notes) {
        mutableStateOf(notes.isNotEmpty())
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = strings.titleOrDescription,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        // 1. Primary Title Field
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = {
                Text(
                    text = strings.titlePlaceholder,
                    color = appColors.textMuted.copy(alpha = 0.7f),
                    fontSize = 13.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(typeAccentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Title,
                        contentDescription = null,
                        tint = typeAccentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }
            },
            trailingIcon = {
                if (title.isNotEmpty()) {
                    IconButton(onClick = { onTitleChange("") }) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = "Clear",
                            tint = appColors.textMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(RadiusS),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = appColors.surfaceVariant,
                unfocusedContainerColor = appColors.surfaceVariant,
                focusedBorderColor = typeAccentColor.copy(alpha = 0.8f),
                unfocusedBorderColor = if (appColors.isDark) Color.Transparent else appColors.border,
                focusedTextColor = appColors.textPrimary,
                unfocusedTextColor = appColors.textPrimary,
                cursorColor = typeAccentColor
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            singleLine = true
        )

        // 2. Expandable "+ Add note" text button or expanded Notes field
        AnimatedVisibility(
            visible = !isNoteExpanded && notes.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            haptics.lightTick()
                            isNoteExpanded = true
                        }
                        .minimumInteractiveComponentSize()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = strings.addNote,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                        color = appColors.accent
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isNoteExpanded || notes.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.notesOptional,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                        color = appColors.textSecondary
                    )
                    if (notes.isEmpty()) {
                        Text(
                            text = strings.cancel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                            color = appColors.textSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    haptics.lightTick()
                                    isNoteExpanded = false
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    placeholder = {
                        Text(
                            text = strings.notesPlaceholder,
                            color = appColors.textMuted.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(appColors.border.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Notes,
                                contentDescription = null,
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        if (notes.isNotEmpty()) {
                            IconButton(onClick = { onNotesChange("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = "Clear",
                                    tint = appColors.textMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(RadiusS),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = appColors.surfaceVariant,
                        unfocusedContainerColor = appColors.surfaceVariant,
                        focusedBorderColor = typeAccentColor.copy(alpha = 0.8f),
                        unfocusedBorderColor = if (appColors.isDark) Color.Transparent else appColors.border,
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        cursorColor = typeAccentColor
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 1,
                    maxLines = 3
                )
            }
        }
    }
}
