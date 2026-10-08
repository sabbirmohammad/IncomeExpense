package com.example.expensetracker.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.StatementExportHelper

@Composable
fun SettingsExportTiles(
    userProfile: UserProfile,
    transactions: List<Transaction>,
    onShareSlipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. PDF Report Tile
        SettingsExportTileItem(
            icon = Icons.Outlined.PictureAsPdf,
            iconTint = ColorDangerExpense,
            title = if (strings.isBangla) "PDF রিপোর্ট" else "PDF Report",
            subtitle = if (strings.isBangla) "প্রিন্টযোগ্য A4" else "Print A4",
            onClick = {
                val summary = transactions.toFinancialSummary()
                StatementExportHelper.exportAndSharePdf(
                    context = context,
                    profile = userProfile,
                    summary = summary,
                    transactions = transactions,
                    periodTitle = if (strings.isBangla) "সম্পূর্ণ স্টেটমেন্ট" else "Complete Statement"
                )
            }
        )

        // 2. Image Slip Tile
        SettingsExportTileItem(
            icon = Icons.Outlined.Image,
            iconTint = ColorAccent,
            title = if (strings.isBangla) "ইমেজ স্লিপ" else "Image Slip",
            subtitle = if (strings.isBangla) "শেয়ারযোগ্য" else "Shareable",
            onClick = onShareSlipClick
        )

        // 3. Excel (CSV) Tile
        SettingsExportTileItem(
            icon = Icons.Outlined.TableChart,
            iconTint = ColorSuccessIncome,
            title = if (strings.isBangla) "এক্সেল (CSV)" else "Excel (CSV)",
            subtitle = if (strings.isBangla) "স্প্রেডশীট" else "Sheets",
            onClick = {
                StatementExportHelper.exportAndShareCsv(
                    context = context,
                    profile = userProfile,
                    transactions = transactions,
                    periodTitle = if (strings.isBangla) "সম্পূর্ণ বিবরণী" else "Complete Statement"
                )
            }
        )
    }
}

@Composable
private fun RowScope.SettingsExportTileItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = ColorBackground,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, ColorBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = ColorTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = ColorTextMuted,
                maxLines = 1
            )
        }
    }
}
