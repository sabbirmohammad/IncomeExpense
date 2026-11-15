package com.example.expensetracker.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
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
    var showFullEmail by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, SettingsProfileGradients.border(appColors)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SettingsProfileGradients.background(appColors))
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
                        SettingsProfileAvatar(
                            userProfile = userProfile,
                            isCloud = isCloud
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        // User Name & Meta Badges
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = userProfile.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
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
                                val email = userProfile.googleAccountEmail
                                val displayEmail = if (email != null) {
                                    if (showFullEmail) email else if (email.length > 22) {
                                        val half = (22 - 3) / 2
                                        email.take(half) + "..." + email.takeLast(half)
                                    } else email
                                } else {
                                    if (isBangla) "লোকাল অ্যাকাউন্ট • সুরক্ষিত" else "Local Account • Secure"
                                }

                                Text(
                                    text = displayEmail,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.Medium),
                                    color = appColors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.clickable { showFullEmail = !showFullEmail }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Modern "Edit" Pill Button (>= 48dp hit area)
                    Surface(
                        onClick = {
                            haptics.lightTick()
                            onClick()
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = appColors.accent.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.35f)),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = null, tint = appColors.accent, modifier = Modifier.size(13.dp))
                            Text(
                                text = if (isBangla) "এডিট" else "Edit",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.Bold),
                                color = appColors.accent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HairlineDivider(color = appColors.border.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                SettingsProfileMetricsRow(
                    userProfile = userProfile,
                    categoriesCount = categoriesCount,
                    isCloud = isCloud,
                    isBangla = isBangla
                )
            }
        }
    }
}
