package com.example.expensetracker.ui.settings

import android.net.Uri
import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SettingsModals(
    userProfile: UserProfile,
    categories: List<Category>,
    showAllCategoriesSheet: Boolean,
    onDismissAllCategoriesSheet: () -> Unit,
    showEditProfileDialog: Boolean,
    onDismissEditProfileDialog: () -> Unit,
    onSaveProfile: (String, String, Int) -> Unit,
    showAddCategoryDialog: Boolean,
    categoryToEdit: Category?,
    onDismissAddEditCategoryDialog: () -> Unit,
    onSaveCategory: (String, TransactionType, String, String) -> Unit,
    onOpenAddCategory: () -> Unit,
    categoryToDelete: Category?,
    onDismissDeleteCategoryDialog: () -> Unit,
    onConfirmDeleteCategory: (Category) -> Unit,
    onSelectEditCategory: (Category) -> Unit,
    onSelectDeleteCategory: (Category) -> Unit,
    showRestoreConfirmDialog: Boolean,
    onDismissRestoreConfirmDialog: () -> Unit,
    onConfirmDriveRestore: () -> Unit,
    selectedLocalBackupUri: Uri?,
    showLocalRestoreConfirmDialog: Boolean,
    onDismissLocalRestoreConfirmDialog: () -> Unit,
    onConfirmLocalRestore: (Uri) -> Unit,
    showShareSlipSheet: Boolean,
    onDismissShareSlipSheet: () -> Unit,
    showAiSuccessDialog: Boolean,
    onDismissAiSuccessDialog: () -> Unit,
    isBangla: Boolean
) {
    if (showAllCategoriesSheet) {
        AllCategoriesSheet(
            categories = categories,
            onDismiss = onDismissAllCategoriesSheet,
            onAddNew = {
                onDismissAllCategoriesSheet()
                onOpenAddCategory()
            },
            onEditCategory = {
                onDismissAllCategoriesSheet()
                onSelectEditCategory(it)
            },
            onDeleteCategory = {
                onDismissAllCategoriesSheet()
                onSelectDeleteCategory(it)
            }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            userProfile = userProfile,
            onDismiss = onDismissEditProfileDialog,
            onSave = onSaveProfile
        )
    }

    if (showAddCategoryDialog || categoryToEdit != null) {
        AddEditCategoryDialog(
            category = categoryToEdit,
            categories = categories,
            onDismiss = onDismissAddEditCategoryDialog,
            onSave = onSaveCategory
        )
    }

    if (categoryToDelete != null) {
        CategoryDeleteConfirmDialog(
            category = categoryToDelete,
            onDismiss = onDismissDeleteCategoryDialog,
            onConfirmDelete = onConfirmDeleteCategory
        )
    }

    if (showRestoreConfirmDialog) {
        GoogleDriveRestoreConfirmDialog(
            onDismiss = onDismissRestoreConfirmDialog,
            onConfirm = onConfirmDriveRestore
        )
    }

    if (showLocalRestoreConfirmDialog && selectedLocalBackupUri != null) {
        LocalFileRestoreConfirmDialog(
            backupUri = selectedLocalBackupUri,
            onDismiss = onDismissLocalRestoreConfirmDialog,
            onConfirm = onConfirmLocalRestore
        )
    }

    if (showShareSlipSheet) {
        ShareExpenseSheet(
            initialPeriod = SharePeriodType.THIS_MONTH,
            onDismiss = onDismissShareSlipSheet
        )
    }

    if (showAiSuccessDialog) {
        AiConnectionSuccessDialog(
            isBangla = isBangla,
            onDismiss = onDismissAiSuccessDialog
        )
    }
}
