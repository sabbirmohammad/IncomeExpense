package com.example.expensetracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusPill

/**
 * PulseSegmented:
 * Unified Pill Track Segmented Control for Pulse Design Language:
 * - Pill track in surfaceVariant
 * - Selected pill in surface with soft shadow and textPrimary
 * - Unselected pills in textSecondary
 * - Min target height >= 48dp (WCAG AA touch requirement)
 * - Horizontal scrolling if items > 4 or on small screens
 */
@Composable
fun <T> PulseSegmented(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    scrollable: Boolean = items.size > 4
) {
    val selectedIndex = items.indexOf(selectedItem).coerceAtLeast(0)
    PulseSegmented(
        itemCount = items.size,
        selectedIndex = selectedIndex,
        onIndexSelected = { index -> onItemSelected(items[index]) },
        label = { index -> label(items[index]) },
        modifier = modifier,
        scrollable = scrollable
    )
}

@Composable
fun PulseSegmented(
    itemCount: Int,
    selectedIndex: Int,
    onIndexSelected: (Int) -> Unit,
    label: (Int) -> String,
    modifier: Modifier = Modifier,
    scrollable: Boolean = itemCount > 4
) {
    val appColors = LocalAppColors.current
    val pillShape = RoundedCornerShape(RadiusPill)

    val rowModifier = if (scrollable) {
        modifier
            .fillMaxWidth()
            .clip(pillShape)
            .background(appColors.surfaceVariant)
            .padding(4.dp)
            .horizontalScroll(rememberScrollState())
    } else {
        modifier
            .fillMaxWidth()
            .clip(pillShape)
            .background(appColors.surfaceVariant)
            .padding(4.dp)
    }

    Row(
        modifier = rowModifier,
        horizontalArrangement = if (scrollable) Arrangement.spacedBy(4.dp) else Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until itemCount) {
            val isSelected = i == selectedIndex

            val textColor by animateColorAsState(
                targetValue = if (isSelected) appColors.textPrimary else appColors.textSecondary,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                label = "PulseSegmentedTextColor"
            )

            val itemModifier = if (scrollable) {
                Modifier.padding(horizontal = 4.dp)
            } else {
                Modifier.weight(1f)
            }

            Box(
                modifier = itemModifier
                    .sizeIn(minHeight = 48.dp)
                    .clip(pillShape)
                    .then(
                        if (isSelected) {
                            if (!appColors.isDark) {
                                Modifier.shadow(
                                    elevation = 2.dp,
                                    shape = pillShape,
                                    clip = false,
                                    ambientColor = Color.Black.copy(alpha = 0.06f),
                                    spotColor = Color.Black.copy(alpha = 0.08f)
                                )
                            } else {
                                Modifier
                            }
                        } else Modifier
                    )
                    .background(if (isSelected) appColors.surface else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Tab,
                        onClick = { onIndexSelected(i) }
                    )
                    .semantics {
                        selected = isSelected
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label(i),
                    color = textColor,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(name = "PulseSegmented - Dark")
@Composable
private fun PulseSegmentedDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.background, modifier = Modifier.padding(16.dp)) {
            PulseSegmented(
                itemCount = 3,
                selectedIndex = 0,
                onIndexSelected = {},
                label = { i -> listOf("Activity", "Wallets", "Insights")[i] }
            )
        }
    }
}

@Preview(name = "PulseSegmented - Light Bengali")
@Composable
private fun PulseSegmentedLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.background, modifier = Modifier.padding(16.dp)) {
            PulseSegmented(
                itemCount = 4,
                selectedIndex = 1,
                onIndexSelected = {},
                label = { i -> listOf("বাজেট", "বিল", "লক্ষ্য", "ধার ও দেনা")[i] }
            )
        }
    }
}
