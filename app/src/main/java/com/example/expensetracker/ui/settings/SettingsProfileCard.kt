package com.example.expensetracker.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.getAvatarIcon
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun SettingsProfileCard(
    userProfile: UserProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    categoriesCount: Int = 0,
    transactionsCount: Int = 0
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val isBangla = strings.isBangla
    val isCloud = userProfile.googleAccountEmail != null

    // Background gradient for sleek fintech executive feel
    val cardBackgroundGradient = if (appColors.isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF141A2D),
                Color(0xFF161C30),
                Color(0xFF0F1424)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFF7F9FC)
            )
        )
    }

    val cardBorderBrush = Brush.linearGradient(
        colors = listOf(
            appColors.accent.copy(alpha = 0.45f),
            appColors.border.copy(alpha = 0.7f),
            appColors.accent.copy(alpha = 0.2f)
        )
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, cardBorderBrush),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackgroundGradient)
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Identity Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Multi-ring glowing Avatar with status dot
                        Box(contentAlignment = Alignment.BottomEnd) {
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

                        Spacer(modifier = Modifier.width(14.dp))

                        // User Name & Meta Badges
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = userProfile.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = appColors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = appColors.accent.copy(alpha = 0.12f),
                                    border = BorderStroke(0.6.dp, appColors.accent.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = if (isBangla) "ব্যক্তিগত" else "PRO",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = appColors.accent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Sync / Account Status Row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCloud) Icons.Outlined.CloudDone else Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = if (isCloud) appColors.income else appColors.textSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = userProfile.googleAccountEmail
                                        ?: if (isBangla) "লোকাল অ্যাকাউন্ট • সুরক্ষিত" else "Local Account • Secure",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = appColors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Modern "Edit" Pill Button
                    Surface(
                        onClick = {
                            haptics.lightTick()
                            onClick()
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = appColors.accent.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isBangla) "এডিট" else "Edit",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = appColors.accent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HairlineDivider(color = appColors.border.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Command Shelf (Micro-metrics)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Metric 1: Active Categories
                    ProfileMiniMetricPill(
                        icon = Icons.Outlined.Category,
                        iconTint = appColors.accent,
                        title = if (isBangla) "ক্যাটাগরি" else "Categories",
                        value = "${DateTimeHelper.formatCounter(categoriesCount, isBangla, userProfile.useBengaliNumerals || isBangla)} ${if (isBangla) "টি" else ""}".trim(),
                        modifier = Modifier.weight(1f)
                    )

                    // Metric 2: Currency
                    ProfileMiniMetricPill(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        iconTint = appColors.income,
                        title = if (isBangla) "মুদ্রা" else "Currency",
                        value = "${userProfile.currencyCode} (${userProfile.currencySymbol})",
                        modifier = Modifier.weight(1.1f)
                    )

                    // Metric 3: Backup Sync Mode
                    ProfileMiniMetricPill(
                        icon = if (isCloud) Icons.Outlined.CloudDone else Icons.Outlined.CloudOff,
                        iconTint = if (isCloud) appColors.income else appColors.textMuted,
                        title = if (isBangla) "ক্লাউড সিঙ্ক" else "Cloud Sync",
                        value = if (isCloud) (if (isBangla) "সক্রিয়" else "Active") else (if (isBangla) "লোকাল" else "Offline"),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMiniMetricPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = appColors.surface.copy(alpha = 0.55f),
        border = BorderStroke(0.5.dp, appColors.border.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
