package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MovementFilter
import com.example.expensetracker.theme.*
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun ProductPriceFilterControls(
    selectedMovementFilter: MovementFilter,
    onMovementFilterChange: (MovementFilter) -> Unit,
    selectedSortOrder: PriceSortOrder,
    onSortOrderChange: (PriceSortOrder) -> Unit,
    totalCommoditiesCount: Int,
    priceUpCount: Int,
    priceDownCount: Int,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var isSortMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = appColors.cardBackground,
            border = BorderStroke(HairlineInkStroke, appColors.divider),
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 44.dp)
                .height(44.dp)
                .minimumInteractiveComponentSize()
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(3.dp), verticalAlignment = Alignment.CenterVertically) {
                val isAll = selectedMovementFilter == MovementFilter.ALL
                Box(
                    modifier = Modifier
                        .weight(1f).fillMaxHeight().clip(RoundedCornerShape(9.dp))
                        .background(if (isAll) appColors.accent.copy(alpha = 0.14f) else Color.Transparent)
                        .clickable { haptics.lightTick(); onMovementFilterChange(MovementFilter.ALL) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) "সকল (${convertToBengaliDigits(totalCommoditiesCount.toString())})" else "All ($totalCommoditiesCount)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = if (isAll) WeightBold else WeightMedium,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = if (isAll) appColors.accent else appColors.textSecondary
                    )
                }

                val isUp = selectedMovementFilter == MovementFilter.UP
                Box(
                    modifier = Modifier
                        .weight(1f).fillMaxHeight().clip(RoundedCornerShape(9.dp))
                        .background(if (isUp) appColors.expense.copy(alpha = 0.16f) else Color.Transparent)
                        .clickable { haptics.lightTick(); onMovementFilterChange(if (isUp) MovementFilter.ALL else MovementFilter.UP) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) "বেড়েছে (${convertToBengaliDigits(priceUpCount.toString())})" else "Hiked ($priceUpCount)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = if (isUp) WeightBold else WeightMedium,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = if (isUp) appColors.expense else appColors.textSecondary
                    )
                }

                val isDown = selectedMovementFilter == MovementFilter.DOWN
                Box(
                    modifier = Modifier
                        .weight(1f).fillMaxHeight().clip(RoundedCornerShape(9.dp))
                        .background(if (isDown) appColors.income.copy(alpha = 0.16f) else Color.Transparent)
                        .clickable { haptics.lightTick(); onMovementFilterChange(if (isDown) MovementFilter.ALL else MovementFilter.DOWN) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) "কমেছে (${convertToBengaliDigits(priceDownCount.toString())})" else "Dropped ($priceDownCount)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = if (isDown) WeightBold else WeightMedium,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = if (isDown) appColors.income else appColors.textSecondary
                    )
                }
            }
        }

        Box {
            Surface(
                onClick = { haptics.lightTick(); isSortMenuExpanded = true },
                shape = RoundedCornerShape(12.dp),
                color = appColors.cardBackground,
                border = BorderStroke(HairlineInkStroke, appColors.divider),
                modifier = Modifier.defaultMinSize(minHeight = 44.dp).height(44.dp).minimumInteractiveComponentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${selectedSortOrder.iconEmoji} ${if (isBangla) selectedSortOrder.titleBn else selectedSortOrder.titleEn}",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = WeightSemiBold),
                        color = appColors.textPrimary
                    )
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = appColors.textSecondary, modifier = Modifier.size(16.dp))
                }
            }

            DropdownMenu(expanded = isSortMenuExpanded, onDismissRequest = { isSortMenuExpanded = false }) {
                PriceSortOrder.entries.forEach { sortOption ->
                    DropdownMenuItem(
                        text = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = sortOption.iconEmoji, fontSize = 14.sp)
                                Text(
                                    text = if (isBangla) sortOption.titleBn else sortOption.titleEn,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (selectedSortOrder == sortOption) WeightBold else WeightRegular,
                                        fontSize = 13.sp
                                    ),
                                    color = if (selectedSortOrder == sortOption) appColors.accent else appColors.textPrimary
                                )
                            }
                        },
                        onClick = {
                            haptics.lightTick()
                            onSortOrderChange(sortOption)
                            isSortMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}
