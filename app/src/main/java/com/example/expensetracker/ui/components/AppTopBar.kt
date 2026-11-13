package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors

/**
 * AppTopBar:
 * Living Ledger 2.0 Top Navigation Bar:
 * - 56dp content height
 * - Title: titleLarge (20sp, SemiBold)
 * - Optional navigation back icon (>= 48dp touch target)
 * - Up to 2 action icons (each >= 48dp touch target)
 * - Hairline ink stroke bottom border appears after scroll or when explicitly enabled
 * - Accessible semantics and TalkBack support
 */
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    backContentDescription: String = "Back",
    action1Icon: ImageVector? = null,
    action1ContentDescription: String? = null,
    onAction1Click: (() -> Unit)? = null,
    action2Icon: ImageVector? = null,
    action2ContentDescription: String? = null,
    onAction2Click: (() -> Unit)? = null,
    showHairline: Boolean = false,
    backgroundColor: Color = LocalAppColors.current.background
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Optional Back Button (>= 48dp touch target)
            if (onBackClick != null) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .clickable {
                            haptics.lightTick()
                            onBackClick()
                        }
                        .semantics { role = Role.Button },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = backContentDescription,
                        tint = appColors.textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(12.dp))
            }

            // Title & optional Subtitle
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = appColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Action 1 (>= 48dp touch target)
            if (action1Icon != null && onAction1Click != null) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .clickable {
                            haptics.lightTick()
                            onAction1Click()
                        }
                        .semantics { role = Role.Button },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action1Icon,
                        contentDescription = action1ContentDescription,
                        tint = appColors.textPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Action 2 (>= 48dp touch target)
            if (action2Icon != null && onAction2Click != null) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .clickable {
                            haptics.lightTick()
                            onAction2Click()
                        }
                        .semantics { role = Role.Button },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action2Icon,
                        contentDescription = action2ContentDescription,
                        tint = appColors.textPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(4.dp))
        }

        // Hairline divider on scroll
        AnimatedVisibility(
            visible = showHairline,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            HorizontalDivider(
                thickness = HairlineInkStroke,
                color = appColors.border
            )
        }
    }
}

// =========================================================================
// PREVIEWS IN ALL 4 THEMES (EN + BN)
// =========================================================================

@Preview(name = "AppTopBar - Dark with Back and Actions", showBackground = true)
@Composable
private fun AppTopBarDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        AppTopBar(
            title = "Ledger",
            subtitle = "All accounts · November",
            onBackClick = {},
            action1Icon = Icons.Default.Search,
            action1ContentDescription = "Search",
            onAction1Click = {},
            action2Icon = Icons.Default.MoreVert,
            action2ContentDescription = "More",
            onAction2Click = {},
            showHairline = true
        )
    }
}

@Preview(name = "AppTopBar - Light Bn Simple", showBackground = true)
@Composable
private fun AppTopBarLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        AppTopBar(
            title = "খাতা ও হিসাব",
            action1Icon = Icons.Default.Search,
            onAction1Click = {}
        )
    }
}

@Preview(name = "AppTopBar - AMOLED Bn with Back", showBackground = true)
@Composable
private fun AppTopBarAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        AppTopBar(
            title = "সেটিংস",
            onBackClick = {},
            backContentDescription = "ফিরে যান"
        )
    }
}

@Preview(name = "AppTopBar - System En Scrolled", showBackground = true)
@Composable
private fun AppTopBarSystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        AppTopBar(
            title = "Market Intelligence",
            showHairline = true
        )
    }
}
