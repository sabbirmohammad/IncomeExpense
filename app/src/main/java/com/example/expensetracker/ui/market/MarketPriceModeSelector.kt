package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.LocalAppStrings

/**
 * 48dp Segmented control for toggling between "Per unit" rate and "Total price" entry mode.
 */
@Composable
fun MarketPriceModeSelector(
    priceMode: Int,
    onPriceModeChange: (Int) -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.surface,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Option 0: Per unit
            Surface(
                onClick = {
                    haptics.lightTick()
                    onPriceModeChange(0)
                },
                shape = RoundedCornerShape(11.dp),
                color = if (priceMode == 0) appColors.cardBackground else Color.Transparent,
                border = if (priceMode == 0) BorderStroke(HairlineInkStroke, appColors.divider) else null,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = strings.marketPerUnit,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (priceMode == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = if (priceMode == 0) appColors.accent else appColors.textSecondary
                    )
                }
            }

            // Option 1: Total price
            Surface(
                onClick = {
                    haptics.lightTick()
                    onPriceModeChange(1)
                },
                shape = RoundedCornerShape(11.dp),
                color = if (priceMode == 1) appColors.cardBackground else Color.Transparent,
                border = if (priceMode == 1) BorderStroke(HairlineInkStroke, appColors.divider) else null,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = strings.marketTotalPrice,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (priceMode == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = if (priceMode == 1) appColors.accent else appColors.textSecondary
                    )
                }
            }
        }
    }
}
