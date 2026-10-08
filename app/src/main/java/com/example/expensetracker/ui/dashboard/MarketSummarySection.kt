package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketSummarySection(
    totalCount: Int,
    purchasedCount: Int,
    totalSpent: Double,
    currencySymbol: String,
    isBangla: Boolean,
    selectedCategoryFilter: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()
    val strings = LocalAppStrings.current
    val progress = if (totalCount > 0) purchasedCount.toFloat() / totalCount.toFloat() else 0f

    Column(modifier = modifier.fillMaxWidth()) {
        // Summary Progress Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = LocalAppColors.current.background,
            border = BorderStroke(1.dp, LocalAppColors.current.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla)
                            "কেনা সম্পন্ন: ${convertToBengaliDigits(purchasedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}টি"
                        else
                            "Purchased: $purchasedCount/$totalCount items",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ColorTextPrimary
                    )

                    Text(
                        text = "${if (isBangla) "মোট খরচ: " else "Total Spent: "}${formatCurrency(totalSpent, currencySymbol)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorSuccessIncome
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ColorSuccessIncome,
                    trackColor = LocalAppColors.current.border
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        val categories = listOf(
            null to (if (isBangla) "সব" else "All"),
            "veggies" to strings.categoryVeggies,
            "meat_fish" to strings.categoryMeatFish,
            "groceries" to strings.categoryGroceries,
            "dairy_bakery" to strings.categoryDairyBakery
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(categories) { (catKey, label) ->
                val isSelected = selectedCategoryFilter == catKey
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) ColorAccent.copy(alpha = 0.15f) else LocalAppColors.current.background,
                    border = BorderStroke(1.dp, if (isSelected) ColorAccent else LocalAppColors.current.border),
                    modifier = Modifier.clickable {
                        haptics.lightTick()
                        onCategorySelected(catKey)
                    }
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) ColorAccent else ColorTextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Table Column Header
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = LocalAppColors.current.background.copy(alpha = 0.65f),
            border = BorderStroke(1.dp, LocalAppColors.current.border.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = strings.marketColItem,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    ),
                    color = ColorTextSecondary,
                    modifier = Modifier.weight(1.3f)
                )
                Text(
                    text = strings.marketColQty,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    ),
                    color = ColorTextSecondary,
                    modifier = Modifier.weight(1.1f)
                )
                Row(
                    modifier = Modifier.weight(1.2f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = strings.marketColPrice,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        ),
                        color = ColorTextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = strings.marketColDone,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        ),
                        color = ColorTextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(28.dp)) // space for delete action
            }
        }
    }
}
