package com.example.expensetracker.ui.market

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.MarketSearchBar
import com.example.expensetracker.ui.components.rememberAppHaptics
import kotlinx.coroutines.delay

@Composable
fun MarketTopBar(
    isBangla: Boolean,
    selectedTab: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    onOpenMergeDuplicates: () -> Unit = {},
    onOpenPastTrips: () -> Unit = {}
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isSearchActive, selectedTab) {
        if (isSearchActive && selectedTab == 1) {
            delay(50)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        AnimatedContent(
            targetState = isSearchActive && selectedTab == 1,
            transitionSpec = {
                fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
            },
            label = "topBarSearchAnimation"
        ) { inSearchMode ->
            if (inSearchMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MarketSearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        placeholder = if (isBangla) "পণ্য খুঁজুন (যেমন: আলু, তেল, চাল)..." else "Search product (e.g. Potato, Oil)...",
                        modifier = Modifier.weight(1f),
                        height = 40.dp,
                        shape = RoundedCornerShape(12.dp),
                        focusRequester = focusRequester,
                        onClear = { onSearchQueryChange("") },
                        onSearch = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )

                    IconButton(
                        onClick = {
                            haptics.lightTick()
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onSearchActiveChange(false)
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = if (isBangla) "অনুসন্ধান বন্ধ করুন" else "Close search",
                            tint = appColors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBangla) "বাজার" else "Market",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = appColors.textPrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (selectedTab == 1) {
                            val hasQuery = searchQuery.isNotBlank()
                            IconButton(
                                onClick = {
                                    haptics.lightTick()
                                    onSearchActiveChange(true)
                                },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = if (isBangla) "পণ্য খুঁজুন" else "Search",
                                    tint = if (hasQuery) appColors.accent else appColors.textPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        MarketTopBarMenu(
                            isBangla = isBangla,
                            onOpenMergeDuplicates = onOpenMergeDuplicates,
                            onOpenPastTrips = onOpenPastTrips
                        )
                    }
                }
            }
        }
    }
}
