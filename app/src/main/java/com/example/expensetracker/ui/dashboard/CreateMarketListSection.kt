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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.util.Calendar

/**
 * Stepped workflow states for creating a new market list.
 */
enum class CreateMarketListStep {
    TITLE,      // Step 1: ফর্দের নাম ও দ্রুত সাজেশন
    METHOD,     // Step 2: মাধ্যম নির্বাচন (সরাসরি লিখবেন নাকি হোয়াটসঅ্যাপ পেস্ট)
    ITEMS       // Step 3: সরাসরি পণ্য এন্ট্রি অথবা হোয়াটসঅ্যাপ পেস্ট
}

/**
 * Dedicated Section for Creating New Market Lists.
 * - Step-by-Step Modern Guided Wizard (Title -> Method -> Items).
 * - Time-aware smart title suggestions (সকালের কাঁচাবাজার, বিকালের মুদি সদাই).
 * - Interactive method selector cards (Direct Manual Entry vs WhatsApp Bulk Paste).
 * - Live item counter, quick quantity presets, and staple suggestions.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateMarketListSection(
    isBangla: Boolean,
    onListCreated: (title: String, items: List<MarketItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current

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

    val addManualItem = { rawText: String ->
        if (rawText.isNotBlank()) {
            haptics.lightTick()
            val parsed = MarketListParser.parseSingleLine(rawText.trim())
            val newItem = if (parsed != null) {
                MarketItem(name = parsed.name, quantity = parsed.quantity, unit = parsed.unit)
            } else {
                MarketItem(name = rawText.trim(), quantity = 1.0, unit = if (isBangla) "কেজি" else "kg")
            }
            manualItems.value = (manualItems.value + newItem).toMutableList()
            manualItemName = ""
        }
    }

    val effectiveItems = if (inputMode == 0) parsedBulkItems else manualItems.value

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
                        Surface(
                            onClick = {
                                haptics.lightTick()
                                currentStep = when (currentStep) {
                                    CreateMarketListStep.ITEMS -> CreateMarketListStep.METHOD
                                    CreateMarketListStep.METHOD -> CreateMarketListStep.TITLE
                                    CreateMarketListStep.TITLE -> CreateMarketListStep.TITLE
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = appColors.surface,
                            border = BorderStroke(1.dp, appColors.border)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
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
                    } else {
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
                    }

                    // Step Indicator Pill
                    val stepNumber = when (currentStep) {
                        CreateMarketListStep.TITLE -> 1
                        CreateMarketListStep.METHOD -> 2
                        CreateMarketListStep.ITEMS -> 3
                    }
                    val stepLabel = when (currentStep) {
                        CreateMarketListStep.TITLE -> if (isBangla) "ধাপ ১/৩: নাম" else "Step 1/3: Name"
                        CreateMarketListStep.METHOD -> if (isBangla) "ধাপ ২/৩: মাধ্যম" else "Step 2/3: Method"
                        CreateMarketListStep.ITEMS -> if (isBangla) "ধাপ ৩/৩: পণ্য" else "Step 3/3: Items"
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = appColors.accent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = stepLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = appColors.accent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
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
                        CreateMarketListStep.TITLE -> 0.33f
                        CreateMarketListStep.METHOD -> 0.66f
                        CreateMarketListStep.ITEMS -> 1f
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
                                        currentStep = CreateMarketListStep.METHOD
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
                                    currentStep = CreateMarketListStep.METHOD
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
                                    text = if (isBangla) "পরবর্তী ধাপ: মাধ্যম নির্বাচন" else "Next: Choose Method",
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
        // Step 2: METHOD CHOICE (Direct Entry vs WhatsApp Bulk Paste)
        // -------------------------------------------------------------
        if (currentStep == CreateMarketListStep.METHOD) {
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
                        // Current List Title Banner with Edit Shortcut
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = appColors.background,
                            border = BorderStroke(1.dp, appColors.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ShoppingCart,
                                        contentDescription = null,
                                        tint = appColors.accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (isBangla) "ফর্দের নাম" else "List Title",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = appColors.textSecondary
                                        )
                                        Text(
                                            text = listTitle.ifBlank { defaultInitialTitle },
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp
                                            ),
                                            color = appColors.textPrimary
                                        )
                                    }
                                }

                                TextButton(
                                    onClick = {
                                        haptics.lightTick()
                                        currentStep = CreateMarketListStep.TITLE
                                    }
                                ) {
                                    Text(
                                        text = if (isBangla) "নাম পরিবর্তন" else "Change",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = appColors.accent
                                    )
                                }
                            }
                        }

                        // Title & Subtitle for Method Choice
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = if (isBangla) "ফর্দ তৈরির মাধ্যম বেছে নিন" else "Choose How to Build List",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = appColors.textPrimary
                            )
                            Text(
                                text = if (isBangla) "নিচের যেকোনো একটি উপায় নির্বাচন করুন:"
                                else "Select your preferred entry method:",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = appColors.textSecondary
                            )
                        }

                        // Option 1: Direct Manual Entry Card
                        Surface(
                            onClick = {
                                haptics.mediumClick()
                                inputMode = 1
                                currentStep = CreateMarketListStep.ITEMS
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = appColors.background,
                            border = BorderStroke(1.5.dp, appColors.accent.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(appColors.accent.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ListAlt,
                                        contentDescription = null,
                                        tint = appColors.accent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = if (isBangla) "সরাসরি পণ্য লিখুন" else "Add Items Manually",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = appColors.textPrimary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = appColors.accent.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = if (isBangla) "জনপ্রিয়" else "Popular",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = appColors.accent,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isBangla) "একটি একটি করে নাম ও পরিমাণ লিখুন বা কুইক শর্টকাট থেকে সহজে যোগ করুন"
                                        else "Type items one by one or tap quick shortcut staples",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = appColors.textSecondary
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                    contentDescription = null,
                                    tint = appColors.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Option 2: WhatsApp Bulk Paste Card
                        val clipboardText = remember { clipboardManager.getText()?.text ?: "" }
                        val hasClipboardText = remember(clipboardText) {
                            clipboardText.isNotBlank() && clipboardText.length > 5
                        }

                        Surface(
                            onClick = {
                                haptics.mediumClick()
                                inputMode = 0
                                if (bulkInputText.isBlank() && hasClipboardText) {
                                    bulkInputText = clipboardText
                                }
                                currentStep = CreateMarketListStep.ITEMS
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF25D366).copy(alpha = 0.06f),
                            border = BorderStroke(1.5.dp, Color(0xFF25D366).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF25D366).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ContentPaste,
                                            contentDescription = null,
                                            tint = Color(0xFF25D366),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isBangla) "হোয়াটসঅ্যাপ পেস্ট" else "Bulk Paste",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = appColors.textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isBangla) "হোয়াটসঅ্যাপ বা নোটবুক থেকে কপি করা ফর্দ এক ক্লিকে পেস্ট করুন"
                                            else "Paste WhatsApp message or checklist to auto-parse",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                            color = appColors.textSecondary
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF25D366),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                if (hasClipboardText) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF25D366).copy(alpha = 0.15f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF25D366),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = if (isBangla) "✓ ক্লিপবোর্ডে কপি করা ফর্দ রয়েছে! ট্যাপ করলেই পেস্ট হবে"
                                                else "✓ Copied text detected on clipboard! Tap to auto-paste",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = Color(0xFF25D366)
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

        // -------------------------------------------------------------
        // Step 3: ITEMS ENTRY (Direct Manual Entry OR Bulk Paste)
        // -------------------------------------------------------------
        if (currentStep == CreateMarketListStep.ITEMS) {
            // Summary Header with Mode Switching Pill
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.surface,
                    border = BorderStroke(1.dp, appColors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = listTitle.ifBlank { defaultInitialTitle },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = appColors.textPrimary
                            )
                        }

                        // Toggle Mode Button
                        Surface(
                            onClick = {
                                haptics.lightTick()
                                inputMode = if (inputMode == 1) 0 else 1
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = appColors.accent.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (inputMode == 1) Icons.Filled.ContentPaste else Icons.AutoMirrored.Outlined.ListAlt,
                                    contentDescription = null,
                                    tint = appColors.accent,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = if (inputMode == 1) {
                                        if (isBangla) "পেস্ট মোডে যান" else "Switch to Paste"
                                    } else {
                                        if (isBangla) "সরাসরি লিখুন" else "Switch to Manual"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = appColors.accent
                                )
                            }
                        }
                    }
                }
            }

            // Mode Content: Bulk Paste (inputMode == 0)
            if (inputMode == 0) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "হোয়াটসঅ্যাপ বা নোটবুক থেকে কপি করা ফর্দ এখানে পেস্ট করুন:"
                                else "Paste your copied market list below:",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = appColors.textSecondary,
                                modifier = Modifier.weight(1f)
                            )

                            // Quick Clipboard Paste Button
                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    val clip = clipboardManager.getText()?.text
                                    if (!clip.isNullOrBlank()) {
                                        bulkInputText = clip
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF25D366).copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ContentPaste,
                                        contentDescription = null,
                                        tint = Color(0xFF25D366),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (isBangla) "পেস্ট করুন" else "Paste",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFF25D366)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = bulkInputText,
                            onValueChange = { bulkInputText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            placeholder = {
                                Text(
                                    text = if (isBangla)
                                        "যেমন:\n১. আলু ২ কেজি\n২. পেঁয়াজ ১ কেজি\n৩. সয়াবিন তেল ২ লিটার\n৪. ডিম ১ ডজন"
                                    else
                                        "e.g.\nPotato 2kg\nOnion 1kg\nOil 2L\nEggs 12pcs",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = appColors.textSecondary.copy(alpha = 0.5f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF25D366),
                                unfocusedBorderColor = appColors.border,
                                focusedContainerColor = appColors.background,
                                unfocusedContainerColor = appColors.background
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (parsedBulkItems.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF25D366).copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isBangla)
                                            "✓ ${convertToBengaliDigits(parsedBulkItems.size.toString())}টি পণ্য সফলভাবে শনাক্ত করা হয়েছে"
                                        else
                                            "✓ ${parsedBulkItems.size} items detected successfully",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFF25D366)
                                    )

                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        parsedBulkItems.take(10).forEach { item ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF25D366).copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "${item.name} (${item.formatQuantity()})",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = appColors.textPrimary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                        if (parsedBulkItems.size > 10) {
                                            Text(
                                                text = if (isBangla)
                                                    "+ আরও ${convertToBengaliDigits((parsedBulkItems.size - 10).toString())}টি..."
                                                else
                                                    "+ ${parsedBulkItems.size - 10} more...",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = 10.5.sp,
                                                    color = appColors.textSecondary
                                                ),
                                                modifier = Modifier.padding(vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Mode Content: Manual Entry (inputMode == 1)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Input Row with Add Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = manualItemName,
                                onValueChange = { manualItemName = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                placeholder = {
                                    Text(
                                        text = if (isBangla) "পণ্যের নাম ও পরিমাণ (যেমন: আলু ২ কেজি)"
                                        else "Item & qty (e.g. Potato 2kg)",
                                        fontSize = 12.sp,
                                        color = appColors.textSecondary.copy(alpha = 0.5f)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { addManualItem(manualItemName) }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = appColors.accent,
                                    unfocusedBorderColor = appColors.border,
                                    focusedContainerColor = appColors.surface,
                                    unfocusedContainerColor = appColors.surface
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Button(
                                onClick = { addManualItem(manualItemName) },
                                enabled = manualItemName.isNotBlank(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Quick Quantity Presets Row
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

                        // Quick Staples Pills
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isBangla) "দ্রুত যোগ করুন (জনপ্রিয় পণ্য):" else "Quick suggestions:",
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
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                            color = appColors.accent,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Added Items Pills
                        if (manualItems.value.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) "যুক্ত করা পণ্যসমূহ (${convertToBengaliDigits(manualItems.value.size.toString())}টি):"
                                else "Added Items (${manualItems.value.size}):",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = appColors.textPrimary
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                manualItems.value.forEachIndexed { idx, itm ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = appColors.accent.copy(alpha = 0.1f),
                                        border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${itm.name} (${itm.formatQuantity()})",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = appColors.textPrimary
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        haptics.lightTick()
                                                        val updated = manualItems.value.toMutableList()
                                                        updated.removeAt(idx)
                                                        manualItems.value = updated
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Remove",
                                                    tint = appColors.expense,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = appColors.background,
                                border = BorderStroke(1.dp, appColors.border),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isBangla)
                                        "এখনো কোনো পণ্য যুক্ত করা হয়নি। উপরের বক্সে লিখুন বা দ্রুত সাজেশনে চাপ দিন।"
                                    else
                                        "No items added yet. Type above or pick quick suggestions.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = appColors.textSecondary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Save and Start Market List Button (Step 3 Action)
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
                            currentStep = CreateMarketListStep.TITLE
                        }
                    },
                    enabled = effectiveItems.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (inputMode == 0) Color(0xFF25D366) else appColors.accent,
                        disabledContainerColor = (if (inputMode == 0) Color(0xFF25D366) else appColors.accent).copy(alpha = 0.35f)
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
