package com.example.expensetracker.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import kotlinx.coroutines.launch

/**
 * 3-Screen Skippable Onboarding per V7 spec:
 * - 1. Track (receipt/tx overview)
 * - 2. Plan (budgeting & savings goals)
 * - 3. Bazaar (market checklist & prices)
 * - Guaranteed >= 48dp touch targets, smooth pager, reduce-motion support.
 */
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val motion = LocalMotion.current
    val haptics = rememberAppHaptics()
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPageData(strings.onboardingTrackTitle, strings.onboardingTrackDesc, Icons.AutoMirrored.Outlined.ReceiptLong, appColors.accent),
        OnboardingPageData(strings.onboardingPlanTitle, strings.onboardingPlanDesc, Icons.Outlined.Savings, appColors.warning),
        OnboardingPageData(strings.onboardingBazaarTitle, strings.onboardingBazaarDesc, Icons.Outlined.ShoppingCart, appColors.infoContainer)
    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })

    BackHandler(enabled = pagerState.currentPage > 0) {
        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Skip button (>= 48dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    haptics.lightTick()
                    onFinish()
                },
                modifier = Modifier
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Text(
                    text = strings.onboardingSkip,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold),
                    color = appColors.textSecondary
                )
            }
        }

        // Center Content: Horizontal Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            val page = pages[pageIndex]
            OnboardingPageItem(
                title = page.title,
                description = page.description,
                icon = page.icon
            )
        }

        // Bottom Controls: Page Indicator Dots + Primary Action Button
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 3-dot Animated Page Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = tween(if (motion.reduceMotion) 0 else motion.durationStandard),
                        label = "dot_width"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(if (isSelected) appColors.accent else appColors.border)
                            .clickable {
                                scope.launch {
                                    if (motion.reduceMotion) pagerState.scrollToPage(index)
                                    else pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }

            // Primary Action Button (Next / Get Started) strictly >= 48dp
            val isLastPage = pagerState.currentPage == pages.size - 1
            Button(
                onClick = {
                    haptics.mediumClick()
                    if (isLastPage) onFinish() else scope.launch {
                        if (motion.reduceMotion) pagerState.scrollToPage(pagerState.currentPage + 1)
                        else pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(RadiusM),
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent, contentColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isLastPage) strings.onboardingGetStarted else strings.onboardingNext,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    )
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
