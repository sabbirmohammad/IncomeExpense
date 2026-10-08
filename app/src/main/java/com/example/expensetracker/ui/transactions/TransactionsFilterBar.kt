package com.example.expensetracker.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionsFilterBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChanged: (Boolean) -> Unit,
    selectedType: TransactionType?,
    onTypeFilterChanged: (TransactionType?) -> Unit,
    isMarketTripsView: Boolean,
    onMarketTripsViewChanged: (Boolean) -> Unit,
    selectedTimeFilter: TimeFilter,
    onTimeFilterChanged: (TimeFilter) -> Unit,
    onOpenCustomDateRange: () -> Unit,
    onResetFilters: () -> Unit,
    activeFilterCount: Int,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()

    AnimatedVisibility(
        visible = isSearchActive || searchQuery.isNotBlank(),
        enter = fadeIn() + expandHorizontally(),
        exit = fadeOut() + shrinkHorizontally()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        text = strings.searchTransactions,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = ColorTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = ColorAccent,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = "Clear",
                                tint = ColorTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorSurface,
                    unfocusedContainerColor = ColorSurface,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary,
                    cursorColor = ColorAccent
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ColorSurface,
                border = BorderStroke(1.dp, ColorBorder),
                modifier = Modifier
                    .size(44.dp)
                    .clickable {
                        haptics.lightTick()
                        onSearchQueryChanged("")
                        onSearchActiveChanged(false)
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close search",
                        tint = ColorTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (!isSearchActive && searchQuery.isBlank()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ColorSurface,
                border = BorderStroke(1.dp, ColorBorder),
                modifier = Modifier
                    .size(38.dp)
                    .clickable {
                        haptics.lightTick()
                        onSearchActiveChanged(true)
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = ColorTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            TransactionTypeFilterDropdown(
                selectedType = selectedType,
                isMarketTripsView = isMarketTripsView,
                onTypeFilterChanged = onTypeFilterChanged,
                onMarketTripsViewChanged = onMarketTripsViewChanged,
                strings = strings
            )

            TransactionTimeFilterDropdown(
                selectedTimeFilter = selectedTimeFilter,
                onTimeFilterChanged = onTimeFilterChanged,
                onOpenCustomDateRange = onOpenCustomDateRange,
                strings = strings
            )

            if (activeFilterCount > 0 || isMarketTripsView) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ColorDangerExpense.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ColorDangerExpense.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable {
                        haptics.mediumClick()
                        onResetFilters()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = null,
                            tint = ColorDangerExpense,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (strings.isBangla) "রিসেট (${DateTimeHelper.formatCounter(activeFilterCount, true)})" else "Reset ($activeFilterCount)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorDangerExpense
                        )
                    }
                }
            }
        }
    }
}
