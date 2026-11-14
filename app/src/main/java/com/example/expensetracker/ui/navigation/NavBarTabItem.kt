package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
internal fun NavBarTabItem(
    selected: Boolean,
    label: String,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasDotBadge: Boolean = false,
    badgeCount: Int = 0,
    isBangla: Boolean = false
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor = if (selected) appColors.accent else appColors.textSecondary
    val textColor = if (selected) appColors.accent else appColors.textSecondary
    val fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptics.lightTick()
                    onClick()
                }
            )
            .semantics {
                role = Role.Tab
                this.selected = selected
                stateDescription = if (selected) "Selected" else "Not selected"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(width = NavPillWidth, height = NavPillHeight),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(width = NavPillWidth, height = NavPillHeight)
                            .clip(RoundedCornerShape(14.dp))
                            .background(appColors.accent.copy(alpha = 0.14f))
                    )
                }

                Icon(
                    imageVector = if (selected) activeIcon else inactiveIcon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )

                if (hasDotBadge) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 2.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(appColors.expense)
                    )
                }

                if (badgeCount > 0) {
                    val countText = if (badgeCount > 9) "9+" else badgeCount.toString()
                    val displayCount = if (isBangla) convertToBengaliDigits(countText) else countText
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 0.dp)
                            .defaultMinSize(minWidth = 14.dp, minHeight = 14.dp)
                            .clip(CircleShape)
                            .background(appColors.expense)
                            .padding(horizontal = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayCount,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = TabularNumFeature
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.5.sp,
                    fontWeight = fontWeight
                ),
                color = textColor,
                maxLines = 1
            )
        }
    }
}
