package com.example.expensetracker.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun ReportExportActionsCard(
    isExporting: Boolean,
    hasTransactions: Boolean,
    strings: Strings,
    onExportPdf: () -> Unit,
    onViewPdf: () -> Unit,
    onExportCsv: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = ColorSurface,
        border = BorderStroke(1.dp, ColorBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (strings.isBangla) "রিপোর্ট ডাউনলোড ও শেয়ার" else "Export & Share Statement",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ColorTextPrimary
            )

            // 1. PDF Share Button (Primary)
            Button(
                onClick = {
                    haptics.mediumClick()
                    onExportPdf()
                },
                enabled = !isExporting && hasTransactions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorAccent,
                    contentColor = Color.White
                )
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.generatingReport, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Icon(
                        imageVector = Icons.Outlined.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.exportPdf, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Direct PDF View
                OutlinedButton(
                    onClick = {
                        haptics.lightTick()
                        onViewPdf()
                    },
                    enabled = !isExporting && hasTransactions,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ColorBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTextPrimary)
                ) {
                    Icon(Icons.Outlined.Visibility, null, modifier = Modifier.size(16.dp), tint = ColorAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.viewPdf, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                // CSV Export
                OutlinedButton(
                    onClick = {
                        haptics.lightTick()
                        onExportCsv()
                    },
                    enabled = !isExporting && hasTransactions,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ColorBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTextPrimary)
                ) {
                    Icon(Icons.Outlined.TableChart, null, modifier = Modifier.size(16.dp), tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.exportCsv, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
