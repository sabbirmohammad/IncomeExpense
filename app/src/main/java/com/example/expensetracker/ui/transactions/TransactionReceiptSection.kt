package com.example.expensetracker.ui.transactions

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings

@Composable
fun TransactionReceiptTrigger(
    isReceiptAttached: Boolean,
    isBangla: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isReceiptAttached) ColorSuccessIncome.copy(alpha = 0.15f) else Color(0xFF0D9488).copy(alpha = 0.08f),
        border = BorderStroke(1.dp, if (isReceiptAttached) ColorSuccessIncome else Color(0xFF0D9488).copy(alpha = 0.35f)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isReceiptAttached) Icons.Outlined.Check else Icons.Outlined.ReceiptLong,
                contentDescription = null,
                tint = if (isReceiptAttached) ColorSuccessIncome else Color(0xFF0D9488),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isReceiptAttached) (if (isBangla) "রসিদ যুক্ত আছে" else "Receipt Added")
                       else (if (isBangla) "রসিদ / ভাউচার" else "Attach Receipt"),
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = if (isReceiptAttached) ColorSuccessIncome else ColorTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TransactionReceiptPreviewBar(
    visible: Boolean,
    receiptBitmap: Bitmap?,
    isScanningReceipt: Boolean,
    scanStatusMessage: String?,
    typeAccentColor: Color,
    strings: Strings,
    onViewReceipt: () -> Unit,
    onRemoveReceipt: () -> Unit,
    onScanReceiptClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = visible, modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = LocalAppColors.current.surfaceVariant,
            border = BorderStroke(1.dp, LocalAppColors.current.border),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ColorBorder.copy(alpha = 0.4f))
                            .clickable(onClick = onViewReceipt),
                        contentAlignment = Alignment.Center
                    ) {
                        if (receiptBitmap != null) {
                            Image(
                                bitmap = receiptBitmap.asImageBitmap(),
                                contentDescription = "Receipt Thumbnail",
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                tint = ColorTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (strings.isBangla) "রসিদ সংযুক্ত আছে" else "Receipt Attached",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp
                            ),
                            color = ColorTextPrimary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = strings.viewReceipt,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = typeAccentColor,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.clickable(onClick = onViewReceipt)
                            )
                            Text("•", color = ColorTextMuted, fontSize = 10.sp)
                            Text(
                                text = strings.removeReceipt,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ColorDangerExpense,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.clickable(onClick = onRemoveReceipt)
                            )
                        }
                    }

                    Button(
                        onClick = onScanReceiptClick,
                        enabled = !isScanningReceipt && receiptBitmap != null,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorAccent.copy(alpha = 0.15f),
                            contentColor = ColorAccent
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        elevation = null
                    ) {
                        if (isScanningReceipt) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = ColorAccent,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SmartToy,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = strings.scanReceiptWithAi,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                    color = ColorAccent
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = scanStatusMessage != null) {
                    scanStatusMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (msg == strings.receiptScanSuccess) ColorSuccessIncome else ColorTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
