package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.util.Calendar

/**
 * Stepped workflow states for creating a new market list.
 */
enum class CreateMarketListStep {
    TITLE,      // Step 1: ফর্দের নাম ও দ্রুত সাজেশন
    METHOD,     // Legacy step for compatibility
    ITEMS       // Step 2: স্মার্ট পণ্য তালিকা (একক এন্ট্রি ও অটো-ডিটেকটিং বাল্ক পেস্ট)
}

/**
 * Dedicated Section for Creating New Market Lists.
 * - 2-Step Modern Streamlined Wizard (Title -> Smart Items Omnibox).
 * - Time-aware smart title suggestions (সকালের কাঁচাবাজার, বিকালের মুদি সদাই).
 * - Unified Smart Omnibox combining single-item entry with instant WhatsApp multi-line bulk auto-parsing.
 * - Live item counter, quick quantity presets, and staple suggestions.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateMarketListSection(
    isBangla: Boolean,
    onListCreated: (title: String, items: List<MarketItem>) -> Unit,
    modifier: Modifier = Modifier,
    priceInsights: Map<String, ProductPriceInsight> = emptyMap(),
    currencySymbol: String = "৳"
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current

    var clipboardText by remember { mutableStateOf("") }
    var isClipboardDismissed by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        try {
            clipboardText = clipboardManager.getText()?.text ?: ""
        } catch (_: Exception) {}
    }
    val hasClipboardText = remember(clipboardText) {
        clipboardText.isNotBlank() && clipboardText.length > 5
    }

    // Determine smart default title based on time of day
    val defaultInitialTitle = remember(isBangla) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour in 5..11 -> if (isBangla) "সকালের কাঁচাবাজার" else "Morning Bazaar"
            hour in 12..16 -> if (isBangla) "দুপুরের বাজার" else "Afternoon Market"
            hour in 17..21 -> if (isBangla) "বিকালের মুদি সদাই" else "Evening Groceries"
            else -> if (isBangla) "বাজারের ফর্দ" else "Grocery List"
        }
    }

    var currentStep by remember { mutableStateOf(CreateMarketListStep.TITLE) }
    var listTitle by remember { mutableStateOf(defaultInitialTitle) }
    var inputMode by remember { mutableIntStateOf(1) } // 1: Add Items Manually, 0: Bulk Paste
    var lastBulkAddedCount by remember { mutableIntStateOf(0) }

    // Bulk paste state
    var bulkInputText by remember { mutableStateOf("") }
    val parsedBulkItems = remember(bulkInputText) {
        if (bulkInputText.isNotBlank()) MarketListParser.parseBulkText(bulkInputText) else emptyList()
    }

    // Manual items state
    var manualItemName by remember { mutableStateOf("") }
    val manualItems = remember { mutableStateOf(mutableListOf<MarketItem>()) }

    val quickTitles = if (isBangla) {
        listOf(
            "🛒 সকালের কাঁচাবাজার",
            "🛍️ বিকালের মুদি সদাই",
            "🥩 মাছ-মাংসের বাজার",
            "📅 সাপ্তাহিক বাজার",
            "💊 ফার্মেসি ও অন্যান্য",
            "🍳 সকালের নাস্তা"
        )
    } else {
        listOf(
            "🛒 Morning Market",
            "🛍️ Evening Groceries",
            "🥩 Fish & Meat",
            "📅 Weekly Market",
            "💊 Pharmacy",
            "🍳 Breakfast & Groceries"
        )
    }

    val quickStaples = if (isBangla) {
        listOf(
            "আলু ১ কেজি",
            "পেঁয়াজ ১ কেজি",
            "ডিম ১ ডজন",
            "সয়াবিন তেল ১ লিটার",
            "কাঁচামরিচ ২৫০ গ্রাম",
            "চিনি ১ কেজি",
            "মুরগি ১ কেজি",
            "দুধ ১ লিটার",
            "রসুন ২৫০ গ্রাম",
            "আদা ২৫০ গ্রাম"
        )
    } else {
        listOf(
            "Potato 1kg",
            "Onion 1kg",
            "Eggs 12pcs",
            "Oil 1L",
            "Chili 250g",
            "Sugar 1kg",
            "Chicken 1kg",
            "Milk 1L",
            "Garlic 250g"
        )
    }

    val quickQuantities = if (isBangla) {
        listOf("১ কেজি", "২ কেজি", "৫০০ গ্রাম", "২৫০ গ্রাম", "১ ডজন", "১ লিটার", "১ প্যাকেট", "১ হালি")
    } else {
        listOf("1kg", "2kg", "500g", "250g", "12pcs", "1L", "1 pack")
    }

    // Universal omnibox parsing supporting single item, comma separated, or multi-line bulk paste
    val addManualItem = { rawText: String ->
        if (rawText.isNotBlank()) {
            haptics.lightTick()
            val parsedList = MarketListParser.parseBulkText(rawText)
            val itemsToAdd = if (parsedList.isNotEmpty()) {
                parsedList
            } else {
                val clean = rawText.trim()
                val parsed = MarketListParser.parseSingleLine(clean)
                listOf(parsed ?: MarketItem(name = clean, quantity = 1.0, unit = if (isBangla) "কেজি" else "kg"))
            }
            manualItems.value = (manualItems.value + itemsToAdd).toMutableList()
            manualItemName = ""
            if (itemsToAdd.size > 1) {
                lastBulkAddedCount = itemsToAdd.size
            }
        }
    }

    val effectiveItems = manualItems.value

    val liveEstimatedBudget = remember(effectiveItems, priceInsights) {
        effectiveItems.sumOf { item ->
            val matchingInsight = UnitConverterEngine.findMatchingInsight(item.name, priceInsights)
            if (matchingInsight != null) {
                UnitConverterEngine.estimateCost(matchingInsight.currentUnitPrice, item.quantity, item.unit)
            } else 0.0
        }
    }
    val estimatedItemsCount = remember(effectiveItems, priceInsights) {
        effectiveItems.count { item ->
            val matchingInsight = UnitConverterEngine.findMatchingInsight(item.name, priceInsights)
            matchingInsight != null && UnitConverterEngine.estimateCost(matchingInsight.currentUnitPrice, item.quantity, item.unit) > 0.0
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // -------------------------------------------------------------
        // Stepper Header & Progress Indicator
        // -------------------------------------------------------------
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep != CreateMarketListStep.TITLE) {
                        // STEP 2 CONSOLIDATED TOP BAR: Back Navigation + Market Title & Edit + Live Item Counter Badge
                        Surface(
                            onClick = {
                                haptics.lightTick()
                                currentStep = CreateMarketListStep.TITLE
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = appColors.surface,
                            border = BorderStroke(1.dp, appColors.border)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Back",
                                    tint = appColors.textPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = if (isBangla) "পেছনে" else "Back",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = appColors.textPrimary
                                )
                            }
                        }

                        // Middle: Current List Title with Quick Edit Indicator
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    haptics.lightTick()
                                    currentStep = CreateMarketListStep.TITLE
                                },
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = listTitle.ifBlank { defaultInitialTitle },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    ),
                                    color = appColors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit title",
                                    tint = appColors.accent,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = if (isBangla) "ধাপ ২/২ • পণ্যের তালিকা" else "Step 2/2 • Market Items",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = appColors.textSecondary
                            )
                        }

                        // Right: Live Item Counter Badge
                        val totalCount = manualItems.value.size
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (totalCount > 0) appColors.accent.copy(alpha = 0.14f) else appColors.surface,
                            border = BorderStroke(
                                1.dp,
                                if (totalCount > 0) appColors.accent.copy(alpha = 0.4f) else appColors.border
                            )
                        ) {
                            Text(
                                text = if (isBangla) {
                                    if (totalCount > 0) "${convertToBengaliDigits(totalCount.toString())}টি পণ্য যুক্ত"
                                    else "০টি পণ্য"
                                } else {
                                    if (totalCount > 0) "$totalCount items"
                                    else "0 items"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (totalCount > 0) appColors.accent else appColors.textSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    } else {
                        // STEP 1 TOP BAR
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(appColors.accent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBangla) "১" else "1",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = appColors.accent
                                )
                            }
                            Text(
                                text = if (isBangla) "নতুন ফর্দ তৈরি" else "Create New List",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = appColors.textPrimary
                            )
                        }

                        // Step Indicator Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = appColors.accent.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (isBangla) "ধাপ ১/২: নাম" else "Step 1/2: Name",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = appColors.accent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Smooth Segmented Progress Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(appColors.border.copy(alpha = 0.4f))
                ) {
                    val progressFraction = when (currentStep) {
                        CreateMarketListStep.TITLE -> 0.5f
                        else -> 1f
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .fillMaxHeight()
                            .background(appColors.accent)
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // Step 1: TITLE SELECTION
        // -------------------------------------------------------------
        if (currentStep == CreateMarketListStep.TITLE) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = appColors.surface,
                    border = BorderStroke(1.dp, appColors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(appColors.accent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null,
                                    tint = appColors.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isBangla) "ফর্দের নাম নির্ধারণ করুন" else "Name your shopping list",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = appColors.textPrimary
                                )
                                Text(
                                    text = if (isBangla) "নিচের সাজেশন থেকে বেছে নিন অথবা কাস্টম নাম লিখুন:"
                                    else "Pick from quick suggestions or type custom title:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = appColors.textSecondary
                                )
                            }
                        }

                        // Title Text Field
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isBangla) "ফর্দের নাম (কিসের বাজার):" else "List Title:",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = appColors.textPrimary
                            )

                            OutlinedTextField(
                                value = listTitle,
                                onValueChange = { listTitle = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = {
                                    Text(
                                        text = if (isBangla) "যেমন: সকালের কাঁচাবাজার" else "e.g. Morning Market",
                                        color = appColors.textSecondary.copy(alpha = 0.5f),
                                        fontSize = 13.sp
                                    )
                                },
                                trailingIcon = {
                                    if (listTitle.isNotBlank()) {
                                        IconButton(onClick = { listTitle = "" }) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Clear",
                                                tint = appColors.textSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = {
                                    if (listTitle.isNotBlank()) {
                                        haptics.mediumClick()
                                        currentStep = CreateMarketListStep.ITEMS
                                    }
                                }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = appColors.accent,
                                    unfocusedBorderColor = appColors.border,
                                    focusedContainerColor = appColors.background,
                                    unfocusedContainerColor = appColors.background
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Suggestion Chips
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isBangla) "দ্রুত বাছাই করুন:" else "Suggested titles:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = appColors.textSecondary
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                quickTitles.forEach { titleOption ->
                                    val cleanTitle = titleOption.substringAfter(" ").trim()
                                    val isSelected = listTitle.trim() == cleanTitle

                                    Surface(
                                        onClick = {
                                            haptics.lightTick()
                                            listTitle = cleanTitle
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) appColors.accent.copy(alpha = 0.15f) else appColors.background,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) appColors.accent else appColors.border
                                        )
                                    ) {
                                        Text(
                                            text = titleOption,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (isSelected) appColors.accent else appColors.textSecondary,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Next Button to Step 2
                        Button(
                            onClick = {
                                if (listTitle.isNotBlank()) {
                                    haptics.mediumClick()
                                    currentStep = CreateMarketListStep.ITEMS
                                }
                            },
                            enabled = listTitle.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = appColors.accent,
                                disabledContainerColor = appColors.accent.copy(alpha = 0.35f)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (isBangla) "পরবর্তী: পণ্য যোগ করুন" else "Next: Add Items",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // Step 2: ITEMS ENTRY (Smart Omnibox with Auto Bulk Paste Detection)
        // -------------------------------------------------------------
        if (currentStep == CreateMarketListStep.ITEMS) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Clipboard Quick Suggestion Banner (Dismissible 1-tap bulk paste)
                    if (hasClipboardText && !isClipboardDismissed) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF25D366).copy(alpha = 0.09f),
                            border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            haptics.lightTick()
                                            addManualItem(clipboardText)
                                            clipboardText = ""
                                            isClipboardDismissed = true
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF25D366).copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ContentPaste,
                                            contentDescription = null,
                                            tint = Color(0xFF25D366),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = if (isBangla) "ক্লিপবোর্ডে ফর্দ শনাক্ত হয়েছে" else "List detected on clipboard",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = appColors.textPrimary
                                        )
                                        Text(
                                            text = if (isBangla) "এক ট্যাপে সবগুলো পণ্য স্বয়ংক্রিয়ভাবে যোগ করুন"
                                            else "Tap to auto-parse and add all items",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = appColors.textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        onClick = {
                                            haptics.lightTick()
                                            addManualItem(clipboardText)
                                            clipboardText = ""
                                            isClipboardDismissed = true
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF25D366)
                                    ) {
                                        Text(
                                            text = if (isBangla) "যোগ করুন" else "Paste",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            haptics.lightTick()
                                            isClipboardDismissed = true
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Dismiss",
                                            tint = appColors.textSecondary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bulk Addition Feedback Banner
                    if (lastBulkAddedCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF25D366).copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF25D366),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isBangla)
                                            "✓ ${convertToBengaliDigits(lastBulkAddedCount.toString())}টি পণ্য সফলভাবে তালিকায় যোগ করা হয়েছে"
                                        else
                                            "✓ $lastBulkAddedCount items successfully added to list",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFF25D366)
                                    )
                                }
                                IconButton(
                                    onClick = { lastBulkAddedCount = 0 },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color(0xFF25D366),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 1. Added Items Section (Chat-Style Stream - Placed ABOVE the writing box)
                    if (manualItems.value.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = appColors.surface,
                            border = BorderStroke(1.dp, appColors.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.ListAlt,
                                            contentDescription = null,
                                            tint = appColors.accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (isBangla) "যুক্ত করা পণ্যসমূহ" else "Added Items",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = appColors.textPrimary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = appColors.accent.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = if (isBangla)
                                                    "${convertToBengaliDigits(manualItems.value.size.toString())}টি"
                                                else
                                                    "${manualItems.value.size}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = appColors.accent,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    if (manualItems.value.size >= 2) {
                                        Text(
                                            text = if (isBangla) "সব মুছুন" else "Clear All",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = appColors.expense,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .clickable {
                                                    haptics.lightTick()
                                                    manualItems.value = mutableListOf()
                                                }
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    manualItems.value.forEachIndexed { idx, itm ->
                                        val isLatestItem = idx == manualItems.value.size - 1
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isLatestItem) appColors.accent.copy(alpha = 0.10f) else appColors.background,
                                            border = BorderStroke(
                                                1.dp,
                                                if (isLatestItem) appColors.accent.copy(alpha = 0.45f) else appColors.border
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    // Serial number badge
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = if (isLatestItem) appColors.accent else appColors.surface,
                                                        border = BorderStroke(
                                                            1.dp,
                                                            if (isLatestItem) appColors.accent else appColors.border
                                                        )
                                                    ) {
                                                        Text(
                                                            text = if (isBangla) convertToBengaliDigits((idx + 1).toString()) else "${idx + 1}",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 11.sp
                                                            ),
                                                            color = if (isLatestItem) Color.White else appColors.textSecondary,
                                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                        )
                                                    }

                                                    // Product Name
                                                    Text(
                                                        text = itm.name,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontSize = 13.5.sp,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        color = appColors.textPrimary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    // Quantity Badge
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = appColors.accent.copy(alpha = 0.16f)
                                                    ) {
                                                        Text(
                                                            text = itm.formatQuantity(),
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold
                                                            ),
                                                            color = appColors.accent,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                        )
                                                    }

                                                    // Delete Button
                                                    IconButton(
                                                        onClick = {
                                                            haptics.lightTick()
                                                            val updated = manualItems.value.toMutableList()
                                                            updated.removeAt(idx)
                                                            manualItems.value = updated
                                                        },
                                                        modifier = Modifier.size(26.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Close,
                                                            contentDescription = "Remove",
                                                            tint = appColors.expense,
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = appColors.surface,
                            border = BorderStroke(1.dp, appColors.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingCart,
                                    contentDescription = null,
                                    tint = appColors.textSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = if (isBangla) "ফর্দে কোনো পণ্য যুক্ত করা হয়নি" else "No items added yet",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = appColors.textPrimary
                                )
                                Text(
                                    text = if (isBangla)
                                        "নিচের বক্সে লিখুন বা দ্রুত জনপ্রিয় সাজেশন থেকে ট্যাপ করুন"
                                    else
                                        "Type in the box below or tap popular suggestions",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = appColors.textSecondary.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // 2. Quick Staples Pills (Popular Suggestions - Placed below Added Items)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = null,
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isBangla) "দ্রুত যোগ করুন (জনপ্রিয় পণ্য):" else "Quick suggestions:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = appColors.textSecondary
                            )
                        }

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickStaples.forEach { staple ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = appColors.background,
                                    border = BorderStroke(1.dp, appColors.border),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { addManualItem(staple) }
                                ) {
                                    Text(
                                        text = "+ $staple",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = appColors.accent,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 3. Quick Quantity Presets Row (Attached directly above the writing box)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (isBangla) "পরিমাণ যোগ করুন:" else "Add quantity:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.textSecondary
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            items(quickQuantities) { qty ->
                                Surface(
                                    onClick = {
                                        haptics.lightTick()
                                        if (manualItemName.isNotBlank()) {
                                            addManualItem("${manualItemName.trim()} $qty")
                                        } else {
                                            manualItemName = qty
                                        }
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    color = appColors.accent.copy(alpha = 0.08f),
                                    border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.25f))
                                ) {
                                    Text(
                                        text = "+ $qty",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = appColors.accent,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 4. Input Composer Row (Writing Box + Add Button - Modern Redesigned Dock)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = manualItemName,
                            onValueChange = { newText ->
                                if (newText.contains("\n") || newText.contains("\r")) {
                                    addManualItem(newText)
                                } else {
                                    manualItemName = newText
                                }
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            placeholder = {
                                Text(
                                    text = if (isBangla) "পণ্যের নাম ও পরিমাণ লিখুন..."
                                    else "Type item & quantity...",
                                    fontSize = 12.sp,
                                    color = appColors.textSecondary.copy(alpha = 0.5f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingCart,
                                    contentDescription = null,
                                    tint = if (manualItemName.isNotBlank()) appColors.accent else appColors.textSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            trailingIcon = {
                                if (manualItemName.isNotBlank()) {
                                    IconButton(onClick = { manualItemName = "" }) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Clear",
                                            tint = appColors.textSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { addManualItem(manualItemName) }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = appColors.accent,
                                unfocusedBorderColor = appColors.border,
                                focusedContainerColor = appColors.surface,
                                unfocusedContainerColor = appColors.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = { addManualItem(manualItemName) },
                            enabled = manualItemName.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(50.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = appColors.accent,
                                disabledContainerColor = appColors.accent.copy(alpha = 0.35f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Live Budget Estimator Card
            if (effectiveItems.isNotEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = appColors.accent.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "সম্ভাব্য মোট বাজেট" else "Estimated Budget",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = appColors.textPrimary
                                    )
                                    if (estimatedItemsCount > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = appColors.accent.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = if (isBangla)
                                                    "${convertToBengaliDigits(estimatedItemsCount.toString())}/${convertToBengaliDigits(effectiveItems.size.toString())} দর পাওয়া গেছে"
                                                else
                                                    "$estimatedItemsCount/${effectiveItems.size} priced",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = appColors.accent,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = if (liveEstimatedBudget > 0.0) {
                                        if (isBangla) "পূর্বের বাজার দরের হিসাব অনুযায়ী" else "Based on historical commodity prices"
                                    } else {
                                        if (isBangla) "দর তথ্য পাওয়া গেলে স্বয়ংক্রিয় প্রাক্কলন হবে" else "Matches with commodity price database"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = appColors.textSecondary
                                )
                            }

                            Text(
                                text = if (liveEstimatedBudget > 0.0) {
                                    formatCurrency(liveEstimatedBudget, currencySymbol)
                                } else {
                                    "—"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = if (liveEstimatedBudget > 0.0) appColors.accent else appColors.textSecondary
                            )
                        }
                    }
                }
            }

            // Save and Start Market List Button (Step 2 Action)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        if (effectiveItems.isNotEmpty()) {
                            haptics.mediumClick()
                            val finalTitle = listTitle.trim().ifBlank { defaultInitialTitle }
                            onListCreated(finalTitle, effectiveItems)
                            bulkInputText = ""
                            manualItems.value = mutableListOf()
                            manualItemName = ""
                            lastBulkAddedCount = 0
                            currentStep = CreateMarketListStep.TITLE
                        }
                    },
                    enabled = effectiveItems.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.accent,
                        disabledContainerColor = appColors.accent.copy(alpha = 0.35f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isBangla)
                                "ফর্দ তৈরি করুন ও শুরু করুন (${convertToBengaliDigits(effectiveItems.size.toString())}টি পণ্য)"
                            else
                                "Create & Start List (${effectiveItems.size} items)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Bottom nav bar clearance
        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}
