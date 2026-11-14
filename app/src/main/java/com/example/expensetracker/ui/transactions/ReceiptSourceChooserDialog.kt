package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun ReceiptSourceChooserDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onSelectCamera: () -> Unit,
    onSelectGallery: () -> Unit,
    strings: Strings
) {
    if (!show) return

    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.attachReceipt,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Option 1: Take Photo
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.surfaceVariant,
                    border = BorderStroke(1.dp, appColors.border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            haptics.lightTick()
                            onDismiss()
                            onSelectCamera()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(appColors.accent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = strings.takePhoto,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = appColors.textPrimary
                            )
                            Text(
                                text = if (strings.isBangla) "ক্যামেরা দিয়ে রসিদের নতুন ছবি তুলুন" else "Take new receipt photo with camera",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = appColors.textSecondary
                            )
                        }
                    }
                }

                // Option 2: Choose from Gallery
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.surfaceVariant,
                    border = BorderStroke(1.dp, appColors.border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            haptics.lightTick()
                            onDismiss()
                            onSelectGallery()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(appColors.accent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoLibrary,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = strings.chooseFromGallery,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = appColors.textPrimary
                            )
                            Text(
                                text = if (strings.isBangla) "ডিভাইসের গ্যালারি থেকে ছবি নির্বাচন করুন" else "Select photo from device gallery",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text(strings.cancel, color = appColors.textSecondary)
            }
        },
        containerColor = appColors.surface,
        shape = RoundedCornerShape(16.dp)
    )
}
