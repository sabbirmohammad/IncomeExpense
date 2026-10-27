package com.example.expensetracker.ui.dashboard

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.util.Calendar

/**
 * Dedicated Section for Creating New Market Lists.
 * - Set custom title (e.g. সকালের কাঁচাবাজার, বিকালের মুদি বাজার).
 * - Choose between WhatsApp Bulk Paste OR Item-by-Item Entry.
 * - Live item counter and quick suggestions.
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
        listOf("🛒 সকালের কাঁচাবাজার", "🛍️ বিকালের মুদি সদাই", "🥩 মাছ-মাংসের বাজার", "📅 সাপ্তাহিক বাজার", "💊 ফার্মেসি ও অন্যান্য")
    } else {
        listOf("🛒 Morning Market", "🛍️ Evening Groceries", "🥩 Fish & Meat", "📅 Weekly Market", "💊 Pharmacy")
    }

    val quickStaples = if (isBangla) {
        listOf("আলু ১ কেজি", "পেঁয়াজ ১ কেজি", "ডিম ১ ডজন", "সয়াবিন তেল ১ লিটার", "কাঁচামরিচ ২৫০ গ্রাম", "চিনি ১ কেজি", "মুরগি ১ কেজি", "দুধ ১ লিটার", "রসুন ২৫০ গ্রাম", "আদা ২৫০ গ্রাম")
    } else {
        listOf("Potato 1kg", "Onion 1kg", "Eggs 12pcs", "Oil 1L", "Chili 250g", "Sugar 1kg", "Chicken 1kg", "Milk 1L", "Garlic 250g")
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. List Title Input
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isBangla) "ফর্দের নাম (কিসের বাজার):" else "List Title:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
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
                            color = appColors.textSecondary.copy(alpha = 0.5f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = LocalAppColors.current.border,
                        focusedContainerColor = LocalAppColors.current.surface,
                        unfocusedContainerColor = LocalAppColors.current.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick Title Suggestions Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    items(quickTitles) { titleOption ->
                        val cleanTitle = titleOption.substringAfter(" ").trim()
                        val isSelected = listTitle == cleanTitle
                        Surface(
                            onClick = {
                                haptics.lightTick()
                                listTitle = cleanTitle
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) appColors.accent.copy(alpha = 0.15f) else LocalAppColors.current.background,
                            border = BorderStroke(1.dp, if (isSelected) appColors.accent else LocalAppColors.current.border)
                        ) {
                            Text(
                                text = titleOption,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) appColors.accent else appColors.textSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Creation Method Toggle (Bulk Paste vs Item by Item)
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = LocalAppColors.current.background,
                border = BorderStroke(1.dp, LocalAppColors.current.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Manual Add Tab (Primary / Default)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (inputMode == 1) appColors.accent else Color.Transparent)
                            .clickable {
                                haptics.lightTick()
                                inputMode = 1
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ListAlt,
                                contentDescription = null,
                                tint = if (inputMode == 1) Color.White else appColors.textSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isBangla) "সরাসরি পণ্য লিখুন" else "Add Items",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = if (inputMode == 1) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (inputMode == 1) Color.White else appColors.textSecondary
                            )
                        }
                    }

                    // Bulk Paste Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (inputMode == 0) appColors.accent else Color.Transparent)
                            .clickable {
                                haptics.lightTick()
                                inputMode = 0
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Filled.ContentPaste,
                                contentDescription = null,
                                tint = if (inputMode == 0) Color.White else appColors.textSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isBangla) "হোয়াটসঅ্যাপ পেস্ট" else "Bulk Paste",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = if (inputMode == 0) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (inputMode == 0) Color.White else appColors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // 3. Mode Content
        if (inputMode == 0) {
            // Bulk Paste Mode
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isBangla) "হোয়াটসঅ্যাপ বা নোটবুক থেকে কপি করা ফর্দ এখানে পেস্ট করুন:"
                        else "Paste your copied market list below:",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = appColors.textSecondary
                    )

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
                            unfocusedBorderColor = LocalAppColors.current.border,
                            focusedContainerColor = LocalAppColors.current.background,
                            unfocusedContainerColor = LocalAppColors.current.background
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (parsedBulkItems.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF25D366).copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
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
                                color = Color(0xFF25D366),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Manual Add Mode
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    text = if (isBangla) "পণ্যের নাম (যেমন: আলু ২ কেজি)" else "Item (e.g. Potato 2kg)",
                                    fontSize = 12.sp,
                                    color = appColors.textSecondary.copy(alpha = 0.5f)
                                )
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { addManualItem(manualItemName) }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = appColors.accent,
                                unfocusedBorderColor = LocalAppColors.current.border,
                                focusedContainerColor = LocalAppColors.current.surface,
                                unfocusedContainerColor = LocalAppColors.current.surface
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Button(
                            onClick = { addManualItem(manualItemName) },
                            enabled = manualItemName.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        }
                    }

                    // Quick staples pills
                    Text(
                        text = if (isBangla) "দ্রুত যোগ করুন:" else "Quick suggestions:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
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
                                color = LocalAppColors.current.background,
                                border = BorderStroke(1.dp, LocalAppColors.current.border),
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

                    // Added items pills
                    if (manualItems.value.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "যুক্ত করা পণ্যসমূহ (${convertToBengaliDigits(manualItems.value.size.toString())}টি):"
                            else "Added Items (${manualItems.value.size}):",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
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
                    }
                }
            }
        }

        // 4. Save and Start Market List Button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (effectiveItems.isNotEmpty()) {
                        haptics.mediumClick()
                        val finalTitle = listTitle.trim().ifBlank { defaultInitialTitle }
                        onListCreated(finalTitle, effectiveItems)
                        bulkInputText = ""
                        manualItems.value = mutableListOf()
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

        // Bottom nav bar clearance
        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}
