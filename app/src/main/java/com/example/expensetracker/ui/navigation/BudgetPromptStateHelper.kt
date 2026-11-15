package com.example.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.checkShouldShowUpcomingBudgetPrompt

@Composable
fun rememberUpcomingBudgetPromptState(
    repository: ExpenseRepository,
    triggerBudgetPromptFromNotification: Boolean,
    onBudgetPromptHandled: () -> Unit
): MutableState<Boolean> {
    val showUpcomingBudgetPrompt = remember { mutableStateOf(false) }

    LaunchedEffect(triggerBudgetPromptFromNotification) {
        if (triggerBudgetPromptFromNotification) {
            showUpcomingBudgetPrompt.value = true
            onBudgetPromptHandled()
        } else {
            val shouldShow = repository.checkShouldShowUpcomingBudgetPrompt()
            if (shouldShow) {
                showUpcomingBudgetPrompt.value = true
            }
        }
    }

    return showUpcomingBudgetPrompt
}
