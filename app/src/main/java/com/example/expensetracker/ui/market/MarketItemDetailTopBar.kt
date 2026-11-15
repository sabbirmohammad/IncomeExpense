package com.example.expensetracker.ui.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MergeType
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketItemDetailTopBar(
    commodity: MarketCommodityUiModel,
    isBangla: Boolean,
    onClose: () -> Unit,
    onRenameClick: (() -> Unit)?,
    onMergeClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    haptics.lightTick()
                    onClose()
                },
                modifier = Modifier
                    .size(44.dp)
                    .minimumInteractiveComponentSize()
                    .clip(CircleShape)
                    .background(appColors.cardBackground)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = if (isBangla) "ফিরে যান" else "Back",
                    tint = appColors.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = commodity.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = WeightBold,
                        fontSize = 18.sp
                    ),
                    color = appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (commodity.englishName.isNotBlank() && commodity.englishName != commodity.name) {
                        "${commodity.englishName} • ${commodity.category}"
                    } else commodity.category,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )
            }
        }

        // Header Action Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (onRenameClick != null) {
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        onRenameClick()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = if (isBangla) "নাম পরিবর্তন" else "Rename",
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            if (onMergeClick != null) {
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        onMergeClick()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MergeType,
                        contentDescription = if (isBangla) "মার্জ করুন" else "Merge with…",
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}
