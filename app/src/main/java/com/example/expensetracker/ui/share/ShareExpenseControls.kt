package com.example.expensetracker.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.PillFilterChip
import com.example.expensetracker.ui.components.Strings

@Composable
fun ShareFormatSwitcher(
    selectedFormat: ShareFormatType,
    strings: Strings,
    onFormatChange: (ShareFormatType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F131D))
            .border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf(
            Triple(ShareFormatType.PDF, strings.formatPdf, Icons.Outlined.PictureAsPdf),
            Triple(ShareFormatType.IMAGE, strings.formatImage, Icons.Outlined.Image),
            Triple(ShareFormatType.TEXT, strings.formatText, Icons.Outlined.ContentCopy)
        ).forEach { (format, label, icon) ->
            val isSelected = selectedFormat == format
            val activeBg = when (format) {
                ShareFormatType.PDF -> Color(0xFFE53935)
                ShareFormatType.IMAGE -> ColorAccent
                ShareFormatType.TEXT -> Color(0xFF2EC4B6)
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) activeBg else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onFormatChange(format) }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 9.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else ColorTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        ),
                        color = if (isSelected) Color.White else ColorTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SharePeriodSelector(
    selectedPeriod: SharePeriodType,
    strings: Strings,
    onPeriodChange: (SharePeriodType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SharePeriodType.entries.forEach { period ->
            val chipLabel = when (period) {
                SharePeriodType.TODAY -> strings.today
                SharePeriodType.YESTERDAY -> strings.yesterday
                SharePeriodType.THIS_WEEK -> strings.thisWeek
                SharePeriodType.THIS_MONTH -> strings.thisMonth
                SharePeriodType.CUSTOM -> strings.customRange
            }
            PillFilterChip(
                text = chipLabel,
                selected = selectedPeriod == period,
                onClick = { onPeriodChange(period) }
            )
        }
    }
}
