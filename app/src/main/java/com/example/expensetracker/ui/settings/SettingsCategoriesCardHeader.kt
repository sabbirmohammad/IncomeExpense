package com.example.expensetracker.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.DateTimeHelper

/**
 * Interactive Header for SettingsCategoriesCard with category icon, active count badge, "+ New" button, and animated chevron.
 */
@Composable
fun SettingsCategoriesCardHeader(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    chevronRotation: Float,
    categoriesCount: Int,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    onAddCategoryClick: () -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                haptics.lightTick()
                onToggleExpand()
            }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Category Section Icon Badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Category,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isBangla) "ক্যাটাগরি সমূহ" else "Categories",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary
                )

                // Counter badge capsule
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.accent.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${DateTimeHelper.formatCounter(categoriesCount, isBangla, useBengaliNumerals || isBangla)} ${if (isBangla) "টি" else "active"}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = appColors.accent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Action Buttons on Right: Quick "+ New" and Chevron Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Quick Add Button
            Surface(
                onClick = {
                    haptics.lightTick()
                    onAddCategoryClick()
                },
                shape = RoundedCornerShape(20.dp),
                color = appColors.accent.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.25f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isBangla) "নতুন" else "New",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        ),
                        color = appColors.accent
                    )
                }
            }

            // Rotating Chevron Toggle Button
            Surface(
                onClick = {
                    haptics.lightTick()
                    onToggleExpand()
                },
                shape = CircleShape,
                color = appColors.surface.copy(alpha = 0.7f),
                border = BorderStroke(0.5.dp, appColors.border)
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = if (isExpanded) appColors.accent else appColors.textSecondary,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(chevronRotation)
                    )
                }
            }
        }
    }
}
