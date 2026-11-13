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
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * V7 Export actions card with two distinct >=48dp export buttons (PDF & CSV)
 * plus quick view PDF action.
 */
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
    val appColors = LocalAppColors.current

    val borderStroke = if (!appColors.isDark) BorderStroke(HairlineInkStroke, appColors.border) else null

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RadiusM),
        color = appColors.surfaceRaised,
        border = borderStroke
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (strings.isBangla) "রিপোর্ট ডাউনলোড ও শেয়ার" else "Export & Share Statement",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = appColors.textPrimary
            )

            // Two Clear Export Buttons (PDF & CSV), strictly >= 48dp height
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Export PDF Button (Primary Accent)
                Button(
                    onClick = {
                        haptics.mediumClick()
                        onExportPdf()
                    },
                    enabled = !isExporting && hasTransactions,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.accent,
                        contentColor = Color.White
                    )
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.generatingReport,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.exportPdf,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // 2. Export CSV Button (Secondary Tonal)
                Button(
                    onClick = {
                        haptics.lightTick()
                        onExportCsv()
                    },
                    enabled = !isExporting && hasTransactions,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.accentContainer,
                        contentColor = appColors.accent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TableChart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = appColors.accent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.exportCsv,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = appColors.accent
                    )
                }
            }

            // Quick Direct PDF View (48dp height)
            OutlinedButton(
                onClick = {
                    haptics.lightTick()
                    onViewPdf()
                },
                enabled = !isExporting && hasTransactions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(HairlineInkStroke, appColors.border),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = appColors.textPrimary)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = appColors.accent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.viewPdf,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
