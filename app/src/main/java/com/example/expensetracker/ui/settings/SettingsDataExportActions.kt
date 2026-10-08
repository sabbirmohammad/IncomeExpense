package com.example.expensetracker.ui.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.StatementExportHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SettingsDataExportActions(
    private val context: Context,
    private val strings: Strings,
    private val clipboardManager: ClipboardManager,
    private val scope: CoroutineScope,
    private val viewModel: SettingsViewModel
) {
    fun copyCsv() {
        scope.launch {
            val csv = viewModel.getCsvData()
            clipboardManager.setText(AnnotatedString(csv))
            Toast.makeText(context, strings.csvCopied, Toast.LENGTH_SHORT).show()
        }
    }

    fun copyJson() {
        scope.launch {
            val json = viewModel.getJsonBackup()
            clipboardManager.setText(AnnotatedString(json))
            Toast.makeText(context, strings.jsonBackupCopied, Toast.LENGTH_SHORT).show()
        }
    }

    fun shareCsv() {
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
    }

    fun shareJson() {
        scope.launch {
            val json = viewModel.getJsonBackup()
            val shared = StatementExportHelper.exportAndShareJsonBackup(context, json)
            if (!shared) {
                Toast.makeText(context, strings.shareBackupError, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
