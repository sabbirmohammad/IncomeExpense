package com.example.expensetracker.ui.bazaar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.MaterialTheme
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.adaptive.LocalWindowClass
import com.example.expensetracker.ui.adaptive.WindowWidthClass
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PulseTabularText
import com.example.expensetracker.ui.components.PulseText
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.getDepartmentMeta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazaarPasteSheet(
    onDismiss: () -> Unit,
    onCreateList: (title: String, items: List<MarketItem>) -> Unit,
    masterProductsMap: Map<String, MasterProduct>,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false
) {
    val windowClass = LocalWindowClass.current
    val isDialog = !windowClass.widthClass.isCompact

    if (isDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = LocalAppColors.current.surface,
                modifier = Modifier
                    .width(560.dp)
                    .fillMaxHeight(0.85f)
            ) {
                BazaarPasteContent(
                    onDismiss = onDismiss,
                    onCreateList = onCreateList,
                    masterProductsMap = masterProductsMap,
                    isBangla = isBangla,
                    useBengaliNumerals = useBengaliNumerals
                )
            }
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = LocalAppColors.current.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            modifier = modifier.imePadding()
        ) {
            BazaarPasteContent(
                onDismiss = onDismiss,
                onCreateList = onCreateList,
                masterProductsMap = masterProductsMap,
                isBangla = isBangla,
                useBengaliNumerals = useBengaliNumerals
            )
        }
    }
}

@Composable
fun BazaarPasteContent(
    onDismiss: () -> Unit,
    onCreateList: (title: String, items: List<MarketItem>) -> Unit,
    masterProductsMap: Map<String, MasterProduct>,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val strings = LocalAppStrings.current
    val clipboardManager = LocalClipboardManager.current
    val haptics = rememberAppHaptics()

    var rawInputText by remember { mutableStateOf("") }
    var isPreviewMode by remember { mutableStateOf(false) }

    var previewItems by remember { mutableStateOf<List<ParsedPreviewItem>>(emptyList()) }
    var flaggedLines by remember { mutableStateOf<List<FlaggedLine>>(emptyList()) }
    var listTitle by remember { mutableStateOf(if (isBangla) "বাজারের ফর্দ" else "Market List") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isPreviewMode) {
                    IconButton(
                        onClick = { isPreviewMode = false },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = if (isBangla) "ফিরে যান" else "Back",
                            tint = appColors.textPrimary
                        )
                    }
                }
                Text(
                    text = if (isPreviewMode) {
                        if (isBangla) "ফর্দ প্রিভিউ" else "List Preview"
                    } else {
                        if (isBangla) "লিস্ট পেস্ট করুন" else "Paste List"
                    },
                    style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = if (isBangla) "বন্ধ করুন" else "Close",
                    tint = appColors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (!isPreviewMode) {
            // STEP 1: Input text mode
            // Explicit button to read from clipboard
            Button(
                onClick = {
                    haptics.lightTick()
                    val clipText = clipboardManager.getText()?.text ?: ""
                    if (clipText.isNotBlank()) {
                        rawInputText = clipText
                        val result = BazaarDomainLogic.parseTextForPastePreview(clipText, masterProductsMap)
                        previewItems = result.validItems
                        flaggedLines = result.flaggedLines
                        if (result.validItems.isNotEmpty() || result.flaggedLines.isNotEmpty()) {
                            isPreviewMode = true
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.accent,
                    contentColor = appColors.onAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = if (isBangla) "ক্লিপবোর্ড থেকে নিন" else "Read from clipboard"
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.bazaarClipboardRead,
                    style = typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isBangla) "অথবা নিচে সরাসরি পেস্ট বা টাইপ করুন:" else "Or paste/type directly below:",
                style = typography.bodyMedium,
                color = appColors.textSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = rawInputText,
                onValueChange = { rawInputText = it },
                placeholder = {
                    Text(
                        text = if (isBangla) "১. আলু ২ কেজি\n২. সয়াবিন তেল ৫ লিটার\n৩. ডিম ১ ডজন\n৪. মুরগি দেড় কেজি" else "1. Potato 2 kg\n2. Soybean oil 5 liter\n3. Eggs 1 dozen",
                        style = typography.bodyMedium,
                        color = appColors.textMuted
                    )
                },
                minLines = 6,
                maxLines = 10,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = appColors.surfaceVariant,
                    unfocusedContainerColor = appColors.surfaceVariant,
                    focusedBorderColor = appColors.accent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    haptics.lightTick()
                    val result = BazaarDomainLogic.parseTextForPastePreview(rawInputText, masterProductsMap)
                    previewItems = result.validItems
                    flaggedLines = result.flaggedLines
                    isPreviewMode = true
                },
                enabled = rawInputText.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.accent,
                    contentColor = appColors.onAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = strings.bazaarPastePreview,
                    style = typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        } else {
            // STEP 2: Editable Preview mode
            // Flagged lines banner if any
            if (flaggedLines.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = appColors.warning.copy(alpha = 0.12f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = appColors.warning,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = strings.bazaarFlaggedLines,
                            style = typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = appColors.textPrimary
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // List of valid preview items
                itemsIndexed(previewItems) { index, item ->
                    PreviewItemCard(
                        item = item,
                        onUpdate = { updated ->
                            previewItems = previewItems.toMutableList().also { it[index] = updated }
                        },
                        onRemove = {
                            previewItems = previewItems.toMutableList().also { it.removeAt(index) }
                        },
                        isBangla = isBangla,
                        useBengaliNumerals = useBengaliNumerals
                    )
                }

                // Flagged unparsed lines for inline fix or discard
                if (flaggedLines.isNotEmpty()) {
                    item {
                        Text(
                            text = if (isBangla) "সংশোধনযোগ্য লাইনসমূহ:" else "Unrecognized lines:",
                            style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = appColors.warning,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }

                    itemsIndexed(flaggedLines) { fIndex, flagged ->
                        FlaggedLineCard(
                            flagged = flagged,
                            onConvert = { convertedItem ->
                                flaggedLines = flaggedLines.toMutableList().also { it.removeAt(fIndex) }
                                previewItems = previewItems + convertedItem
                            },
                            onDiscard = {
                                flaggedLines = flaggedLines.toMutableList().also { it.removeAt(fIndex) }
                            },
                            masterProductsMap = masterProductsMap,
                            isBangla = isBangla
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Create List Button
            Button(
                onClick = {
                    haptics.lightTick()
                    val marketItems = previewItems.map {
                        MarketItem(
                            name = it.name.trim(),
                            quantity = it.quantity,
                            unit = it.unit,
                            amount = null,
                            isPurchased = false
                        )
                    }
                    onCreateList(listTitle, marketItems)
                    onDismiss()
                },
                enabled = previewItems.isNotEmpty(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.accent,
                    contentColor = appColors.onAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = strings.bazaarCreateListWithCount(previewItems.size),
                    style = typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun PreviewItemCard(
    item: ParsedPreviewItem,
    onUpdate: (ParsedPreviewItem) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    val deptMeta = getDepartmentMeta(item.stallKey)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.surfaceVariant,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Stall Emoji & Name
            Text(
                text = "${deptMeta.icon} ${item.name}",
                style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = appColors.textPrimary,
                modifier = Modifier.weight(1f)
            )

            // Quantity stepper
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (item.quantity > 0.5) {
                            onUpdate(item.copy(quantity = item.quantity - 0.5))
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = if (isBangla) "কমান" else "Decrease",
                        tint = appColors.textSecondary
                    )
                }

                val qtyStr = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()}" else "${item.quantity}"
                val displayQty = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(qtyStr) else qtyStr
                PulseText(
                    text = "$displayQty ${item.unit}",
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                IconButton(
                    onClick = { onUpdate(item.copy(quantity = item.quantity + 0.5)) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (isBangla) "বাড়ান" else "Increase",
                        tint = appColors.textSecondary
                    )
                }
            }

            // Remove button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = if (isBangla) "মুছুন" else "Remove",
                    tint = appColors.expense
                )
            }
        }
    }
}

@Composable
fun FlaggedLineCard(
    flagged: FlaggedLine,
    onConvert: (ParsedPreviewItem) -> Unit,
    onDiscard: () -> Unit,
    masterProductsMap: Map<String, MasterProduct>,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography
    var editText by remember { mutableStateOf(flagged.lineText) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.warning.copy(alpha = 0.08f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            OutlinedTextField(
                value = editText,
                onValueChange = { editText = it },
                label = { Text(if (isBangla) "লাইনটি সংশোধন করুন" else "Edit line", style = typography.bodySmall) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = appColors.warning,
                    unfocusedBorderColor = appColors.warning.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDiscard,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(if (isBangla) "বাদ দিন" else "Discard", color = appColors.textSecondary)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val parsed = BazaarDomainLogic.parseTextForPastePreview(editText, masterProductsMap)
                        if (parsed.validItems.isNotEmpty()) {
                            onConvert(parsed.validItems.first())
                        } else {
                            onConvert(
                                ParsedPreviewItem(
                                    name = editText.trim(),
                                    quantity = 1.0,
                                    unit = "কেজি",
                                    stallKey = BazaarDomainLogic.classifyStallWithOverride(editText.trim(), masterProductsMap),
                                    originalLine = editText
                                )
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.warning),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(if (isBangla) "যোগ করুন" else "Add", color = Color.White)
                }
            }
        }
    }
}
