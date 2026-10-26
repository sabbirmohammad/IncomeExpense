package com.example.expensetracker.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import com.example.expensetracker.common.AppConstants
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.ProfileAvatarButton
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency

/**
 * Modern Dribbble-style 3D Drawer Side Panel
 * Renders behind the scaled/shifted main screen surface.
 */
@Composable
fun SideMenuDrawer(
    selectedItem: NavigationItem,
    onItemSelected: (NavigationItem) -> Unit,
    userProfile: UserProfile,
    totalBalance: Double,
    modifier: Modifier = Modifier,
    onAddTransactionClick: () -> Unit = {}
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.surface)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .fillMaxWidth(0.75f)
                .widthIn(max = 320.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Profile & Balance
            Column {
                // User Profile Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onItemSelected(NavigationItem.SETTINGS) }
                        .padding(vertical = 6.dp)
                ) {
                    ProfileAvatarButton(
                        onClick = { onItemSelected(NavigationItem.SETTINGS) },
                        avatarIndex = userProfile.avatarIndex,
                        initials = userProfile.initials
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${strings.greeting}, ${userProfile.effectiveFirstName}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = userProfile.googleAccountEmail ?: if (strings.isBangla) "লোকাল অ্যাকাউন্ট" else "Local Workspace",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = appColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Balance Status Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = appColors.surfaceVariant,
                    border = BorderStroke(1.dp, appColors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = strings.totalBalance,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = appColors.textMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatCurrency(totalBalance, userProfile.currencySymbol),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (totalBalance >= 0) appColors.textPrimary else appColors.expense
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(appColors.accent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section Title: Navigation
                Text(
                    text = if (strings.isBangla) "মেনু ও নেভিগেশন" else "NAVIGATION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = appColors.textMuted,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
                )

                // Menu Items List
                val drawerMenuItems = listOf(
                    NavigationItem.ACCOUNTS,
                    NavigationItem.BILLS,
                    NavigationItem.RECEIVABLES,
                    NavigationItem.BUDGETS,
                    NavigationItem.REPORTS,
                    NavigationItem.SETTINGS
                )
                drawerMenuItems.forEach { item ->
                    val isSelected = selectedItem == item
                    DrawerMenuItemRow(
                        item = item,
                        isSelected = isSelected,
                        strings = strings,
                        onClick = { onItemSelected(item) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Bottom Section: Footer / App Info
            DrawerFooter(strings = strings)
        }
    }
}

