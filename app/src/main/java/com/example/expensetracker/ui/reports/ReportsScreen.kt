package com.example.expensetracker.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()
    val periodTitle = viewModel.getPeriodTitle(strings.isBangla)

    Box(modifier = modifier.fillMaxSize().background(LocalAppColors.current.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ReportsTopBar(
                periodTitle = periodTitle,
                isPrivacyMode = isPrivacyMode,
                strings = strings,
                onTogglePrivacy = { viewModel.togglePrivacyMode() },
                onNavigateBack = onNavigateBack,
                onMenuClick = onMenuClick
            )

            HairlineDivider()

            ReportsContent(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
