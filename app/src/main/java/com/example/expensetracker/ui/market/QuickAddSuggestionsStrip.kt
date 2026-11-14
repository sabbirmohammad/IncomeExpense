package com.example.expensetracker.ui.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun QuickAddSuggestionsStrip(
    suggestions: List<Triple<String, Double?, String>>,
    isBangla: Boolean,
    onSuggestionClick: (name: String, price: Double?, unit: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    AnimatedVisibility(
        visible = suggestions.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(suggestions, key = { it.first }) { (sugName, price, unit) ->
                val priceLabel = if (price != null && price > 0) {
                    val pStr = if (price % 1.0 == 0.0) price.toInt().toString() else "%.1f".format(price)
                    val pDigits = if (isBangla) convertToBengaliDigits(pStr) else pStr
                    "৳$pDigits/$unit"
                } else unit

                Surface(
                    onClick = { onSuggestionClick(sugName, price, unit) },
                    shape = RoundedCornerShape(10.dp),
                    color = appColors.accentContainer.copy(alpha = 0.35f),
                    border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 36.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = sugName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = "($priceLabel)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
