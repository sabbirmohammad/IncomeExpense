package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.TableChart
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.toFinancialSummary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.StatementExportHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TransactionExportDialog(
    transactions: List<Transaction>,
    userProfile: UserProfile,
    strings: Strings,
    scope: CoroutineScope,
    onDismiss: () -> Unit,
    onOpenShareSlip: () -> Unit
) {
    val context = LocalContext.current
    val appColors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = appColors.surface,
        title = {
            Text(
                text = if (strings.isBangla) "স্টেটমেন্ট এক্সপোর্ট" else "Export Statement",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                color = appColors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExportOptionItem(
                    icon = Icons.Outlined.PictureAsPdf,
                    iconTint = appColors.expense,
                    title = if (strings.isBangla) "PDF আর্থিক স্টেটমেন্ট" else "PDF Financial Statement",
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val summary = transactions.toFinancialSummary()
                            StatementExportHelper.exportAndSharePdf(
                                context = context,
                                profile = userProfile,
                                summary = summary,
                                transactions = transactions,
                                periodTitle = if (strings.isBangla) "লেনদেন বিবরণী" else "Transactions Report"
                            )
                        }
                        onDismiss()
                    }
                )

                ExportOptionItem(
                    icon = Icons.Outlined.TableChart,
                    iconTint = appColors.income,
                    title = if (strings.isBangla) "এক্সেল স্প্রেডশীট (CSV)" else "Excel Spreadsheet (CSV)",
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            StatementExportHelper.exportAndShareCsv(
                                context = context,
                                profile = userProfile,
                                transactions = transactions,
                                periodTitle = if (strings.isBangla) "লেনদেন বিবরণী" else "Transactions Report"
                            )
                        }
                        onDismiss()
                    }
                )

                ExportOptionItem(
                    icon = Icons.Outlined.Image,
                    iconTint = appColors.accent,
                    title = if (strings.isBangla) "ইমেজ স্লিপ ও সারাংশ" else "Image Slip & Summary",
                    onClick = {
                        onDismiss()
                        onOpenShareSlip()
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.close, color = appColors.textSecondary)
            }
        }
    )
}

@Composable
private fun ExportOptionItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.surfaceVariant,
        border = BorderStroke(1.dp, appColors.border),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                    color = appColors.textPrimary
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = appColors.textSecondary
                    )
                }
            }
        }
    }
}
