package com.example.expensetracker.ui.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketShopItemSwipeBackground(
    dismissState: SwipeToDismissBoxState,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val direction = dismissState.dismissDirection
    val color = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> appColors.income.copy(alpha = 0.2f)
        SwipeToDismissBoxValue.EndToStart -> appColors.warning.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        if (direction == SwipeToDismissBoxValue.StartToEnd) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = appColors.income)
        } else if (direction == SwipeToDismissBoxValue.EndToStart) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = null, tint = appColors.warning)
        }
    }
}
