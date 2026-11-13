package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.LocalAppStrings

/**
 * Action buttons for Market Price Entry sheet: Primary Save & Next / Save, plus Auto-advance toggle.
 */
@Composable
fun MarketPriceEntryActionButtons(
    hasNextPendingItem: Boolean,
    autoAdvanceEnabled: Boolean,
    onAutoAdvanceToggle: () -> Unit,
    onSaveClick: (shouldAdvance: Boolean) -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val isNextAvailable = hasNextPendingItem && autoAdvanceEnabled

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Primary Action Button
        Button(
            onClick = {
                haptics.mediumClick()
                onSaveClick(isNextAvailable)
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isNextAvailable) Icons.AutoMirrored.Filled.ArrowForward else Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isNextAvailable) strings.marketSaveAndNext else strings.save,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = Color.White
                )
            }
        }

        // Secondary row: "Save" (without advancing) + Auto-advance toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (hasNextPendingItem) {
                OutlinedButton(
                    onClick = {
                        haptics.lightTick()
                        onSaveClick(false)
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(HairlineInkStroke, appColors.divider),
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(
                        text = strings.save,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = appColors.textPrimary
                    )
                }
            }

            // Auto-advance toggle chip
            Surface(
                onClick = {
                    haptics.lightTick()
                    onAutoAdvanceToggle()
                },
                shape = RoundedCornerShape(8.dp),
                color = if (autoAdvanceEnabled) appColors.accentContainer.copy(alpha = 0.4f) else Color.Transparent,
                border = BorderStroke(
                    HairlineInkStroke,
                    if (autoAdvanceEnabled) appColors.accent else appColors.divider
                ),
                modifier = Modifier
                    .defaultMinSize(minHeight = 36.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (autoAdvanceEnabled) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = null,
                        tint = if (autoAdvanceEnabled) appColors.accent else appColors.textMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = strings.marketAutoAdvance,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (autoAdvanceEnabled) appColors.accent else appColors.textSecondary
                    )
                }
            }
        }
    }
}
