package com.example.expensetracker.ui.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.LocalAppStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SettingsDialogsStateHost(
    viewModel: SettingsViewModel,
    userProfile: UserProfile,
    categories: List<Category>,
    dialogState: SettingsDialogState,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    val strings = LocalAppStrings.current

    SettingsModals(
        userProfile = userProfile,
        categories = categories,
        showAllCategoriesSheet = dialogState.showAllCategoriesSheet,
        onDismissAllCategoriesSheet = { dialogState.showAllCategoriesSheet = false },
        showEditProfileDialog = dialogState.showEditProfileDialog,
        onDismissEditProfileDialog = { dialogState.showEditProfileDialog = false },
        onSaveProfile = { first, last, avatar ->
            viewModel.updateProfile(first, last, avatar)
            scope.launch {
                snackbarHostState.showSnackbar(strings.profileUpdatedSuccess)
            }
            dialogState.showEditProfileDialog = false
        },
        showAddCategoryDialog = dialogState.showAddCategoryDialog,
        categoryToEdit = dialogState.categoryToEdit,
        onDismissAddEditCategoryDialog = {
            dialogState.showAddCategoryDialog = false
            dialogState.categoryToEdit = null
        },
        onSaveCategory = { name, type, icon, colorHex ->
            val toEdit = dialogState.categoryToEdit
            if (toEdit != null) {
                viewModel.updateCategory(toEdit.copy(name = name, type = type, icon = icon, colorHex = colorHex))
            } else {
                viewModel.addCategory(name = name, type = type, icon = icon, colorHex = colorHex)
            }
            dialogState.showAddCategoryDialog = false
            dialogState.categoryToEdit = null
        },
        onOpenAddCategory = { dialogState.showAddCategoryDialog = true },
        categoryToDelete = dialogState.categoryToDelete,
        onDismissDeleteCategoryDialog = { dialogState.categoryToDelete = null },
        onConfirmDeleteCategory = { cat ->
            viewModel.deleteCategory(cat.id)
            dialogState.categoryToDelete = null
        },
        onSelectEditCategory = { cat ->
            dialogState.categoryToEdit = cat
            dialogState.showAddCategoryDialog = true
        },
        onSelectDeleteCategory = { cat ->
            dialogState.categoryToDelete = cat
        },
        showRestoreConfirmDialog = dialogState.showRestoreConfirmDialog,
        onDismissRestoreConfirmDialog = { dialogState.showRestoreConfirmDialog = false },
        onConfirmDriveRestore = {
            viewModel.restoreFromGoogleDrive()
            dialogState.showRestoreConfirmDialog = false
        },
        showShareSlipSheet = dialogState.showShareSlipSheet,
        onDismissShareSlipSheet = { dialogState.showShareSlipSheet = false },
        isBangla = strings.isBangla
    )
}
