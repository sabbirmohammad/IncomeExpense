package com.example.expensetracker.ui.share

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PictureAsPdf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.toFinancialSummary
import com.example.expensetracker.ui.components.StatementExportHelper
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun SharePdfSection(
    periodTitle: String,
    periodSubtitle: String,
    totalIncome: Double,
    totalExpense: Double,
    transactions: List<Transaction>,
    userProfile: UserProfile,
    strings: Strings,
    context: Context,
    coroutineScope: CoroutineScope,
    isSharing: Boolean,
    isSharingPdf: Boolean,
    isLoading: Boolean,
    onSharingPdfChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val pdfRed = if (appColors.isDark) Color(0xFFE53935) else Color(0xFFDC2626)
    Column(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = appColors.surface,
            border = BorderStroke(1.dp, appColors.border),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(pdfRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PictureAsPdf,
                            contentDescription = null,
                            tint = pdfRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.formatPdf,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$periodTitle • $periodSubtitle",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = appColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(strings.income, fontSize = 11.sp, color = appColors.textSecondary)
                        Text(
                            formatCurrency(totalIncome, userProfile.currencySymbol),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = appColors.income
                        )
                    }
                    Column {
                        Text(strings.expense, fontSize = 11.sp, color = appColors.textSecondary)
                        Text(
                            formatCurrency(totalExpense, userProfile.currencySymbol),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = appColors.expense
                        )
                    }
                    Column {
                        Text(strings.transactionsTitle, fontSize = 11.sp, color = appColors.textSecondary)
                        Text(
                            "${transactions.size} ${strings.recordsFound}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = appColors.textPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (!isSharing && !isSharingPdf && !isLoading) {
                    coroutineScope.launch {
                        onSharingPdfChange(true)
                        try {
                            withContext(Dispatchers.IO) {
                                val summary = transactions.toFinancialSummary()
                                StatementExportHelper.exportAndSharePdf(
                                    context = context,
                                    profile = userProfile,
                                    summary = summary,
                                    transactions = transactions,
                                    periodTitle = "$periodTitle ($periodSubtitle)"
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            onSharingPdfChange(false)
                        }
                    }
                }
            },
            enabled = !isSharing && !isSharingPdf && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = pdfRed,
                contentColor = Color.White
            )
        ) {
            if (isSharingPdf) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${strings.sharePdfStatement}...",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    fontSize = 14.sp
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.PictureAsPdf,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.sharePdfStatement,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    fontSize = 14.sp
                )
            }
        }
    }
}
