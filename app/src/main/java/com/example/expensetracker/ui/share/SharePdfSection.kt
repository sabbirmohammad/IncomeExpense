package com.example.expensetracker.ui.share

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.toFinancialSummary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.StatementExportHelper
import com.example.expensetracker.ui.components.Strings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        SharePdfSummaryCard(
            periodTitle = periodTitle,
            periodSubtitle = periodSubtitle,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            transactions = transactions,
            userProfile = userProfile,
            strings = strings,
            pdfRed = pdfRed
        )

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
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
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
}
