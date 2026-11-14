package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun MarketSheetTabsRow(
    isBangla: Boolean,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    activeListsCount: Int,
    haptics: AppHaptics
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.background,
        border = BorderStroke(1.dp, appColors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val tab0Text = if (isBangla) {
                if (activeListsCount > 1) "চলমান ফর্দসমূহ (${convertToBengaliDigits(activeListsCount.toString())})"
                else "বাজার ফর্দ"
            } else {
                if (activeListsCount > 1) "Active Lists ($activeListsCount)"
                else "Checklist"
            }

            // Tab 0
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 0) appColors.accent else Color.Transparent)
                    .minimumInteractiveComponentSize()
                    .clickable {
                        haptics.lightTick()
                        onTabSelected(0)
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab0Text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (selectedTab == 0) Color.White else appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Tab 1
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 1) Color(0xFF2EC4B6) else Color.Transparent)
                    .minimumInteractiveComponentSize()
                    .clickable {
                        haptics.lightTick()
                        onTabSelected(1)
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = if (selectedTab == 1) Color.White else appColors.textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isBangla) "নতুন ফর্দ তৈরি" else "Create New List",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selectedTab == 1) Color.White else appColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
