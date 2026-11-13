package com.example.expensetracker.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.EmptyState
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val appColors = LocalAppColors.current

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val selectedAccountId by viewModel.selectedAccountId.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val categorySpends by viewModel.categorySpends.collectAsStateWithLifecycle()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()
    val isExporting by viewModel.isExporting.collectAsStateWithLifecycle()

    val periodTitle = viewModel.getPeriodTitle(strings.isBangla)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        ReportsTopBar(
            periodTitle = periodTitle,
            isPrivacyMode = isPrivacyMode,
            strings = strings,
            onTogglePrivacy = { viewModel.togglePrivacyMode() },
            onNavigateBack = onNavigateBack,
            onMenuClick = onMenuClick
        )

        HairlineDivider()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ReportFilterBar(
                    selectedPeriod = selectedPeriod,
                    selectedAccountId = selectedAccountId,
                    selectedType = selectedType,
                    accounts = accounts,
                    strings = strings,
                    onSelectPeriod = { viewModel.setPeriod(it) },
                    onSelectAccount = { viewModel.setAccountFilter(it) },
                    onSelectType = { viewModel.setTypeFilter(it) },
                    onCustomRangeSelected = { start, end -> viewModel.setCustomRange(start, end) }
                )
            }

            item {
                val activeAccName = accounts.find { it.id == selectedAccountId }?.name ?: strings.allAccounts
                StatementPreviewCard(
                    periodTitle = periodTitle,
                    accountName = activeAccName,
                    summary = summary,
                    transactionCount = filteredTransactions.size,
                    currencySymbol = userProfile.currencySymbol,
                    isPrivacyMode = isPrivacyMode,
                    strings = strings
                )
            }

            item {
                ReportExportActionsCard(
                    isExporting = isExporting,
                    hasTransactions = filteredTransactions.isNotEmpty(),
                    strings = strings,
                    onExportPdf = { viewModel.exportAndSharePdf(context, strings.isBangla) },
                    onViewPdf = { viewModel.openPdfDirectly(context, strings.isBangla) },
                    onExportCsv = { viewModel.exportAndShareCsv(context, strings.isBangla) }
                )
            }

            item {
                ReportSummaryKpiSection(
                    summary = summary,
                    currencySymbol = userProfile.currencySymbol,
                    isPrivacyMode = isPrivacyMode,
                    strings = strings
                )
            }

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
                        color = appColors.textPrimary
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.surfaceVariant,
                        border = BorderStroke(1.dp, appColors.border)
                    ) {
                        Text(
                            text = "${filteredTransactions.size} ${if (strings.isBangla) "টি লেনদেন" else "items"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = appColors.textMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (filteredTransactions.isEmpty()) {
                item {
                    EmptyState(
                        title = strings.noTransactionsForPeriod,
                        body = if (strings.isBangla) "নির্বাচিত সময়ের জন্য কোনো লেনদেনের রেকর্ড পাওয়া যায়নি" else "No transaction records found for the selected timeframe",
                        icon = Icons.Outlined.ReceiptLong
                    )
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
}
