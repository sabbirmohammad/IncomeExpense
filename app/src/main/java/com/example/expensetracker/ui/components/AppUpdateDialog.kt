package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.expensetracker.data.update.AppUpdateManager
import com.example.expensetracker.data.update.UpdateState
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun AppUpdateDialog(
    modifier: Modifier = Modifier
) {
    val isVisible by AppUpdateManager.isDialogVisible.collectAsState()
    val updateState by AppUpdateManager.updateState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val isBangla = strings.isBangla

    if (!isVisible) return

    val currentVersion = remember(context) {
        AppUpdateManager.getCurrentVersion(context)
    }

    Dialog(
        onDismissRequest = {
            if (updateState !is UpdateState.Downloading) {
                AppUpdateManager.dismissDialog()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = updateState !is UpdateState.Downloading,
            dismissOnClickOutside = updateState !is UpdateState.Downloading
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(26.dp),
            color = appColors.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (updateState !is UpdateState.Downloading) {
                        IconButton(
                            onClick = { AppUpdateManager.dismissDialog() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isBangla) "বন্ধ করুন" else "Close",
                                tint = appColors.textMuted
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }

                AnimatedUpdateHeaderIcon(updateState = updateState)

                Spacer(modifier = Modifier.height(16.dp))

                UpdateDialogTitleSubtitle(
                    updateState = updateState,
                    currentVersion = currentVersion,
                    isBangla = isBangla
                )

                Spacer(modifier = Modifier.height(16.dp))

                // State-specific Content & Actions
                when (val state = updateState) {
                    is UpdateState.UpToDate -> {
                        UpdateUpToDateSection(
                            state = state,
                            isBangla = isBangla
                        )
                    }
                    is UpdateState.Available -> {
                        UpdateAvailableSection(
                            state = state,
                            isBangla = isBangla,
                            context = context,
                            coroutineScope = coroutineScope
                        )
                    }
                    is UpdateState.Downloading -> {
                        DownloadProgressSection(
                            state = state,
                            isBangla = isBangla
                        )
                    }
                    is UpdateState.ReadyToInstall -> {
                        UpdateReadyToInstallSection(
                            state = state,
                            isBangla = isBangla,
                            context = context
                        )
                    }
                    is UpdateState.Error -> {
                        UpdateErrorSection(
                            state = state,
                            isBangla = isBangla,
                            context = context,
                            coroutineScope = coroutineScope
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}
