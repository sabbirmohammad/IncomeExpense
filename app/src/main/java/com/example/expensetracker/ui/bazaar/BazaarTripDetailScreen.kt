package com.example.expensetracker.ui.bazaar

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.PulseText
import com.example.expensetracker.ui.components.PulseTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazaarTripDetailScreen(
    listId: Long,
    bazaarViewModel: BazaarViewModel,
    onNavigateBack: () -> Unit,
    onOpenPlan: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val allLists by bazaarViewModel.allMarketLists.collectAsStateWithLifecycle()
    val userProfile by bazaarViewModel.userProfile.collectAsStateWithLifecycle()
    val isBangla = userProfile.appLanguage != "en"

    val trip = remember(allLists, listId) {
        allLists.find { it.list.id == listId }
    }

    if (trip == null) {
        // Fallback if trip not found (e.g. after deletion)
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = appColors.background,
            topBar = {
                PulseTopBar(
                    title = if (isBangla) "বাজারের বিবরণ" else "Trip Detail",
                    onBackClick = onNavigateBack,
                    backContentDescription = if (isBangla) "ফিরে যান" else "Back"
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                PulseText(
                    text = if (isBangla) "বাজারের তথ্য পাওয়া যায়নি" else "Trip details not found",
                    color = appColors.textMuted
                )
            }
        }
        return
    }

    val stats = remember(trip) {
        BazaarDomainLogic.calculateTripDetailStats(trip)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = appColors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PulseTopBar(
                title = trip.list.title,
                subtitle = DateTimeHelper.formatDateHeader(trip.list.createdAt, isBangla, userProfile.useBengaliNumerals),
                onBackClick = onNavigateBack,
                backContentDescription = if (isBangla) "ফিরে যান" else "Back",
                action1Icon = Icons.Default.DeleteOutline,
                action1ContentDescription = if (isBangla) "বাজার মুছুন" else "Delete trip",
                onAction1Click = {
                    bazaarViewModel.deleteMarketListWithUndo(
                        listId = trip.list.id,
                        listTitle = trip.list.title,
                        showSnackbar = { msg, act ->
                            snackbarHostState.showSnackbar(msg, act) == SnackbarResult.ActionPerformed
                        },
                        onDeletedFinal = onNavigateBack
                    )
                    onNavigateBack()
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // KPI Summary Card
            item(key = "kpi_card") {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = appColors.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        PulseText(
                            text = if (isBangla) "মোট খরচ" else "Total Spent",
                            style = MaterialTheme.typography.labelMedium,
                            color = appColors.textMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val totalNum = if (stats.totalSpend % 1.0 == 0.0) "${stats.totalSpend.toInt()}" else "%.1f".format(stats.totalSpend)
                        val totalSpendFormatted = if (userProfile.useBengaliNumerals || isBangla) {
                            "৳${DigitConverter.toBengaliDigits(totalNum)}"
                        } else {
                            "৳$totalNum"
                        }
                        PulseText(
                            text = totalSpendFormatted,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = appColors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Bought Count Badge
                            val boughtNum = if (userProfile.useBengaliNumerals || isBangla) {
                                DigitConverter.toBengaliDigits(stats.purchasedCount.toString())
                            } else {
                                stats.purchasedCount.toString()
                            }
                            val boughtCountStr = if (isBangla) {
                                "${boughtNum}টি কেনা হয়েছে"
                            } else {
                                "$boughtNum items bought"
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = appColors.surfaceVariant
                            ) {
                                PulseText(
                                    text = boughtCountStr,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = appColors.accent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            // Unbought Count Badge (if any)
                            if (stats.unboughtCount > 0) {
                                val unboughtNum = if (userProfile.useBengaliNumerals || isBangla) {
                                    DigitConverter.toBengaliDigits(stats.unboughtCount.toString())
                                } else {
                                    stats.unboughtCount.toString()
                                }
                                val unboughtCountStr = if (isBangla) {
                                    "${unboughtNum}টি কেনা হয়নি"
                                } else {
                                    "$unboughtNum not bought"
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = appColors.surfaceVariant
                                ) {
                                    PulseText(
                                        text = unboughtCountStr,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = appColors.warning,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Primary Actions Bar
            item(key = "actions_bar") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Carry Over Unbought Items Action (if unbought items exist)
                    if (stats.unboughtCount > 0) {
                        Button(
                            onClick = {
                                bazaarViewModel.carryOverUnboughtItems(listId) { newListId ->
                                    onOpenPlan(newListId)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = appColors.accent,
                                contentColor = appColors.onAccent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PostAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val unboughtNum = if (userProfile.useBengaliNumerals || isBangla) {
                                DigitConverter.toBengaliDigits(stats.unboughtCount.toString())
                            } else {
                                stats.unboughtCount.toString()
                            }
                            PulseText(
                                text = if (isBangla) "অসম্পূর্ণ পণ্য দিয়ে নতুন ফর্দ (${unboughtNum}টি)" else "Carry over unbought items ($unboughtNum)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = appColors.onAccent
                            )
                        }
                    }

                    // Buy Again Button (clones purchased items only)
                    Button(
                        onClick = {
                            bazaarViewModel.cloneTrip(listId) { newListId ->
                                onOpenPlan(newListId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (stats.unboughtCount > 0) appColors.surfaceVariant else appColors.accent,
                            contentColor = if (stats.unboughtCount > 0) appColors.textPrimary else appColors.onAccent
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (stats.unboughtCount > 0) appColors.textPrimary else appColors.onAccent
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PulseText(
                            text = if (isBangla) "আবার কিনুন (কেনা পণ্য দিয়ে ফর্দ)" else "Buy again (repeat purchased)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (stats.unboughtCount > 0) appColors.textPrimary else appColors.onAccent
                        )
                    }
                }
            }

            // Section 1: Purchased Items
            if (stats.purchasedItems.isNotEmpty()) {
                item(key = "purchased_header") {
                    val boughtNum = if (userProfile.useBengaliNumerals || isBangla) {
                        DigitConverter.toBengaliDigits(stats.purchasedCount.toString())
                    } else {
                        stats.purchasedCount.toString()
                    }
                    val countStr = if (isBangla) "${boughtNum}টি" else "$boughtNum"
                    PulseText(
                        text = if (isBangla) "কেনা হয়েছে ($countStr)" else "Purchased Items ($countStr)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = appColors.textPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(stats.purchasedItems, key = { "purchased_${it.id}" }) { item ->
                    BazaarTripDetailItemRow(
                        item = item,
                        isPurchased = true,
                        isBangla = isBangla,
                        useBengaliNumerals = userProfile.useBengaliNumerals
                    )
                }
            }

            // Section 2: Unbought Items (if any)
            if (stats.unboughtItems.isNotEmpty()) {
                item(key = "unbought_header") {
                    val unboughtNum = if (userProfile.useBengaliNumerals || isBangla) {
                        DigitConverter.toBengaliDigits(stats.unboughtCount.toString())
                    } else {
                        stats.unboughtCount.toString()
                    }
                    val countStr = if (isBangla) "${unboughtNum}টি" else "$unboughtNum"
                    PulseText(
                        text = if (isBangla) "কেনা হয়নি ($countStr)" else "Not Bought ($countStr)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = appColors.warning,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                item(key = "unbought_notice") {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = appColors.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = appColors.warning,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            PulseText(
                                text = if (isBangla) {
                                    "এই পণ্যগুলো বাজারে কেনা হয়নি। আপনি এগুলোকে নতুন ফর্দে নিয়ে যেতে পারেন।"
                                } else {
                                    "These items were not bought during this trip. You can carry them over to a new list."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = appColors.textSecondary
                            )
                        }
                    }
                }

                items(stats.unboughtItems, key = { "unbought_${it.id}" }) { item ->
                    BazaarTripDetailItemRow(
                        item = item,
                        isPurchased = false,
                        isBangla = isBangla
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun BazaarTripDetailItemRow(
    item: MarketItem,
    isPurchased: Boolean,
    isBangla: Boolean,
    useBengaliNumerals: Boolean = false,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.surface,
        tonalElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isPurchased) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isPurchased) appColors.accent else appColors.textMuted,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    PulseText(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    PulseText(
                        text = item.formatQuantity(),
                        style = MaterialTheme.typography.bodySmall,
                        color = appColors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Amount or Not Bought indicator
            if (isPurchased) {
                val amountStr = if (item.amount != null && item.amount > 0.0) {
                    val formatted = if (item.amount % 1.0 == 0.0) "${item.amount.toInt()}" else "%.1f".format(item.amount)
                    val numStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(formatted) else formatted
                    "৳$numStr"
                } else {
                    if (isBangla) "মূল্য ছাড়া" else "No price"
                }
                PulseText(
                    text = amountStr,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = appColors.textPrimary
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.surfaceVariant
                ) {
                    PulseText(
                        text = if (isBangla) "কেনা হয়নি" else "Not bought",
                        style = MaterialTheme.typography.labelSmall,
                        color = appColors.textMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
