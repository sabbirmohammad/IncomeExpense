package com.example.expensetracker.ui.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * V7 Target Design D: Market Trip Finish Summary Sheet.
 * - Total spent amount
 * - Bought vs skipped counts
 * - Biggest price changes
 * - Account chips (defaulting to last used/default account)
 * - Date chip
 * - If items left: choose "Move to next list", "Keep in list", "Discard"
 * - Primary button "Save ৳... as expense" (reuses atomic trip completion)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketTripFinishSummarySheet(
    trip: MarketListWithItems,
    accounts: List<Account>,
    currencySymbol: String,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConfirmComplete: (selectedAccountId: Long?, leftoverChoice: LeftoverHandlingOption) -> Unit,
    productPriceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val defaultAccount = remember(accounts) {
        accounts.find { it.isDefault } ?: accounts.firstOrNull()
    }
    var selectedAccountId by remember(defaultAccount?.id) { mutableStateOf<Long?>(defaultAccount?.id) }

    val boughtItems = remember(trip.items) { trip.items.filter { it.isPurchased } }
    val unboughtItems = remember(trip.items) { trip.items.filter { !it.isPurchased } }
    val totalSpent = remember(boughtItems) { boughtItems.sumOf { it.amount ?: 0.0 } }

    var leftoverOption by remember { mutableStateOf(LeftoverHandlingOption.MOVE_TO_NEXT) }

    val priceChanges = remember(boughtItems, productPriceInsights) {
        calculateTripPriceChanges(boughtItems, productPriceInsights)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = appColors.cardBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(38.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(appColors.divider)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TripFinishHeader(
                tripTitle = trip.list.title,
                onDismiss = onDismiss
            )

            TripSummaryTotalCard(
                totalSpent = totalSpent,
                currencySymbol = currencySymbol,
                boughtCount = boughtItems.size,
                skippedCount = unboughtItems.size,
                isBangla = isBangla
            )

            if (priceChanges.isNotEmpty()) {
                PriceChangesSection(
                    priceChanges = priceChanges,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla
                )
            }

            TripPaymentAccountSelector(
                accounts = accounts,
                selectedAccountId = selectedAccountId,
                onSelectAccount = { selectedAccountId = it },
                isBangla = isBangla
            )

            if (unboughtItems.isNotEmpty()) {
                TripLeftoversCard(
                    unboughtCount = unboughtItems.size,
                    leftoverOption = leftoverOption,
                    onOptionSelected = { leftoverOption = it },
                    isBangla = isBangla
                )
            }

            val totalStr = formatCurrency(totalSpent, currencySymbol)
            Button(
                onClick = {
                    haptics.successFeedback()
                    onConfirmComplete(selectedAccountId, leftoverOption)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${strings.marketSaveAsExpense} ($totalStr)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
