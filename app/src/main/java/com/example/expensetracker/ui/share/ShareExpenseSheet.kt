package com.example.expensetracker.ui.share

import android.app.Application
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.toFinancialSummary
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PillFilterChip
import com.example.expensetracker.ui.components.StatementExportHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextOverflow

enum class ShareFormatType {
    PDF,
    IMAGE,
    TEXT
}

enum class SharePeriodType(val label: String) {
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    CUSTOM("Custom Range")
}

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
    val repository = remember {
        ExpenseRepository.getInstance(context.applicationContext as Application)
    }

    val userProfile by repository.userProfile.collectAsState()

    var selectedPeriod by remember { mutableStateOf(initialPeriod) }
    var selectedFormat by remember { mutableStateOf(ShareFormatType.PDF) }

    // Date range states
    val calNow = Calendar.getInstance()
    var customStartDateMillis by remember {
        val cal = Calendar.getInstance()
        if (initialTargetDateMillis != null) {
            cal.timeInMillis = initialTargetDateMillis
        } else {
            cal.add(Calendar.DAY_OF_YEAR, -7)
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        mutableLongStateOf(cal.timeInMillis)
    }

    var customEndDateMillis by remember {
        val cal = Calendar.getInstance()
        if (initialTargetDateMillis != null) {
            cal.timeInMillis = initialTargetDateMillis
        }
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        mutableLongStateOf(cal.timeInMillis)
    }

    val strings = LocalAppStrings.current
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSharing by remember { mutableStateOf(false) }
    var isSharingPdf by remember { mutableStateOf(false) }

    val dayOnlyFormat = remember(strings.isBangla) { SimpleDateFormat("EEEE, d MMMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }
    val rangeDateFormat = remember(strings.isBangla) { SimpleDateFormat("d MMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }

    // Helper to compute range based on period
    fun getActiveDateRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        return when (selectedPeriod) {
            SharePeriodType.TODAY -> {
                val target = initialTargetDateMillis ?: System.currentTimeMillis()
                cal.timeInMillis = target
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            SharePeriodType.YESTERDAY -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            SharePeriodType.THIS_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.add(Calendar.DAY_OF_YEAR, 6)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            SharePeriodType.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            SharePeriodType.CUSTOM -> {
                Pair(customStartDateMillis, customEndDateMillis)
            }
        }
    }

    // Load data when period or custom dates change
    LaunchedEffect(selectedPeriod, customStartDateMillis, customEndDateMillis) {
        isLoading = true
        val (start, end) = getActiveDateRange()
        val list = repository.getTransactionsBetween(start, end)
        transactions = list
        isLoading = false
    }

    val (activeStart, activeEnd) = getActiveDateRange()
    val periodTitle = when (selectedPeriod) {
        SharePeriodType.TODAY -> "${strings.today} (${strings.exportStatement})"
        SharePeriodType.YESTERDAY -> "${strings.yesterday} (${strings.exportStatement})"
        SharePeriodType.THIS_WEEK -> "${strings.thisWeek} (${strings.exportStatement})"
        SharePeriodType.THIS_MONTH -> "${strings.thisMonth} (${strings.exportStatement})"
        SharePeriodType.CUSTOM -> "${strings.customRange} (${strings.exportStatement})"
    }
    val periodSubtitle = when (selectedPeriod) {
        SharePeriodType.TODAY, SharePeriodType.YESTERDAY -> {
            val d = dayOnlyFormat.format(Date(activeStart))
            if (strings.isBangla) convertToBengaliDigits(d) else d
        }
        SharePeriodType.THIS_WEEK, SharePeriodType.THIS_MONTH, SharePeriodType.CUSTOM -> {
            val s = rangeDateFormat.format(Date(activeStart))
            val e = rangeDateFormat.format(Date(activeEnd))
            val sDisplay = if (strings.isBangla) convertToBengaliDigits(s) else s
            val eDisplay = if (strings.isBangla) convertToBengaliDigits(e) else e
            "$sDisplay - $eDisplay"
        }
    }

    val totalExpense = remember(transactions) {
        transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val totalIncome = remember(transactions) {
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }

    val graphicsContext = LocalGraphicsContext.current
    val graphicsLayer = remember(graphicsContext) {
        graphicsContext.createGraphicsLayer()
    }
    DisposableEffect(graphicsContext, graphicsLayer) {
        onDispose {
            graphicsContext.releaseGraphicsLayer(graphicsLayer)
        }
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
            // Header: Title & Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.shareExpenseTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1F2436))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = strings.close,
                        tint = ColorTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Prominent Format Switcher (PDF vs Image vs Text)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F131D))
                    .border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple(ShareFormatType.PDF, strings.formatPdf, Icons.Outlined.PictureAsPdf),
                    Triple(ShareFormatType.IMAGE, strings.formatImage, Icons.Outlined.Image),
                    Triple(ShareFormatType.TEXT, strings.formatText, Icons.Outlined.ContentCopy)
                ).forEach { (format, label, icon) ->
                    val isSelected = selectedFormat == format
                    val activeBg = when (format) {
                        ShareFormatType.PDF -> Color(0xFFE53935)
                        ShareFormatType.IMAGE -> ColorAccent
                        ShareFormatType.TEXT -> Color(0xFF2EC4B6)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) activeBg else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFormat = format }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 9.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else ColorTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else ColorTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Period Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SharePeriodType.entries.forEach { period ->
                    val chipLabel = when (period) {
                        SharePeriodType.TODAY -> strings.today
                        SharePeriodType.YESTERDAY -> strings.yesterday
                        SharePeriodType.THIS_WEEK -> strings.thisWeek
                        SharePeriodType.THIS_MONTH -> strings.thisMonth
                        SharePeriodType.CUSTOM -> strings.customRange
                    }
                    PillFilterChip(
                        text = chipLabel,
                        selected = selectedPeriod == period,
                        onClick = { selectedPeriod = period }
                    )
                }
            }

            // Custom Range Date Pickers (Shown if CUSTOM selected)
            if (selectedPeriod == SharePeriodType.CUSTOM) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Start Date Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1A2033))
                            .border(1.dp, ColorBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                val c = Calendar.getInstance().apply { timeInMillis = customStartDateMillis }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val newCal = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth, 0, 0, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }
                                        customStartDateMillis = newCal.timeInMillis
                                        if (customStartDateMillis > customEndDateMillis) {
                                            customEndDateMillis = newCal.timeInMillis + (24 * 60 * 60 * 1000 - 1)
                                        }
                                    },
                                    c.get(Calendar.YEAR),
                                    c.get(Calendar.MONTH),
                                    c.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = "${strings.date} (From)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ColorTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val s = rangeDateFormat.format(Date(customStartDateMillis))
                                Text(
                                    text = if (strings.isBangla) convertToBengaliDigits(s) else s,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = ColorTextPrimary
                                )
                            }
                        }
                    }

                    // End Date Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1A2033))
                            .border(1.dp, ColorBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                val c = Calendar.getInstance().apply { timeInMillis = customEndDateMillis }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val newCal = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth, 23, 59, 59)
                                            set(Calendar.MILLISECOND, 999)
                                        }
                                        customEndDateMillis = newCal.timeInMillis
                                        if (customEndDateMillis < customStartDateMillis) {
                                            customStartDateMillis = newCal.timeInMillis - (24 * 60 * 60 * 1000 - 1)
                                        }
                                    },
                                    c.get(Calendar.YEAR),
                                    c.get(Calendar.MONTH),
                                    c.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = "${strings.date} (To)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ColorTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val e = rangeDateFormat.format(Date(customEndDateMillis))
                                Text(
                                    text = if (strings.isBangla) convertToBengaliDigits(e) else e,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = ColorTextPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Content Area based on selectedFormat
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = ColorAccent,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else {
                when (selectedFormat) {
                    ShareFormatType.PDF -> {
                        // PDF Preview Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF141824),
                            border = BorderStroke(1.dp, ColorBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE53935).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.PictureAsPdf,
                                            contentDescription = null,
                                            tint = Color(0xFFE53935),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = strings.formatPdf,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            ),
                                            color = ColorTextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "$periodTitle • $periodSubtitle",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = ColorTextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(strings.income, fontSize = 11.sp, color = ColorTextSecondary)
                                        Text(
                                            formatCurrency(totalIncome, userProfile.currencySymbol),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ColorSuccessIncome
                                        )
                                    }
                                    Column {
                                        Text(strings.expense, fontSize = 11.sp, color = ColorTextSecondary)
                                        Text(
                                            formatCurrency(totalExpense, userProfile.currencySymbol),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ColorDangerExpense
                                        )
                                    }
                                    Column {
                                        Text(strings.transactionsTitle, fontSize = 11.sp, color = ColorTextSecondary)
                                        Text(
                                            "${transactions.size} ${strings.recordsFound}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ColorTextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Share as PDF Primary Button
                        Button(
                            onClick = {
                                if (!isSharing && !isSharingPdf && !isLoading) {
                                    coroutineScope.launch {
                                        isSharingPdf = true
                                        try {
                                            withContext(Dispatchers.IO) {
                                                val summary = transactions.toFinancialSummary()
                                                StatementExportHelper.exportAndSharePdf(
                                                    context = context,
                                                    profile = userProfile,
                                                    summary = summary,
                                                    transactions = transactions,
                                                    periodTitle = "$periodTitle ($periodSubtitle)"
                                                )
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        } finally {
                                            isSharingPdf = false
                                        }
                                    }
                                }
                            },
                            enabled = !isSharing && !isSharingPdf && !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White
                            )
                        ) {
                            if (isSharingPdf) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${strings.sharePdfStatement}...",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    fontSize = 14.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.PictureAsPdf,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.sharePdfStatement,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    ShareFormatType.IMAGE -> {
                        // Image Slip Card Preview Area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .drawWithContent {
                                        graphicsLayer.record {
                                            this@drawWithContent.drawContent()
                                        }
                                        drawLayer(graphicsLayer)
                                    }
                            ) {
                                ExpenseShareSlipCard(
                                    periodTitle = periodTitle,
                                    periodSubtitle = periodSubtitle,
                                    userName = userProfile.name,
                                    currencySymbol = userProfile.currencySymbol,
                                    totalExpense = totalExpense,
                                    totalIncome = totalIncome,
                                    transactions = transactions
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Share as Image Primary Button
                        Button(
                            onClick = {
                                if (!isSharing && !isSharingPdf && !isLoading) {
                                    coroutineScope.launch {
                                        isSharing = true
                                        try {
                                            val imageBitmap = graphicsLayer.toImageBitmap()
                                            val bitmap = imageBitmap.asAndroidBitmap()
                                            val uri = ShareExpenseHelper.saveBitmapToCache(context, bitmap)
                                            if (uri != null) {
                                                ShareExpenseHelper.shareImage(
                                                    context = context,
                                                    uri = uri,
                                                    caption = "Expense Statement: $periodSubtitle"
                                                )
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        } finally {
                                            isSharing = false
                                        }
                                    }
                                }
                            },
                            enabled = !isSharing && !isSharingPdf && !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAccent,
                                contentColor = Color.White
                            )
                        ) {
                            if (isSharing) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${strings.shareImageSlip}...",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    fontSize = 14.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.shareImageSlip,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    ShareFormatType.TEXT -> {
                        val textSummary = remember(periodTitle, periodSubtitle, totalExpense, totalIncome, transactions, userProfile) {
                            buildString {
                                appendLine("📊 *${periodTitle}*")
                                appendLine("📅 ${periodSubtitle}")
                                appendLine("💰 ${strings.expense}: ${formatCurrency(totalExpense, userProfile.currencySymbol)}")
                                if (totalIncome > 0) {
                                    appendLine("📈 ${strings.income}: ${formatCurrency(totalIncome, userProfile.currencySymbol)}")
                                }
                                appendLine("-------------------------")
                                transactions.forEach { tx ->
                                    val sign = if (tx.type == TransactionType.INCOME) "+" else "-"
                                    appendLine("• ${tx.title}: $sign${formatCurrency(tx.amount, userProfile.currencySymbol)} (${tx.categoryName})")
                                }
                                appendLine("-------------------------")
                                appendLine("Shared via ${strings.appName}")
                            }
                        }

                        // Text Preview Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF141824),
                            border = BorderStroke(1.dp, ColorBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = textSummary,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = ColorTextPrimary,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Copy & Share Text Primary Button
                        Button(
                            onClick = {
                                ShareExpenseHelper.shareText(context, textSummary, "Share Expense Text Summary")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2EC4B6),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.copyTextSummary,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
