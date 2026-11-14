package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

/**
 * PulseTopBar:
 * Unified Top Bar for Pulse Design Language:
 * - 56dp height with status bar padding
 * - Clean surface separation without hairline borders
 * - 48x48dp touch target for back arrow
 * - Up to 2 action icon buttons (each >= 48dp)
 */
@Composable
fun PulseTopBar(
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
    containerColor: Color = LocalAppColors.current.background
) {
    val appColors = LocalAppColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onBackClick),
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
                Box(modifier = Modifier.padding(start = 8.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
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

            if (action1Icon != null && onAction1Click != null) {
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onAction1Click),
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

            if (action2Icon != null && onAction2Click != null) {
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onAction2Click),
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
        }
    }
}

@Preview(name = "PulseTopBar - Dark")
@Composable
private fun PulseTopBarDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        PulseTopBar(
            title = "Bazaar Hub",
            subtitle = "Shopping checklist & prices",
            onBackClick = {}
        )
    }
}

@Preview(name = "PulseTopBar - Light")
@Composable
private fun PulseTopBarLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        PulseTopBar(
            title = "বাজার তালিকা",
            onBackClick = {}
        )
    }
}
