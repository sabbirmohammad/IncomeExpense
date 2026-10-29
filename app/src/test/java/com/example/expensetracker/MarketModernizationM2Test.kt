package com.example.expensetracker

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Unit and structural verification tests for Milestone M2:
 * 1. Persistent Docked Bottom Trip Summary Bar (MarketBottomTripSummaryBar) in MarketScreen and MarketListSheet.
 * 2. Celebratory tactile feedback (haptics.successFeedback) and 48dp touch target on MarketItemRow check-off.
 * 3. Frictionless Price & Quantity Logging (MarketPromptAmountDialog) with smart unit multipliers,
 *    1-tap last-rate auto-suggest banner, real-time price variance indicators, and on-screen thumb keypad.
 */
class MarketModernizationM2Test {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Expected file to exist: ${file.absolutePath}", file.exists())
        return file.readText()
    }

    @Test
    fun testMarketBottomTripSummaryBarStructureAndFeatures() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketBottomTripSummaryBar.kt")

        // 1. Signature
        assertTrue(
            "MarketBottomTripSummaryBar must accept totalSpent, purchasedCount, totalCount, isBangla, onFinishShopping",
            content.contains("fun MarketBottomTripSummaryBar(") &&
            content.contains("totalSpent: Double") &&
            content.contains("purchasedCount: Int") &&
            content.contains("totalCount: Int") &&
            content.contains("isBangla: Boolean") &&
            content.contains("onFinishShopping: () -> Unit")
        )

        // 2. Spend and Progress display
        assertTrue(
            "MarketBottomTripSummaryBar must format totalSpent and show count progress pill",
            content.contains("formatCurrency(totalSpent") &&
            content.contains("সম্পন্ন (") &&
            content.contains("LinearProgressIndicator")
        )

        // 3. Checkout button with success haptics
        assertTrue(
            "MarketBottomTripSummaryBar must feature checkout button with celebratory haptics",
            content.contains("haptics.successFeedback()") &&
            content.contains("বাজার সম্পন্ন করুন") &&
            content.contains("Checkout")
        )
    }

    @Test
    fun testMarketScreenDockedSummaryBarAndScrollPadding() {
        val content = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // 1. Docks MarketBottomTripSummaryBar with bottom clearance
        assertTrue(
            "MarketScreen must dock MarketBottomTripSummaryBar at the bottom of active shopping checklist with clearance",
            content.contains("MarketBottomTripSummaryBar(") &&
            content.contains("Alignment.BottomCenter") &&
            content.contains("bottom = 80.dp")
        )

        // 2. LazyColumn has 160.dp bottom padding to avoid clipping
        assertTrue(
            "MarketScreen LazyColumn must have 160.dp bottom padding",
            content.contains("PaddingValues(bottom = 160.dp)")
        )

        // 3. Retains MarketFinishButton inside scroll list
        assertTrue(
            "MarketScreen must retain MarketFinishButton for backward compatibility",
            content.contains("MarketFinishButton(")
        )
    }

    @Test
    fun testMarketListSheetDockedSummaryBar() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketListSheet.kt")

        // 1. Docks MarketBottomTripSummaryBar
        assertTrue(
            "MarketListSheet must dock MarketBottomTripSummaryBar at bottom",
            content.contains("MarketBottomTripSummaryBar(") &&
            content.contains("Alignment.BottomCenter")
        )

        // 2. LazyColumn has 100.dp bottom padding
        assertTrue(
            "MarketListSheet LazyColumn must have 100.dp bottom padding",
            content.contains("PaddingValues(bottom = if (items.isNotEmpty()) 100.dp else 16.dp)")
        )
    }

    @Test
    fun testMarketItemRowHapticsAndVisualPolish() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // 1. Celebratory tactile feedback on check-off
        assertTrue(
            "MarketItemRow must fire haptics.successFeedback() when checking off an unpurchased item",
            content.contains("haptics.successFeedback()")
        )

        // 2. 48dp minimum touch target on checkbox
        assertTrue(
            "MarketItemRow circular checkbox must enforce minimum 48dp touch target",
            content.contains("sizeIn(minWidth = 48.dp, minHeight = 48.dp)") &&
            content.contains("minimumInteractiveComponentSize()")
        )

        // 3. Spring animation
        assertTrue(
            "MarketItemRow must use spring animation on checkbox check icon",
            content.contains("Spring.DampingRatioMediumBouncy")
        )

        // 4. Visual differentiation: tinted background & strikethrough for purchased
        assertTrue(
            "MarketItemRow must show tinted background and strikethrough title for purchased items",
            content.contains("appColors.income.copy(alpha = 0.08f)") &&
            content.contains("TextDecoration.LineThrough")
        )
    }

    @Test
    fun testMarketPromptAmountDialogPresetsAndKeypad() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")

        // 1. Smart Preset Unit Multipliers for weight, discrete, and volume
        assertTrue(
            "MarketPromptAmountDialog must provide smart preset unit multipliers",
            content.contains("unitPresets") &&
            content.contains("০.৫ কেজি") &&
            content.contains("১ কেজি") &&
            content.contains("১ পোয়া") &&
            content.contains("১ পাল্লা") &&
            content.contains("৪টি") &&
            content.contains("১ হালি") &&
            content.contains("০.৫ লি")
        )

        // 2. 1-Tap Auto-Suggest Rate Banner with 1-tap apply
        assertTrue(
            "MarketPromptAmountDialog must display 1-tap auto-suggest rate banner",
            content.contains("💡 গতবারের দর") &&
            content.contains("বসিয়ে দিন") &&
            content.contains("সম্ভাব্য দাম")
        )

        // 3. Real-time Price Variance Indicator
        assertTrue(
            "MarketPromptAmountDialog must show cheaper/expensive/neutral indicators in real-time",
            content.contains("সাশ্রয়ী") &&
            content.contains("বেশি দর") &&
            content.contains("একই দর")
        )

        // 4. On-Screen Thumb-Zone Numeric Keypad & Increments
        assertTrue(
            "MarketPromptAmountDialog must have on-screen numeric keypad and increment chips",
            content.contains("listOf(\"1\", \"2\", \"3\")") &&
            content.contains("listOf(50, 100, 200, 500)") &&
            content.contains("listOf(10) + chips") &&
            content.contains("minimumInteractiveComponentSize()")
        )

        // 5. Fast 1-Tap Action buttons
        assertTrue(
            "MarketPromptAmountDialog must feature 1-tap confirm and mark without price",
            content.contains("কেনা নিশ্চিত করুন") &&
            content.contains("মূল্য ছাড়া কেনা") &&
            content.contains("onConfirm(0.0)")
        )
    }

    @Test
    fun testReviewerRemediationQuantityPropagationAndKeypad() {
        val dialogContent = readFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")
        val marketScreenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")
        val marketSheetContent = readFile("com/example/expensetracker/ui/dashboard/MarketListSheet.kt")
        val marketNativeContent = readFile("com/example/expensetracker/ui/dashboard/MarketNativeContent.kt")

        // 1. MarketPromptAmountDialog onConfirm signature passes amount, quantity, unit
        assertTrue(
            "MarketPromptAmountDialog must provide onConfirm with amount, quantity, and unit",
            dialogContent.contains("onConfirm: (amount: Double, quantity: Double, unit: String) -> Unit")
        )

        // 2. MarketScreen copies updated quantity and unit into item for markMarketItemPurchased
        assertTrue(
            "MarketScreen must pass targetItem with updated quantity and unit to markMarketItemPurchased",
            marketScreenContent.contains("val updatedItem = targetItem.copy(quantity = qty, unit = unit)") &&
            marketScreenContent.contains("dashboardViewModel.markMarketItemPurchased(updatedItem")
        )

        // 3. MarketListSheet copies updated quantity and unit into item for onMarkPurchased
        assertTrue(
            "MarketListSheet must pass updatedItem with quantity and unit to onMarkPurchased",
            marketSheetContent.contains("val updatedItem = targetItem.copy(quantity = qty, unit = unit)") &&
            marketSheetContent.contains("onMarkPurchased(updatedItem")
        )

        // 4. MarketNativeContent copies updated quantity and unit into item for onMarkPurchased
        assertTrue(
            "MarketNativeContent must pass curItem with quantity and unit to onMarkPurchased",
            marketNativeContent.contains("onMarkPurchased(curItem.copy(quantity = qty, unit = unit), amt)")
        )

        // 5. Keypad prevents multiple decimal inputs per number token
        assertTrue(
            "Keypad must prevent multiple decimal points in current token",
            dialogContent.contains("val currentToken = promptAmountText.takeLastWhile { it !in \"+-*xX/×÷\" }") &&
            dialogContent.contains("!currentToken.contains(\".\")")
        )

        // 6. MarketBottomTripSummaryBar has bottom = 80.dp clearance in MarketScreen
        assertTrue(
            "MarketBottomTripSummaryBar must have 80.dp bottom clearance in MarketScreen",
            marketScreenContent.contains("bottom = 80.dp")
        )
    }
}
