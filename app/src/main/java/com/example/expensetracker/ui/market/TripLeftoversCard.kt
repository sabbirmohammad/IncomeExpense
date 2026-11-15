package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TripLeftoversCard(
    unboughtCount: Int,
    leftoverOption: LeftoverHandlingOption,
    onOptionSelected: (LeftoverHandlingOption) -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.surface,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val unboughtCountDigits = if (isBangla) convertToBengaliDigits(unboughtCount.toString()) else unboughtCount.toString()
            Text(
                text = if (isBangla) "বাকি থাকা ($unboughtCountDigits) পণ্যের ক্ষেত্রে কি করবেন?"
                else "What to do with $unboughtCountDigits unbought item(s)?",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                ),
                color = appColors.textPrimary
            )

            // 3 Option Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Move to next list
                val isMove = leftoverOption == LeftoverHandlingOption.MOVE_TO_NEXT
                Surface(
                    onClick = {
                        haptics.lightTick()
                        onOptionSelected(LeftoverHandlingOption.MOVE_TO_NEXT)
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isMove) appColors.accentContainer else Color.Transparent,
                    border = BorderStroke(
                        HairlineInkStroke,
                        if (isMove) appColors.accent else appColors.divider
                    ),
                    modifier = Modifier
                        .weight(1.1f)
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.marketMoveToNextList,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = if (isMove) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isMove) appColors.accent else appColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 2. Keep in list
                val isKeep = leftoverOption == LeftoverHandlingOption.KEEP_IN_LIST
                Surface(
                    onClick = {
                        haptics.lightTick()
                        onOptionSelected(LeftoverHandlingOption.KEEP_IN_LIST)
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isKeep) appColors.accentContainer else Color.Transparent,
                    border = BorderStroke(
                        HairlineInkStroke,
                        if (isKeep) appColors.accent else appColors.divider
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.marketKeepInList,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = if (isKeep) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isKeep) appColors.accent else appColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 3. Discard
                val isDiscard = leftoverOption == LeftoverHandlingOption.DISCARD
                Surface(
                    onClick = {
                        haptics.lightTick()
                        onOptionSelected(LeftoverHandlingOption.DISCARD)
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDiscard) appColors.warning.copy(alpha = 0.15f) else Color.Transparent,
                    border = BorderStroke(
                        HairlineInkStroke,
                        if (isDiscard) appColors.warning else appColors.divider
                    ),
                    modifier = Modifier
                        .weight(0.9f)
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.marketDiscardLeftover,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = if (isDiscard) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isDiscard) appColors.warning else appColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
