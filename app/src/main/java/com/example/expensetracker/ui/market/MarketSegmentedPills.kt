package com.example.expensetracker.ui.market

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun MarketSegmentedPills(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isBangla: Boolean,
    activeListsCount: Int,
    priceInsightsCount: Int,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.surface,
        border = BorderStroke(1.dp, appColors.border),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Market Lists (Lists · ফর্দ)
            MarketTabPill(
                title = if (isBangla) "ফর্দ" else "Lists",
                badge = if (activeListsCount > 0) (if (isBangla) convertToBengaliDigits(activeListsCount.toString()) else activeListsCount.toString()) else null,
                icon = Icons.Outlined.Checklist,
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )

            // Tab 1: Price Intelligence (Prices · দরদাম)
            MarketTabPill(
                title = if (isBangla) "দরদাম" else "Prices",
                badge = if (priceInsightsCount > 0) (if (isBangla) convertToBengaliDigits(priceInsightsCount.toString()) else priceInsightsCount.toString()) else null,
                icon = Icons.AutoMirrored.Outlined.TrendingUp,
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )

            // Tab 2: Create List (New · নতুন)
            MarketTabPill(
                title = if (isBangla) "নতুন" else "New",
                badge = null,
                icon = Icons.Filled.Add,
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MarketTabPill(
    title: String,
    badge: String?,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val pillBackground by animateColorAsState(
        targetValue = if (isSelected) appColors.accent else Color.Transparent,
        animationSpec = tween(150),
        label = "pillBackground"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(11.dp),
        color = pillBackground,
        modifier = modifier
            .fillMaxHeight()
            .minimumInteractiveComponentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else appColors.textSecondary,
                modifier = Modifier.size(14.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = if (isSelected) Color.White else appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )

            if (badge != null) {
                Spacer(modifier = Modifier.width(3.dp))
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .defaultMinSize(minWidth = 18.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White.copy(alpha = 0.25f) else appColors.accent.copy(alpha = 0.15f))
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (isSelected) Color.White else appColors.accent,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
