package com.example.expensetracker.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.theme.ShapeChip

data class SegmentItem(
    val title: String,
    val icon: ImageVector? = null,
    val badge: String? = null
)

/**
 * V7 Segmented Control:
 * Exactly 48dp height, equal width distribution, smooth animated selection pill,
 * full touch target accessibility.
 */
@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val motion = LocalMotion.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(ShapeChip)
            .background(colors.surfaceVariant)
            .padding(4.dp)
    ) {
        val count = items.size.coerceAtLeast(1)
        val segmentWidth = maxWidth / count

        val indicatorOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = motion.standardTween(),
            label = "segmentOffset"
        )

        // Animated Selection Pill
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(segmentWidth)
                .fillMaxHeight()
                .shadow(elevation = if (colors.isDark) 0.dp else 2.dp, shape = ShapeChip)
                .clip(ShapeChip)
                .background(if (colors.isDark) colors.surfaceRaised else colors.surface)
        )

        // Segment Tabs Row
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            items.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(ShapeChip)
                        .semantics {
                            role = Role.Tab
                            stateDescription = if (isSelected) "Selected" else "Not selected"
                        }
                        .clickable {
                            if (index != selectedIndex) {
                                haptics.lightTick()
                                onItemSelected(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (isSelected) colors.textPrimary else colors.textSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
