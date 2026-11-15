package com.example.expensetracker.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.expensetracker.data.model.Category

class SettingsDialogState {
    var showEditProfileDialog by mutableStateOf(false)
    var showAddCategoryDialog by mutableStateOf(false)
    var categoryToEdit by mutableStateOf<Category?>(null)
    var categoryToDelete by mutableStateOf<Category?>(null)
    var showRestoreConfirmDialog by mutableStateOf(false)
    var showAllCategoriesSheet by mutableStateOf(false)
    var showShareSlipSheet by mutableStateOf(false)
}

@Composable
fun rememberSettingsDialogState(): SettingsDialogState = remember { SettingsDialogState() }
