package com.example.expensetracker.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CategoryIconBadge

/**
 * Compact preview ribbon shown when the Settings Categories card is collapsed.
 */
@Composable
fun SettingsCategoriesCollapsedRibbon(
    categories: List<Category>,
    isBangla: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(appColors.background.copy(alpha = 0.45f))
            .clickable { onExpandClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            categories.take(6).forEach { cat ->
                CategoryIconBadge(
                    iconName = cat.icon,
                    colorHex = cat.colorHex,
                    size = 24.dp
                )
            }
            if (categories.size > 6) {
                Surface(
                    shape = CircleShape,
                    color = appColors.surface,
                    border = BorderStroke(0.5.dp, appColors.border)
                ) {
                    Text(
                        text = "+${categories.size - 6}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = appColors.textSecondary,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Text(
            text = if (isBangla) "খুলুন →" else "Expand →",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = appColors.accent
        )
    }
}
