package com.example.expensetracker.ui.transactions

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionFullReceiptPreviewDialog(
    show: Boolean,
    receiptBitmap: Bitmap?,
    typeAccentColor: Color,
    strings: Strings,
    onRemoveReceipt: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show || receiptBitmap == null) return

    val haptics = rememberAppHaptics()
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.96f))
        ) {
            ZoomableReceiptImage(
                receiptBitmap = receiptBitmap,
                contentDescription = strings.viewReceipt,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                onTransform = { zoom, panX, panY, containerSize ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    if (scale > 1.02f) {
                        val maxPanX = if (containerSize.width > 0) ((containerSize.width * (scale - 1f)) / 2f) else 1000f
                        val maxPanY = if (containerSize.height > 0) ((containerSize.height * (scale - 1f)) / 2f) else 1000f
                        offsetX = (offsetX + panX).coerceIn(-maxPanX, maxPanX)
                        offsetY = (offsetY + panY).coerceIn(-maxPanY, maxPanY)
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                },
                onDoubleTap = {
                    haptics.lightTick()
                    if (scale > 1.05f) {
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    } else {
                        scale = 2.5f
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            )

            ReceiptPreviewTopBar(
                strings = strings,
                onDismiss = onDismiss,
                onDeleteClick = {
                    haptics.warningFeedback()
                    showDeleteConfirm = true
                }
            )

            // Bottom control bar (Reset zoom button if zoomed)
            if (scale > 1.05f) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 24.dp)
                        .heightIn(min = 48.dp)
                        .clickable {
                            haptics.lightTick()
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (strings.isBangla) "রিসেট জুম (1x)" else "Reset Zoom (1x)",
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    ReceiptDeleteConfirmDialog(
        show = showDeleteConfirm,
        strings = strings,
        onConfirm = {
            showDeleteConfirm = false
            haptics.successFeedback()
            onRemoveReceipt()
            onDismiss()
        },
        onDismiss = { showDeleteConfirm = false }
    )
}
