package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import com.example.expensetracker.data.model.toFinancialSummary
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.ui.components.convertToBengaliDigits
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.StatementExportHelper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.ListRow
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PillFilterChip
import com.example.expensetracker.ui.components.ProfileAvatarButton
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onEditTransaction: (Transaction) -> Unit,
    onAddTransactionForDate: ((Long) -> Unit)? = null,
    onNavigateToSettings: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val transactions by viewModel.filteredTransactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val selectedTimeFilter by viewModel.selectedTimeFilter.collectAsState()
    val isMarketTripsView by viewModel.isMarketTripsView.collectAsState()
    val marketLists by viewModel.marketLists.collectAsState()

    val groupedTransactions = remember(transactions, strings.today, strings.yesterday, strings.isBangla, userProfile.useBengaliNumerals) {
        val calNow = Calendar.getInstance()
        val nowYear = calNow.get(Calendar.YEAR)
        val nowDayOfYear = calNow.get(Calendar.DAY_OF_YEAR)
        val calTx = Calendar.getInstance()

        transactions.groupBy { tx ->
            calTx.timeInMillis = tx.dateMillis
            val txYear = calTx.get(Calendar.YEAR)
            val txDayOfYear = calTx.get(Calendar.DAY_OF_YEAR)

            if (nowYear == txYear && nowDayOfYear == txDayOfYear) {
                strings.today
            } else if (nowYear == txYear && nowDayOfYear - txDayOfYear == 1) {
                strings.yesterday
            } else {
                DateTimeHelper.formatDayGroup(tx.dateMillis, strings.isBangla, userProfile.useBengaliNumerals || strings.isBangla)
            }
        }
    }

    val activeFilterCount = (if (selectedType != null) 1 else 0) +
        (if (selectedTimeFilter != TimeFilter.ALL) 1 else 0) +
        (if (searchQuery.isNotBlank()) 1 else 0)

    var showShareSheet by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var sharingTargetDateMillis by remember { mutableStateOf<Long?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptics = rememberAppHaptics()
    var showCustomDateRangeDialog by remember { mutableStateOf(false) }
    var tripToDelete by remember { mutableStateOf<MarketListWithItems?>(null) }
    var isSearchActive by remember { mutableStateOf(searchQuery.isNotBlank()) }
    var showTypeMenu by remember { mutableStateOf(false) }
    var showTimeMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // Header per spec: screen title (18px / 500) + subtitle (12px / 400) + Profile Avatar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onMenuClick != null) {
                    DrawerMenuButton(onClick = onMenuClick)
                }
                Text(
                    text = strings.transactionsTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextPrimary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quick Statement Export Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ColorSurface)
                        .border(1.dp, ColorBorder, CircleShape)
                        .clickable { showExportDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FileDownload,
                        contentDescription = "Export statement",
                        tint = ColorTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Share Expense Slip Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ColorSurface)
                        .border(1.dp, ColorBorder, CircleShape)
                        .clickable {
                            sharingTargetDateMillis = null
                            showShareSheet = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share expense report",
                        tint = ColorTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (onNavigateToSettings != null) {
                    ProfileAvatarButton(onClick = onNavigateToSettings)
                }
            }
        }

        // --- COMPACT SINGLE ROW FILTER BAR & EXPANDABLE SEARCH ---
        AnimatedVisibility(
            visible = isSearchActive || searchQuery.isNotBlank(),
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
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
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
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
                            viewModel.onSearchQueryChanged("")
                            isSearchActive = false
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
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Search Icon Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ColorSurface,
                    border = BorderStroke(1.dp, ColorBorder),
                    modifier = Modifier
                        .size(38.dp)
                        .clickable {
                            haptics.lightTick()
                            isSearchActive = true
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

                // 2. Type Filter Dropdown
                Box {
                    val typeInfo = when {
                        isMarketTripsView -> FilterTypeInfo(
                            strings.marketTripsFilter,
                            Icons.Outlined.ShoppingCart,
                            Color(0xFF2EC4B6),
                            true
                        )
                        selectedType == TransactionType.EXPENSE -> FilterTypeInfo(
                            strings.expensesFilter,
                            Icons.Outlined.ArrowDownward,
                            ColorDangerExpense,
                            true
                        )
                        selectedType == TransactionType.INCOME -> FilterTypeInfo(
                            strings.incomesFilter,
                            Icons.Outlined.ArrowUpward,
                            ColorSuccessIncome,
                            true
                        )
                        else -> FilterTypeInfo(
                            if (strings.isBangla) "সকল ধরন" else "All Types",
                            Icons.Outlined.FilterList,
                            ColorAccent,
                            false
                        )
                    }

                    DropdownFilterChip(
                        text = typeInfo.text,
                        isActive = typeInfo.isActive,
                        icon = typeInfo.icon,
                        activeColor = typeInfo.color,
                        onClick = {
                            haptics.lightTick()
                            showTypeMenu = true
                        }
                    )

                    DropdownMenu(
                        expanded = showTypeMenu,
                        onDismissRequest = { showTypeMenu = false },
                        modifier = Modifier.background(ColorSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (strings.isBangla) "সব লেনদেন" else "All Transactions") },
                            leadingIcon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = ColorTextPrimary) },
                            onClick = {
                                haptics.lightTick()
                                viewModel.setMarketTripsView(false)
                                viewModel.setTypeFilter(null)
                                showTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.expensesFilter, color = ColorDangerExpense) },
                            leadingIcon = { Icon(Icons.Outlined.ArrowDownward, contentDescription = null, tint = ColorDangerExpense) },
                            onClick = {
                                haptics.lightTick()
                                viewModel.setMarketTripsView(false)
                                viewModel.setTypeFilter(TransactionType.EXPENSE)
                                showTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.incomesFilter, color = ColorSuccessIncome) },
                            leadingIcon = { Icon(Icons.Outlined.ArrowUpward, contentDescription = null, tint = ColorSuccessIncome) },
                            onClick = {
                                haptics.lightTick()
                                viewModel.setMarketTripsView(false)
                                viewModel.setTypeFilter(TransactionType.INCOME)
                                showTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (strings.isBangla) "বাজারের ফর্দ ও হিস্ট্রি" else "Market Trips", color = Color(0xFF2EC4B6)) },
                            leadingIcon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = Color(0xFF2EC4B6)) },
                            onClick = {
                                haptics.lightTick()
                                viewModel.setMarketTripsView(true)
                                showTypeMenu = false
                            }
                        )
                    }
                }

                // 3. Time Filter Dropdown
                Box {
                    val (timeText, isTimeActive) = when (selectedTimeFilter) {
                        TimeFilter.ALL -> Pair(strings.allTime, false)
                        TimeFilter.THIS_MONTH -> Pair(strings.thisMonth, true)
                        TimeFilter.THIS_WEEK -> Pair(strings.thisWeek, true)
                        TimeFilter.TODAY -> Pair(strings.today, true)
                        TimeFilter.THIS_YEAR -> Pair(strings.thisYear, true)
                        TimeFilter.CUSTOM -> Pair(strings.customDateRange, true)
                    }

                    DropdownFilterChip(
                        text = timeText,
                        isActive = isTimeActive,
                        icon = Icons.Outlined.CalendarToday,
                        activeColor = ColorAccent,
                        onClick = {
                            haptics.lightTick()
                            showTimeMenu = true
                        }
                    )

                    DropdownMenu(
                        expanded = showTimeMenu,
                        onDismissRequest = { showTimeMenu = false },
                        modifier = Modifier.background(ColorSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text(strings.allTime) },
                            onClick = {
                                haptics.lightTick()
                                viewModel.setTimeFilter(TimeFilter.ALL)
                                showTimeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.thisMonth) },
                            onClick = {
                                haptics.lightTick()
                                viewModel.setTimeFilter(TimeFilter.THIS_MONTH)
                                showTimeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.thisWeek) },
                            onClick = {
                                haptics.lightTick()
                                viewModel.setTimeFilter(TimeFilter.THIS_WEEK)
                                showTimeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.today) },
                            onClick = {
                                haptics.lightTick()
                                viewModel.setTimeFilter(TimeFilter.TODAY)
                                showTimeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.thisYear) },
                            onClick = {
                                haptics.lightTick()
                                viewModel.setTimeFilter(TimeFilter.THIS_YEAR)
                                showTimeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.customDateRange) },
                            onClick = {
                                haptics.lightTick()
                                showTimeMenu = false
                                showCustomDateRangeDialog = true
                            }
                        )
                    }
                }

                // 4. Reset All Filters Button
                if (activeFilterCount > 0 || isMarketTripsView) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ColorDangerExpense.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ColorDangerExpense.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable {
                            haptics.mediumClick()
                            viewModel.onSearchQueryChanged("")
                            viewModel.setTypeFilter(null)
                            viewModel.setCategoryFilter(null)
                            viewModel.setTimeFilter(TimeFilter.ALL)
                            viewModel.setMarketTripsView(false)
                            isSearchActive = false
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

        Spacer(modifier = Modifier.height(4.dp))

        // Transactions List, Market Trips History, or Empty State
        if (isMarketTripsView) {
            MarketTripsHistoryView(
                marketLists = marketLists,
                currencySymbol = userProfile.currencySymbol,
                isBangla = strings.isBangla,
                useBengaliNumerals = userProfile.useBengaliNumerals || strings.isBangla,
                strings = strings,
                onDeleteTrip = { trip -> tripToDelete = trip },
                onBackToTransactions = { viewModel.setMarketTripsView(false) }
            )
        } else if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard(
                    icon = Icons.Outlined.ReceiptLong,
                    headline = strings.noTransactionsFound,
                    explanation = if (searchQuery.isNotEmpty() || selectedType != null || selectedCategoryId != null) {
                        strings.noTransactionsAdjustFilter
                    } else {
                        strings.startTrackingSubtitle
                    },
                    buttonText = strings.clearFilters,
                    onButtonClick = {
                        viewModel.onSearchQueryChanged("")
                        viewModel.setTypeFilter(null)
                        viewModel.setCategoryFilter(null)
                    }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                groupedTransactions.forEach { (dateGroup, itemsInGroup) ->
                    var groupExpense = 0.0
                    var groupIncome = 0.0
                    for (item in itemsInGroup) {
                        if (item.type == TransactionType.EXPENSE) groupExpense += item.amount
                        else if (item.type == TransactionType.INCOME) groupIncome += item.amount
                    }
                    val net = groupIncome - groupExpense

                    item(key = "header_$dateGroup") {
                        // Date header: Friendly date + right-aligned daily net summary (+ green / - red)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = dateGroup,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = ColorTextPrimary
                                )
                                Text(
                                    text = "• ${DateTimeHelper.formatCounter(itemsInGroup.size, strings.isBangla)}${if (strings.isBangla) "টি" else ""}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = ColorTextMuted
                                )
                            }

                            val netText = "${if (net >= 0) "+" else "-"}${formatCurrency(abs(net), userProfile.currencySymbol)}"
                            val netColor = if (net >= 0) ColorSuccessIncome else ColorDangerExpense

                            Text(
                                text = netText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = netColor
                            )
                        }
                    }

                    item(key = "card_$dateGroup") {
                        // Continuous card for the date group with 0.5px hairline divider (none after last row)
                        AppCard(
                            padding = 0.dp
                        ) {
                            Column {
                                val visibleItems = remember(itemsInGroup, marketLists) {
                                    val seenTripIds = mutableSetOf<Long>()
                                    val list = mutableListOf<Pair<Transaction, MarketListWithItems?>>()
                                    for (tx in itemsInGroup) {
                                        val trip = marketLists.find { t ->
                                            t.items.any { it.transactionId == tx.id } ||
                                            (tx.notes.contains("বাজারের বিবরণ") && t.list.createdAt == tx.dateMillis)
                                        }
                                        if (trip != null) {
                                            if (seenTripIds.add(trip.list.id)) {
                                                list.add(Pair(tx, trip))
                                            }
                                        } else {
                                            list.add(Pair(tx, null))
                                        }
                                    }
                                    list
                                }

                                visibleItems.forEachIndexed { index, (tx, associatedMarketTrip) ->
                                    if (associatedMarketTrip != null) {
                                        MarketTripHistoryCard(
                                            trip = associatedMarketTrip,
                                            currencySymbol = userProfile.currencySymbol,
                                            isBangla = strings.isBangla,
                                            useBengaliNumerals = userProfile.useBengaliNumerals || strings.isBangla,
                                            onDeleteClick = { tripToDelete = associatedMarketTrip },
                                            isEmbedded = true
                                        )
                                    } else {
                                        val isIncome = tx.type == TransactionType.INCOME
                                        val amountFormatted = "${if (isIncome) "+" else "-"} ${formatCurrency(tx.amount, userProfile.currencySymbol, userProfile.useBengaliNumerals || strings.isBangla)}"
                                        val amountColor = if (isIncome) ColorSuccessIncome else ColorDangerExpense
                                        val meta = "${getLocalizedCategoryName(tx.categoryName, strings.isBangla)} • ${DateTimeHelper.formatTimeOnly(tx.dateMillis, strings.isBangla, userProfile.useBengaliNumerals || strings.isBangla)}"

                                        val dismissState = rememberSwipeToDismissBoxState(
                                            confirmValueChange = { dismissValue ->
                                                when (dismissValue) {
                                                    SwipeToDismissBoxValue.EndToStart -> {
                                                        // Swipe Left: Delete with Undo snackbar
                                                        viewModel.deleteTransaction(tx)
                                                        scope.launch {
                                                            val result = snackbarHostState.showSnackbar(
                                                                message = strings.transactionDeleted,
                                                                actionLabel = strings.undo,
                                                                duration = SnackbarDuration.Short
                                                            )
                                                            if (result == SnackbarResult.ActionPerformed) {
                                                                viewModel.restoreTransaction(tx)
                                                            }
                                                        }
                                                        true
                                                    }
                                                    SwipeToDismissBoxValue.StartToEnd -> {
                                                        // Swipe Right: Quick Edit
                                                        onEditTransaction(tx)
                                                        false
                                                    }
                                                    SwipeToDismissBoxValue.Settled -> false
                                                }
                                            }
                                        )

                                        SwipeToDismissBox(
                                            state = dismissState,
                                            backgroundContent = {
                                                val direction = dismissState.dismissDirection
                                                val isDelete = direction == SwipeToDismissBoxValue.EndToStart
                                                val bgColor = if (isDelete) ColorDangerExpense else ColorAccent
                                                val icon = if (isDelete) Icons.Outlined.Delete else Icons.Outlined.Edit
                                                val alignment = if (isDelete) Alignment.CenterEnd else Alignment.CenterStart
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(bgColor)
                                                        .padding(horizontal = 20.dp),
                                                    contentAlignment = alignment
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            },
                                            enableDismissFromStartToEnd = true,
                                            enableDismissFromEndToStart = true
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(LocalAppColors.current.surface)
                                                    .padding(horizontal = 16.dp)
                                            ) {
                                                ListRow(
                                                    iconName = tx.categoryIcon,
                                                    categoryColorHex = tx.categoryColor,
                                                    title = tx.title,
                                                    meta = meta,
                                                    amountText = amountFormatted,
                                                    amountColor = amountColor,
                                                    onClick = { onEditTransaction(tx) },
                                                    paymentMethod = tx.paymentMethod,
                                                    paymentMethodName = getLocalizedAccountName(tx.accountName, strings.isBangla),
                                                    hasReceipt = tx.receiptImagePath != null
                                                )
                                            }
                                        }
                                    }

                                    if (index < visibleItems.size - 1) {
                                        HairlineDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
    )

    if (showShareSheet) {
        ShareExpenseSheet(
            initialPeriod = if (sharingTargetDateMillis != null) SharePeriodType.TODAY else SharePeriodType.THIS_MONTH,
            initialTargetDateMillis = sharingTargetDateMillis,
            onDismiss = {
                showShareSheet = false
                sharingTargetDateMillis = null
            }
        )
    }

    if (tripToDelete != null) {
        val target = tripToDelete!!
        AlertDialog(
            onDismissRequest = { tripToDelete = null },
            title = {
                Text(
                    text = if (strings.isBangla) "বাজার রেকর্ড মুছে ফেলবেন?" else "Delete Market Record?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ColorTextPrimary
                )
            },
            text = {
                Text(
                    text = if (strings.isBangla) "এই বাজার ফর্দটি এবং এর সমস্ত বিবরণ লেনদেন থেকে মুছে যাবে।"
                    else "This market trip record and all its items will be removed from transactions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        haptics.mediumClick()
                        viewModel.deleteMarketTrip(target.list.id)
                        tripToDelete = null
                    }
                ) {
                    Text(
                        text = if (strings.isBangla) "মুছে ফেলুন" else "Delete",
                        color = ColorDangerExpense,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { tripToDelete = null }) {
                    Text(
                        text = if (strings.isBangla) "বাতিল" else "Cancel",
                        color = ColorTextSecondary
                    )
                }
            },
            containerColor = ColorSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showExportDialog) {
        val context = androidx.compose.ui.platform.LocalContext.current

        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = ColorSurface,
            title = {
                Text(
                    text = if (strings.isBangla) "স্টেটমেন্ট এক্সপোর্ট" else "Export Statement",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = ColorTextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val txCount = DateTimeHelper.formatCounter(transactions.size, strings.isBangla, userProfile.useBengaliNumerals || strings.isBangla)
                    val exportSubtitleText = if (strings.isBangla) {
                        "আপনার $txCount টি লেনদেনের অফিসিয়াল রেকর্ড ডাউনলোড ও শেয়ার করুন।"
                    } else {
                        "Download and share official records for your ${transactions.size} transactions."
                    }
                    Text(
                        text = exportSubtitleText,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = ColorTextSecondary
                    )

                    // PDF Option Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ColorBackground,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch(Dispatchers.IO) {
                                    val summary = transactions.toFinancialSummary()
                                    StatementExportHelper.exportAndSharePdf(
                                        context = context,
                                        profile = userProfile,
                                        summary = summary,
                                        transactions = transactions,
                                        periodTitle = if (strings.isBangla) "লেনদেন বিবরণী" else "Transactions Report"
                                    )
                                }
                                showExportDialog = false
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ColorDangerExpense.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PictureAsPdf,
                                    contentDescription = null,
                                    tint = ColorDangerExpense,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (strings.isBangla) "PDF আর্থিক স্টেটমেন্ট" else "PDF Financial Statement",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                                    color = ColorTextPrimary
                                )
                                Text(
                                    text = if (strings.isBangla) "ব্র্যান্ডেড লেআউট, মেট্রিক্স ও লেজার সহ A4 ফরম্যাট" else "A4 format with branded layout, metrics & ledger",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = ColorTextSecondary
                                )
                            }
                        }
                    }

                    // CSV / Excel Option Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ColorBackground,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch(Dispatchers.IO) {
                                    StatementExportHelper.exportAndShareCsv(
                                        context = context,
                                        profile = userProfile,
                                        transactions = transactions,
                                        periodTitle = if (strings.isBangla) "লেনদেন বিবরণী" else "Transactions Report"
                                    )
                                }
                                showExportDialog = false
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ColorSuccessIncome.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.TableChart,
                                    contentDescription = null,
                                    tint = ColorSuccessIncome,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (strings.isBangla) "এক্সেল স্প্রেডশীট (CSV)" else "Excel Spreadsheet (CSV)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                                    color = ColorTextPrimary
                                )
                                Text(
                                    text = if (strings.isBangla) "Excel ও Google Sheets এর জন্য UTF-8 BOM সমর্থিত" else "UTF-8 BOM encoded for Excel & Google Sheets",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = ColorTextSecondary
                                )
                            }
                        }
                    }

                    // Image Slip Option Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ColorBackground,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showExportDialog = false
                                sharingTargetDateMillis = null
                                showShareSheet = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ColorAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (strings.isBangla) "ইমেজ স্লিপ ও সারাংশ" else "Image Slip & Summary",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                                    color = ColorTextPrimary
                                )
                                Text(
                                    text = if (strings.isBangla) "লাইভ প্রিভিউ ও ছবি শেয়ারিং সুবিধা" else "Visual receipt card with live preview & image sharing",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = ColorTextSecondary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showExportDialog = false }) {
                    Text(strings.close, color = ColorTextSecondary)
                }
            }
        )
    }

    if (showCustomDateRangeDialog) {
        val dateRangePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showCustomDateRangeDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis ?: start
                        if (start != null && end != null) {
                            val endCal = Calendar.getInstance().apply {
                                timeInMillis = end
                                set(Calendar.HOUR_OF_DAY, 23)
                                set(Calendar.MINUTE, 59)
                                set(Calendar.SECOND, 59)
                            }
                            viewModel.setCustomDateRange(start, endCal.timeInMillis)
                        }
                        showCustomDateRangeDialog = false
                    }
                ) {
                    Text(strings.confirm, color = ColorAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDateRangeDialog = false }) {
                    Text(strings.cancel, color = LocalAppColors.current.textSecondary)
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = strings.selectDateRange,
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LocalAppColors.current.textPrimary
                    )
                }
            )
        }
    }
    }
}

private data class FilterTypeInfo(
    val text: String,
    val icon: ImageVector,
    val color: Color,
    val isActive: Boolean
)

@Composable
fun MarketTripsHistoryView(
    marketLists: List<MarketListWithItems>,
    currencySymbol: String,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    strings: com.example.expensetracker.ui.components.Strings,
    onDeleteTrip: (MarketListWithItems) -> Unit,
    onBackToTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (marketLists.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateCard(
                icon = Icons.Outlined.ShoppingCart,
                headline = strings.noMarketTrips,
                explanation = strings.noMarketTripsHint,
                buttonText = if (isBangla) "সব লেনদেন দেখুন" else "View All Transactions",
                onButtonClick = onBackToTransactions
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(marketLists, key = { it.list.id }) { trip ->
                MarketTripHistoryCard(
                    trip = trip,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla,
                    useBengaliNumerals = useBengaliNumerals,
                    onDeleteClick = { onDeleteTrip(trip) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun MarketTripHistoryCard(
    trip: MarketListWithItems,
    currencySymbol: String,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEmbedded: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    val haptics = rememberAppHaptics()
    val dateText = remember(trip.list.createdAt, isBangla, useBengaliNumerals) {
        val datePart = SimpleDateFormat("d MMMM yyyy", DateTimeHelper.getLocale(isBangla)).format(Date(trip.list.createdAt))
        val timePart = DateTimeHelper.formatTimeOnly(trip.list.createdAt, isBangla, useBengaliNumerals)
        val localizedDate = if (useBengaliNumerals || isBangla) convertToBengaliDigits(datePart) else datePart
        "$localizedDate, $timePart"
    }

    val totalItems = trip.totalItems
    val purchasedItems = trip.purchasedItems
    val totalSpent = trip.totalSpent

    val cardContent = @Composable {
        Column(
            modifier = Modifier
                .padding(if (isEmbedded) 12.dp else 14.dp)
                .animateContentSize()
        ) {
            // Header Row: Shopping Cart Icon + Title/Date + Total Amount Spent Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2EC4B6).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFF2EC4B6),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        val displayTitle = remember(trip.list.title, trip.list.createdAt, isBangla, useBengaliNumerals) {
                            val generatedTitle = DateTimeHelper.formatMarketTripTitle(trip.list.createdAt, isBangla, useBengaliNumerals)
                            if (trip.list.title.isBlank() ||
                                trip.list.title == "আজকের বাজার" ||
                                trip.list.title == "বাজারের ফর্দ" ||
                                trip.list.title == "Market Checklist" ||
                                trip.list.title.endsWith("বাজার") ||
                                trip.list.title.endsWith("Market")
                            ) {
                                generatedTitle
                            } else {
                                "$generatedTitle (${trip.list.title})"
                            }
                        }
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ColorTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ColorTextSecondary
                        )
                    }
                }

                // Total Spent Badge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isBangla) "মোট বাজার খরচ" else "Total Spent",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = ColorTextMuted
                    )
                    Text(
                        text = formatCurrency(totalSpent, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorSuccessIncome
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Row: Bought items summary + Expand toggle button + Overflow menu (Protected delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isAllCompleted = purchasedItems == totalItems && totalItems > 0
                val countText = if (isBangla) {
                    if (isAllCompleted) {
                        "✓ ${convertToBengaliDigits(purchasedItems.toString())}/${convertToBengaliDigits(totalItems.toString())}টি সম্পন্ন"
                    } else {
                        "${convertToBengaliDigits(purchasedItems.toString())}/${convertToBengaliDigits(totalItems.toString())}টি কেনা হয়েছে"
                    }
                } else {
                    if (isAllCompleted) "✓ All $totalItems completed" else "$purchasedItems/$totalItems purchased"
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isAllCompleted) ColorSuccessIncome.copy(alpha = 0.12f) else Color(0xFF2EC4B6).copy(alpha = 0.12f),
                    border = BorderStroke(
                        1.dp,
                        if (isAllCompleted) ColorSuccessIncome.copy(alpha = 0.35f) else Color(0xFF2EC4B6).copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text = countText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isAllCompleted) ColorSuccessIncome else Color(0xFF2EC4B6),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Expand/Collapse text pill button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorAccent.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.25f)),
                        modifier = Modifier.clickable {
                            haptics.lightTick()
                            expanded = !expanded
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (expanded) (if (isBangla) "সংক্ষেপ" else "Collapse") else (if (isBangla) "বিস্তারিত" else "Details"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorAccent
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Accidental touch protection: 3-dots overflow menu
                    Box {
                        IconButton(
                            onClick = {
                                haptics.lightTick()
                                showOptionsMenu = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Options",
                                tint = ColorTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false },
                            modifier = Modifier.background(ColorSurface)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isBangla) "বাজার ফর্দ মুছে ফেলুন" else "Delete Market List",
                                        color = ColorDangerExpense,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = null,
                                        tint = ColorDangerExpense,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showOptionsMenu = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }

            // Expandable Items Breakdown
            if (expanded && trip.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                HairlineDivider(color = ColorBorder.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    trip.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(LocalAppColors.current.background)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (item.isPurchased) ColorSuccessIncome else ColorTextMuted.copy(alpha = 0.3f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (item.isPurchased) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = ColorTextPrimary
                                )

                                Text(
                                    text = "(${item.formatQuantity()})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = ColorTextSecondary
                                )
                            }

                            Text(
                                text = if (item.amount != null && item.amount > 0) {
                                    formatCurrency(item.amount, currencySymbol)
                                } else {
                                    if (isBangla) "মূল্য ছাড়া" else "No price"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (item.amount != null && item.amount > 0) ColorSuccessIncome else ColorTextMuted
                            )
                        }
                    }
                }
            }
        }
    }

    if (isEmbedded) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(LocalAppColors.current.surface)
        ) {
            cardContent()
        }
    } else {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = ColorSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
            modifier = modifier.fillMaxWidth()
        ) {
            cardContent()
        }
    }
}

