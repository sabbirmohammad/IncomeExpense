package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun ReceivablesTopBar(
    selectedTab: DebtLedgerTab,
    isPrivacyMode: Boolean,
    onTogglePrivacy: () -> Unit,
    onSplitBillClick: () -> Unit,
    onQuickAddClick: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPaddingHorizontal, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onMenuClick != null) {
                DrawerMenuButton(onClick = onMenuClick)
                Spacer(modifier = Modifier.width(12.dp))
            } else if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ColorTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = strings.debtsLedgerTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = ColorTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Privacy Toggle Button
            IconButton(
                onClick = {
                    haptics.lightTick()
                    onTogglePrivacy()
                }
            ) {
                Icon(
                    imageVector = if (isPrivacyMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = "Toggle Privacy",
                    tint = if (isPrivacyMode) ColorAccent else ColorTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Split Bill Button (only on Receivables tab)
            if (selectedTab == DebtLedgerTab.RECEIVABLES) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ColorAccent.copy(alpha = 0.15f),
                    modifier = Modifier.clickable {
                        haptics.mediumClick()
                        onSplitBillClick()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Group,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = strings.actionSplitBill,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorAccent,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            // Quick Add Button
            IconButton(
                onClick = {
                    haptics.mediumClick()
                    onQuickAddClick()
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ColorAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = if (selectedTab == DebtLedgerTab.RECEIVABLES) strings.addReceivable else strings.addPayable,
                        tint = ColorAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
