package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Collapsible Department Section Header for Market Shopping Checklist.
 * Features:
 * - Tonal avatar with department emoji
 * - Title in MaterialTheme.typography.titleMedium
 * - Live progress counter ("৪/৫ টি সম্পন্ন" / "4/5 Completed" or "✓ সম্পন্ন" / "✓ Done")
 * - Smooth 180° rotation animated chevron
 * - Minimum 48dp full-width touch surface with haptic feedback
 */
@Composable
fun MarketDepartmentSectionHeader(
    dept: MarketDepartment,
    isCollapsed: Boolean,
    purchasedCount: Int,
    totalCount: Int,
    isBangla: Boolean,
    onToggleCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val rotationAngle by animateFloatAsState(
        targetValue = if (isCollapsed) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "deptChevronRotation"
    )

    val isAllCompleted = totalCount > 0 && purchasedCount == totalCount
    val progressText = if (isAllCompleted) {
        if (isBangla) "✓ সম্পন্ন" else "✓ Done"
    } else {
        if (isBangla) "${convertToBengaliDigits(purchasedCount.toString())}/${convertToBengaliDigits(totalCount.toString())} টি সম্পন্ন"
        else "$purchasedCount/$totalCount Completed"
    }

    Surface(
        onClick = {
            haptics.lightTick()
            onToggleCollapse()
        },
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .heightIn(min = 48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Department Avatar + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(appColors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dept.icon,
                        fontSize = 16.sp
                    )
                }

                Text(
                    text = if (isBangla) dept.titleBn else dept.titleEn,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
            }

            // Right: Live Progress Pill + Rotating Animated Chevron
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isAllCompleted) appColors.income.copy(alpha = 0.15f) else appColors.surfaceVariant.copy(alpha = 0.7f),
                    border = BorderStroke(
                        1.dp,
                        if (isAllCompleted) appColors.income.copy(alpha = 0.3f) else appColors.border.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isAllCompleted) appColors.income else appColors.textSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isCollapsed) {
                        if (isBangla) "বিভাগ খুলুন" else "Expand section"
                    } else {
                        if (isBangla) "বিভাগ বন্ধ করুন" else "Collapse section"
                    },
                    tint = appColors.textSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = rotationAngle }
                )
            }
        }
    }
}

/**
 * Overload accepting DepartmentMeta for full backward compatibility.
 */
@Composable
fun MarketDepartmentSectionHeader(
    dept: DepartmentMeta,
    isCollapsed: Boolean,
    purchasedCount: Int,
    totalCount: Int,
    isBangla: Boolean,
    onToggleCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    MarketDepartmentSectionHeader(
        dept = MarketDepartment.fromKey(dept.key),
        isCollapsed = isCollapsed,
        purchasedCount = purchasedCount,
        totalCount = totalCount,
        isBangla = isBangla,
        onToggleCollapse = onToggleCollapse,
        modifier = modifier
    )
}
