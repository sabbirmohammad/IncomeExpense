package com.example.expensetracker.ui.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.PulseText
import com.example.expensetracker.ui.components.PulseTopBar
import kotlinx.coroutines.launch

@Composable
fun BazaarHistoryScreen(
    bazaarViewModel: BazaarViewModel,
    onNavigateBack: () -> Unit,
    onOpenTripDetail: (Long) -> Unit,
    onOpenPlan: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val historyMonthGroups by bazaarViewModel.historyMonthGroups.collectAsStateWithLifecycle()
    val searchQuery by bazaarViewModel.pastTripsSearchQuery.collectAsStateWithLifecycle()
    val userProfile by bazaarViewModel.userProfile.collectAsStateWithLifecycle()
    val pendingDeletedListIds by bazaarViewModel.pendingDeletedListIds.collectAsStateWithLifecycle()
    val isBangla = userProfile.appLanguage != "en"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = appColors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PulseTopBar(
                title = if (isBangla) "বিগত বাজারসমূহ" else "Past Trips",
                subtitle = if (isBangla) "আগের বাজারের তালিকা ও বিবরণ" else "History of completed shopping trips",
                onBackClick = onNavigateBack,
                backContentDescription = if (isBangla) "ফিরে যান" else "Back"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { bazaarViewModel.setPastTripsSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        PulseText(
                            text = if (isBangla) "ফর্দ বা পণ্যের নাম দিয়ে খুঁজুন..." else "Search by trip title or item...",
                            color = appColors.textMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = if (isBangla) "খুঁজুন" else "Search",
                            tint = appColors.textMuted
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { bazaarViewModel.setPastTripsSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = if (isBangla) "মুছুন" else "Clear",
                                    tint = appColors.textMuted
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = appColors.surfaceVariant,
                        focusedContainerColor = appColors.surface,
                        unfocusedContainerColor = appColors.surface,
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        cursorColor = appColors.accent
                    )
                )
            }

            // Grouped Trips Content
            if (historyMonthGroups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = appColors.surfaceVariant,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.History,
                                    contentDescription = null,
                                    tint = appColors.textMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        PulseText(
                            text = if (searchQuery.isNotBlank()) {
                                if (isBangla) "কোনো বাজার খুঁজে পাওয়া যায়নি" else "No matching trips found"
                            } else {
                                if (isBangla) "এখনো কোনো সম্পন্ন বাজার নেই" else "No completed trips yet"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        PulseText(
                            text = if (searchQuery.isNotBlank()) {
                                if (isBangla) "\"$searchQuery\" এর সাথে মেলে এমন কোনো বাজার নেই" else "No trips match \"$searchQuery\""
                            } else {
                                if (isBangla) "বাজার সম্পন্ন করলে তা এই তালিকায় সংরক্ষিত থাকবে" else "Shopping trips you complete will appear here"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = appColors.textMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    historyMonthGroups.forEach { group ->
                        val validTrips = group.trips.filter { it.listId !in pendingDeletedListIds }
                        if (validTrips.isNotEmpty()) {
                            item(key = "header_${group.year}_${group.month}") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PulseText(
                                        text = if (isBangla) group.monthLabelBn else group.monthLabelEn,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = appColors.textPrimary
                                    )
                                    val countNum = if (userProfile.useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(validTrips.size.toString()) else validTrips.size.toString()
                                    val countStr = if (isBangla) "${countNum}টি বাজার" else "${validTrips.size} trips"
                                    val spendTotal = validTrips.sumOf { it.totalSpend }
                                    val spendNum = if (userProfile.useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(spendTotal.toInt().toString()) else spendTotal.toInt().toString()
                                    val spendStr = "৳$spendNum"
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = appColors.surfaceVariant
                                    ) {
                                        PulseText(
                                            text = "$countStr • $spendStr",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Medium,
                                            color = appColors.textSecondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            items(validTrips, key = { it.listId }) { trip ->
                                BazaarHistoryTripCard(
                                    trip = trip,
                                    isBangla = isBangla,
                                    useBengaliNumerals = userProfile.useBengaliNumerals,
                                    onClick = { onOpenTripDetail(trip.listId) },
                                    onBuyAgain = {
                                        bazaarViewModel.cloneTrip(trip.listId) { newListId ->
                                            onOpenPlan(newListId)
                                        }
                                    },
                                    onDelete = {
                                        bazaarViewModel.deleteMarketListWithUndo(
                                            listId = trip.listId,
                                            listTitle = trip.title,
                                            showSnackbar = { msg, act ->
                                                snackbarHostState.showSnackbar(msg, act) == SnackbarResult.ActionPerformed
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BazaarHistoryTripCard(
    trip: BazaarTripSummary,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    onClick: () -> Unit,
    onBuyAgain: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.surface,
        tonalElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Title & Spend Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    PulseText(
                        text = trip.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    PulseText(
                        text = DateTimeHelper.formatDateHeader(trip.createdAt, isBangla, useBengaliNumerals),
                        style = MaterialTheme.typography.bodySmall,
                        color = appColors.textMuted
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    val totalNum = if (trip.totalSpend % 1.0 == 0.0) "${trip.totalSpend.toInt()}" else "%.1f".format(trip.totalSpend)
                    val amountStr = if (useBengaliNumerals || isBangla) {
                        "৳${DigitConverter.toBengaliDigits(totalNum)}"
                    } else {
                        "৳$totalNum"
                    }
                    PulseText(
                        text = amountStr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val countNum = if (useBengaliNumerals || isBangla) {
                        DigitConverter.toBengaliDigits(trip.purchasedItemCount.toString())
                    } else {
                        trip.purchasedItemCount.toString()
                    }
                    val countStr = if (isBangla) {
                        "${countNum}টি পণ্য"
                    } else {
                        "$countNum items"
                    }
                    PulseText(
                        text = countStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = appColors.textSecondary
                    )
                }
            }

            // Preview items line
            if (trip.purchasedItemsPreview.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                val previewStr = trip.purchasedItemsPreview.joinToString(", ") +
                    if (trip.purchasedItemCount > trip.purchasedItemsPreview.size) "..." else ""
                PulseText(
                    text = previewStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Buy Again Button
                Button(
                    onClick = onBuyAgain,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.surfaceVariant,
                        contentColor = appColors.textPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    PulseText(
                        text = if (isBangla) "আবার কিনুন" else "Buy again",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = appColors.textPrimary
                    )
                }

                // Delete Button (list + items + cap only, never balance/transaction)
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = if (isBangla) "বাজার মুছুন" else "Delete trip",
                        tint = appColors.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
