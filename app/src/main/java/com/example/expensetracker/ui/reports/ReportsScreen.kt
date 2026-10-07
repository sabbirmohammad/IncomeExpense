package com.example.expensetracker.ui.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.ui.components.DateTimeHelper
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
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PillFilterChip
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val haptics = rememberAppHaptics()

    val userProfile by viewModel.userProfile.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val categorySpends by viewModel.categorySpends.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val exportMessage by viewModel.exportMessage.collectAsState()

    var showPeriodMenu by remember { mutableStateOf(false) }
    var showAccountMenu by remember { mutableStateOf(false) }
    var showTypeMenu by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var tempStartDate by remember { mutableStateOf(System.currentTimeMillis()) }

    val periodTitle = viewModel.getPeriodTitle(strings.isBangla)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                Column {
                    Text(
                        text = strings.reportsTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorTextPrimary
                    )
                    Text(
                        text = periodTitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorAccent
                    )
                }
            }

            // Privacy mode toggle eye
            IconButton(
                onClick = {
                    haptics.lightTick()
                    viewModel.togglePrivacyMode()
                }
            ) {
                Icon(
                    imageVector = if (isPrivacyMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (isPrivacyMode) strings.showBalance else strings.hideBalance,
                    tint = if (isPrivacyMode) ColorAccent else ColorTextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        HairlineDivider()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Consolidated Filter Row (Period, Account, Type Dropdowns)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Period Filter Dropdown
                    Box {
                        DropdownFilterChip(
                            text = selectedPeriod.getDisplayName(strings.isBangla),
                            isActive = selectedPeriod != ReportPeriod.THIS_MONTH,
                            icon = Icons.Outlined.CalendarToday,
                            activeColor = ColorAccent,
                            onClick = {
                                haptics.lightTick()
                                showPeriodMenu = true
                            }
                        )
                        DropdownMenu(
                            expanded = showPeriodMenu,
                            onDismissRequest = { showPeriodMenu = false }
                        ) {
                            ReportPeriod.entries.forEach { period ->
                                val isSelected = selectedPeriod == period
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = period.getDisplayName(strings.isBangla),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) ColorAccent else ColorTextPrimary
                                        )
                                    },
                                    onClick = {
                                        haptics.lightTick()
                                        showPeriodMenu = false
                                        if (period == ReportPeriod.CUSTOM) {
                                            showStartDatePicker = true
                                        } else {
                                            viewModel.setPeriod(period)
                                        }
                                    },
                                    leadingIcon = {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, null, tint = ColorAccent)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Account Filter Dropdown
                    Box {
                        val activeAccName = accounts.find { it.id == selectedAccountId }?.name ?: strings.allAccounts
                        DropdownFilterChip(
                            text = activeAccName,
                            isActive = selectedAccountId != null,
                            icon = Icons.Outlined.AccountBalance,
                            activeColor = Color(0xFF3B8FF0),
                            onClick = {
                                haptics.lightTick()
                                showAccountMenu = true
                            }
                        )
                        DropdownMenu(
                            expanded = showAccountMenu,
                            onDismissRequest = { showAccountMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(strings.allAccounts) },
                                onClick = {
                                    haptics.lightTick()
                                    viewModel.setAccountFilter(null)
                                    showAccountMenu = false
                                },
                                leadingIcon = {
                                    if (selectedAccountId == null) Icon(Icons.Default.Check, null, tint = ColorAccent)
                                }
                            )
                            accounts.forEach { acc ->
                                DropdownMenuItem(
                                    text = { Text(acc.name) },
                                    onClick = {
                                        haptics.lightTick()
                                        viewModel.setAccountFilter(acc.id)
                                        showAccountMenu = false
                                    },
                                    leadingIcon = {
                                        if (selectedAccountId == acc.id) Icon(Icons.Default.Check, null, tint = ColorAccent)
                                    }
                                )
                            }
                        }
                    }

                    // Type Filter Dropdown
                    Box {
                        val activeTypeName = when (selectedType) {
                            TransactionType.EXPENSE -> strings.onlyExpenses
                            TransactionType.INCOME -> strings.onlyIncomes
                            null -> strings.allTypes
                        }
                        val typeColor = when (selectedType) {
                            TransactionType.EXPENSE -> ColorDangerExpense
                            TransactionType.INCOME -> ColorSuccessIncome
                            else -> ColorAccent
                        }
                        DropdownFilterChip(
                            text = activeTypeName,
                            isActive = selectedType != null,
                            icon = Icons.Outlined.FilterList,
                            activeColor = typeColor,
                            onClick = {
                                haptics.lightTick()
                                showTypeMenu = true
                            }
                        )
                        DropdownMenu(
                            expanded = showTypeMenu,
                            onDismissRequest = { showTypeMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(strings.allTypes) },
                                onClick = {
                                    haptics.lightTick()
                                    viewModel.setTypeFilter(null)
                                    showTypeMenu = false
                                },
                                leadingIcon = {
                                    if (selectedType == null) Icon(Icons.Default.Check, null, tint = ColorAccent)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(strings.onlyExpenses) },
                                onClick = {
                                    haptics.lightTick()
                                    viewModel.setTypeFilter(TransactionType.EXPENSE)
                                    showTypeMenu = false
                                },
                                leadingIcon = {
                                    if (selectedType == TransactionType.EXPENSE) Icon(Icons.Default.Check, null, tint = ColorDangerExpense)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(strings.onlyIncomes) },
                                onClick = {
                                    haptics.lightTick()
                                    viewModel.setTypeFilter(TransactionType.INCOME)
                                    showTypeMenu = false
                                },
                                leadingIcon = {
                                    if (selectedType == TransactionType.INCOME) Icon(Icons.Default.Check, null, tint = ColorSuccessIncome)
                                }
                            )
                        }
                    }
                }
            }

            // 3. Financial KPI Summary Cards (2x2 Grid)
            item {
                ReportSummaryKpiSection(
                    summary = summary,
                    currencySymbol = userProfile.currencySymbol,
                    isPrivacyMode = isPrivacyMode,
                    strings = strings
                )
            }

            // 4. Primary Export Action Buttons
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = ColorSurface,
                    border = BorderStroke(1.dp, ColorBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (strings.isBangla) "রিপোর্ট ডাউনলোড ও শেয়ার" else "Export & Share Statement",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ColorTextPrimary
                        )

                        // 1. PDF Share Button (Primary)
                        Button(
                            onClick = {
                                haptics.mediumClick()
                                viewModel.exportAndSharePdf(context, strings.isBangla)
                            },
                            enabled = !isExporting && filteredTransactions.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAccent,
                                contentColor = Color.White
                            )
                        ) {
                            if (isExporting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.generatingReport, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.PictureAsPdf,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.exportPdf, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Direct PDF View
                            OutlinedButton(
                                onClick = {
                                    haptics.lightTick()
                                    viewModel.openPdfDirectly(context, strings.isBangla)
                                },
                                enabled = !isExporting && filteredTransactions.isNotEmpty(),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, ColorBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTextPrimary)
                            ) {
                                Icon(Icons.Outlined.Visibility, null, modifier = Modifier.size(16.dp), tint = ColorAccent)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.viewPdf, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            // CSV Export
                            OutlinedButton(
                                onClick = {
                                    haptics.lightTick()
                                    viewModel.exportAndShareCsv(context, strings.isBangla)
                                },
                                enabled = !isExporting && filteredTransactions.isNotEmpty(),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, ColorBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTextPrimary)
                            ) {
                                Icon(Icons.Outlined.TableChart, null, modifier = Modifier.size(16.dp), tint = Color(0xFF10B981))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.exportCsv, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // 5. Category Breakdown Section (if any expense)
            if (categorySpends.isNotEmpty()) {
                item {
                    ReportCategoryBreakdownCard(
                        categorySpends = categorySpends,
                        currencySymbol = userProfile.currencySymbol,
                        isPrivacyMode = isPrivacyMode,
                        strings = strings
                    )
                }
            }

            // 6. Section Title for Itemized Transactions
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.transactionsInReport,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorTextPrimary
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorSurface,
                        border = BorderStroke(1.dp, ColorBorder)
                    ) {
                        Text(
                            text = "${filteredTransactions.size} ${if (strings.isBangla) "টি লেনদেন" else "items"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorTextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // 7. Transactions List / Empty State
            if (filteredTransactions.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = ColorSurface,
                        border = BorderStroke(1.dp, ColorBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(ColorAccent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ReceiptLong,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = strings.noTransactionsForPeriod,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredTransactions, key = { it.id }) { tx ->
                    ReportTransactionRowItem(
                        transaction = tx,
                        currencySymbol = userProfile.currencySymbol,
                        isPrivacyMode = isPrivacyMode
                    )
                }
            }
        }
    }

    // Date Range Picker Dialogs for Custom Period
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = tempStartDate)
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selected ->
                        tempStartDate = selected
                        showStartDatePicker = false
                        showEndDatePicker = true
                    }
                }) {
                    Text(strings.confirm, color = ColorAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text(strings.cancel, color = ColorTextMuted)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        strings.startDate,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selected ->
                        showEndDatePicker = false
                        viewModel.setCustomRange(tempStartDate, selected)
                    }
                }) {
                    Text(strings.done, color = ColorAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text(strings.cancel, color = ColorTextMuted)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        strings.endDate,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }
}

@Composable
private fun ReportSummaryKpiSection(
    summary: FinancialSummary,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    strings: com.example.expensetracker.ui.components.Strings
) {
    val savingsRate = if (summary.totalIncome > 0) {
        ((summary.netBalance / summary.totalIncome) * 100).toInt().coerceIn(-999, 100)
    } else 0

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Total Income
            ReportKpiCard(
                title = strings.totalIncomeLabel,
                amount = summary.totalIncome,
                currencySymbol = currencySymbol,
                isPrivacyMode = isPrivacyMode,
                accentColor = ColorSuccessIncome,
                icon = Icons.Outlined.ArrowUpward,
                modifier = Modifier.weight(1f)
            )

            // Card 2: Total Expense
            ReportKpiCard(
                title = strings.totalExpenseLabel,
                amount = summary.totalExpense,
                currencySymbol = currencySymbol,
                isPrivacyMode = isPrivacyMode,
                accentColor = ColorDangerExpense,
                icon = Icons.Outlined.ArrowDownward,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 3: Net Cash Flow
            ReportKpiCard(
                title = strings.netCashFlowLabel,
                amount = summary.netBalance,
                currencySymbol = currencySymbol,
                isPrivacyMode = isPrivacyMode,
                accentColor = if (summary.netBalance >= 0) Color(0xFF3B82F6) else ColorDangerExpense,
                icon = if (summary.netBalance >= 0) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                modifier = Modifier.weight(1f)
            )

            // Card 4: Savings Rate %
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = ColorSurface,
                border = BorderStroke(1.dp, ColorBorder)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = strings.savingsRateLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorTextMuted
                    )
                    Text(
                        text = if (isPrivacyMode) "••%" else "$savingsRate%",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (savingsRate >= 0) ColorSuccessIncome else ColorDangerExpense
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportKpiCard(
    title: String,
    amount: Double,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = ColorSurface,
        border = BorderStroke(1.dp, ColorBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = if (isPrivacyMode) "••••••" else formatCurrency(amount, currencySymbol),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ReportCategoryBreakdownCard(
    categorySpends: List<CategorySpend>,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    strings: com.example.expensetracker.ui.components.Strings
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = ColorSurface,
        border = BorderStroke(1.dp, ColorBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = strings.categoryBreakdown,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ColorTextPrimary
            )

            categorySpends.take(5).forEach { cat ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            CategoryIconBadge(
                                iconName = cat.categoryIcon,
                                colorHex = cat.categoryColor,
                                size = 26.dp
                            )
                            Text(
                                text = cat.categoryName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.0f%%", cat.percentage * 100),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorTextMuted
                            )
                            Text(
                                text = if (isPrivacyMode) "••••" else formatCurrency(cat.totalAmount, currencySymbol),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextPrimary
                            )
                        }
                    }

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ColorBorder)
                    ) {
                        val barColor = try {
                            Color(android.graphics.Color.parseColor(cat.categoryColor))
                        } catch (_: Exception) {
                            ColorAccent
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(cat.percentage)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(barColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportTransactionRowItem(
    transaction: Transaction,
    currencySymbol: String,
    isPrivacyMode: Boolean
) {
    val strings = LocalAppStrings.current
    val isIncome = transaction.type == TransactionType.INCOME

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ColorSurface,
        border = BorderStroke(1.dp, ColorBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                CategoryIconBadge(
                    iconName = transaction.categoryIcon,
                    colorHex = transaction.categoryColor,
                    size = 36.dp
                )

                Column {
                    Text(
                        text = transaction.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = DateTimeHelper.formatItemDateTime(transaction.dateMillis, strings.isBangla),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = ColorTextMuted
                        )
                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = ColorTextMuted
                        )
                        Text(
                            text = getLocalizedAccountName(transaction.accountName, strings.isBangla) ?: transaction.paymentMethod.getDisplayName(strings.isBangla),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isPrivacyMode) {
                        if (isIncome) "+ ••••" else "- ••••"
                    } else {
                        val sign = if (isIncome) "+" else "-"
                        "$sign ${formatCurrency(transaction.amount, currencySymbol)}"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) ColorSuccessIncome else ColorDangerExpense
                )

                if (transaction.receiptImagePath != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ReceiptLong,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Receipt",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorAccent
                        )
                    }
                }
            }
        }
    }
}
