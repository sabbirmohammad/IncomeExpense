package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.WeightBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommoditySwipeAddBackground(
    dismissState: SwipeToDismissBoxState,
    addToListLabel: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val direction = dismissState.dismissDirection

    if (direction == SwipeToDismissBoxValue.StartToEnd) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
                .background(appColors.income.copy(alpha = 0.85f))
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddShoppingCart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = addToListLabel,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = WeightBold,
                        fontSize = 13.5.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}
