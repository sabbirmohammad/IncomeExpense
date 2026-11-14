package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.*
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun ProductPriceSearchActiveBanner(
    effectiveSearchQuery: String,
    itemsCount: Int,
    isBangla: Boolean,
    onOpenSearch: (() -> Unit)?,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        onClick = { onOpenSearch?.invoke() },
        shape = RoundedCornerShape(10.dp),
        color = appColors.accent.copy(alpha = 0.10f),
        border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.25f)),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 44.dp)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isBangla) "অনুসন্ধান: \"${effectiveSearchQuery.trim()}\" (${convertToBengaliDigits(itemsCount.toString())}টি পণ্য)"
                           else "Search: \"${effectiveSearchQuery.trim()}\" ($itemsCount items)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = WeightSemiBold,
                        fontFeatureSettings = TabularNumFeature
                    ),
                    color = appColors.accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .minimumInteractiveComponentSize()
                    .clickable { onClearQuery() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(appColors.accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = appColors.accent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
