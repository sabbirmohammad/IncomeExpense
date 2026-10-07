package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.ai.GeminiAiService
import com.example.expensetracker.data.model.AiChatMessage
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAdvisorChatSheet(
    sheetState: SheetState,
    financialContext: String,
    apiKey: String?,
    isBangla: Boolean,
    userName: String = "Sabbir",
    currencySymbol: String = "৳",
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptics = rememberAppHaptics()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputQuery by remember { mutableStateOf("") }
    var isWaitingForResponse by remember { mutableStateOf(false) }

    val welcomeGreeting = remember(isBangla, userName) {
        if (isBangla) {
            val nameGreeting = if (userName.isNotBlank()) " $userName ভাই" else ""
            "আসসালামু আলাইকুম$nameGreeting! আমি আপনার ব্যক্তিগত AI পরামর্শক। আপনার চলতি মাসের আয়, ব্যয়, বাজেট বা সঞ্চয় নিয়ে যেকোনো প্রশ্ন করুন।"
        } else {
            val nameGreeting = if (userName.isNotBlank()) " $userName" else ""
            "Hello$nameGreeting! I am your personal AI Financial Advisor. Ask me anything about your spending habits, budget limits, or smart savings strategies."
        }
    }

    val messages = remember {
        mutableStateListOf(
            AiChatMessage(
                text = welcomeGreeting,
                isFromUser = false
            )
        )
    }

    val suggestionChips = remember(isBangla) {
        if (isBangla) {
            listOf(
                "📊 এই মাসের খরচের পূর্ণাঙ্গ বিশ্লেষণ",
                "💡 কোন কোন খাতে খরচ কমানো সম্ভব?",
                "💰 কীভাবে আরও বেশি টাকা সঞ্চয় করবো?",
                "🎯 বাজেটের বর্তমান স্বাস্থ্য ও সতর্কতা"
            )
        } else {
            listOf(
                "📊 Comprehensive spending breakdown",
                "💡 Actionable steps to cut high expenses",
                "💰 Practical money-saving strategies",
                "🎯 Budget health & forecast"
            )
        }
    }

    fun sendMessage(question: String) {
        val trimmed = question.trim()
        if (trimmed.isBlank() || isWaitingForResponse) return
        haptics.lightTick()

        messages.add(AiChatMessage(text = trimmed, isFromUser = true))
        inputQuery = ""
        isWaitingForResponse = true

        scope.launch {
            try {
                listState.animateScrollToItem(messages.size - 1)
                val answer = GeminiAiService.askAdvisor(
                    userQuestion = trimmed,
                    chatHistory = messages.toList(),
                    contextSummary = financialContext,
                    isBangla = isBangla,
                    apiKey = apiKey,
                    userName = userName
                )
                messages.add(AiChatMessage(text = answer, isFromUser = false))
                listState.animateScrollToItem(messages.size - 1)
                haptics.mediumClick()
            } catch (e: Exception) {
                val errorMsg = if (isBangla) {
                    "দুঃখিত, সংযোগে ত্রুটি হয়েছে। অনুগ্রহ করে আবার চেষ্টা করুন।"
                } else {
                    "Sorry, a connection error occurred. Please try again."
                }
                messages.add(AiChatMessage(text = errorMsg, isFromUser = false))
            } finally {
                isWaitingForResponse = false
            }
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
                .navigationBarsPadding()
                .imePadding()
                .padding(top = 16.dp)
        ) {
            // 1. Redesigned Clean Header: Modern AI Avatar with Live Status + Short Title + FinAI Badge (No Subtitle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Modern AI Profile Avatar with Online Indicator
                    Box(
                        modifier = Modifier.size(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF6366F1), ColorAccent)
                                    )
                                )
                                .border(1.dp, ColorAccent.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SmartToy,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        // Live active status dot
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(ColorSuccessIncome)
                                .border(1.5.dp, ColorSurface, CircleShape)
                        )
                    }

                    // Short Header Title + FinAI Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = strings.aiAdvisorTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ColorTextPrimary,
                                fontSize = 17.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorAccent.copy(alpha = 0.14f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "FinAI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorAccent
                                )
                            )
                        }
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = strings.close,
                        tint = ColorTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Suggestions Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestionChips.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ColorBackground)
                            .border(1.dp, ColorBorder, RoundedCornerShape(20.dp))
                            .clickable(enabled = !isWaitingForResponse) {
                                sendMessage(prompt)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = ColorTextPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    if (msg.isFromUser) {
                        // User message bubble
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .clip(RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp))
                                    .background(ColorAccent)
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                            }
                        }
                    } else {
                        // Advisor message bubble with dedicated AI Avatar and professional formatting
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            // AI Avatar icon
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF6366F1), ColorAccent)
                                        )
                                    )
                                    .border(1.dp, ColorAccent.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SmartToy,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .widthIn(max = 360.dp)
                                    .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                                    .background(ColorBackground)
                                    .border(1.dp, ColorBorder, RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Column {
                                    // Formatted response content (headings, bullets, numbered lists, bold text)
                                    AiFormattedMessageView(
                                        rawText = msg.text,
                                        isBangla = isBangla
                                    )

                                    // Bottom utility bar: FinAI tag + Copy button
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.SmartToy,
                                                contentDescription = null,
                                                tint = ColorAccent.copy(alpha = 0.7f),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Text(
                                                text = "FinAI",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    color = ColorTextMuted,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .clickable {
                                                    clipboardManager.setText(AnnotatedString(msg.text))
                                                    haptics.lightTick()
                                                    Toast.makeText(context, strings.copiedToClipboard, Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = ColorTextMuted,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = if (isBangla) "কপি" else "Copy",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.5.sp,
                                                    color = ColorTextMuted
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (isWaitingForResponse) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF6366F1), ColorAccent)
                                        )
                                    )
                                    .border(1.dp, ColorAccent.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    color = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) "পরামর্শ তৈরি হচ্ছে..." else "Analyzing & drafting advice...",
                                style = MaterialTheme.typography.bodySmall.copy(color = ColorTextMuted)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Row
            Surface(
                color = ColorBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = {
                            Text(
                                text = strings.askAdvisorPlaceholder,
                                style = MaterialTheme.typography.bodySmall,
                                color = ColorTextMuted
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorAccent,
                            unfocusedBorderColor = ColorBorder,
                            focusedContainerColor = ColorSurface,
                            unfocusedContainerColor = ColorSurface,
                            focusedTextColor = ColorTextPrimary,
                            unfocusedTextColor = ColorTextPrimary
                        ),
                        maxLines = 3
                    )

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputQuery.isNotBlank() && !isWaitingForResponse) ColorAccent
                                else ColorBorder
                            )
                            .clickable(enabled = inputQuery.isNotBlank() && !isWaitingForResponse) {
                                sendMessage(inputQuery)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputQuery.isNotBlank() && !isWaitingForResponse) Color.White else ColorTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Rich AI Response Formatter & Markdown Block Parser
// ─────────────────────────────────────────────────────────────────────────────

private fun normalizeBengaliNumber(input: String): String {
    val bnDigits = "০১২৩৪৫৬৭৮৯"
    val enDigits = "0123456789"
    var res = input
    for (i in 0..9) {
        res = res.replace(bnDigits[i], enDigits[i])
    }
    return res.replace(Regex("[^0-9.]"), "")
}

private sealed class AiContentBlock {
    data class Header(val text: String) : AiContentBlock()
    data class Bullet(val text: String) : AiContentBlock()
    data class Numbered(val index: String, val text: String) : AiContentBlock()
    data class Chart(val items: List<Pair<String, Float>>) : AiContentBlock()
    data class Metrics(val items: List<Pair<String, String>>) : AiContentBlock()
    data class Table(val rows: List<List<String>>) : AiContentBlock()
    data class Paragraph(val text: String) : AiContentBlock()
}

private fun parseAiContent(raw: String): List<AiContentBlock> {
    val lines = raw.lines()
    val blocks = mutableListOf<AiContentBlock>()
    var currentParagraph = StringBuilder()

    fun flushParagraph() {
        if (currentParagraph.isNotBlank()) {
            blocks.add(AiContentBlock.Paragraph(currentParagraph.toString().trim()))
            currentParagraph = StringBuilder()
        }
    }

    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trim()
        if (trimmed.isBlank()) {
            flushParagraph()
            i++
            continue
        }

        // Visual Chart Tag: [Chart: মুদি বাজার: 98%, অন্যান্য: 2%] or [Bar: ...] or [Graph: ...] or Bengali tags
        val chartMatch = Regex("""\[(?:Chart|Bar|Graph|চার্ট|গ্রাফ):\s*(.*?)\]""", RegexOption.IGNORE_CASE).find(trimmed)
        if (chartMatch != null) {
            flushParagraph()
            val content = chartMatch.groupValues[1]
            val items = content.split(Regex("[,|;]")).mapNotNull { part ->
                val pieces = part.split(":")
                if (pieces.size >= 2) {
                    val label = pieces[0].trim()
                    val cleanVal = normalizeBengaliNumber(pieces[1])
                    val pct = cleanVal.toFloatOrNull() ?: 0f
                    if (pct > 0f) label to pct else null
                } else null
            }
            if (items.isNotEmpty()) {
                blocks.add(AiContentBlock.Chart(items))
                i++
                continue
            }
        }

        // Visual Metrics Tag: [Metrics: আয়: ৳৮৮,০০০ | ব্যয়: ৳৬,১৬,৩৫৫ | নিট: -৳৫,২৮,৩৫৫] or [KPI: ...]
        val metricsMatch = Regex("""\[(?:Metrics|KPI):\s*(.*?)\]""", RegexOption.IGNORE_CASE).find(trimmed)
        if (metricsMatch != null) {
            flushParagraph()
            val content = metricsMatch.groupValues[1]
            val items = content.split("|").mapNotNull { part ->
                val pieces = part.split(":")
                if (pieces.size >= 2) {
                    val label = pieces[0].trim()
                    val value = pieces.drop(1).joinToString(":").trim()
                    label to value
                } else null
            }
            if (items.isNotEmpty()) {
                blocks.add(AiContentBlock.Metrics(items))
                i++
                continue
            }
        }

        // Markdown Table: | Col1 | Col2 |
        if (trimmed.startsWith("|") && trimmed.endsWith("|") && trimmed.count { it == '|' } >= 2) {
            flushParagraph()
            val tableRows = mutableListOf<List<String>>()
            while (i < lines.size) {
                val rowLine = lines[i].trim()
                if (rowLine.startsWith("|") && rowLine.endsWith("|")) {
                    // Check if it's separator row like |---|---|
                    val isSeparator = rowLine.replace("|", "").replace("-", "").replace(":", "").isBlank()
                    if (!isSeparator) {
                        val cells = rowLine.removeSurrounding("|")
                            .split("|")
                            .map { it.trim() }
                        if (cells.isNotEmpty()) tableRows.add(cells)
                    }
                    i++
                } else {
                    break
                }
            }
            if (tableRows.isNotEmpty()) {
                blocks.add(AiContentBlock.Table(tableRows))
                continue
            }
        }

        // Markdown Header: ### Title or ## Title
        if (trimmed.startsWith("###") || trimmed.startsWith("##")) {
            flushParagraph()
            val headerText = trimmed.replace(Regex("^#+\\s*"), "").replace("**", "").trim()
            blocks.add(AiContentBlock.Header(headerText))
        }
        // Standalone bold title as header
        else if (trimmed.startsWith("**") && trimmed.endsWith("**") && trimmed.length < 60 && !trimmed.contains("\n")) {
            flushParagraph()
            val headerText = trimmed.removeSurrounding("**").trim()
            blocks.add(AiContentBlock.Header(headerText))
        }
        // Bullet list item: •, *, -, ✦
        else if (trimmed.startsWith("•") || trimmed.startsWith("* ") || trimmed.startsWith("- ") || trimmed.startsWith("✦")) {
            flushParagraph()
            val bulletText = trimmed.replace(Regex("^[•*\\-✦]\\s*"), "").trim()
            blocks.add(AiContentBlock.Bullet(bulletText))
        }
        // Numbered list item: 1. or 1)
        else if (trimmed.matches(Regex("""^\d+[\.\)]\s+.*"""))) {
            flushParagraph()
            val num = trimmed.substringBefore(".").substringBefore(")").trim()
            val itemText = trimmed.replace(Regex("""^\d+[\.\)]\s*"""), "").trim()
            blocks.add(AiContentBlock.Numbered(num, itemText))
        } else {
            if (currentParagraph.isNotEmpty()) {
                currentParagraph.append("\n")
            }
            currentParagraph.append(trimmed)
        }
        i++
    }
    flushParagraph()
    return blocks
}

@Composable
private fun AiFormattedMessageView(
    rawText: String,
    isBangla: Boolean
) {
    val blocks = remember(rawText) { parseAiContent(rawText) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is AiContentBlock.Metrics -> {
                    // Professional Compact Financial KPI Metric Cards
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ColorSurface)
                            .border(1.dp, ColorBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        block.items.forEachIndexed { idx, (label, value) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.5.sp,
                                        color = ColorTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                val valueColor = when {
                                    label.contains("আয়", ignoreCase = true) || label.contains("Income", ignoreCase = true) -> ColorSuccessIncome
                                    label.contains("ব্যয়", ignoreCase = true) || label.contains("Expense", ignoreCase = true) || value.contains("-") -> ColorDangerExpense
                                    else -> ColorAccent
                                }
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = valueColor
                                    )
                                )
                            }
                            if (idx < block.items.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(22.dp)
                                        .background(ColorBorder)
                                )
                            }
                        }
                    }
                }
                is AiContentBlock.Chart -> {
                    // Embedded Native Visual Segmented Bar Chart
                    val chartColors = listOf(
                        Color(0xFF38BDF8), // Sky Blue
                        Color(0xFFF472B6), // Rose Pink
                        Color(0xFF34D399), // Emerald
                        Color(0xFFA78BFA), // Violet
                        Color(0xFFFBBF24), // Amber
                        Color(0xFF94A3B8)  // Slate
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ColorSurface)
                            .border(1.dp, ColorBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PieChart,
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isBangla) "ব্যয় বণ্টন গ্রাফ" else "Spend Distribution",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = ColorTextSecondary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Segmented bar chart
                        val totalVal = block.items.sumOf { it.second.toDouble() }.toFloat().coerceAtLeast(1f)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(9.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(ColorBorder)
                        ) {
                            block.items.forEachIndexed { i, item ->
                                val fraction = (item.second / totalVal).coerceIn(0.01f, 1f)
                                val segColor = chartColors[i % chartColors.size]
                                Box(
                                    modifier = Modifier
                                        .weight(fraction)
                                        .fillMaxHeight()
                                        .background(segColor)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category legend pills using FlowRow so labels NEVER break or word-wrap
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            block.items.take(6).forEachIndexed { i, item ->
                                val segColor = chartColors[i % chartColors.size]
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(segColor.copy(alpha = 0.12f))
                                        .border(0.5.dp, segColor.copy(alpha = 0.28f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 7.dp, vertical = 3.5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(segColor)
                                    )
                                    Text(
                                        text = "${item.first} ${item.second.toInt()}%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = ColorTextPrimary
                                        ),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }
                }
                is AiContentBlock.Table -> {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(8.dp)
                        ) {
                            block.rows.forEachIndexed { rowIdx, rowCells ->
                                Row(
                                    modifier = Modifier
                                        .background(if (rowIdx == 0) ColorAccent.copy(alpha = 0.10f) else Color.Transparent)
                                        .padding(vertical = 4.dp, horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    rowCells.forEach { cell ->
                                        Text(
                                            text = cell,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.5.sp,
                                                fontWeight = if (rowIdx == 0) FontWeight.Bold else FontWeight.Normal,
                                                color = if (rowIdx == 0) ColorTextPrimary else ColorTextSecondary
                                            ),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                                if (rowIdx < block.rows.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(0.5.dp)
                                            .background(ColorBorder.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }
                    }
                }
                is AiContentBlock.Header -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 3.dp, height = 14.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(ColorAccent)
                        )
                        Text(
                            text = block.text,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = ColorTextPrimary
                            )
                        )
                    }
                }
                is AiContentBlock.Bullet -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(ColorAccent)
                        )
                        Text(
                            text = formatAnnotatedText(block.text),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ColorTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is AiContentBlock.Numbered -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(ColorAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = block.index,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = ColorAccent
                                )
                            )
                        }
                        Text(
                            text = formatAnnotatedText(block.text),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ColorTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is AiContentBlock.Paragraph -> {
                    Text(
                        text = formatAnnotatedText(block.text),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ColorTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun formatAnnotatedText(text: String): AnnotatedString {
    return buildAnnotatedString {
        val boldRegex = Regex("""\*\*(.*?)\*\*""")
        var currentIndex = 0
        val matches = boldRegex.findAll(text)

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1
            if (start > currentIndex) {
                append(text.substring(currentIndex, start))
            }
            val boldContent = match.groupValues[1]
            pushStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = ColorTextPrimary
                )
            )
            append(boldContent)
            pop()
            currentIndex = end
        }
        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }
}
