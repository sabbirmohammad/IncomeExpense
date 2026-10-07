package com.example.expensetracker.ui.share

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import java.text.SimpleDateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareExpenseSheet(
    initialPeriod: SharePeriodType = SharePeriodType.TODAY,
    initialTargetDateMillis: Long? = null,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { ExpenseRepository.getInstance(context.applicationContext as Application) }
    val userProfile by repository.userProfile.collectAsState()

    var selectedPeriod by remember { mutableStateOf(initialPeriod) }
    var selectedFormat by remember { mutableStateOf(ShareFormatType.PDF) }

    var customStartDateMillis by remember {
        val cal = Calendar.getInstance().apply {
            if (initialTargetDateMillis != null) timeInMillis = initialTargetDateMillis
            else add(Calendar.DAY_OF_YEAR, -7)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        mutableLongStateOf(cal.timeInMillis)
    }

    var customEndDateMillis by remember {
        val cal = Calendar.getInstance().apply {
            if (initialTargetDateMillis != null) timeInMillis = initialTargetDateMillis
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }
        mutableLongStateOf(cal.timeInMillis)
    }

    val strings = LocalAppStrings.current
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSharing by remember { mutableStateOf(false) }
    var isSharingPdf by remember { mutableStateOf(false) }

    val dayOnlyFormat = remember(strings.isBangla) { SimpleDateFormat("EEEE, d MMMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }
    val rangeDateFormat = remember(strings.isBangla) { SimpleDateFormat("d MMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }

    fun getActiveDateRange(): Pair<Long, Long> {
        return ShareDateRangeHelper.computeActiveDateRange(
            period = selectedPeriod,
            initialTargetDateMillis = initialTargetDateMillis,
            customStartDateMillis = customStartDateMillis,
            customEndDateMillis = customEndDateMillis
        )
    }

    LaunchedEffect(selectedPeriod, customStartDateMillis, customEndDateMillis) {
        isLoading = true
        val (start, end) = getActiveDateRange()
        transactions = repository.getTransactionsBetween(start, end)
        isLoading = false
    }

    val (activeStart, activeEnd) = getActiveDateRange()
    val periodTitle = ShareDateRangeHelper.getPeriodTitle(selectedPeriod, strings)
    val periodSubtitle = ShareDateRangeHelper.getPeriodSubtitle(
        period = selectedPeriod,
        activeStart = activeStart,
        activeEnd = activeEnd,
        dayOnlyFormat = dayOnlyFormat,
        rangeDateFormat = rangeDateFormat,
        strings = strings
    )

    val totalExpense = remember(transactions) { transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount } }
    val totalIncome = remember(transactions) { transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount } }

    val graphicsContext = LocalGraphicsContext.current
    val graphicsLayer = remember(graphicsContext) { graphicsContext.createGraphicsLayer() }
    DisposableEffect(graphicsContext, graphicsLayer) {
        onDispose { graphicsContext.releaseGraphicsLayer(graphicsLayer) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ColorSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.shareExpenseTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = ColorTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF1F2436))
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = strings.close, tint = ColorTextSecondary, modifier = Modifier.size(18.dp))
                }
            }

            ShareFormatSwitcher(selectedFormat = selectedFormat, strings = strings, onFormatChange = { selectedFormat = it })
            SharePeriodSelector(selectedPeriod = selectedPeriod, strings = strings, onPeriodChange = { selectedPeriod = it })

            if (selectedPeriod == SharePeriodType.CUSTOM) {
                ShareCustomDatePickerRow(
                    context = context,
                    customStartDateMillis = customStartDateMillis,
                    customEndDateMillis = customEndDateMillis,
                    rangeDateFormat = rangeDateFormat,
                    strings = strings,
                    onStartDateChange = { customStartDateMillis = it },
                    onEndDateChange = { customEndDateMillis = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorAccent, modifier = Modifier.size(36.dp))
                }
            } else {
                when (selectedFormat) {
                    ShareFormatType.PDF -> SharePdfSection(
                        periodTitle = periodTitle,
                        periodSubtitle = periodSubtitle,
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        transactions = transactions,
                        userProfile = userProfile,
                        strings = strings,
                        context = context,
                        coroutineScope = coroutineScope,
                        isSharing = isSharing,
                        isSharingPdf = isSharingPdf,
                        isLoading = isLoading,
                        onSharingPdfChange = { isSharingPdf = it }
                    )
                    ShareFormatType.IMAGE -> ShareImageSection(
                        periodTitle = periodTitle,
                        periodSubtitle = periodSubtitle,
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        transactions = transactions,
                        userProfile = userProfile,
                        strings = strings,
                        context = context,
                        graphicsLayer = graphicsLayer,
                        coroutineScope = coroutineScope,
                        isSharing = isSharing,
                        isSharingPdf = isSharingPdf,
                        isLoading = isLoading,
                        onSharingChange = { isSharing = it }
                    )
                    ShareFormatType.TEXT -> ShareTextSection(
                        periodTitle = periodTitle,
                        periodSubtitle = periodSubtitle,
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        transactions = transactions,
                        userProfile = userProfile,
                        strings = strings,
                        context = context
                    )
                }
            }
        }
    }
}
