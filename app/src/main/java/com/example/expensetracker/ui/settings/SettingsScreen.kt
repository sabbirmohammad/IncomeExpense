package com.example.expensetracker.ui.settings

import android.widget.Toast
import com.example.expensetracker.common.AppConstants
import com.example.expensetracker.data.model.toFinancialSummary
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import android.content.Intent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.Work
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.expensetracker.ui.components.CategoryColorHelper
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.StatementExportHelper
import com.example.expensetracker.ui.components.getAvatarIcon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import com.example.expensetracker.ui.components.rememberAppHaptics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.AppThemeMode
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PillFilterChip
import kotlinx.coroutines.launch
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val feedbackController = LocalFeedbackController.current
    if (onNavigateBack != null) {
        BackHandler(onBack = onNavigateBack)
    }

    val userProfile by viewModel.userProfile.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val signInIntent by viewModel.signInIntent.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }
    var showLocalRestoreConfirmDialog by remember { mutableStateOf(false) }
    var selectedLocalBackupUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showAllCategoriesSheet by remember { mutableStateOf(false) }
    var showShareSlipSheet by remember { mutableStateOf(false) }
    var showAiSuccessDialog by remember { mutableStateOf(false) }
    val haptics = rememberAppHaptics()

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedLocalBackupUri = uri
            showLocalRestoreConfirmDialog = true
        }
    }

    val signInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(result.data)
    }

    LaunchedEffect(signInIntent) {
        signInIntent?.let { intent ->
            try {
                signInLauncher.launch(intent)
            } catch (e: Exception) {
                // Ignore
            }
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
        // Header per spec: screen title (18px / 500) + subtitle (12px / 400)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onMenuClick != null) {
                    DrawerMenuButton(onClick = onMenuClick)
                    Spacer(modifier = Modifier.width(12.dp))
                } else if (onNavigateBack != null) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = ColorTextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = strings.settingsTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextPrimary
                )
            }
        }

        // Profile Card
        // Profile Card
        item {
            AppCard(
                onClick = { showEditProfileDialog = true },
                borderColor = ColorAccent.copy(alpha = 0.22f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Elevated Avatar with glowing border and status dot
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                ColorAccent.copy(alpha = 0.25f),
                                                Color(0xFF6366F1).copy(alpha = 0.15f)
                                            )
                                        )
                                    )
                                    .border(1.5.dp, ColorAccent.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (userProfile.avatarIndex == 0 && userProfile.initials.isNotBlank()) {
                                    Text(
                                        text = userProfile.initials.take(2),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = ColorAccent
                                    )
                                } else {
                                    Icon(
                                        imageVector = getAvatarIcon(userProfile.avatarIndex),
                                        contentDescription = null,
                                        tint = ColorAccent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            // Active status dot
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(ColorSuccessIncome)
                                    .border(2.dp, ColorSurface, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = userProfile.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ColorTextPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            // Account badge pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val isCloud = userProfile.googleAccountEmail != null
                                Icon(
                                    imageVector = if (isCloud) Icons.Outlined.CloudDone else Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = if (isCloud) ColorSuccessIncome else ColorTextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = userProfile.googleAccountEmail ?: if (userProfile.appLanguage == "bn") "লোকাল অ্যাকাউন্ট" else "Local Account",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = ColorTextSecondary
                                )
                            }
                        }
                    }

                    // Modern "Edit" pill button
                    Surface(
                        onClick = { showEditProfileDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        color = ColorAccent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (userProfile.appLanguage == "bn") "এডিট" else "Edit",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorAccent
                            )
                        }
                    }
                }
            }
        }

        // Theme Selector Card: System, Dark, Light, AMOLED
        item {
            val appColors = LocalAppColors.current
            val currentTheme = AppThemeMode.fromKey(userProfile.appTheme)

            AppCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ColorAccent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Palette,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = strings.themeSettingTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = appColors.textPrimary
                                )
                                Text(
                                    text = strings.themeSettingSubtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = appColors.textSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(appColors.surface)
                            .border(1.dp, appColors.border.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val themes = listOf(
                            Pair(AppThemeMode.SYSTEM, strings.themeSystem),
                            Pair(AppThemeMode.DARK, strings.themeDark),
                            Pair(AppThemeMode.LIGHT, strings.themeLight),
                            Pair(AppThemeMode.AMOLED, strings.themeAmoled)
                        )

                        themes.forEach { (mode, label) ->
                            val isSelected = currentTheme == mode
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) ColorAccent else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (!isSelected) {
                                            viewModel.setAppTheme(mode.key)
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 9.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                    }
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else appColors.textSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Language Selector Card: বাংলা vs English
        item {
            AppCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ColorAccent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Language,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = strings.languageSection,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = ColorTextPrimary
                                )
                                Text(
                                    text = if (userProfile.appLanguage == "bn") "বর্তমান: বাংলা" else "Current: English",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = ColorTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ColorSurface)
                            .border(1.dp, ColorBorder.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isBangla = userProfile.appLanguage == "bn"

                        // Bangla button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isBangla) ColorAccent else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (!isBangla) {
                                        viewModel.setAppLanguage("bn")
                                        viewModel.setBengaliNumeralsEnabled(true)
                                        com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled = true
                                        Toast.makeText(context, strings.langSwitchedBangla, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 9.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isBangla) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                                Text(
                                    text = "বাংলা",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.5.sp,
                                        fontWeight = if (isBangla) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isBangla) Color.White else ColorTextSecondary
                                )
                            }
                        }

                        // English button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isBangla) ColorAccent else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (isBangla) {
                                        viewModel.setAppLanguage("en")
                                        viewModel.setBengaliNumeralsEnabled(false)
                                        com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled = false
                                        Toast.makeText(context, strings.langSwitchedEnglish, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 9.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!isBangla) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                                Text(
                                    text = "English",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.5.sp,
                                        fontWeight = if (!isBangla) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (!isBangla) Color.White else ColorTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bengali Numerals Switch Card
        item {
            val appColors = LocalAppColors.current
            AppCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ColorAccent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "১২৩",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorAccent
                            )
                        }
                        Column {
                            Text(
                                text = strings.bengaliNumeralsTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = appColors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = strings.bengaliNumeralsSubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = appColors.textSecondary
                            )
                        }
                    }

                    Switch(
                        checked = userProfile.useBengaliNumerals,
                        onCheckedChange = { enabled ->
                            viewModel.setBengaliNumeralsEnabled(enabled)
                            com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled = enabled
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ColorAccent,
                            uncheckedThumbColor = appColors.textMuted,
                            uncheckedTrackColor = appColors.surface
                        )
                    )
                }
            }
        }

        // Modern Category Manager Card
        item {
            AppCard {
                Column {
                    // Header with category badge count and modern "+ New" button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = strings.category,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorTextPrimary
                            )
                            // Pill badge with total category count
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ColorAccent.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = DateTimeHelper.formatCounter(categories.size, strings.isBangla, userProfile.useBengaliNumerals || strings.isBangla),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = ColorAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Modern + New button
                        Surface(
                            onClick = {
                                haptics.lightTick()
                                showAddCategoryDialog = true
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = ColorAccent.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.25f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Add,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (strings.isBangla) "নতুন" else "New",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    ),
                                    color = ColorAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val displayCategories = categories.take(5)
                    displayCategories.forEachIndexed { index, cat ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ColorBackground.copy(alpha = 0.45f),
                            border = BorderStroke(0.5.dp, ColorBorder.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    CategoryIconBadge(
                                        iconName = cat.icon,
                                        colorHex = cat.colorHex,
                                        size = 36.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(
                                            text = cat.getLocalizedName(strings.isBangla),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = ColorTextPrimary
                                        )
                                        // Type pill
                                        val isIncome = cat.type == TransactionType.INCOME
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isIncome) ColorSuccessIncome.copy(alpha = 0.12f) else ColorDangerExpense.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = if (isIncome) (if (strings.isBangla) "↑ আয়" else "↑ Income") else (if (strings.isBangla) "↓ ব্যয়" else "↓ Expense"),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = if (isIncome) ColorSuccessIncome else ColorDangerExpense,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            haptics.lightTick()
                                            categoryToEdit = cat
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Edit,
                                            contentDescription = "Edit",
                                            tint = ColorTextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            haptics.warningFeedback()
                                            categoryToDelete = cat
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "Delete",
                                            tint = ColorDangerExpense.copy(alpha = 0.85f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (categories.size > 5) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            onClick = {
                                haptics.lightTick()
                                showAllCategoriesSheet = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = ColorBackground.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, ColorBorder.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (strings.isBangla) "সব ক্যাটাগরি দেখুন (${categories.size})" else "View all categories (${categories.size})",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = ColorAccent
                                )
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Google Drive Backup Card: title + explanation, connection status, Back up / Restore, Auto sync, Last sync
        item {
            AppCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.googleDriveBackup,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorTextPrimary
                        )

                        if (userProfile.googleAccountEmail != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(SmallElementRadius))
                                    .background(ColorSuccessIncome.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = strings.connected,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = ColorSuccessIncome
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.googleDriveBackupDesc,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = ColorTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (userProfile.googleAccountEmail != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(SmallElementRadius))
                                .background(ColorSurface)
                                .border(1.dp, ColorBorder, RoundedCornerShape(SmallElementRadius))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = userProfile.googleAccountEmail ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = ColorTextPrimary,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = strings.disconnect,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = ColorDangerExpense,
                                modifier = Modifier
                                    .clickable { viewModel.disconnectGoogleAccount() }
                                    .padding(4.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.initiateGoogleSignIn() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(SmallElementRadius),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudSync,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.connectGoogleDrive,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Backup & Restore button pair
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.backupToGoogleDriveNow() },
                            enabled = !isSyncing,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(SmallElementRadius),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.CloudUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strings.backupNow,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = Color.White
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showRestoreConfirmDialog = true },
                            enabled = !isSyncing,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(SmallElementRadius),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = ColorSurface,
                                contentColor = ColorTextPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = ColorTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.restoreFromDrive,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = ColorTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Auto Daily Sync toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.autoCloudSync,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = ColorTextPrimary
                            )
                            Text(
                                text = strings.autoCloudSyncDesc,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary
                            )
                        }
                        Switch(
                            checked = userProfile.isAutoSyncEnabled,
                            onCheckedChange = { viewModel.setAutoSync(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ColorAccent,
                                uncheckedThumbColor = ColorTextSecondary,
                                uncheckedTrackColor = ColorBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Last Sync Status row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.lastSyncStatus,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = ColorTextSecondary
                        )
                        Text(
                            text = if (userProfile.lastSyncMillis > 0)
                                DateTimeHelper.formatFullDateTime(userProfile.lastSyncMillis, strings.isBangla, userProfile.useBengaliNumerals || strings.isBangla)
                            else
                                strings.notSyncedYet,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorTextPrimary
                        )
                    }
                }
            }
        }

        // Data Export Card
        item {
            AppCard {
                Column {
                    Text(
                        text = "Data export",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Export your financial records for spreadsheets or external tools.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = ColorTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val csv = viewModel.getCsvData()
                                    clipboardManager.setText(AnnotatedString(csv))
                                    Toast.makeText(context, strings.csvCopied, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(SmallElementRadius),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorSurface)
                        ) {
                            Text(strings.copyCsv, color = ColorTextPrimary, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val json = viewModel.getJsonBackup()
                                    clipboardManager.setText(AnnotatedString(json))
                                    Toast.makeText(context, strings.jsonBackupCopied, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(SmallElementRadius),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorSurface)
                        ) {
                            Text(strings.copyJson, color = ColorTextPrimary, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val csv = viewModel.getCsvData()
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, csv)
                                        type = "text/plain"
                                    }
                                    val chooser = Intent.createChooser(sendIntent, strings.shareCsvData)
                                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(chooser)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(SmallElementRadius),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorSurface)
                        ) {
                            Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(15.dp), tint = ColorAccent)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.shareCsv, color = ColorTextPrimary, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val json = viewModel.getJsonBackup()
                                    val shared = StatementExportHelper.exportAndShareJsonBackup(context, json)
                                    if (!shared) {
                                        Toast.makeText(context, strings.shareBackupError, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(SmallElementRadius),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorSurface)
                        ) {
                            Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(15.dp), tint = ColorAccent)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.shareJson, color = ColorTextPrimary, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            try {
                                filePickerLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                            } catch (_: Exception) {
                                Toast.makeText(context, strings.noFilePicker, Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(SmallElementRadius),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ColorAccent.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = ColorAccent
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.importJsonBackup,
                            color = ColorTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HairlineDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Official Statements (PDF & Excel)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Generate printable A4 PDF statement or UTF-8 BOM CSV sheet for all records.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. PDF Report Tile
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    val summary = transactions.toFinancialSummary()
                                    StatementExportHelper.exportAndSharePdf(
                                        context = context,
                                        profile = userProfile,
                                        summary = summary,
                                        transactions = transactions,
                                        periodTitle = if (strings.isBangla) "সম্পূর্ণ স্টেটমেন্ট" else "Complete Statement"
                                    )
                                },
                            color = ColorBackground,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, ColorBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ColorDangerExpense.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PictureAsPdf,
                                        contentDescription = null,
                                        tint = ColorDangerExpense,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (strings.isBangla) "PDF রিপোর্ট" else "PDF Report",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = ColorTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (strings.isBangla) "প্রিন্টযোগ্য A4" else "Print A4",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = ColorTextMuted,
                                    maxLines = 1
                                )
                            }
                        }

                        // 2. Image Slip Tile
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    showShareSlipSheet = true
                                },
                            color = ColorBackground,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, ColorBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ColorAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Image,
                                        contentDescription = null,
                                        tint = ColorAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (strings.isBangla) "ইমেজ স্লিপ" else "Image Slip",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = ColorTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (strings.isBangla) "শেয়ারযোগ্য" else "Shareable",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = ColorTextMuted,
                                    maxLines = 1
                                )
                            }
                        }

                        // 3. Excel (CSV) Tile
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    StatementExportHelper.exportAndShareCsv(
                                        context = context,
                                        profile = userProfile,
                                        transactions = transactions,
                                        periodTitle = if (strings.isBangla) "সম্পূর্ণ বিবরণী" else "Complete Statement"
                                    )
                                },
                            color = ColorBackground,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, ColorBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ColorSuccessIncome.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.TableChart,
                                        contentDescription = null,
                                        tint = ColorSuccessIncome,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (strings.isBangla) "এক্সেল (CSV)" else "Excel (CSV)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = ColorTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (strings.isBangla) "স্প্রেডশীট" else "Sheets",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = ColorTextMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Modern Gemini AI Configuration Card
        item {
            val focusManager = LocalFocusManager.current
            var apiKeyInput by remember(userProfile.geminiApiKey) { mutableStateOf(userProfile.geminiApiKey ?: "") }
            var isKeyVisible by remember { mutableStateOf(false) }
            val apiConnectionTest by viewModel.apiConnectionTest.collectAsState()
            val isTestingApi by viewModel.isTestingApi.collectAsState()
            val uriHandler = LocalUriHandler.current
            val isConfigured = !userProfile.geminiApiKey.isNullOrBlank()

            AppCard(
                backgroundColor = Color(0xFF131525),
                borderColor = ColorAccent.copy(alpha = 0.35f)
            ) {
                Column {
                    // Header: AI icon, Title, Subtitle, and Connection Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                ColorAccent.copy(alpha = 0.25f),
                                                Color(0xFF8B5CF6).copy(alpha = 0.25f)
                                            )
                                        )
                                    )
                                    .border(1.dp, ColorAccent.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ReceiptLong,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (userProfile.appLanguage == "bn") "স্মার্ট রসিদ স্ক্যানার (Gemini API)" else "Receipt Scanner (Gemini API)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 15.5.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = ColorTextPrimary
                                )
                                Text(
                                    text = if (userProfile.appLanguage == "bn") "রসিদের ছবি ও টেক্সট থেকে দ্রুত হিসাব তুলতে" else "For receipt OCR & smart quick text input",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp
                                    ),
                                    color = ColorTextSecondary
                                )
                            }
                        }

                        // Status badge (pill with indicator dot)
                        val statusText: String
                        val statusColor: Color
                        val statusBg: Color
                        when {
                            isTestingApi -> {
                                statusText = if (userProfile.appLanguage == "bn") "যাচাই হচ্ছে..." else "Testing..."
                                statusColor = ColorAccent
                                statusBg = ColorAccent.copy(alpha = 0.12f)
                            }
                            apiConnectionTest?.isSuccess == true -> {
                                statusText = if (userProfile.appLanguage == "bn") "সংযুক্ত (${apiConnectionTest!!.latencyMs}ms)" else "Active (${apiConnectionTest!!.latencyMs}ms)"
                                statusColor = ColorSuccessIncome
                                statusBg = ColorSuccessIncome.copy(alpha = 0.12f)
                            }
                            apiConnectionTest?.isSuccess == false -> {
                                statusText = if (userProfile.appLanguage == "bn") "সংযোগ ব্যর্থ" else "Failed"
                                statusColor = ColorDangerExpense
                                statusBg = ColorDangerExpense.copy(alpha = 0.12f)
                            }
                            isConfigured -> {
                                statusText = if (userProfile.appLanguage == "bn") "সংযুক্ত" else "Active"
                                statusColor = ColorSuccessIncome
                                statusBg = ColorSuccessIncome.copy(alpha = 0.12f)
                            }
                            else -> {
                                statusText = if (userProfile.appLanguage == "bn") "অসংযুক্ত" else "Offline"
                                statusColor = ColorTextMuted
                                statusBg = ColorSurface
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = statusBg,
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = statusColor
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // API Key Input Field (Full Width)
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        placeholder = {
                            Text(
                                text = if (userProfile.appLanguage == "bn") "এপিআই কি পেস্ট করুন (AIzaSy...)" else "Paste API key (AIzaSy...)",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColorTextMuted
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            val trimmed = apiKeyInput.trim()
                            if (trimmed.isNotBlank()) {
                                viewModel.setGeminiApiKey(trimmed)
                            }
                        }),
                        trailingIcon = {
                            // Only visibility toggle icon (paste is done directly via system clipboard)
                            IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                Icon(
                                    imageVector = if (isKeyVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (isKeyVisible) "Hide key" else "Show key",
                                    tint = if (isKeyVisible) ColorAccent else ColorTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorAccent,
                            unfocusedBorderColor = ColorBorder,
                            focusedContainerColor = Color(0xFF0F111E),
                            unfocusedContainerColor = Color(0xFF0F111E),
                            focusedTextColor = ColorTextPrimary,
                            unfocusedTextColor = ColorTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons Row directly BELOW API Key Input (Clear, Test, Save)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Clear Button
                        OutlinedButton(
                            onClick = {
                                focusManager.clearFocus()
                                haptics.warningFeedback()
                                apiKeyInput = ""
                                viewModel.setGeminiApiKey(null)
                                viewModel.clearApiTestResult()
                            },
                            enabled = apiKeyInput.isNotBlank() || isConfigured,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ColorDangerExpense
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (apiKeyInput.isNotBlank() || isConfigured) ColorDangerExpense.copy(alpha = 0.55f) else ColorBorder.copy(alpha = 0.3f)
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = if (apiKeyInput.isNotBlank() || isConfigured) ColorDangerExpense else ColorTextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (userProfile.appLanguage == "bn") "ক্লিয়ার" else "Clear",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                ),
                                color = if (apiKeyInput.isNotBlank() || isConfigured) ColorDangerExpense else ColorTextMuted
                            )
                        }

                        // Test Connection Button
                        OutlinedButton(
                            onClick = {
                                focusManager.clearFocus()
                                haptics.mediumClick()
                                val keyToTest = apiKeyInput.trim().ifEmpty { userProfile.geminiApiKey ?: "" }
                                if (keyToTest.isNotBlank()) {
                                    viewModel.testGeminiApiKey(keyToTest)
                                }
                            },
                            enabled = !isTestingApi && (apiKeyInput.isNotBlank() || isConfigured),
                            modifier = Modifier
                                .weight(1.05f)
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ColorAccent
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (!isTestingApi && (apiKeyInput.isNotBlank() || isConfigured)) ColorAccent.copy(alpha = 0.55f) else ColorBorder.copy(alpha = 0.3f)
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            if (isTestingApi) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 1.8.dp,
                                    color = ColorAccent
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.ReceiptLong,
                                    contentDescription = null,
                                    tint = if (apiKeyInput.isNotBlank() || isConfigured) ColorAccent else ColorTextMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (userProfile.appLanguage == "bn") "যাচাই" else "Test",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                ),
                                color = if (!isTestingApi && (apiKeyInput.isNotBlank() || isConfigured)) ColorAccent else ColorTextMuted
                            )
                        }

                        // Save Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                val trimmed = apiKeyInput.trim()
                                if (trimmed.isNotBlank()) {
                                    viewModel.setGeminiApiKey(trimmed)
                                    viewModel.testGeminiApiKey(trimmed)
                                    haptics.successFeedback()
                                    showAiSuccessDialog = true
                                }
                            },
                            enabled = apiKeyInput.isNotBlank(),
                            modifier = Modifier
                                .weight(1.25f)
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorAccent,
                                disabledContainerColor = ColorAccent.copy(alpha = 0.25f)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = if (apiKeyInput.isNotBlank()) Color.White else ColorTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (userProfile.appLanguage == "bn") "সেভ করুন" else "Save Key",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = if (apiKeyInput.isNotBlank()) Color.White else ColorTextMuted
                            )
                        }
                    }

                    // Test Connection Result Banner
                    if (apiConnectionTest != null) {
                        val testRes = apiConnectionTest!!
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (testRes.isSuccess) ColorSuccessIncome.copy(alpha = 0.12f) else ColorDangerExpense.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, if (testRes.isSuccess) ColorSuccessIncome.copy(alpha = 0.35f) else ColorDangerExpense.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (testRes.isSuccess) Icons.Filled.CheckCircle else Icons.Outlined.Clear,
                                    contentDescription = null,
                                    tint = if (testRes.isSuccess) ColorSuccessIncome else ColorDangerExpense,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (testRes.isSuccess) {
                                        if (userProfile.appLanguage == "bn")
                                            "✓ সক্রিয় (${testRes.modelUsed} • ${testRes.latencyMs}ms)"
                                        else
                                            "✓ Active (${testRes.modelUsed} • ${testRes.latencyMs}ms)"
                                    } else {
                                        if (userProfile.appLanguage == "bn")
                                            "✕ ত্রুটি: ${testRes.message}"
                                        else
                                            "✕ Error: ${testRes.message}"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = if (testRes.isSuccess) ColorSuccessIncome else ColorDangerExpense
                                )
                            }
                        }
                    }

                    // Privacy & Security reassurance note
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = strings.apiKeyPrivacyNotice,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        ),
                        color = ColorTextMuted
                    )
                }
            }
        }

        // App Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${transactions.size} transactions · ${categories.size} categories active",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${AppConstants.APP_NAME} ${AppConstants.APP_VERSION}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = ColorTextMuted
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Develop by AWFI ENTERPRISE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorAccent.copy(alpha = 0.9f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }

    // All Categories Sheet
    if (showAllCategoriesSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAllCategoriesSheet = false },
            sheetState = sheetState,
            containerColor = ColorSurface,
            shape = RoundedCornerShape(topStart = CardCornerRadius, topEnd = CardCornerRadius)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (strings.isBangla) "সব ক্যাটাগরি (${categories.size})" else "All categories (${categories.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextPrimary
                    )
                    Text(
                        text = if (strings.isBangla) "+ নতুন" else "+ New",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorAccent,
                        modifier = Modifier
                            .clickable {
                                showAllCategoriesSheet = false
                                showAddCategoryDialog = true
                            }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories, key = { it.id }) { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                CategoryIconBadge(
                                    iconName = cat.icon,
                                    colorHex = cat.colorHex,
                                    size = 36.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = cat.getLocalizedName(strings.isBangla),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Normal
                                        ),
                                        color = ColorTextPrimary
                                    )
                                    Text(
                                        text = if (cat.type == TransactionType.INCOME) strings.income else strings.expense,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = ColorTextMuted
                                    )
                                }
                            }

                            Row {
                                IconButton(onClick = {
                                    showAllCategoriesSheet = false
                                    categoryToEdit = cat
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = "Edit",
                                        tint = ColorTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(onClick = {
                                    showAllCategoriesSheet = false
                                    categoryToDelete = cat
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete",
                                        tint = ColorDangerExpense,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        HairlineDivider()
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var firstNameInput by remember { mutableStateOf(userProfile.firstName) }
        var lastNameInput by remember { mutableStateOf(userProfile.lastName) }
        var selectedAvatar by remember { mutableStateOf(userProfile.avatarIndex) }

        val effectiveFirst = firstNameInput.trim()
        val isFirstValid = effectiveFirst.isNotEmpty()
        val dashboardNamePreview = if (isFirstValid) effectiveFirst else "..."

        val avatarOptions = listOf(
            0 to "Initials",
            1 to "Person",
            2 to "Smile",
            3 to "Star",
            4 to "Rocket",
            5 to "Work"
        )

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            titleContentColor = ColorTextPrimary,
            textContentColor = ColorTextSecondary,
            title = {
                Text(
                    text = strings.editProfileTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // First Name Field
                    Text(
                        text = strings.firstNameLabel,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                        color = ColorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = firstNameInput,
                        onValueChange = { firstNameInput = it },
                        placeholder = { Text(strings.egFirstName, color = ColorTextMuted) },
                        shape = RoundedCornerShape(SmallElementRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ColorSurface,
                            unfocusedContainerColor = ColorSurface,
                            focusedBorderColor = ColorAccent,
                            unfocusedBorderColor = ColorBorder,
                            focusedTextColor = ColorTextPrimary,
                            unfocusedTextColor = ColorTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Last Name Field
                    Text(
                        text = strings.lastNameLabel,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                        color = ColorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = lastNameInput,
                        onValueChange = { lastNameInput = it },
                        placeholder = { Text(strings.egLastName, color = ColorTextMuted) },
                        shape = RoundedCornerShape(SmallElementRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ColorSurface,
                            unfocusedContainerColor = ColorSurface,
                            focusedBorderColor = ColorAccent,
                            unfocusedBorderColor = ColorBorder,
                            focusedTextColor = ColorTextPrimary,
                            unfocusedTextColor = ColorTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Avatar Picker
                    Text(
                        text = strings.chooseAvatarLabel,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                        color = ColorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        avatarOptions.forEach { (idx, _) ->
                            val isSel = selectedAvatar == idx
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSel) ColorAccent.copy(alpha = 0.25f) else ColorBackground)
                                    .border(
                                        width = if (isSel) 2.dp else 1.dp,
                                        color = if (isSel) ColorAccent else ColorBorder,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatar = idx },
                                contentAlignment = Alignment.Center
                            ) {
                                if (idx == 0) {
                                    val init = when {
                                        firstNameInput.isNotBlank() && lastNameInput.isNotBlank() ->
                                            "${firstNameInput.trim().first()}${lastNameInput.trim().first()}".uppercase()
                                        firstNameInput.isNotBlank() -> firstNameInput.trim().take(2).uppercase()
                                        else -> "U"
                                    }
                                    Text(
                                        text = init,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (isSel) ColorAccent else ColorTextSecondary
                                    )
                                } else {
                                    Icon(
                                        imageVector = getAvatarIcon(idx),
                                        contentDescription = null,
                                        tint = if (isSel) ColorAccent else ColorTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Dashboard Preview Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(SmallElementRadius))
                            .background(ColorAccent.copy(alpha = 0.1f))
                            .border(1.dp, ColorAccent.copy(alpha = 0.3f), RoundedCornerShape(SmallElementRadius))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = strings.dashboardPreviewLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ColorAccent
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = strings.previewGreeting(dashboardNamePreview),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ColorTextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isFirstValid) {
                            viewModel.updateProfile(
                                firstName = firstNameInput.trim(),
                                lastName = lastNameInput.trim(),
                                avatarIndex = selectedAvatar
                            )
                            Toast.makeText(context, strings.profileUpdatedSuccess, Toast.LENGTH_SHORT).show()
                            showEditProfileDialog = false
                        }
                    },
                    enabled = isFirstValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorAccent,
                        disabledContainerColor = ColorBorder
                    ),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.save, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Add / Edit Category Dialog
    val isEditingCategory = categoryToEdit != null
    if (showAddCategoryDialog || isEditingCategory) {
        val initialColor = remember(categoryToEdit, categories) {
            categoryToEdit?.colorHex ?: CategoryColorHelper.getAvailableColor(categories)
        }
        var catName by remember { mutableStateOf(categoryToEdit?.name ?: "") }
        var catType by remember { mutableStateOf(categoryToEdit?.type ?: TransactionType.EXPENSE) }
        var catIcon by remember { mutableStateOf(categoryToEdit?.icon ?: "ShoppingCart") }
        var catColor by remember(categoryToEdit) { mutableStateOf(initialColor) }
        var selectedIconGroup by remember { mutableStateOf("All") }
        var colorWarningMessage by remember { mutableStateOf<String?>(null) }

        val displayedIcons = remember(selectedIconGroup) {
            if (selectedIconGroup == "All") CategoryIconHelper.allIcons
            else CategoryIconHelper.allIcons.filter { it.group == selectedIconGroup }
        }

        AlertDialog(
            onDismissRequest = {
                showAddCategoryDialog = false
                categoryToEdit = null
            },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            titleContentColor = ColorTextPrimary,
            textContentColor = ColorTextSecondary,
            title = {
                Text(
                    text = if (isEditingCategory) (if (strings.isBangla) "ক্যাটাগরি সম্পাদনা" else "Edit category") else (if (strings.isBangla) "নতুন ক্যাটাগরি" else "New category"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // Live Category Preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(SmallElementRadius))
                            .background(ColorBackground)
                            .border(1.dp, ColorBorder, RoundedCornerShape(SmallElementRadius))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryIconBadge(
                            iconName = catIcon,
                            colorHex = catColor,
                            size = 40.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (catName.isBlank()) (if (strings.isBangla) "ক্যাটাগরি প্রিভিউ" else "Category preview") else catName,
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                color = ColorTextPrimary
                            )
                            Text(
                                text = if (strings.isBangla) (if (catType == TransactionType.INCOME) "আয় ক্যাটাগরি" else "ব্যয় ক্যাটাগরি") else "${catType.name.lowercase().replaceFirstChar { it.uppercase() }} Category",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (strings.isBangla) "ক্যাটাগরির নাম" else "Category name",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = ColorTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = catName,
                        onValueChange = { catName = it },
                        placeholder = { Text(if (strings.isBangla) "যেমন: বাজার, জিম, রেস্তোরাঁ" else "e.g. Groceries, Gym, Dining", color = ColorTextMuted) },
                        shape = RoundedCornerShape(SmallElementRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ColorSurface,
                            unfocusedContainerColor = ColorSurface,
                            focusedBorderColor = ColorAccent,
                            unfocusedBorderColor = ColorBorder,
                            focusedTextColor = ColorTextPrimary,
                            unfocusedTextColor = ColorTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (strings.isBangla) "ক্যাটাগরির ধরন" else "Category type",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = ColorTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PillFilterChip(
                            text = strings.expense,
                            selected = catType == TransactionType.EXPENSE,
                            onClick = { catType = TransactionType.EXPENSE }
                        )
                        PillFilterChip(
                            text = strings.income,
                            selected = catType == TransactionType.INCOME,
                            onClick = { catType = TransactionType.INCOME }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (strings.isBangla) "আইকন নির্বাচন করুন (${displayedIcons.size})" else "Select icon (${displayedIcons.size})",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = ColorTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // Category Group Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CategoryIconHelper.iconGroups.forEach { group ->
                            val isSelected = selectedIconGroup == group
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) ColorAccent else ColorBackground,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
                                modifier = Modifier.clickable { selectedIconGroup = group }
                            ) {
                                Text(
                                    text = group,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = if (isSelected) Color.White else ColorTextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        displayedIcons.forEach { iconItem ->
                            val isSel = catIcon == iconItem.name
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(SmallElementRadius))
                                    .background(if (isSel) ColorAccent.copy(alpha = 0.22f) else ColorBackground)
                                    .border(
                                        1.5.dp,
                                        if (isSel) ColorAccent else ColorBorder,
                                        RoundedCornerShape(SmallElementRadius)
                                    )
                                    .clickable { catIcon = iconItem.name },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CategoryIconHelper.getIcon(iconItem.name),
                                    contentDescription = iconItem.label,
                                    tint = if (isSel) ColorAccent else ColorTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.selectUniqueColor,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                            color = ColorTextSecondary
                        )
                        Text(
                            text = "🎲 " + strings.pickNextColor,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = ColorAccent,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    val nextColor = CategoryColorHelper.getAvailableColor(
                                        categories.filter { !it.colorHex.equals(catColor, ignoreCase = true) },
                                        categoryToEdit?.id
                                    )
                                    catColor = nextColor
                                    colorWarningMessage = null
                                    haptics.lightTick()
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryColorHelper.distinctColors.forEach { hex ->
                            val parsed = CategoryIconHelper.parseColor(hex)
                            val isSel = catColor.equals(hex, ignoreCase = true)
                            val usedBy = CategoryColorHelper.getCategoryUsingColor(hex, categories, categoryToEdit?.id)
                            val isTaken = usedBy != null

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isTaken) parsed.copy(alpha = 0.28f) else parsed)
                                    .then(
                                        when {
                                            isSel -> Modifier.border(2.5.dp, Color.White, CircleShape)
                                            isTaken -> Modifier.border(1.dp, ColorBorder.copy(alpha = 0.4f), CircleShape)
                                            else -> Modifier.border(1.dp, ColorBorder, CircleShape)
                                        }
                                    )
                                    .clickable {
                                        if (isTaken) {
                                            haptics.lightTick()
                                            colorWarningMessage = strings.colorUsedByFormat(usedBy.getLocalizedName(strings.isBangla))
                                        } else {
                                            haptics.lightTick()
                                            catColor = hex
                                            colorWarningMessage = null
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSel) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isTaken) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.6f))
                                    )
                                }
                            }
                        }
                    }

                    if (colorWarningMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = colorWarningMessage!!,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                            color = ColorDangerExpense
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (catName.isNotBlank()) {
                            val conflictingCat = CategoryColorHelper.getCategoryUsingColor(catColor, categories, categoryToEdit?.id)
                            if (conflictingCat != null) {
                                colorWarningMessage = strings.colorUsedByFormat(conflictingCat.getLocalizedName(strings.isBangla))
                                haptics.lightTick()
                                return@Button
                            }

                            val savedName = catName.trim()
                            if (isEditingCategory) {
                                viewModel.updateCategory(
                                    categoryToEdit!!.copy(
                                        name = savedName,
                                        type = catType,
                                        icon = catIcon,
                                        colorHex = catColor
                                    )
                                )
                                feedbackController.show(
                                    TransactionFeedbackData(
                                        type = FeedbackVisualType.GENERIC_SUCCESS,
                                        title = if (strings.isBangla) "ক্যাটাগরি আপডেট হয়েছে!" else "Category Updated!",
                                        subtitle = savedName
                                    )
                                )
                            } else {
                                viewModel.addCategory(savedName, catType, catIcon, catColor)
                                feedbackController.show(
                                    TransactionFeedbackData(
                                        type = FeedbackVisualType.GENERIC_SUCCESS,
                                        title = if (strings.isBangla) "নতুন ক্যাটাগরি তৈরি হয়েছে!" else "Category Created!",
                                        subtitle = savedName
                                    )
                                )
                            }
                            showAddCategoryDialog = false
                            categoryToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.save, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddCategoryDialog = false
                    categoryToEdit = null
                }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Delete Category Confirm Dialog
    if (categoryToDelete != null) {
        val cat = categoryToDelete!!
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            titleContentColor = ColorTextPrimary,
            textContentColor = ColorTextSecondary,
            title = {
                Text(
                    text = if (strings.isBangla) "ক্যাটাগরি মুছে ফেলবেন?" else "Delete category?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            text = {
                Text(
                    text = if (strings.isBangla) "আপনি কি নিশ্চিতভাবে \"${cat.getLocalizedName(strings.isBangla)}\" ক্যাটাগরিটি মুছে ফেলতে চান? এই ক্যাটাগরির লেনদেনগুলো প্রভাবিত হতে পারে।" else "Are you sure you want to delete \"${cat.name}\"? Any transactions in this category will be affected.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val deletedCatName = cat.getLocalizedName(strings.isBangla)
                        viewModel.deleteCategory(cat.id)
                        categoryToDelete = null
                        feedbackController.show(
                            TransactionFeedbackData(
                                type = FeedbackVisualType.ITEM_DELETED,
                                title = FeedbackStrings.itemDeleted(strings.isBangla),
                                subtitle = deletedCatName
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.delete, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Restore Confirmation Dialog
    if (showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirmDialog = false },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            titleContentColor = ColorTextPrimary,
            textContentColor = ColorTextSecondary,
            title = {
                Text(
                    text = strings.googleDriveRestoreTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            text = {
                Text(
                    text = strings.googleDriveRestoreDesc,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRestoreConfirmDialog = false
                        viewModel.restoreFromGoogleDrive()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.restoreNow, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirmDialog = false }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    // Local JSON File Restore Confirmation Dialog
    if (showLocalRestoreConfirmDialog && selectedLocalBackupUri != null) {
        AlertDialog(
            onDismissRequest = {
                showLocalRestoreConfirmDialog = false
                selectedLocalBackupUri = null
            },
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = ColorSurface,
            titleContentColor = ColorTextPrimary,
            textContentColor = ColorTextSecondary,
            title = {
                Text(
                    text = strings.restoreFromJsonTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            text = {
                Text(
                    text = strings.restoreFromJsonDesc,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uri = selectedLocalBackupUri
                        showLocalRestoreConfirmDialog = false
                        selectedLocalBackupUri = null
                        if (uri != null) {
                            viewModel.restoreFromUri(uri)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                    shape = RoundedCornerShape(SmallElementRadius)
                ) {
                    Text(strings.restoreNow, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLocalRestoreConfirmDialog = false
                    selectedLocalBackupUri = null
                }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        )
    }

    if (showShareSlipSheet) {
        ShareExpenseSheet(
            initialPeriod = SharePeriodType.THIS_MONTH,
            onDismiss = { showShareSlipSheet = false }
        )
    }

    if (showAiSuccessDialog) {
        AiConnectionSuccessDialog(
            isBangla = strings.isBangla,
            onDismiss = { showAiSuccessDialog = false }
        )
    }
}

@Composable
fun AiConnectionSuccessDialog(
    isBangla: Boolean,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    LaunchedEffect(Unit) {
        haptics.successFeedback()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "aiCelebration")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = ColorSurface,
        titleContentColor = ColorTextPrimary,
        textContentColor = ColorTextSecondary,
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(
                    text = if (isBangla) "দারুণ, শুরু করুন! 🎉" else "Awesome, Let's Go! 🎉",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Glowing AI Sparkle Ring
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsing glow
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        ColorAccent.copy(alpha = glowAlpha),
                                        Color(0xFF8B5CF6).copy(alpha = glowAlpha * 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Inner decorative circle with gradient
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(ColorAccent, Color(0xFF8B5CF6))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ReceiptLong,
                            contentDescription = "Receipt Scanner",
                            tint = Color.White,
                            modifier = Modifier
                                .size(34.dp)
                                .scale(pulseScale)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Celebration Title
                Text(
                    text = if (isBangla) "রসিদ স্ক্যানার সক্রিয় হয়েছে!" else "Receipt Scanner Connected!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ColorTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = strings.geminiConnectedSuccessDesc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = ColorTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Feature Highlights Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    listOf<String>(
                        strings.featureReceiptOcr,
                        strings.featureMagicFill,
                        strings.featureSecureOnDevice
                    ).forEach { feature ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ColorAccent.copy(alpha = 0.1f),
                            border = BorderStroke(0.5.dp, ColorAccent.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}
