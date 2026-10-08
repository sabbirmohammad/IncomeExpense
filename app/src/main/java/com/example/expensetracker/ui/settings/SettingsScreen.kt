package com.example.expensetracker.ui.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    if (onNavigateBack != null) {
        BackHandler(onBack = onNavigateBack)
    }

    val userProfile by viewModel.userProfile.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val signInIntent by viewModel.signInIntent.collectAsState()
    val apiConnectionTest by viewModel.apiConnectionTest.collectAsState()
    val isTestingApi by viewModel.isTestingApi.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val exportActions = remember(context, strings, clipboardManager, scope, viewModel) {
        SettingsDataExportActions(context, strings, clipboardManager, scope, viewModel)
    }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }
    var showLocalRestoreConfirmDialog by remember { mutableStateOf(false) }
    var selectedLocalBackupUri by remember { mutableStateOf<Uri?>(null) }
    var showAllCategoriesSheet by remember { mutableStateOf(false) }
    var showShareSlipSheet by remember { mutableStateOf(false) }
    var showAiSuccessDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            selectedLocalBackupUri = uri
            showLocalRestoreConfirmDialog = true
        }
    }

    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.handleSignInResult(result.data)
    }

    LaunchedEffect(signInIntent) {
        signInIntent?.let { intent ->
            try { signInLauncher.launch(intent) } catch (_: Exception) {}
            viewModel.clearSignInIntent()
        }
    }

    LaunchedEffect(syncMessage) {
        if (syncMessage != null) {
            Toast.makeText(context, syncMessage, Toast.LENGTH_LONG).show()
            viewModel.clearSyncMessage()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SettingsTopBar(onNavigateBack = onNavigateBack, onMenuClick = onMenuClick)
        }
        item {
            SettingsProfileCard(userProfile = userProfile, onClick = { showEditProfileDialog = true })
        }
        item {
            SettingsThemeCard(currentThemeKey = userProfile.appTheme, onThemeChange = { viewModel.setAppTheme(it) })
        }
        item {
            SettingsLanguageCard(currentLanguage = userProfile.appLanguage, onLanguageChange = { viewModel.setAppLanguage(it) })
        }
        item {
            SettingsBengaliNumeralsCard(useBengaliNumerals = userProfile.useBengaliNumerals, onToggle = { viewModel.setBengaliNumeralsEnabled(it) })
        }
        item {
            SettingsCategoriesCard(
                categories = categories,
                useBengaliNumerals = userProfile.useBengaliNumerals,
                onAddCategoryClick = { showAddCategoryDialog = true },
                onEditCategoryClick = { categoryToEdit = it },
                onDeleteCategoryClick = { categoryToDelete = it },
                onViewAllCategoriesClick = { showAllCategoriesSheet = true }
            )
        }
        item {
            SettingsDriveBackupCard(
                userProfile = userProfile,
                isSyncing = isSyncing,
                onDisconnectGoogleAccount = { viewModel.disconnectGoogleAccount() },
                onInitiateGoogleSignIn = { viewModel.initiateGoogleSignIn() },
                onBackupToGoogleDriveNow = { viewModel.backupToGoogleDriveNow() },
                onRestoreFromDriveClick = { showRestoreConfirmDialog = true },
                onAutoSyncChange = { viewModel.setAutoSync(it) }
            )
        }
        item {
            SettingsDataBackupCard(
                userProfile = userProfile,
                transactions = transactions,
                onCopyCsv = { exportActions.copyCsv() },
                onCopyJson = { exportActions.copyJson() },
                onShareCsv = { exportActions.shareCsv() },
                onShareJson = { exportActions.shareJson() },
                onImportJson = {
                    try {
                        filePickerLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                    } catch (_: Exception) {
                        Toast.makeText(context, strings.noFilePicker, Toast.LENGTH_SHORT).show()
                    }
                },
                onShareSlipClick = { showShareSlipSheet = true }
            )
        }
        item {
            SettingsAiIntegrationCard(
                userProfile = userProfile,
                isTestingApi = isTestingApi,
                apiConnectionTest = apiConnectionTest,
                onSaveKey = { viewModel.setGeminiApiKey(it) },
                onTestKey = { viewModel.testGeminiApiKey(it) },
                onClearKey = {
                    viewModel.setGeminiApiKey(null)
                    viewModel.clearApiTestResult()
                },
                onSuccessSaved = { showAiSuccessDialog = true }
            )
        }
        item {
            SettingsAboutAppCard(transactionsCount = transactions.size, categoriesCount = categories.size)
        }
        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }

    SettingsModals(
        userProfile = userProfile,
        categories = categories,
        showAllCategoriesSheet = showAllCategoriesSheet,
        onDismissAllCategoriesSheet = { showAllCategoriesSheet = false },
        showEditProfileDialog = showEditProfileDialog,
        onDismissEditProfileDialog = { showEditProfileDialog = false },
        onSaveProfile = { first, last, avatar ->
            viewModel.updateProfile(first, last, avatar)
            Toast.makeText(context, strings.profileUpdatedSuccess, Toast.LENGTH_SHORT).show()
            showEditProfileDialog = false
        },
        showAddCategoryDialog = showAddCategoryDialog,
        categoryToEdit = categoryToEdit,
        onDismissAddEditCategoryDialog = {
            showAddCategoryDialog = false
            categoryToEdit = null
        },
        onSaveCategory = { name, type, icon, colorHex ->
            if (categoryToEdit != null) {
                viewModel.updateCategory(categoryToEdit!!.copy(name = name, type = type, icon = icon, colorHex = colorHex))
            } else {
                viewModel.addCategory(name = name, type = type, icon = icon, colorHex = colorHex)
            }
            showAddCategoryDialog = false
            categoryToEdit = null
        },
        onOpenAddCategory = { showAddCategoryDialog = true },
        categoryToDelete = categoryToDelete,
        onDismissDeleteCategoryDialog = { categoryToDelete = null },
        onConfirmDeleteCategory = {
            viewModel.deleteCategory(it.id)
            categoryToDelete = null
        },
        onSelectEditCategory = { categoryToEdit = it },
        onSelectDeleteCategory = { categoryToDelete = it },
        showRestoreConfirmDialog = showRestoreConfirmDialog,
        onDismissRestoreConfirmDialog = { showRestoreConfirmDialog = false },
        onConfirmDriveRestore = {
            showRestoreConfirmDialog = false
            viewModel.restoreFromGoogleDrive()
        },
        selectedLocalBackupUri = selectedLocalBackupUri,
        showLocalRestoreConfirmDialog = showLocalRestoreConfirmDialog,
        onDismissLocalRestoreConfirmDialog = {
            showLocalRestoreConfirmDialog = false
            selectedLocalBackupUri = null
        },
        onConfirmLocalRestore = { uri ->
            showLocalRestoreConfirmDialog = false
            selectedLocalBackupUri = null
            viewModel.restoreFromUri(uri)
        },
        showShareSlipSheet = showShareSlipSheet,
        onDismissShareSlipSheet = { showShareSlipSheet = false },
        showAiSuccessDialog = showAiSuccessDialog,
        onDismissAiSuccessDialog = { showAiSuccessDialog = false },
        isBangla = strings.isBangla
    )
}
