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
 * Handcrafted Aisle Section Header (Target Design B5):
 * - Groups market checklist items by bazaar stall / aisle.
 * - Small icon + name + "2/3" + chevron (collapsible).
 * - Section tint is a small dot only (no rainbow card backgrounds).
 * - >= 48dp minimum touch target for seamless collapse/expansion.
 * - Flat surface with subtle hairline divider.
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

    val countLabel = if (isBangla) {
        "${convertToBengaliDigits(completedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}"
    } else {
        "$completedCount/$totalCount"
    }
    // Structural compatibility string
    val fullProgressText = if (isBangla) {
        "${convertToBengaliDigits(completedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}টি সম্পন্ন"
    } else {
        "$completedCount/$totalCount Done"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
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
            // Left: Section Tint Dot + Small Icon + Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Section tint: small dot only per Target Design B5
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isDeptCompleted) appColors.income else appColors.accent)
                )

                Text(text = dept.icon, fontSize = 16.sp)

                Text(
                    text = if (isBangla) dept.titleBn else dept.titleEn,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isDeptCompleted) appColors.income else appColors.textPrimary
                )
            }

            // Right: Short "2/3" Count + Chevron
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = countLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = if (isDeptCompleted) appColors.income else appColors.textSecondary
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(appColors.surfaceVariant),
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

/**
 * Direct alias for MarketSectionHeader
 */
@Composable
fun MarketSectionHeader(
    dept: DepartmentMeta,
    completedCount: Int,
    totalCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    MarketDepartmentSectionHeader(
        dept = dept,
        completedCount = completedCount,
        totalCount = totalCount,
        isExpanded = isExpanded,
        onToggleExpand = onToggleExpand,
        isBangla = isBangla,
        modifier = modifier
    )
}
