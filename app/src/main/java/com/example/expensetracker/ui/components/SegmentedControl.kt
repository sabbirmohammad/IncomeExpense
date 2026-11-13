package com.example.expensetracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion
import com.example.expensetracker.theme.RadiusPill
import com.example.expensetracker.theme.ShapeChip

data class SegmentItem(
    val title: String,
    val icon: ImageVector? = null,
    val badge: String? = null
)

/**
 * V7 Segmented Control (Fixed Equal-Width Variant):
 * - Exactly 48dp height
 * - Equal width distribution
 * - Smooth animated selection pill (accentContainer bg + accent text per spec)
 * - Semantic role Tab / RadioButton, accessibility touch target >= 48dp
 */
@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = LocalAppColors.current.accent,
    selectedContainerColor: Color = LocalAppColors.current.accentContainer
) {
    val colors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val motion = LocalMotion.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(RadiusPill))
            .background(colors.surfaceVariant)
            .padding(3.dp)
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
                .shadow(elevation = if (colors.isDark) 0.dp else 1.dp, shape = RoundedCornerShape(RadiusPill))
                .clip(RoundedCornerShape(RadiusPill))
                .background(selectedContainerColor)
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
                        .clip(RoundedCornerShape(RadiusPill))
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
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        ),
                        color = if (isSelected) selectedColor else colors.textSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * ScrollableSegmentedControl:
 * - Horizontally scrollable row of pill segments
 * - Each segment >= 48dp touch height
 * - Selected = accentContainer bg + accent text
 */
@Composable
fun ScrollableSegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = LocalAppColors.current.accent,
    selectedContainerColor: Color = LocalAppColors.current.accentContainer
) {
    val colors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, title ->
            val isSelected = index == selectedIndex

            val bg = if (isSelected) selectedContainerColor else colors.surfaceVariant
            val textCol = if (isSelected) selectedColor else colors.textSecondary
            val borderStroke = if (isSelected) {
                BorderStroke(1.dp, selectedColor.copy(alpha = 0.5f))
            } else if (!colors.isDark) {
                BorderStroke(HairlineInkStroke, colors.border)
            } else null

            Surface(
                shape = RoundedCornerShape(RadiusPill),
                color = bg,
                border = borderStroke,
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .minimumInteractiveComponentSize()
                    .semantics {
                        role = Role.Tab
                        stateDescription = if (isSelected) "Selected" else "Not selected"
                    }
                    .clickable {
                        if (index != selectedIndex) {
                            haptics.lightTick()
                            onItemSelected(index)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        ),
                        color = textCol,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// =========================================================================
// PREVIEWS IN ALL 4 THEMES (EN + BN)
// =========================================================================

@Preview(name = "SegmentedControl - Dark 3 Segments", showBackground = true)
@Composable
private fun SegmentedControlDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            SegmentedControl(
                items = listOf("Transactions", "Insights", "Reports"),
                selectedIndex = 0,
                onItemSelected = {}
            )
        }
    }
}

@Preview(name = "SegmentedControl - Light Bn 4 Segments", showBackground = true)
@Composable
private fun SegmentedControlLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            SegmentedControl(
                items = listOf("বাজেট", "লক্ষ্য", "বিল", "ধার ও দেনা"),
                selectedIndex = 1,
                onItemSelected = {}
            )
        }
    }
}

@Preview(name = "ScrollableSegmentedControl - AMOLED Bn", showBackground = true)
@Composable
private fun ScrollableSegmentedControlAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            ScrollableSegmentedControl(
                items = listOf("বাজেট", "লক্ষ্য", "বিল", "ধার ও দেনা"),
                selectedIndex = 0,
                onItemSelected = {}
            )
        }
    }
}

@Preview(name = "ScrollableSegmentedControl - System En", showBackground = true)
@Composable
private fun ScrollableSegmentedControlSystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            ScrollableSegmentedControl(
                items = listOf("Budgets", "Goals", "Bills", "Debts"),
                selectedIndex = 2,
                onItemSelected = {}
            )
        }
    }
}
