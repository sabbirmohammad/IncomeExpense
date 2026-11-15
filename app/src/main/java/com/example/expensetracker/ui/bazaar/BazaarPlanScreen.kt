package com.example.expensetracker.ui.bazaar

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import com.example.expensetracker.theme.ExpenseTrackerTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import androidx.compose.material3.MaterialTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.adaptive.LocalWindowClass
import com.example.expensetracker.ui.adaptive.WindowWidthClass
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PulseSegmented
import com.example.expensetracker.ui.components.PulseTabularText
import com.example.expensetracker.ui.components.PulseText
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.getDepartmentMeta
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BazaarPlanScreen(
    listId: Long,
    bazaarViewModel: BazaarViewModel,
    onNavigateBack: () -> Unit,
    onStartShopping: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val strings = LocalAppStrings.current
    val windowClass = LocalWindowClass.current
    val isBangla = strings.isBangla
    val haptics = rememberAppHaptics()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val allLists by bazaarViewModel.allMarketLists.collectAsStateWithLifecycle()
    val lastPurchasedRates by bazaarViewModel.lastPurchasedRates.collectAsStateWithLifecycle()
    val masterProducts by bazaarViewModel.masterProducts.collectAsStateWithLifecycle()
    val masterProductsMap by bazaarViewModel.masterProductsMap.collectAsStateWithLifecycle()
    val isPrivacyMode by bazaarViewModel.isPrivacyMode.collectAsStateWithLifecycle()
    val isMarketListsLoaded by bazaarViewModel.isMarketListsLoaded.collectAsStateWithLifecycle()
    val userProfile by bazaarViewModel.userProfile.collectAsStateWithLifecycle()
    val budgetCaps by bazaarViewModel.budgetCaps.collectAsStateWithLifecycle()
    val pendingDeletedItemIds by bazaarViewModel.pendingDeletedItemIds.collectAsStateWithLifecycle()
    val completedTrips by bazaarViewModel.completedTrips.collectAsStateWithLifecycle()
    val stapleProducts by bazaarViewModel.stapleProducts.collectAsStateWithLifecycle()

    val currentTrip = allLists.find { it.list.id == listId }

    // If list does not exist after repository loading finishes, safely pop back to Hub
    LaunchedEffect(isMarketListsLoaded, currentTrip) {
        if (isMarketListsLoaded && currentTrip == null) {
            onNavigateBack()
        }
    }

    if (currentTrip == null) {
        Box(modifier = Modifier.fillMaxSize().background(appColors.background))
        return
    }

    val activeItems = remember(currentTrip.items, pendingDeletedItemIds) {
        currentTrip.items.filter { it.id !in pendingDeletedItemIds }
    }

    val budgetCap = budgetCaps[listId]

    // Estimate calculation
    val (estimateSummary, lineEstimates) = remember(activeItems, lastPurchasedRates, masterProductsMap, budgetCap) {
        BazaarDomainLogic.calculateMarketListEstimate(
            items = activeItems,
            lastPurchasedRates = lastPurchasedRates,
            masterProducts = masterProductsMap,
            budgetCap = budgetCap
        )
    }

    // Empty state suggestions (Amendment for Bug 2)
    val emptyStateSuggestions = remember(activeItems, completedTrips, stapleProducts) {
        if (activeItems.isNotEmpty()) emptyList()
        else BazaarDomainLogic.buildEmptyStateSuggestions(
            existingItems = activeItems,
            completedTrips = completedTrips,
            stapleProducts = stapleProducts
        )
    }

    // Editable list title with debounce auto-save
    var titleInput by remember(currentTrip.list.title) { mutableStateOf(currentTrip.list.title) }

    // Privacy reveal state (3 seconds)
    var isPrivacyRevealed by remember { mutableStateOf(false) }
    LaunchedEffect(isPrivacyRevealed) {
        if (isPrivacyRevealed) {
            delay(3000L)
            isPrivacyRevealed = false
        }
    }

    // Smart Add Bar text and focus
    var smartAddText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Autocomplete suggestions
    val autocompleteSuggestions = remember(smartAddText, masterProducts) {
        if (smartAddText.isBlank() || smartAddText.length < 2) {
            emptyList()
        } else {
            val clean = UnitConverterEngine.normalizeProductName(smartAddText)
            masterProducts.filter {
                it.name.contains(smartAddText, ignoreCase = true) || it.normalizedName.contains(clean)
            }.take(4)
        }
    }

    // Dialog state for budget cap
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showStallPickerItem by remember { mutableStateOf<MarketItem?>(null) }
    var showUnitPickerItem by remember { mutableStateOf<MarketItem?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    // Compact mode segment toggle: 0 = My List, 1 = Catalog
    var compactSegmentIndex by remember { mutableIntStateOf(0) }
    val isCompact = windowClass.widthClass.isCompact

    Surface(
        color = appColors.background,
        modifier = modifier.fillMaxSize().navigationBarsPadding().imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        onNavigateBack()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (isBangla) "ফিরে যান" else "Back",
                        tint = appColors.textPrimary
                    )
                }

                // Inline editable title
                OutlinedTextField(
                    value = titleInput,
                    onValueChange = {
                        titleInput = it
                        bazaarViewModel.updateMarketListTitleDebounced(listId, it)
                    },
                    singleLine = true,
                    textStyle = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Start shopping CTA button
                Button(
                    onClick = {
                        haptics.lightTick()
                        onStartShopping(listId)
                    },
                    enabled = activeItems.isNotEmpty(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0E9F6E),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = strings.marketStartShopping,
                        style = typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Overflow menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = if (isBangla) "আরও অপশন" else "More options",
                            tint = appColors.textSecondary
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(strings.bazaarShareAsText, color = appColors.textPrimary) },
                            onClick = {
                                showMenu = false
                                val shareText = BazaarDomainLogic.formatListAsWhatsAppText(
                                    listTitle = currentTrip.list.title,
                                    items = activeItems,
                                    masterProductsMap = masterProductsMap,
                                    isBangla = isBangla
                                )
                                if (shareText.isNotBlank()) {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                }
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(strings.bazaarDuplicateList, color = appColors.textPrimary) },
                            onClick = {
                                showMenu = false
                                bazaarViewModel.duplicateMarketList(listId) {
                                    haptics.lightTick()
                                }
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(strings.bazaarDeleteList, color = appColors.expense) },
                            onClick = {
                                showMenu = false
                                bazaarViewModel.deleteMarketListWithUndo(
                                    listId = listId,
                                    listTitle = currentTrip.list.title,
                                    showSnackbar = { msg, act ->
                                        val res = snackbarHostState.showSnackbar(msg, act)
                                        res == SnackbarResult.ActionPerformed
                                    },
                                    onDeletedFinal = { onNavigateBack() }
                                )
                            }
                        )
                    }
                }
            }

            // Compact mode segment bar: [My List | Catalog]
            if (isCompact) {
                PulseSegmented(
                    items = listOf(
                        "${strings.bazaarMyList} (${activeItems.size})",
                        strings.bazaarCatalog
                    ),
                    selectedIndex = compactSegmentIndex,
                    onItemSelected = {
                        haptics.lightTick()
                        compactSegmentIndex = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Main Body Area
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (isCompact) {
                    if (compactSegmentIndex == 0) {
                        // My List View (Compact)
                        PlanMyListPane(
                            activeItems = activeItems,
                            lineEstimates = lineEstimates,
                            masterProductsMap = masterProductsMap,
                            onUpdateQuantity = { item, newQty ->
                                bazaarViewModel.updateMarketItem(item.copy(quantity = newQty))
                            },
                            onDeleteItem = { item ->
                                bazaarViewModel.deleteMarketItemWithUndo(item) { msg, act ->
                                    val res = snackbarHostState.showSnackbar(msg, act)
                                    res == SnackbarResult.ActionPerformed
                                }
                            },
                            onStallClick = { showStallPickerItem = it },
                            onUnitClick = { showUnitPickerItem = it },
                            onOpenCatalog = { compactSegmentIndex = 1 },
                            onAddSuggestion = { name, qty, unit ->
                                bazaarViewModel.addMarketItem(
                                    listId,
                                    MarketItem(
                                        listId = listId,
                                        name = name,
                                        quantity = qty,
                                        unit = unit
                                    )
                                )
                            },
                            onAddTemplate = { templateItems ->
                                templateItems.forEach { tItm ->
                                    bazaarViewModel.addMarketItem(
                                        listId,
                                        tItm.copy(listId = listId)
                                    )
                                }
                            },
                            suggestionGroups = emptyStateSuggestions,
                            isBangla = isBangla,
                            useBengaliNumerals = userProfile.useBengaliNumerals,
                            isPrivacyMode = isPrivacyMode && !isPrivacyRevealed,
                            onRevealPrivacy = { isPrivacyRevealed = true }
                        )
                    } else {
                        // Catalog View (Compact)
                        Column(modifier = Modifier.fillMaxSize()) {
                            BazaarCatalogPane(
                                masterProducts = masterProducts,
                                currentItems = activeItems,
                                onAddOrIncrementProduct = { product ->
                                    val updated = BazaarDomainLogic.addOrIncrementCatalogItem(
                                        existingItems = activeItems,
                                        product = product,
                                        listId = listId
                                    )
                                    val existing = activeItems.find {
                                        UnitConverterEngine.normalizeProductName(it.name) == product.normalizedName
                                    }
                                    if (existing != null) {
                                        bazaarViewModel.updateMarketItem(existing.copy(quantity = existing.quantity + 1.0))
                                    } else {
                                        bazaarViewModel.addMarketItem(
                                            listId,
                                            MarketItem(
                                                listId = listId,
                                                name = product.name,
                                                quantity = 1.0,
                                                unit = product.defaultUnit
                                            )
                                        )
                                    }
                                },
                                isBangla = isBangla,
                                useBengaliNumerals = userProfile.useBengaliNumerals,
                                modifier = Modifier.weight(1f)
                            )

                            // Sticky mini-bar on Catalog: "N items · Estimate ৳X · View list"
                            Surface(
                                color = appColors.surface,
                                tonalElevation = 4.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    val countText = if (userProfile.useBengaliNumerals || isBangla) {
                                        "${DigitConverter.toBengaliDigits(activeItems.size.toString())}টি পণ্য"
                                    } else {
                                        "${activeItems.size} items"
                                    }
                                    val estNum = if (estimateSummary.totalEstimatedPrice % 1.0 == 0.0) {
                                        "${estimateSummary.totalEstimatedPrice.toInt()}"
                                    } else {
                                        "%.1f".format(estimateSummary.totalEstimatedPrice)
                                    }
                                    val estFormatted = if (userProfile.useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(estNum) else estNum

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { isPrivacyRevealed = true }
                                    ) {
                                        PulseText(
                                            text = countText,
                                            style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = appColors.textPrimary
                                        )
                                        Text(
                                            text = " · ",
                                            style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = appColors.textSecondary
                                        )
                                        PulseTabularText(
                                            text = if (isPrivacyMode && !isPrivacyRevealed) "••••••" else "৳$estFormatted",
                                            style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = appColors.textPrimary
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            haptics.lightTick()
                                            compactSegmentIndex = 0
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                                    ) {
                                        Text(strings.bazaarViewList, color = appColors.onAccent)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Medium / Expanded: Two-Pane View
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left Pane: My List
                        Box(modifier = Modifier.weight(1.1f).fillMaxHeight()) {
                            PlanMyListPane(
                                activeItems = activeItems,
                                lineEstimates = lineEstimates,
                                masterProductsMap = masterProductsMap,
                                onUpdateQuantity = { item, newQty ->
                                    bazaarViewModel.updateMarketItem(item.copy(quantity = newQty))
                                },
                                onDeleteItem = { item ->
                                    bazaarViewModel.deleteMarketItemWithUndo(item) { msg, act ->
                                        val res = snackbarHostState.showSnackbar(msg, act)
                                        res == SnackbarResult.ActionPerformed
                                    }
                                },
                                onStallClick = { showStallPickerItem = it },
                                onUnitClick = { showUnitPickerItem = it },
                                onOpenCatalog = {},
                                onAddSuggestion = { name, qty, unit ->
                                    bazaarViewModel.addMarketItem(
                                        listId,
                                        MarketItem(
                                            listId = listId,
                                            name = name,
                                            quantity = qty,
                                            unit = unit
                                        )
                                    )
                                },
                                onAddTemplate = { templateItems ->
                                    templateItems.forEach { tItm ->
                                        bazaarViewModel.addMarketItem(
                                            listId,
                                            tItm.copy(listId = listId)
                                        )
                                    }
                                },
                                suggestionGroups = emptyStateSuggestions,
                                isBangla = isBangla,
                                useBengaliNumerals = userProfile.useBengaliNumerals,
                                isPrivacyMode = isPrivacyMode && !isPrivacyRevealed,
                                onRevealPrivacy = { isPrivacyRevealed = true }
                            )
                        }

                        // Divider
                        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(appColors.border.copy(alpha = 0.5f)))

                        // Right Pane: Catalog
                        Box(modifier = Modifier.weight(0.9f).fillMaxHeight()) {
                            BazaarCatalogPane(
                                masterProducts = masterProducts,
                                currentItems = activeItems,
                                onAddOrIncrementProduct = { product ->
                                    val existing = activeItems.find {
                                        UnitConverterEngine.normalizeProductName(it.name) == product.normalizedName
                                    }
                                    if (existing != null) {
                                        bazaarViewModel.updateMarketItem(existing.copy(quantity = existing.quantity + 1.0))
                                    } else {
                                        bazaarViewModel.addMarketItem(
                                            listId,
                                            MarketItem(
                                                listId = listId,
                                                name = product.name,
                                                quantity = 1.0,
                                                unit = product.defaultUnit
                                            )
                                        )
                                    }
                                },
                                isBangla = isBangla,
                                useBengaliNumerals = userProfile.useBengaliNumerals
                            )
                        }
                    }
                }
            }

            // Bottom Area: Smart Add Bar + Estimate & Budget Bar
            if (!isCompact || compactSegmentIndex == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(appColors.surface)
                ) {
                    // Autocomplete suggestions strip
                    AnimatedVisibility(visible = autocompleteSuggestions.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            for (sugg in autocompleteSuggestions) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = appColors.surfaceVariant,
                                    modifier = Modifier.clickable {
                                        haptics.lightTick()
                                        bazaarViewModel.addMarketItem(
                                            listId,
                                            MarketItem(
                                                listId = listId,
                                                name = sugg.name,
                                                quantity = 1.0,
                                                unit = sugg.defaultUnit
                                            )
                                        )
                                        smartAddText = ""
                                        focusRequester.requestFocus()
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = sugg.name,
                                            style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = appColors.textPrimary
                                        )
                                        if (sugg.latestUnitPrice > 0.0) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "৳${sugg.latestUnitPrice.toInt()}",
                                                style = typography.labelSmall,
                                                color = appColors.textMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Smart Add Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = smartAddText,
                            onValueChange = { smartAddText = it },
                            placeholder = {
                                Text(
                                    text = strings.bazaarEnterItemPrompt,
                                    style = typography.bodyMedium,
                                    color = appColors.textMuted
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (smartAddText.isNotBlank()) {
                                        val parsed = MarketListParser.parseSingleLine(smartAddText)
                                        if (parsed != null && parsed.name.isNotBlank()) {
                                            bazaarViewModel.addMarketItem(
                                                listId,
                                                parsed.copy(listId = listId)
                                            )
                                            smartAddText = ""
                                            haptics.lightTick()
                                            focusRequester.requestFocus()
                                        }
                                    }
                                }
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = appColors.surfaceVariant,
                                unfocusedContainerColor = appColors.surfaceVariant,
                                focusedBorderColor = appColors.accent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (smartAddText.isNotBlank()) {
                                    val parsed = MarketListParser.parseSingleLine(smartAddText)
                                    if (parsed != null && parsed.name.isNotBlank()) {
                                        bazaarViewModel.addMarketItem(
                                            listId,
                                            parsed.copy(listId = listId)
                                        )
                                        smartAddText = ""
                                        haptics.lightTick()
                                        focusRequester.requestFocus()
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(appColors.accent)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = if (isBangla) "যোগ করুন" else "Add item",
                                tint = appColors.onAccent
                            )
                        }
                    }

                    // Sticky Estimate & Budget Bar
                    EstimateAndBudgetStickyBar(
                        summary = estimateSummary,
                        onSetBudgetClick = { showBudgetDialog = true },
                        isPrivacyMode = isPrivacyMode && !isPrivacyRevealed,
                        onRevealPrivacy = { isPrivacyRevealed = true },
                        isBangla = isBangla,
                        useBengaliNumerals = userProfile.useBengaliNumerals
                    )
                }
            }
        }

        // Floating Snackbar Host
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 80.dp)
            )
        }
    }

    // Set Budget Dialog
    if (showBudgetDialog) {
        BudgetCapDialog(
            currentCap = budgetCap,
            onDismiss = { showBudgetDialog = false },
            onConfirm = { newBudget ->
                bazaarViewModel.setBudgetCap(listId, newBudget)
                showBudgetDialog = false
            },
            isBangla = isBangla
        )
    }

    // Stall Picker Dialog
    if (showStallPickerItem != null) {
        val targetItem = showStallPickerItem!!
        StallPickerDialog(
            itemName = targetItem.name,
            currentStall = BazaarDomainLogic.classifyStallWithOverride(targetItem.name, masterProductsMap),
            onDismiss = { showStallPickerItem = null },
            onSelectStall = { newStall ->
                bazaarViewModel.updateCommodityStall(targetItem.name, newStall)
                showStallPickerItem = null
            },
            isBangla = isBangla
        )
    }

    // Unit Picker Dialog
    if (showUnitPickerItem != null) {
        val targetItem = showUnitPickerItem!!
        UnitPickerDialog(
            currentUnit = targetItem.unit,
            onDismiss = { showUnitPickerItem = null },
            onSelectUnit = { newUnit ->
                bazaarViewModel.updateMarketItem(targetItem.copy(unit = newUnit))
                showUnitPickerItem = null
            },
            isBangla = isBangla
        )
    }
}

@Composable
fun PlanMyListPane(
    activeItems: List<MarketItem>,
    lineEstimates: List<MarketItemPriceEstimate>,
    masterProductsMap: Map<String, MasterProduct>,
    onUpdateQuantity: (MarketItem, Double) -> Unit,
    onDeleteItem: (MarketItem) -> Unit,
    onStallClick: (MarketItem) -> Unit,
    onUnitClick: (MarketItem) -> Unit,
    modifier: Modifier = Modifier,
    onOpenCatalog: () -> Unit = {},
    onAddSuggestion: (name: String, quantity: Double, unit: String) -> Unit = { _, _, _ -> },
    onAddTemplate: (items: List<MarketItem>) -> Unit = {},
    suggestionGroups: List<PlanSuggestionGroup> = emptyList(),
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false,
    isPrivacyMode: Boolean = false,
    onRevealPrivacy: () -> Unit = {}
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    if (activeItems.isEmpty()) {
        PlanEmptyStateView(
            suggestionGroups = suggestionGroups,
            onAddSuggestion = onAddSuggestion,
            onAddTemplate = onAddTemplate,
            onOpenCatalog = onOpenCatalog,
            isBangla = isBangla,
            useBengaliNumerals = useBengaliNumerals,
            isPrivacyMode = isPrivacyMode,
            onRevealPrivacy = onRevealPrivacy,
            modifier = modifier
        )
        return
    }

    val itemsByDepartment = remember(activeItems, masterProductsMap) {
        activeItems.groupBy {
            BazaarDomainLogic.classifyStallWithOverride(it.name, masterProductsMap)
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        for (dept in ALL_DEPARTMENTS) {
            val stallItems = itemsByDepartment[dept.key] ?: continue
            if (stallItems.isEmpty()) continue

            // Stall Header
            item(key = "stall_header_${dept.key}") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = "${dept.icon} ${if (isBangla) dept.titleBn else dept.titleEn}",
                        style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val countStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(stallItems.size.toString()) else stallItems.size.toString()
                    Text(
                        text = "($countStr)",
                        style = typography.bodySmall,
                        color = appColors.textMuted
                    )
                }
            }

            items(stallItems, key = { it.id }) { item ->
                val lineEst = lineEstimates.find { it.item.id == item.id }
                PlanItemRow(
                    item = item,
                    lineEstimate = lineEst,
                    deptMeta = dept,
                    onUpdateQuantity = { onUpdateQuantity(item, it) },
                    onDelete = { onDeleteItem(item) },
                    onStallClick = { onStallClick(item) },
                    onUnitClick = { onUnitClick(item) },
                    isBangla = isBangla,
                    useBengaliNumerals = useBengaliNumerals,
                    isPrivacyMode = isPrivacyMode,
                    onRevealPrivacy = onRevealPrivacy
                )
            }
        }
    }
}

/**
 * Rich, non-empty initial view for newly created market lists (Amendment for Bug 2).
 * Shows smart suggestion chips and templates with >=48dp touch targets.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlanEmptyStateView(
    suggestionGroups: List<PlanSuggestionGroup>,
    onAddSuggestion: (name: String, quantity: Double, unit: String) -> Unit,
    onAddTemplate: (items: List<MarketItem>) -> Unit,
    onOpenCatalog: () -> Unit,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    isPrivacyMode: Boolean,
    onRevealPrivacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // 1. Prominent Catalog Banner
        item(key = "empty_catalog_banner") {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = appColors.accent.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.35f)),
                contentColor = appColors.textPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        role = Role.Button,
                        onClickLabel = if (isBangla) "ক্যাটালগ থেকে পণ্য বেছে নিন" else "Browse catalog",
                        onClick = onOpenCatalog
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) "ক্যাটালগ থেকে সদাই বেছে নিন" else "Browse Master Catalog",
                            style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBangla) "চাল, ডাল, তেল, মাছ, মাংস ও শাকসবজি" else "Rice, oil, fish, meat & vegetables",
                            style = typography.bodySmall,
                            color = appColors.textSecondary
                        )
                    }
                    Button(
                        onClick = onOpenCatalog,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.accent,
                            contentColor = appColors.onAccent
                        ),
                        modifier = Modifier.heightIn(min = 44.dp)
                    ) {
                        Text(
                            text = if (isBangla) "ক্যাটালগ" else "Catalog",
                            style = typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // 2. Suggestion Groups (Omitted if group has no data)
        for (group in suggestionGroups) {
            if (group.chips.isEmpty()) continue

            item(key = "sugg_group_${group.id}") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isBangla) group.titleBn else group.titleEn,
                        style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = appColors.textPrimary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (chip in group.chips) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = appColors.surface,
                                contentColor = appColors.textPrimary,
                                border = BorderStroke(1.dp, appColors.border),
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .clickable(
                                        role = Role.Button,
                                        onClickLabel = if (chip.isTemplate) "Add template ${chip.name}" else "Add ${chip.name}",
                                        onClick = {
                                            if (chip.isTemplate) {
                                                onAddTemplate(chip.templateItems)
                                            } else {
                                                onAddSuggestion(chip.name, chip.defaultQuantity, chip.defaultUnit)
                                            }
                                        }
                                    )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = appColors.accent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = chip.name,
                                        style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = appColors.textPrimary
                                    )
                                    if (chip.isTemplate) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        val count = chip.templateItems.size.toString()
                                        val countStr = if (useBengaliNumerals || isBangla) "${DigitConverter.toBengaliDigits(count)}টি" else "$count items"
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = appColors.accent.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = countStr,
                                                style = typography.labelSmall,
                                                color = appColors.accent,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else if (chip.estimatedPrice != null && chip.estimatedPrice > 0.0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        val pNum = if (chip.estimatedPrice % 1.0 == 0.0) "${chip.estimatedPrice.toInt()}" else "%.1f".format(chip.estimatedPrice)
                                        val pStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(pNum) else pNum
                                        PulseTabularText(
                                            text = if (isPrivacyMode) "••••" else "~৳$pStr",
                                            style = typography.bodySmall,
                                            color = appColors.textMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Plan Mode item row designed for accessibility and responsive wrapping (Amendment for Bug 3):
 * - Clean two-row structure prevents text truncation to 0 width on small 320dp screens or 200% font scale.
 * - Explicit [contentColor] prevents dark-on-dark invisible text.
 * - Minimum 44-48dp touch targets on steppers and unit picker.
 */
@Composable
fun PlanItemRow(
    item: MarketItem,
    lineEstimate: MarketItemPriceEstimate?,
    deptMeta: com.example.expensetracker.ui.dashboard.DepartmentMeta,
    onUpdateQuantity: (Double) -> Unit,
    onDelete: () -> Unit,
    onStallClick: () -> Unit,
    onUnitClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false,
    isPrivacyMode: Boolean = false,
    onRevealPrivacy: () -> Unit = {}
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val haptics = rememberAppHaptics()

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.surface,
        contentColor = appColors.textPrimary,
        border = BorderStroke(1.dp, appColors.border),
        tonalElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Row 1: Stall Chip + Name (weight 1f, maxLines 2) + Line Estimate + Delete
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Stall Chip (Tappable to change stall)
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(appColors.surfaceVariant)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = if (isBangla) "স্টল পরিবর্তন করুন" else "Change department stall",
                            onClick = onStallClick
                        )
                        .padding(6.dp)
                ) {
                    Text(text = deptMeta.icon, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Item Name (gets ample width, wraps up to 2 lines, never collapses to 0)
                Text(
                    text = item.name,
                    style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                    color = appColors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Line Estimated Price or "৳—"
                val lineTotal = lineEstimate?.lineTotal
                if (lineTotal != null && lineTotal > 0.0) {
                    val priceNum = if (lineTotal % 1.0 == 0.0) "${lineTotal.toInt()}" else "%.1f".format(lineTotal)
                    val priceStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(priceNum) else priceNum
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onRevealPrivacy() }
                    ) {
                        Text(
                            text = if (isBangla) "আনুমানিক: " else "Est: ",
                            style = typography.bodySmall,
                            color = appColors.textMuted
                        )
                        PulseTabularText(
                            text = if (isPrivacyMode) "••••••" else "৳$priceStr",
                            style = typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = appColors.textSecondary
                        )
                    }
                } else {
                    PulseTabularText(
                        text = "৳—",
                        style = typography.bodySmall,
                        color = appColors.textMuted
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Delete item button (triggers 5s Undo)
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        onDelete()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = if (isBangla) "মুছুন" else "Delete",
                        tint = appColors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Unit Chip on left + Quantity Stepper on right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Unit Chip (Tappable to pick unit)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.surfaceVariant,
                    modifier = Modifier
                        .heightIn(min = 36.dp)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = if (isBangla) "পরিমাপের একক পরিবর্তন করুন" else "Change unit",
                            onClick = onUnitClick
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = item.unit,
                            style = typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = appColors.textPrimary
                        )
                    }
                }

                // Quantity Stepper (− qty +)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            haptics.lightTick()
                            val step = if (item.unit in listOf("গ্রাম", "মিলি")) 50.0 else 0.5
                            if (item.quantity > step) {
                                onUpdateQuantity(item.quantity - step)
                            }
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = if (isBangla) "কমান" else "Decrease",
                            tint = appColors.textSecondary
                        )
                    }

                    val qtyNum = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()}" else "${item.quantity}"
                    val qtyDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(qtyNum) else qtyNum

                    PulseTabularText(
                        text = qtyDisplay,
                        style = typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                        color = appColors.textPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = {
                            haptics.lightTick()
                            val step = if (item.unit in listOf("গ্রাম", "মিলি")) 50.0 else 0.5
                            onUpdateQuantity(item.quantity + step)
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = if (isBangla) "বাড়ান" else "Increase",
                            tint = appColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

// ========================================================================
// PREVIEWS FOR BUG 3: 320dp width, 200% font scale, long Bengali name
// ========================================================================

@Preview(name = "PlanItemRow - Light - 320dp - 200% Font", widthDp = 320)
@Composable
private fun PlanItemRowLightPreview() {
    val currentDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(currentDensity.density, fontScale = 2.0f)
    ) {
        ExpenseTrackerTheme(themeMode = "light") {
            Surface(color = LocalAppColors.current.background) {
                PlanItemRow(
                    item = MarketItem(
                        name = "দেশি গোল আলু (নতুন আলু - বিশেষ বাছাইকৃত)",
                        quantity = 2.5,
                        unit = "কেজি"
                    ),
                    lineEstimate = MarketItemPriceEstimate(
                        item = MarketItem(name = "আলু"),
                        unitPrice = 50.0,
                        lineTotal = 125.0,
                        isEstimated = false
                    ),
                    deptMeta = ALL_DEPARTMENTS[0],
                    onUpdateQuantity = {},
                    onDelete = {},
                    onStallClick = {},
                    onUnitClick = {},
                    isBangla = true,
                    useBengaliNumerals = true,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Preview(name = "PlanItemRow - Dark - 320dp - 200% Font", widthDp = 320)
@Composable
private fun PlanItemRowDarkPreview() {
    val currentDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(currentDensity.density, fontScale = 2.0f)
    ) {
        ExpenseTrackerTheme(themeMode = "dark") {
            Surface(color = LocalAppColors.current.background) {
                PlanItemRow(
                    item = MarketItem(
                        name = "দেশি গোল আলু (নতুন আলু - বিশেষ বাছাইকৃত)",
                        quantity = 2.5,
                        unit = "কেজি"
                    ),
                    lineEstimate = MarketItemPriceEstimate(
                        item = MarketItem(name = "আলু"),
                        unitPrice = 50.0,
                        lineTotal = 125.0,
                        isEstimated = false
                    ),
                    deptMeta = ALL_DEPARTMENTS[0],
                    onUpdateQuantity = {},
                    onDelete = {},
                    onStallClick = {},
                    onUnitClick = {},
                    isBangla = true,
                    useBengaliNumerals = true,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Preview(name = "PlanItemRow - AMOLED - 320dp - 200% Font", widthDp = 320)
@Composable
private fun PlanItemRowAmoledPreview() {
    val currentDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(currentDensity.density, fontScale = 2.0f)
    ) {
        ExpenseTrackerTheme(themeMode = "amoled") {
            Surface(color = LocalAppColors.current.background) {
                PlanItemRow(
                    item = MarketItem(
                        name = "দেশি গোল আলু (নতুন আলু - বিশেষ বাছাইকৃত)",
                        quantity = 2.5,
                        unit = "কেজি"
                    ),
                    lineEstimate = MarketItemPriceEstimate(
                        item = MarketItem(name = "আলু"),
                        unitPrice = 50.0,
                        lineTotal = 125.0,
                        isEstimated = false
                    ),
                    deptMeta = ALL_DEPARTMENTS[0],
                    onUpdateQuantity = {},
                    onDelete = {},
                    onStallClick = {},
                    onUnitClick = {},
                    isBangla = true,
                    useBengaliNumerals = true,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun EstimateAndBudgetStickyBar(
    summary: MarketListEstimateSummary,
    onSetBudgetClick: () -> Unit,
    isPrivacyMode: Boolean,
    onRevealPrivacy: () -> Unit,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    val barBgColor by animateColorAsState(
        targetValue = if (summary.isOverBudget) appColors.warning.copy(alpha = 0.12f) else appColors.surfaceVariant,
        label = "barBg"
    )

    Surface(
        color = barBgColor,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.weight(1f).clickable { onRevealPrivacy() }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val estNum = if (summary.totalEstimatedPrice % 1.0 == 0.0) {
                        "${summary.totalEstimatedPrice.toInt()}"
                    } else {
                        "%.1f".format(summary.totalEstimatedPrice)
                    }
                    val estFormatted = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(estNum) else estNum

                    Text(
                        text = if (isBangla) "আনুমানিক " else "Estimated ",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = appColors.textPrimary
                    )

                    PulseTabularText(
                        text = if (isPrivacyMode) "••••••" else "৳$estFormatted",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (summary.isOverBudget) appColors.warning else appColors.textPrimary
                    )
                }

                // Subline: unpriced items count and over-budget difference
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (summary.hasUnpricedItems) {
                        val unpricedStr = if (useBengaliNumerals || isBangla) {
                            "${DigitConverter.toBengaliDigits(summary.unpricedItemsCount.toString())}টি পণ্যের দাম নেই"
                        } else {
                            "${summary.unpricedItemsCount} items have no price"
                        }
                        Text(
                            text = unpricedStr,
                            style = typography.bodySmall,
                            color = appColors.textMuted
                        )
                    }

                    if (summary.isOverBudget) {
                        if (summary.hasUnpricedItems) {
                            Text(text = " · ", style = typography.bodySmall, color = appColors.textMuted)
                        }
                        val diffNum = if (summary.overBudgetAmount % 1.0 == 0.0) {
                            "${summary.overBudgetAmount.toInt()}"
                        } else {
                            "%.1f".format(summary.overBudgetAmount)
                        }
                        val diffStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(diffNum) else diffNum
                        Text(
                            text = if (isBangla) "৳$diffStr বেশি" else "৳$diffStr over",
                            style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = appColors.warning
                        )
                    }
                }
            }

            // Budget Cap Chip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (summary.budgetCap != null) appColors.surface else appColors.accent.copy(alpha = 0.15f),
                border = if (summary.isOverBudget) androidx.compose.foundation.BorderStroke(1.dp, appColors.warning) else null,
                modifier = Modifier
                    .clickable(
                        role = Role.Button,
                        onClickLabel = if (isBangla) "বাজেট সীমা নির্ধারণ করুন" else "Set budget cap",
                        onClick = onSetBudgetClick
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    if (summary.budgetCap != null && summary.budgetCap > 0.0) {
                        val capNum = if (summary.budgetCap % 1.0 == 0.0) "${summary.budgetCap.toInt()}" else "%.1f".format(summary.budgetCap)
                        val capStr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(capNum) else capNum
                        Text(
                            text = if (isBangla) "বাজেট ৳$capStr" else "Budget ৳$capStr",
                            style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (summary.isOverBudget) appColors.warning else appColors.textPrimary
                        )
                    } else {
                        Text(
                            text = if (isBangla) "＋ বাজেট নির্ধারণ" else "＋ Set budget",
                            style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = appColors.accent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetCapDialog(
    currentCap: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Double?) -> Unit,
    isBangla: Boolean
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    var input by remember { mutableStateOf(currentCap?.toInt()?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = appColors.surface,
            modifier = Modifier.width(320.dp).padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isBangla) "বাজেট সীমা নির্ধারণ" else "Set Budget Cap",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("৳২০০০ / 2000", color = appColors.textMuted, style = typography.bodyMedium) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (currentCap != null) {
                        TextButton(onClick = { onConfirm(null) }) {
                            Text(if (isBangla) "মুছুন" else "Clear", color = appColors.expense)
                        }
                    }
                    TextButton(onClick = onDismiss) {
                        Text(if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                    }
                    Button(
                        onClick = {
                            val parsed = DigitConverter.toEnglishDigits(input).toDoubleOrNull()
                            onConfirm(parsed)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                    ) {
                        Text(if (isBangla) "সংরক্ষণ" else "Save", color = appColors.onAccent)
                    }
                }
            }
        }
    }
}

@Composable
fun StallPickerDialog(
    itemName: String,
    currentStall: String,
    onDismiss: () -> Unit,
    onSelectStall: (String) -> Unit,
    isBangla: Boolean
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = appColors.surface,
            modifier = Modifier.width(320.dp).padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isBangla) "$itemName এর স্টল পরিবর্তন" else "Change Stall for $itemName",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                for (dept in ALL_DEPARTMENTS) {
                    val isSelected = dept.key == currentStall
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) appColors.accent.copy(alpha = 0.12f) else Color.Transparent)
                            .clickable { onSelectStall(dept.key) }
                            .padding(12.dp)
                    ) {
                        Text(text = dept.icon, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isBangla) dept.titleBn else dept.titleEn,
                            style = typography.bodyLarge.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                            color = appColors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = appColors.accent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnitPickerDialog(
    currentUnit: String,
    onDismiss: () -> Unit,
    onSelectUnit: (String) -> Unit,
    isBangla: Boolean
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val commonUnits = listOf("কেজি", "গ্রাম", "লিটার", "মিলি", "পিস", "ডজন", "হালি")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = appColors.surface,
            modifier = Modifier.width(300.dp).padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isBangla) "পরিমাপের একক নির্বাচন" else "Select Unit",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                for (u in commonUnits) {
                    val isSelected = u == currentUnit
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) appColors.accent.copy(alpha = 0.12f) else Color.Transparent)
                            .clickable { onSelectUnit(u) }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = u,
                            style = typography.bodyLarge.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                            color = appColors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = appColors.accent)
                        }
                    }
                }
            }
        }
    }
}
