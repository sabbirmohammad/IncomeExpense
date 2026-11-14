package com.example.expensetracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.expensetracker.theme.LocalAppColors

/**
 * Legacy wrapper around [PulseTopBar] for backwards compatibility with un-redesigned screens.
 * To be removed in a later phase once all callers are migrated.
 */
@Deprecated(
    message = "Use PulseTopBar directly for Pulse design language.",
    replaceWith = ReplaceWith("PulseTopBar(title, modifier, subtitle, onBackClick, backContentDescription, action1Icon, action1ContentDescription, onAction1Click, action2Icon, action2ContentDescription, onAction2Click)")
)
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
    showBottomDivider: Boolean = false,
    showHairline: Boolean = false,
    containerColor: Color = LocalAppColors.current.background
) {
    PulseTopBar(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        onBackClick = onBackClick,
        backContentDescription = backContentDescription,
        action1Icon = action1Icon,
        action1ContentDescription = action1ContentDescription,
        onAction1Click = onAction1Click,
        action2Icon = action2Icon,
        action2ContentDescription = action2ContentDescription,
        onAction2Click = onAction2Click,
        containerColor = containerColor
    )
}
