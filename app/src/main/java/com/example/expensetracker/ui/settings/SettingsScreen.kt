package com.example.expensetracker.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppSnackbarHost
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.SyncStatusCard

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    if (onNavigateBack != null) {
        BackHandler(onBack = onNavigateBack)
    }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val signInIntent by viewModel.signInIntent.collectAsStateWithLifecycle()

    val dialogState = rememberSettingsDialogState()

    SettingsSignInEffect(
        signInIntent = signInIntent,
        onHandleResult = { viewModel.handleSignInResult(it) },
        onClearIntent = { viewModel.clearSignInIntent() }
    )

    SettingsSyncEffect(
        syncMessage = syncMessage,
        snackbarHostState = snackbarHostState,
        onRetryBackup = { viewModel.backupToGoogleDriveNow() },
        onClearSyncMessage = { viewModel.clearSyncMessage() }
    )

    Scaffold(
        snackbarHost = { AppSnackbarHost(hostState = snackbarHostState) },
        containerColor = appColors.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(appColors.background)
                .padding(paddingValues)
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
                    onClick = { dialogState.showEditProfileDialog = true },
                    categoriesCount = categories.size,
                    transactionsCount = transactions.size
                )
            }

            // 2. Appearance Section
            item {
                SettingsSectionHeader(
                    title = if (strings.isBangla) "অ্যাপের থিম ও রূপরেখা" else "Appearance"
                )
            }
            item {
                SettingsThemeCard(currentThemeKey = userProfile.appTheme, onThemeChange = { viewModel.setAppTheme(it) })
            }

            // 3. Language & Numerals Section
            item {
                SettingsSectionHeader(
                    title = if (strings.isBangla) "ভাষা ও সংখ্যাপদ্ধতি" else "Language & Numerals"
                )
            }
            item {
                SettingsLanguageCard(currentLanguage = userProfile.appLanguage, onLanguageChange = { viewModel.setAppLanguage(it) })
            }
            item {
                SettingsBengaliNumeralsCard(useBengaliNumerals = userProfile.useBengaliNumerals, onToggle = { viewModel.setBengaliNumeralsEnabled(it) })
            }

            // 4. Category Management
            item {
                SettingsSectionHeader(
                    title = if (strings.isBangla) "ক্যাটাগরি ব্যবস্থাপনা" else "Category Management"
                )
            }
            item {
                SettingsCategoriesCard(
                    categories = categories,
                    useBengaliNumerals = userProfile.useBengaliNumerals,
                    onAddCategoryClick = { dialogState.showAddCategoryDialog = true },
                    onEditCategoryClick = { dialogState.categoryToEdit = it },
                    onDeleteCategoryClick = { dialogState.categoryToDelete = it },
                    onViewAllCategoriesClick = { dialogState.showAllCategoriesSheet = true },
                    initiallyExpanded = false
                )
            }

            // 5. Backup & Sync Section (SyncStatusCard)
            item {
                SettingsSectionHeader(
                    title = if (strings.isBangla) "ব্যাকআপ ও ক্লাউড সিঙ্ক" else "Backup & Cloud Sync"
                )
            }
            item {
                SyncStatusCard(
                    userProfile = userProfile,
                    isSyncing = isSyncing,
                    onDisconnectGoogleAccount = { viewModel.disconnectGoogleAccount() },
                    onInitiateGoogleSignIn = { viewModel.initiateGoogleSignIn() },
                    onBackupToGoogleDriveNow = { viewModel.backupToGoogleDriveNow() },
                    onRestoreFromDriveClick = { dialogState.showRestoreConfirmDialog = true },
                    onAutoSyncChange = { viewModel.setAutoSync(it) }
                )
            }

            // 6. Data & Storage / Local Backup
            item {
                SettingsSectionHeader(
                    title = if (strings.isBangla) "লোকাল ডেটা ও এক্সপোর্ট" else "Data & Storage"
                )
            }
            item {
                SettingsDataBackupCard(
                    userProfile = userProfile,
                    transactions = transactions,
                    onShareSlipClick = { dialogState.showShareSlipSheet = true },
                    initiallyExpanded = false
                )
            }

            // 7. About App & System Info
            item {
                SettingsAboutAppCard(transactionsCount = transactions.size, categoriesCount = categories.size)
            }

            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }

    SettingsDialogsStateHost(
        viewModel = viewModel,
        userProfile = userProfile,
        categories = categories,
        dialogState = dialogState,
        scope = scope,
        snackbarHostState = snackbarHostState
    )
}
