package com.example.expensetracker.ui.transactions

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionReceiptSection(
    receiptImagePath: String?,
    receiptBitmap: Bitmap?,
    typeAccentColor: Color,
    strings: Strings,
    onLaunchPhotoPicker: () -> Unit = {},
    onViewReceipt: () -> Unit,
    onRemoveReceipt: () -> Unit,
    modifier: Modifier = Modifier,
    onLaunchCamera: () -> Unit = onLaunchPhotoPicker,
    onLaunchGallery: () -> Unit = onLaunchPhotoPicker,
    onChangeReceipt: () -> Unit = onLaunchPhotoPicker
) {
    val haptics = rememberAppHaptics()
    val appColors = LocalAppColors.current
    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    if (receiptImagePath == null) {
        // Unattached State: Modern compact single-button launcher under minimal vertical space
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = appColors.surfaceVariant,
            border = BorderStroke(1.dp, appColors.border),
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable {
                    haptics.lightTick()
                    onChangeReceipt()
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(appColors.accent.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (strings.isBangla) "রশিদ বা মেমোর ছবি যোগ করুন" else "Attach Receipt or Voucher",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (strings.isBangla) "ছবি তুলুন অথবা গ্যালারি থেকে বেছে নিন" else "Take photo or choose from gallery",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = appColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.accent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (strings.isBangla) "যোগ করুন" else "Attach",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = appColors.accent
                        )
                    }
                }
            }
        }
    } else {
        // Attached State: Polished thumbnail preview with subtle border, badge, preview launcher, and replace/delete actions
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = appColors.surfaceVariant,
            border = BorderStroke(1.dp, appColors.income.copy(alpha = 0.45f)),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Polished thumbnail preview with subtle border & launcher click
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(appColors.border.copy(alpha = 0.35f))
                        .border(1.dp, appColors.income.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable {
                            onViewReceipt()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (receiptBitmap != null) {
                        Image(
                            bitmap = receiptBitmap.asImageBitmap(),
                            contentDescription = strings.viewReceipt,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Middle: Badge and Preview Launcher
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Badge: "রশিদ সংযুক্ত"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = appColors.income,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (strings.isBangla) "রশিদ সংযুক্ত" else "Receipt Attached",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = strings.tapToViewReceipt,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = typeAccentColor
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable {
                            onViewReceipt()
                        }
                    )
                }

                // Replace action button ("ছবি পরিবর্তন") with minimum 48dp touch target
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        onChangeReceipt()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = if (strings.isBangla) "ছবি পরিবর্তন" else "Change Receipt",
                        tint = appColors.accent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Delete action button ("মুছে ফেলুন") with minimum 48dp touch target & confirmation
                IconButton(
                    onClick = {
                        haptics.warningFeedback()
                        showDeleteConfirmDialog = true
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = if (strings.isBangla) "মুছে ফেলুন" else strings.removeReceipt,
                        tint = appColors.expense,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Confirmation dialog before removing receipt
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = if (strings.isBangla) "রশিদ মুছে ফেলবেন?" else "Remove attached receipt?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
            },
            text = {
                Text(
                    text = if (strings.isBangla) "সংযুক্ত রশিদের ছবিটি স্থায়ীভাবে মুছে ফেলা হবে। আপনি কি নিশ্চিত?" else "The attached receipt image will be removed. Are you sure?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        haptics.successFeedback()
                        onRemoveReceipt()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = if (strings.isBangla) "মুছে ফেলুন" else "Remove",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = strings.cancel,
                        color = appColors.textSecondary
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }
}
