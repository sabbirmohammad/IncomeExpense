package com.example.expensetracker.ui.market

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.dashboard.DepartmentMeta

/**
 * Handcrafted Department Stall Section Header:
 * Groups market checklist items by physical bazaar stall:
 * - 🥬 কাঁচাবাজার (Vegetables)
 * - 🐟 মাছ ও মাংস (Meat & Fish)
 * - 🥛 দুধ ও বেকারি (Dairy & Bakery)
 * - 🧂 মুদি সদাই (Groceries)
 * Features live completion counter ("৩/৪টি সম্পন্ন") and smooth collapsible toggle.
 */
@Composable
fun MarketDepartmentSectionHeader(
    dept: DepartmentMeta,
    completedCount: Int,
    totalCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val isDeptCompleted = completedCount == totalCount && totalCount > 0

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(200),
        label = "deptArrowRotation"
    )

    val progressText = if (isDeptCompleted) {
        if (isBangla) "✓ ${convertToBengaliDigits(completedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}টি সম্পন্ন"
        else "✓ $completedCount/$totalCount Done"
    } else {
        if (isBangla) "${convertToBengaliDigits(completedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}টি সম্পন্ন"
        else "$completedCount/$totalCount Done"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDeptCompleted) appColors.income.copy(alpha = 0.08f) else appColors.cardBackground,
        border = BorderStroke(
            HairlineInkStroke,
            if (isDeptCompleted) appColors.income.copy(alpha = 0.25f) else appColors.divider
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                haptics.lightTick()
                onToggleExpand()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Stall Icon & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Text(
                    text = dept.icon,
                    fontSize = 17.sp
                )
                Text(
                    text = if (isBangla) dept.titleBn else dept.titleEn,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isDeptCompleted) appColors.income else appColors.textPrimary
                )
            }

            // Right: Live Progress Pill & Expand/Collapse Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDeptCompleted) appColors.income.copy(alpha = 0.15f) else appColors.accent.copy(alpha = 0.10f),
                    border = BorderStroke(
                        HairlineInkStroke,
                        if (isDeptCompleted) appColors.income.copy(alpha = 0.35f) else appColors.accent.copy(alpha = 0.25f)
                    )
                ) {
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (isDeptCompleted) appColors.income else appColors.accent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(appColors.border.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = appColors.textSecondary,
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(arrowRotation)
                    )
                }
            }
        }
    }
}
