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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.DateTimeHelper
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

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
        title = {
            Text(
                text = if (strings.isBangla) "স্টেটমেন্ট এক্সপোর্ট" else "Export Statement",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                color = ColorTextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val txCount = DateTimeHelper.formatCounter(transactions.size, strings.isBangla, userProfile.useBengaliNumerals || strings.isBangla)
                val exportSubtitleText = if (strings.isBangla) {
                    "আপনার $txCount টি লেনদেনের অফিসিয়াল রেকর্ড ডাউনলোড ও শেয়ার করুন।"
                } else {
                    "Download and share official records for your ${transactions.size} transactions."
                }
                Text(
                    text = exportSubtitleText,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ColorTextSecondary
                )

                ExportOptionItem(
                    icon = Icons.Outlined.PictureAsPdf,
                    iconTint = ColorDangerExpense,
                    title = if (strings.isBangla) "PDF আর্থিক স্টেটমেন্ট" else "PDF Financial Statement",
                    subtitle = if (strings.isBangla) "ব্র্যান্ডেড লেআউট, মেট্রিক্স ও লেজার সহ A4 ফরম্যাট" else "A4 format with branded layout, metrics & ledger",
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
                    iconTint = ColorSuccessIncome,
                    title = if (strings.isBangla) "এক্সেল স্প্রেডশীট (CSV)" else "Excel Spreadsheet (CSV)",
                    subtitle = if (strings.isBangla) "Excel ও Google Sheets এর জন্য UTF-8 BOM সমর্থিত" else "UTF-8 BOM encoded for Excel & Google Sheets",
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
                    iconTint = ColorAccent,
                    title = if (strings.isBangla) "ইমেজ স্লিপ ও সারাংশ" else "Image Slip & Summary",
                    subtitle = if (strings.isBangla) "লাইভ প্রিভিউ ও ছবি শেয়ারিং সুবিধা" else "Visual receipt card with live preview & image sharing",
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
                Text(strings.close, color = ColorTextSecondary)
            }
        }
    )
}

@Composable
private fun ExportOptionItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ColorBackground,
        border = BorderStroke(1.dp, ColorBorder),
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
                    color = ColorTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = ColorTextSecondary
                )
            }
        }
    }
}
