package com.example.expensetracker.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.ai.GeminiAiService
import com.example.expensetracker.data.model.AiChatMessage
import com.example.expensetracker.theme.ColorSurface
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
            } catch (_: Exception) {
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
            AiAdvisorHeader(
                title = strings.aiAdvisorTitle,
                closeContentDescription = strings.close,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(12.dp))

            AiAdvisorSuggestionsRow(
                suggestions = suggestionChips,
                isWaitingForResponse = isWaitingForResponse,
                onSelectSuggestion = { sendMessage(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                        AiUserMessageBubble(text = msg.text)
                    } else {
                        AiAdvisorMessageBubble(
                            text = msg.text,
                            isBangla = isBangla,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(msg.text))
                                haptics.lightTick()
                                Toast.makeText(context, strings.copiedToClipboard, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                if (isWaitingForResponse) {
                    item {
                        AiAdvisorLoadingItem(isBangla = isBangla)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AiAdvisorInputBar(
                query = inputQuery,
                onQueryChange = { inputQuery = it },
                placeholder = strings.askAdvisorPlaceholder,
                isWaitingForResponse = isWaitingForResponse,
                onSend = { sendMessage(inputQuery) }
            )
        }
    }
}
