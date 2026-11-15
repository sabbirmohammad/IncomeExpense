package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.Space16
import com.example.expensetracker.theme.Space20

/**
 * Debug-only Component Gallery displaying all Refined Ledger v7 base components
 * across themes (Dark, Light, AMOLED) and languages (English, Bengali).
 */
@Composable
fun ComponentGalleryScreen(
    isBengali: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val strings = if (isBengali) BanglaStrings else EnglishStrings

    var selectedSegment by remember { mutableIntStateOf(0) }
    var filterSelected by remember { mutableStateOf(true) }
    var choiceSelected by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalAppStrings provides strings
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colors.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space20, vertical = Space16),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isBengali) "ভি৭ উপাদান গ্যালারি" else "v7 Component Gallery",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )

            GalleryAmountTextSection(isBengali = isBengali)

            GalleryButtonsSection(isBengali = isBengali)

            GalleryChipsSection(
                isBengali = isBengali,
                filterSelected = filterSelected,
                onFilterToggle = { filterSelected = !filterSelected },
                choiceSelected = choiceSelected,
                onChoiceToggle = { choiceSelected = !choiceSelected }
            )

            GalleryCardsAndBannersSection(
                isBengali = isBengali,
                selectedSegment = selectedSegment,
                onSegmentSelected = { selectedSegment = it }
            )

            GalleryFeedbackAndStatesSection(isBengali = isBengali)

            GalleryPickersAndInputsSection(isBengali = isBengali)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
