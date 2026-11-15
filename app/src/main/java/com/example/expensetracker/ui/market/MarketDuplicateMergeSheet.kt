package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Modal Bottom Sheet for reviewing and merging duplicate commodity names (Target Design E).
 * Retains 100% of price history and never deletes without confirmation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketDuplicateMergeSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    commodities: List<MarketCommodityUiModel>,
    isBangla: Boolean,
    currencySymbol: String = "৳",
    onMergeItems: (sourceName: String, targetName: String) -> Unit,
    onDismiss: () -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val duplicateGroups = remember(commodities) { findDuplicateCommodities(commodities) }

    val selectedTargetNames = remember(duplicateGroups) {
        mutableStateMapOf<String, String>().apply {
            duplicateGroups.forEach { group ->
                put(group.canonicalKey, group.primaryName)
            }
        }
    }

    var groupToConfirmMerge by remember { mutableStateOf<DuplicateCommodityGroup?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = appColors.cardBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            MarketDuplicateMergeHeader(
                duplicateGroupCount = duplicateGroups.size,
                isBangla = isBangla,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = appColors.accent.copy(alpha = 0.08f),
                border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isBangla)
                            "একই পণ্যের বিভিন্ন বানান বা স্পেসের তারতম্য থাকলে তা একটি প্রমিত নামে মার্জ করুন। মার্জ করলে অতীত সব কেনাকাটার ইতিহাস ও দর অক্ষুণ্ণ থাকবে।"
                        else
                            "Consolidate variant spellings and spaced duplicates into one primary name. All price history and purchase records are completely retained.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                        color = appColors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (duplicateGroups.isEmpty()) {
                MarketDuplicateEmptyState(
                    isBangla = isBangla,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(duplicateGroups, key = { it.canonicalKey }) { group ->
                        val chosenTarget = selectedTargetNames[group.canonicalKey] ?: group.primaryName
                        DuplicateGroupCard(
                            group = group,
                            chosenTarget = chosenTarget,
                            onSelectTarget = { newTarget ->
                                haptics.lightTick()
                                selectedTargetNames[group.canonicalKey] = newTarget
                            },
                            onMergeClick = { groupToConfirmMerge = group },
                            isBangla = isBangla,
                            currencySymbol = currencySymbol
                        )
                    }
                }
            }
        }
    }

    groupToConfirmMerge?.let { group ->
        val chosenTarget = selectedTargetNames[group.canonicalKey] ?: group.primaryName
        MarketDuplicateConfirmDialog(
            group = group,
            chosenTarget = chosenTarget,
            isBangla = isBangla,
            onDismiss = { groupToConfirmMerge = null },
            onConfirmMerge = { sources, target ->
                sources.forEach { source ->
                    onMergeItems(source.name, target)
                }
                groupToConfirmMerge = null
            }
        )
    }
}
