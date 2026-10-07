package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors

fun getAvatarIcon(index: Int): ImageVector {
    return when (index) {
        1 -> Icons.Outlined.Person
        2 -> Icons.Outlined.SentimentSatisfied
        3 -> Icons.Outlined.Star
        4 -> Icons.Outlined.RocketLaunch
        5 -> Icons.Outlined.Work
        else -> Icons.Outlined.Person
    }
}

@Composable
fun DrawerMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(LocalAppColors.current.surface)
            .border(1.dp, LocalAppColors.current.border, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Open side menu",
            tint = LocalAppColors.current.textPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun OneUiHeader(
    userName: String,
    dateText: String,
    onAvatarClick: () -> Unit,
    avatarIndex: Int = 0,
    initials: String = "",
    onMenuClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onMenuClick != null) {
                DrawerMenuButton(onClick = onMenuClick)
            }
            val strings = LocalAppStrings.current
            Column {
                Text(
                    text = "${strings.greeting}, $userName",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorTextPrimary
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = ColorTextSecondary
                )
            }
        }

        ProfileAvatarButton(
            onClick = onAvatarClick,
            avatarIndex = avatarIndex,
            initials = initials
        )
    }
}

@Composable
fun ProfileAvatarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    avatarIndex: Int = 0,
    initials: String = ""
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(ColorAccent.copy(alpha = 0.15f))
            .border(1.dp, ColorAccent.copy(alpha = 0.5f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (avatarIndex == 0 && initials.isNotBlank()) {
            Text(
                text = initials.take(2),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ColorAccent
            )
        } else {
            Icon(
                imageVector = getAvatarIcon(avatarIndex),
                contentDescription = "Profile and Settings",
                tint = ColorAccent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
