package com.example.expensetracker.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun SettingsDataBackupCard(
    userProfile: UserProfile,
    transactions: List<Transaction>,
    onCopyCsv: () -> Unit,
    onCopyJson: () -> Unit,
    onShareCsv: () -> Unit,
    onShareJson: () -> Unit,
    onImportJson: () -> Unit,
    onShareSlipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    AppCard(modifier = modifier) {
        Column {
            Text(
                text = "Data export",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = ColorTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Export your financial records for spreadsheets or external tools.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = ColorTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyCsv,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(SmallElementRadius),
                    border = BorderStroke(1.dp, ColorBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorSurface)
                ) {
                    Text(strings.copyCsv, color = ColorTextPrimary, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onCopyJson,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(SmallElementRadius),
                    border = BorderStroke(1.dp, ColorBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorSurface)
                ) {
                    Text(strings.copyJson, color = ColorTextPrimary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onShareCsv,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(SmallElementRadius),
                    border = BorderStroke(1.dp, ColorBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorSurface)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = ColorAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.shareCsv, color = ColorTextPrimary, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onShareJson,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(SmallElementRadius),
                    border = BorderStroke(1.dp, ColorBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorSurface)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = ColorAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.shareJson, color = ColorTextPrimary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onImportJson,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SmallElementRadius),
                border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorSurface)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FileDownload,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = ColorAccent
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.importJsonBackup,
                    color = ColorTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HairlineDivider()
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Official Statements (PDF & Excel)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = ColorTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Generate printable A4 PDF statement or UTF-8 BOM CSV sheet for all records.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ColorTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            SettingsExportTiles(
                userProfile = userProfile,
                transactions = transactions,
                onShareSlipClick = onShareSlipClick
            )
        }
    }
}
