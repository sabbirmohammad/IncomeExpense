package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.*
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun PriceDepartmentHeaderView(
    department: DepartmentMeta,
    count: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(200),
        label = "deptArrow"
    )
    val countText = if (isBangla) convertToBengaliDigits(count.toString()) else count.toString()

    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp)
            .defaultMinSize(minHeight = 44.dp)
            .minimumInteractiveComponentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val dotColor = when (department.key) {
                    "veggies" -> appColors.income
                    "meat_fish" -> appColors.expense
                    "dairy_bakery" -> appColors.accent
                    else -> appColors.textSecondary
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Text(
                    text = if (isBangla) department.titleBn else department.titleEn,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = WeightBold,
                        fontSize = 13.5.sp
                    ),
                    color = appColors.textPrimary
                )
                Text(
                    text = "($countText)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = WeightMedium,
                        fontFeatureSettings = TabularNumFeature
                    ),
                    color = appColors.textSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = appColors.textSecondary,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(arrowRotation)
            )
        }
    }
}
