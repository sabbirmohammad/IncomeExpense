package com.example.expensetracker.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ColorWarningBills
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.AppProgressBar
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.DonutChart
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PaymentMethodDonutChart
import com.example.expensetracker.ui.components.PillFilterChip
import com.example.expensetracker.ui.components.ProfileAvatarButton
import com.example.expensetracker.ui.components.SpendingBarChart
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.getPaymentMethodColor
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onNavigateToCategoryTransactions: (Long) -> Unit = {},
    onNavigateToSettings: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val categorySpends by viewModel.categorySpends.collectAsState()
    val paymentMethodSpends by viewModel.paymentMethodSpends.collectAsState()
    val dailySpends by viewModel.dailySpends.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val overview by viewModel.overview.collectAsState()
    val breakdownMode by viewModel.breakdownMode.collectAsState()
    val timeFilter by viewModel.timeFilter.collectAsState()
    val transactionType by viewModel.transactionType.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val totalAmount = if (transactionType == TransactionType.EXPENSE) summary.totalExpense else summary.totalIncome
    val topCategory = categorySpends.firstOrNull()

    var showShareSheet by remember { mutableStateOf(false) }
    var showTypeMenu by remember { mutableStateOf(false) }
    var showTimeMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
            .padding(horizontal = 16.dp)
    ) {
        // Pinned Header: title + subtitle + Share & Profile buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onMenuClick != null) {
                    DrawerMenuButton(onClick = onMenuClick)
                } else if (onNavigateBack != null) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = ColorTextPrimary
                        )
                    }
                }
                Text(
                    text = strings.analyticsTitle,
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
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ColorSurface)
                        .border(1.dp, ColorBorder, CircleShape)
                        .clickable { showShareSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share analytics report",
                        tint = ColorTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (onNavigateToSettings != null) {
                    ProfileAvatarButton(onClick = onNavigateToSettings)
                }
            }
        }

        // --- COMPACT SINGLE ROW FILTER BAR (TYPE & TIME DROPDOWNS) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Type Dropdown (Expense / Income)
            Box {
                val isExpense = transactionType == TransactionType.EXPENSE
                val typeText = if (isExpense) strings.expense else strings.income
                val typeIcon = if (isExpense) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward
                val typeColor = if (isExpense) ColorDangerExpense else ColorSuccessIncome

                DropdownFilterChip(
                    text = typeText,
                    isActive = true,
                    icon = typeIcon,
                    activeColor = typeColor,
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
                        text = {
                            Text(
                                text = strings.expense,
                                color = ColorDangerExpense,
                                fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ArrowDownward,
                                contentDescription = null,
                                tint = ColorDangerExpense,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            haptics.lightTick()
                            viewModel.setTransactionType(TransactionType.EXPENSE)
                            showTypeMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = strings.income,
                                color = ColorSuccessIncome,
                                fontWeight = if (!isExpense) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ArrowUpward,
                                contentDescription = null,
                                tint = ColorSuccessIncome,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            haptics.lightTick()
                            viewModel.setTransactionType(TransactionType.INCOME)
                            showTypeMenu = false
                        }
                    )
                }
            }

            // 2. Time / Period Dropdown
            Box {
                val timeText = when (timeFilter) {
                    TimeFilter.THIS_MONTH -> strings.thisMonth
                    TimeFilter.THIS_WEEK -> strings.thisWeek
                    TimeFilter.TODAY -> strings.today
                    TimeFilter.THIS_YEAR -> strings.thisYear
                    TimeFilter.ALL -> strings.allTime
                    TimeFilter.CUSTOM -> strings.customDateRange
                }

                DropdownFilterChip(
                    text = timeText,
                    isActive = timeFilter != TimeFilter.ALL,
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
                    listOf(
                        TimeFilter.THIS_MONTH to strings.thisMonth,
                        TimeFilter.THIS_WEEK to strings.thisWeek,
                        TimeFilter.TODAY to strings.today,
                        TimeFilter.THIS_YEAR to strings.thisYear,
                        TimeFilter.ALL to strings.allTime
                    ).forEach { (filter, label) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = label,
                                    color = if (timeFilter == filter) ColorAccent else ColorTextPrimary,
                                    fontWeight = if (timeFilter == filter) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                haptics.lightTick()
                                viewModel.setTimeFilter(filter)
                                showTimeMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

        // Financial Health & Key KPI Metric Cards Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Daily Average
                AppCard(modifier = Modifier.weight(1f), padding = 12.dp) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.TrendingDown,
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (strings.isBangla) "দৈনিক গড়" else "Daily Avg",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCurrency(overview.dailyAverageSpend, userProfile.currencySymbol),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = if (strings.isBangla) "প্রতিদিন" else "per day",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ColorTextMuted
                        )
                    }
                }

                // Card 2: Savings Rate KPI
                val savingsRate = overview.savingsRate
                val savingsColor = when {
                    savingsRate >= 20 -> ColorSuccessIncome
                    savingsRate >= 5 -> ColorWarningBills
                    else -> ColorDangerExpense
                }
                val savingsLabel = when {
                    savingsRate >= 20 -> if (strings.isBangla) "স্বাস্থ্যকর" else "Healthy"
                    savingsRate >= 5 -> if (strings.isBangla) "মাঝারি" else "Moderate"
                    else -> if (strings.isBangla) "কম" else "Low"
                }

                AppCard(modifier = Modifier.weight(1f), padding = 12.dp) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Savings,
                                contentDescription = null,
                                tint = savingsColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (strings.isBangla) "সঞ্চয়ের হার" else "Savings Rate",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${if (strings.isBangla) convertToBengaliDigits(savingsRate.toString()) else savingsRate}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = savingsColor
                        )
                        Text(
                            text = savingsLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = savingsColor
                        )
                    }
                }

                // Card 3: Period Comparison Trend
                val changePct = overview.periodChangePercent
                val trendText = if (changePct != null) {
                    val raw = String.format(Locale.US, "%.1f%%", Math.abs(changePct))
                    val formatted = if (strings.isBangla) convertToBengaliDigits(raw) else raw
                    if (changePct > 0) "+$formatted" else "-$formatted"
                } else {
                    if (strings.isBangla) "স্থিতিশীল" else "Stable"
                }

                val trendColor = if (overview.isPeriodChangeFavorable) ColorSuccessIncome else ColorDangerExpense

                AppCard(modifier = Modifier.weight(1f), padding = 12.dp) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (overview.isPeriodChangeFavorable) Icons.Outlined.TrendingDown else Icons.Outlined.TrendingUp,
                                contentDescription = null,
                                tint = trendColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (strings.isBangla) "পূর্ববর্তী সময়ের তুলনায়" else "Vs Previous",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = trendText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = trendColor,
                            maxLines = 1
                        )
                        Text(
                            text = if (changePct != null) {
                                if (overview.isPeriodChangeFavorable) (if (strings.isBangla) "অনুকূল" else "Favorable")
                                else (if (strings.isBangla) "বেশি খরচ" else "Higher")
                            } else {
                                if (strings.isBangla) "পূর্ববর্তী তথ্য নেই" else "No past data"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ColorTextMuted
                        )
                    }
                }
            }
        }

        // Breakdown Segmented View Mode Toggle: Categories vs Payment Methods
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(SmallElementRadius))
                    .background(ColorSurface)
                    .border(1.dp, ColorBorder, RoundedCornerShape(SmallElementRadius))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnalyticsBreakdownMode.entries.forEach { mode ->
                    val isSelected = breakdownMode == mode
                    val modeLabel = when (mode) {
                        AnalyticsBreakdownMode.CATEGORIES -> strings.category
                        AnalyticsBreakdownMode.PAYMENT_METHODS -> strings.paymentMethod
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(SmallElementRadius - 2.dp))
                            .background(if (isSelected) ColorAccent else Color.Transparent)
                            .clickable { viewModel.setBreakdownMode(mode) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = modeLabel,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                            ),
                            color = if (isSelected) Color.White else ColorTextSecondary
                        )
                    }
                }
            }
        }

        // Mode 1: CATEGORY BREAKDOWN
        if (breakdownMode == AnalyticsBreakdownMode.CATEGORIES) {
            // Donut Chart Card
            item {
                AppCard {
                    Column {
                        Text(
                            text = if (transactionType == TransactionType.EXPENSE) {
                                if (strings.isBangla) "ক্যাটাগরি ভিত্তিক খরচ" else "Expense by category"
                            } else {
                                if (strings.isBangla) "ক্যাটাগরি ভিত্তিক আয়" else "Income by category"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorTextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        DonutChart(
                            categorySpends = categorySpends,
                            currencySymbol = userProfile.currencySymbol,
                            totalAmount = totalAmount,
                            centerLabel = if (transactionType == TransactionType.EXPENSE) {
                                if (strings.isBangla) "মোট খরচ" else "Total spent"
                            } else {
                                if (strings.isBangla) "মোট আয়" else "Total income"
                            },
                            onCategoryClick = onNavigateToCategoryTransactions
                        )
                    }
                }
            }



            // Detailed Interactive Category Ranking Table Card
            if (categorySpends.isNotEmpty() && totalAmount > 0.0) {
                item {
                    AppCard {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (strings.isBangla) "ক্যাটাগরি র‍্যাঙ্কিং ও অনুপাত" else "Category Ranking & Share",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = ColorTextPrimary
                                )
                                Text(
                                    text = "${categorySpends.size} ${if (strings.isBangla) "টি ক্যাটাগরি" else "categories"}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = ColorTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            categorySpends.forEachIndexed { index, spend ->
                                val color = CategoryIconHelper.parseColor(spend.categoryColor)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToCategoryTransactions(spend.categoryId) }
                                        .padding(vertical = 8.dp)
                                    ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            CategoryIconBadge(
                                                iconName = spend.categoryIcon,
                                                colorHex = spend.categoryColor,
                                                size = 32.dp
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = getLocalizedCategoryName(spend.categoryName, strings.isBangla),
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Medium
                                                        ),
                                                        color = ColorTextPrimary
                                                    )
                                                    if (index == 0) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(ColorAccent.copy(alpha = 0.15f))
                                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                                        ) {
                                                            Text(
                                                                text = if (transactionType == TransactionType.EXPENSE) (if (strings.isBangla) "শীর্ষ খাত" else "Top") else (if (strings.isBangla) "শীর্ষ আয়" else "Top"),
                                                                style = MaterialTheme.typography.labelSmall.copy(
                                                                    fontSize = 9.sp,
                                                                    fontWeight = FontWeight.Bold
                                                                ),
                                                                color = ColorAccent
                                                            )
                                                        }
                                                    }
                                                }
                                                Text(
                                                    text = "${spend.transactionCount} ${if (strings.isBangla) "টি লেনদেন" else "transactions"}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = ColorTextMuted
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = formatCurrency(spend.totalAmount, userProfile.currencySymbol),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = ColorTextPrimary
                                            )
                                            Text(
                                                text = "${(spend.percentage * 100).toInt()}%",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                color = ColorTextSecondary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    AppProgressBar(
                                        progress = spend.percentage,
                                        fillColor = color,
                                        height = 4.dp
                                    )
                                }

                                if (index < categorySpends.lastIndex) {
                                    HairlineDivider()
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Mode 2: PAYMENT METHOD BREAKDOWN
            item {
                AppCard {
                    Column {
                        Text(
                            text = if (transactionType == TransactionType.EXPENSE) {
                                if (strings.isBangla) "পেমেন্ট মাধ্যম অনুযায়ী খরচ" else "Spending by payment method"
                            } else {
                                if (strings.isBangla) "পেমেন্ট মাধ্যম অনুযায়ী আয়" else "Income by payment channel"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorTextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        PaymentMethodDonutChart(
                            spends = paymentMethodSpends,
                            currencySymbol = userProfile.currencySymbol,
                            totalAmount = totalAmount,
                            centerLabel = if (transactionType == TransactionType.EXPENSE) {
                                if (strings.isBangla) "মোট খরচ" else "Total spent"
                            } else {
                                if (strings.isBangla) "মোট আয়" else "Total income"
                            }
                        )
                    }
                }
            }

            // Payment Methods Detailed Share List
            if (paymentMethodSpends.isNotEmpty() && totalAmount > 0.0) {
                item {
                    AppCard {
                        Column {
                            Text(
                                text = if (strings.isBangla) "পেমেন্ট চ্যানেল বিশ্লেষণ" else "Payment Channels Breakdown",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = ColorTextPrimary
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            paymentMethodSpends.forEachIndexed { index, spend ->
                                val color = getPaymentMethodColor(spend.paymentMethod)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(SmallElementRadius))
                                                    .background(color.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = when (spend.paymentMethod.name) {
                                                        "CASH" -> Icons.Outlined.AccountBalanceWallet
                                                        "CREDIT_CARD", "DEBIT_CARD" -> Icons.Outlined.CreditCard
                                                        else -> Icons.Outlined.Payment
                                                    },
                                                    contentDescription = null,
                                                    tint = color,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = spend.paymentMethod.getDisplayName(strings.isBangla),
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = ColorTextPrimary,
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${spend.transactionCount} ${if (strings.isBangla) "টি লেনদেন" else "transactions"}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = ColorTextMuted
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = formatCurrency(spend.totalAmount, userProfile.currencySymbol),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = ColorTextPrimary
                                            )
                                            Text(
                                                text = "${(spend.percentage * 100).toInt()}%",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                color = ColorTextSecondary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    AppProgressBar(
                                        progress = spend.percentage,
                                        fillColor = color,
                                        height = 4.dp
                                    )
                                }

                                if (index < paymentMethodSpends.lastIndex) {
                                    HairlineDivider()
                                }
                            }
                        }
                    }
                }
            }
        }

        // Spending Trend Bar Chart Card
        item {
            AppCard {
                Column {
                    Text(
                        text = if (strings.isBangla) "আয় বনাম ব্যয়ের ধারা" else "Income vs expense trend",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SpendingBarChart(
                        dailySpends = dailySpends,
                        currencySymbol = userProfile.currencySymbol
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }

    if (showShareSheet) {
        val mappedPeriod = when (timeFilter) {
            TimeFilter.THIS_WEEK -> SharePeriodType.THIS_WEEK
            TimeFilter.THIS_MONTH -> SharePeriodType.THIS_MONTH
            TimeFilter.TODAY -> SharePeriodType.TODAY
            else -> SharePeriodType.THIS_MONTH
        }
        ShareExpenseSheet(
            initialPeriod = mappedPeriod,
            onDismiss = { showShareSheet = false }
        )
    }
}
}


