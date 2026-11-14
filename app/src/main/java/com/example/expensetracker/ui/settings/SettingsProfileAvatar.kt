package com.example.expensetracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.getAvatarIcon

@Composable
internal fun SettingsProfileAvatar(
    userProfile: UserProfile,
    isCloud: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            appColors.accent,
                            Color(0xFF6366F1),
                            Color(0xFF38BDF8),
                            appColors.accent
                        )
                    )
                )
                .padding(2.5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(51.dp)
                    .clip(CircleShape)
                    .background(
                        if (appColors.isDark) Color(0xFF131726) else Color(0xFFEEF2F6)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (userProfile.avatarIndex == 0 && userProfile.initials.isNotBlank()) {
                    Text(
                        text = userProfile.initials.take(2),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = appColors.accent
                    )
                } else {
                    Icon(
                        imageVector = getAvatarIcon(userProfile.avatarIndex),
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // Active Connectivity Status Dot
        Box(
            modifier = Modifier
                .size(13.dp)
                .clip(CircleShape)
                .background(if (isCloud) appColors.income else appColors.accent)
                .border(2.dp, if (appColors.isDark) Color(0xFF141A2D) else Color.White, CircleShape)
        )
    }
}
