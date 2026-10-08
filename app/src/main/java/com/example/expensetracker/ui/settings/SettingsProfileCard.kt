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
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.getAvatarIcon

@Composable
fun SettingsProfileCard(
    userProfile: UserProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
        borderColor = ColorAccent.copy(alpha = 0.22f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Elevated Avatar with glowing border and status dot
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        ColorAccent.copy(alpha = 0.25f),
                                        androidx.compose.ui.graphics.Color(0xFF6366F1).copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(1.5.dp, ColorAccent.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userProfile.avatarIndex == 0 && userProfile.initials.isNotBlank()) {
                            Text(
                                text = userProfile.initials.take(2),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ColorAccent
                            )
                        } else {
                            Icon(
                                imageVector = getAvatarIcon(userProfile.avatarIndex),
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    // Active status dot
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(ColorSuccessIncome)
                            .border(2.dp, ColorSurface, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = userProfile.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    // Account badge pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val isCloud = userProfile.googleAccountEmail != null
                        Icon(
                            imageVector = if (isCloud) Icons.Outlined.CloudDone else Icons.Outlined.Person,
                            contentDescription = null,
                            tint = if (isCloud) ColorSuccessIncome else ColorTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = userProfile.googleAccountEmail ?: if (userProfile.appLanguage == "bn") "লোকাল অ্যাকাউন্ট" else "Local Account",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorTextSecondary
                        )
                    }
                }
            }

            // Modern "Edit" pill button
            Surface(
                onClick = onClick,
                shape = RoundedCornerShape(20.dp),
                color = ColorAccent.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (userProfile.appLanguage == "bn") "এডিট" else "Edit",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ColorAccent
                    )
                }
            }
        }
    }
}
