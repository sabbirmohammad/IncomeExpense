package com.example.expensetracker.ui.settings

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    if (onNavigateBack != null) {
        BackHandler(onBack = onNavigateBack)
    }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val signInIntent by viewModel.signInIntent.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }
    var showAllCategoriesSheet by remember { mutableStateOf(false) }
    var showShareSlipSheet by remember { mutableStateOf(false) }

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
            .background(appColors.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SettingsTopBar(onNavigateBack = onNavigateBack, onMenuClick = onMenuClick)
        }

        // 1. Executive User Profile & Command Card
        item {
            SettingsProfileCard(
                userProfile = userProfile,
                onClick = { showEditProfileDialog = true },
                categoriesCount = categories.size,
                transactionsCount = transactions.size
            )
        }

        // 2. Preferences & Display Section
        item {
            SettingsSectionHeader(
                title = if (strings.isBangla) "অ্যাপ পছন্দসমূহ ও ডিসপ্লে" else "Display & Preferences"
            )
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

        // 3. Category Management (Collapsible Accordion)
        item {
            SettingsSectionHeader(
                title = if (strings.isBangla) "ক্যাটাগরি ব্যবস্থাপনা" else "Category Management"
            )
        }
        item {
            SettingsCategoriesCard(
                categories = categories,
                useBengaliNumerals = userProfile.useBengaliNumerals,
                onAddCategoryClick = { showAddCategoryDialog = true },
                onEditCategoryClick = { categoryToEdit = it },
                onDeleteCategoryClick = { categoryToDelete = it },
                onViewAllCategoriesClick = { showAllCategoriesSheet = true },
                initiallyExpanded = false
            )
        }

        // 4. Cloud & Data Backup Section (Collapsible Accordion)
        item {
            SettingsSectionHeader(
                title = if (strings.isBangla) "ব্যাকআপ ও ক্লাউড স্টোরেজ" else "Cloud & Data Storage"
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
                onAutoSyncChange = { viewModel.setAutoSync(it) },
                initiallyExpanded = false
            )
        }
        item {
            SettingsDataBackupCard(
                userProfile = userProfile,
                transactions = transactions,
                onShareSlipClick = { showShareSlipSheet = true },
                initiallyExpanded = false
            )
        }

        // 5. About App & System Info
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
        onConfirmDeleteCategory = { cat ->
            viewModel.deleteCategory(cat.id)
            categoryToDelete = null
        },
        onSelectEditCategory = { cat ->
            categoryToEdit = cat
            showAddCategoryDialog = true
        },
        onSelectDeleteCategory = { cat ->
            categoryToDelete = cat
        },
        showRestoreConfirmDialog = showRestoreConfirmDialog,
        onDismissRestoreConfirmDialog = { showRestoreConfirmDialog = false },
        onConfirmDriveRestore = {
            viewModel.restoreFromGoogleDrive()
            showRestoreConfirmDialog = false
        },
        showShareSlipSheet = showShareSlipSheet,
        onDismissShareSlipSheet = { showShareSlipSheet = false },
        isBangla = strings.isBangla
    )
}

@Composable
fun SettingsSectionHeader(title: String, modifier: Modifier = Modifier) {
    val appColors = LocalAppColors.current
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        ),
        color = appColors.accent,
        modifier = modifier.padding(top = 10.dp, bottom = 4.dp, start = 4.dp)
    )
}
