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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import java.util.Calendar

/**
 * V7 HomeTopBar per UX_FLOWS section 2 & Screenshot Patch C3:
 * - Greeting with user name ("Good evening, Sabbir")
 * - Privacy toggle icon (masks/unmasks amounts)
 * - Notification bell icon
 * - User avatar (opens Settings)
 * - Pinned header with background color and 0.5dp hairline on scroll
 * - Min 48dp touch targets on all interactive elements
 */
@Composable
fun HomeTopBar(
    userName: String,
    onAvatarClick: () -> Unit,
    avatarIndex: Int = 0,
    initials: String = "",
    isPrivacyMode: Boolean = false,
    onTogglePrivacyMode: () -> Unit,
    onNotificationClick: (() -> Unit)? = null,
    hasUnreadNotifications: Boolean = false,
    showHairline: Boolean = false,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(appColors.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPaddingHorizontal, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Greeting with user name
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val greeting = strings.timeBasedGreeting(hour)
            val displayName = if (userName.isBlank()) "" else ", $userName"

            Text(
                text = "$greeting$displayName",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )

            // Right: [👁 Privacy] [🔔 Notifications] [Avatar -> Settings]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Privacy toggle
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .clickable { onTogglePrivacyMode() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPrivacyMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = strings.privacyMode,
                        tint = if (isPrivacyMode) appColors.accent else appColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Notifications icon
                if (onNotificationClick != null) {
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                        .clickable { onNotificationClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = strings.notifications,
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            if (hasUnreadNotifications) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(appColors.warning)
                                )
                            }
                        }
                    }
                }

                // Avatar -> opens Settings
                ProfileAvatarButton(
                    onClick = onAvatarClick,
                    avatarIndex = avatarIndex,
                    initials = initials
                )
            }
        }

        // 0.5dp hairline divider that appears on scroll
        if (showHairline) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(appColors.border.copy(alpha = 0.5f))
            )
        }
    }
}
