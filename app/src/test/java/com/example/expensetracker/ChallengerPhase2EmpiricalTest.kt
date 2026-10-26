package com.example.expensetracker

import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.utils.ReceiptImageHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ChallengerPhase2EmpiricalTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun testReceiptImageHelperCalculateInSampleSizeBoundaries() {
        // <= 1024 should have inSampleSize 1
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(1024, 768))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(768, 1024))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(1024, 1024))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(100, 100))
        assertEquals(1, ReceiptImageHelper.calculateInSampleSize(1, 1))

        // Just over 1024
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(1025, 500))
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(500, 1025))
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(2048, 1536))

        // Over 2048 (at 2050, 2050 / 2 = 1025 > 1024 -> inSampleSize = 4)
        assertEquals(2, ReceiptImageHelper.calculateInSampleSize(2049, 1000))
        assertEquals(4, ReceiptImageHelper.calculateInSampleSize(2050, 1000))
        assertEquals(4, ReceiptImageHelper.calculateInSampleSize(4096, 3072))

        // 8K camera resolution
        assertEquals(8, ReceiptImageHelper.calculateInSampleSize(8192, 4096))
    }

    @Test
    fun testReceiptImageHelperDeleteFileQuietly() {
        // 1. null file should not throw
        ReceiptImageHelper.deleteFileQuietly(null)

        // 2. non-existent file should not throw
        val nonExistent = File(tempFolder.root, "does_not_exist.tmp")
        ReceiptImageHelper.deleteFileQuietly(nonExistent)

        // 3. existing file should be deleted
        val temp = tempFolder.newFile("receipt_temp.jpg")
        assertTrue(temp.exists())
        ReceiptImageHelper.deleteFileQuietly(temp)
        assertFalse(temp.exists())
    }

    @Test
    fun testCalculatorEvaluatorStressAndEdgeCases() {
        // Complex mixed operators with precedence
        // 100 - 20 * 3 + 40 / 8 = 100 - 60 + 5 = 45.0
        val res1 = CalculatorEvaluator.evaluate("100 - 20 * 3 + 40 / 8")
        assertNotNull(res1)
        assertEquals(45.0, res1!!, 0.001)

        // Chained divisions
        // 100 / 2 / 2 = 25.0
        val res2 = CalculatorEvaluator.evaluate("100 / 2 / 2")
        assertNotNull(res2)
        assertEquals(25.0, res2!!, 0.001)

        // Decimal operations
        val res3 = CalculatorEvaluator.evaluate("0.1 + 0.2")
        assertNotNull(res3)
        assertEquals(0.3, res3!!, 0.001)

        // Whitespace and Bengali numeral variations
        val res4 = CalculatorEvaluator.evaluate("  ৫০  +  ২৫.৫  *  ২  ")
        assertNotNull(res4)
        assertEquals(101.0, res4!!, 0.001)

        // Division by zero returns null
        val divZero = CalculatorEvaluator.evaluate("50 / 0")
        assertNull(divZero)

        // Multiple decimal dots in single token returns null
        val invalidDecimal = CalculatorEvaluator.evaluate("12.34.56 + 10")
        assertNull(invalidDecimal)
    }

    @Test
    fun testFileProviderAuthorityAndPathsXmlStructure() {
        val manifestFile = File("src/main/AndroidManifest.xml")
        val manifestContent = if (manifestFile.exists()) manifestFile.readText() else File("app/src/main/AndroidManifest.xml").readText()

        assertTrue(
            "AndroidManifest must declare FileProvider authority with \${applicationId}.fileprovider",
            manifestContent.contains("""android:authorities="${'$'}{applicationId}.fileprovider"""")
        )
        assertTrue(
            "AndroidManifest must link to @xml/file_paths",
            manifestContent.contains("""android:resource="@xml/file_paths"""")
        )

        val pathsFile = File("src/main/res/xml/file_paths.xml")
        val pathsContent = if (pathsFile.exists()) pathsFile.readText() else File("app/src/main/res/xml/file_paths.xml").readText()

        assertTrue(
            "file_paths.xml must specify cache-path for shared_images",
            pathsContent.contains("""<cache-path name="shared_images" path="shared_images/" />""")
        )
        assertTrue(
            "file_paths.xml must specify files-path for receipts",
            pathsContent.contains("""<files-path name="receipts" path="receipts/" />""")
        )
    }

    @Test
    fun testBillsDeletionSafeguardImplementation() {
        val billsScreenFile = File("src/main/java/com/example/expensetracker/ui/bills/BillsScreen.kt")
        val billsScreenContent = if (billsScreenFile.exists()) billsScreenFile.readText() else File("app/src/main/java/com/example/expensetracker/ui/bills/BillsScreen.kt").readText()

        assertTrue(
            "BillsScreen must declare billToDelete state",
            billsScreenContent.contains("var billToDelete by remember { mutableStateOf<RecurringBill?>(null) }")
        )
        assertTrue(
            "BillsScreen onDelete must set billToDelete instead of direct deletion",
            billsScreenContent.contains("billToDelete = bill")
        )
        assertTrue(
            "BillsScreen must invoke BillDeleteConfirmDialog",
            billsScreenContent.contains("BillDeleteConfirmDialog(")
        )

        val dialogFile = File("src/main/java/com/example/expensetracker/ui/bills/BillDeleteConfirmDialog.kt")
        val dialogContent = if (dialogFile.exists()) dialogFile.readText() else File("app/src/main/java/com/example/expensetracker/ui/bills/BillDeleteConfirmDialog.kt").readText()

        assertTrue(
            "BillDeleteConfirmDialog must use AlertDialog",
            dialogContent.contains("AlertDialog(")
        )
        assertTrue(
            "BillDeleteConfirmDialog must have ColorDangerExpense confirm button",
            dialogContent.contains("ColorDangerExpense")
        )
    }

    @Test
    fun testAuditTouchTargetSizesAcrossIdentifiedControls() {
        val basePath = if (File("src/main/java").exists()) "src/main/java" else "app/src/main/java"

        // 1. OneUiHeader.kt DrawerMenuButton & ProfileAvatarButton
        val oneUiHeaderContent = File("$basePath/com/example/expensetracker/ui/components/OneUiHeader.kt").readText()
        assertTrue(
            "OneUiHeader DrawerMenuButton must have sizeIn min 48dp or minimumInteractiveComponentSize",
            oneUiHeaderContent.contains("minWidth = 48.dp, minHeight = 48.dp") || oneUiHeaderContent.contains("minimumInteractiveComponentSize()")
        )

        // 2. FilterChips.kt PillFilterChip & DropdownFilterChip
        val filterChipsContent = File("$basePath/com/example/expensetracker/ui/components/FilterChips.kt").readText()
        assertTrue(
            "FilterChips must have minimumInteractiveComponentSize()",
            filterChipsContent.contains("minimumInteractiveComponentSize()")
        )

        // 3. MarketItemRow.kt checkbox & delete
        val marketItemRowContent = File("$basePath/com/example/expensetracker/ui/dashboard/MarketItemRow.kt").readText()
        assertTrue(
            "MarketItemRow checkbox must have 48dp sizeIn or minimumInteractiveComponentSize",
            marketItemRowContent.contains("minWidth = 48.dp, minHeight = 48.dp")
        )

        // 4. ContributionQuickChips.kt
        val quickChipsContent = File("$basePath/com/example/expensetracker/ui/savings/ContributionQuickChips.kt").readText()
        assertTrue(
            "ContributionQuickChips must have minimumInteractiveComponentSize()",
            quickChipsContent.contains("minimumInteractiveComponentSize()")
        )

        // 5. BillPaymentStatusRow.kt
        val billPaymentContent = File("$basePath/com/example/expensetracker/ui/bills/BillPaymentStatusRow.kt").readText()
        assertTrue(
            "BillPaymentStatusRow must have minimumInteractiveComponentSize()",
            billPaymentContent.contains("minimumInteractiveComponentSize()")
        )
    }

    @Test
    fun testVerifyRemediatedSub48dpControls() {
        val basePath = if (File("src/main/java").exists()) "src/main/java" else "app/src/main/java"

        // 1. BalanceCardHeaderRow.kt: Privacy toggle is remediated (no longer size(36.dp)) and has minimumInteractiveComponentSize
        val balanceCardHeader = File("$basePath/com/example/expensetracker/ui/components/BalanceCardHeaderRow.kt").readText()
        val hasPrivacy36dp = balanceCardHeader.contains(".size(36.dp)")
        assertFalse("BalanceCardHeaderRow must not contain .size(36.dp) for privacy toggle", hasPrivacy36dp)
        assertTrue("BalanceCardHeaderRow must enforce minimumInteractiveComponentSize()", balanceCardHeader.contains("minimumInteractiveComponentSize()"))

        // 2. MarketTripCardHeader.kt: Options menu is remediated (no longer size(32.dp))
        val marketTripCardHeader = File("$basePath/com/example/expensetracker/ui/transactions/MarketTripCardHeader.kt").readText()
        val hasMarketTrip32dp = marketTripCardHeader.contains(".size(32.dp)")
        assertFalse("MarketTripCardHeader must not contain .size(32.dp) for options menu", hasMarketTrip32dp)

        // 3. AccountCardItem.kt: Options menu is remediated (no longer size(44.dp))
        val accountCardItem = File("$basePath/com/example/expensetracker/ui/accounts/AccountCardItem.kt").readText()
        val hasAccountCard44dp = accountCardItem.contains(".size(44.dp)")
        assertFalse("AccountCardItem must not contain .size(44.dp) for options menu", hasAccountCard44dp)

        // 4. SettingsCategoryRowItem.kt: Edit and delete are remediated (no longer size(32.dp))
        val settingsCategoryRow = File("$basePath/com/example/expensetracker/ui/settings/SettingsCategoryRowItem.kt").readText()
        val hasCategory32dp = settingsCategoryRow.contains(".size(32.dp)")
        assertFalse("SettingsCategoryRowItem must not contain .size(32.dp)", hasCategory32dp)

        // 5. PayableItemBottomBar.kt: Call button is remediated (no longer size(32.dp))
        val payableBottomBar = File("$basePath/com/example/expensetracker/ui/receivables/PayableItemBottomBar.kt").readText()
        val hasPayableCall32dp = payableBottomBar.contains(".size(32.dp)")
        assertFalse("PayableItemBottomBar must not contain .size(32.dp)", hasPayableCall32dp)
        assertTrue("PayableItemBottomBar must enforce minimumInteractiveComponentSize()", payableBottomBar.contains("minimumInteractiveComponentSize()"))

        // 6. UnitAndQuantitySections.kt: + and - buttons are remediated (no longer size(44.dp))
        val unitAndQty = File("$basePath/com/example/expensetracker/ui/components/UnitAndQuantitySections.kt").readText()
        val hasQty44dp = unitAndQty.contains(".size(44.dp)")
        assertFalse("UnitAndQuantitySections must not contain .size(44.dp)", hasQty44dp)

        // 7. SettingsThemeCard.kt: Theme pills have minimumInteractiveComponentSize
        val settingsThemeCard = File("$basePath/com/example/expensetracker/ui/settings/SettingsThemeCard.kt").readText()
        assertTrue("SettingsThemeCard must enforce minimumInteractiveComponentSize()", settingsThemeCard.contains("minimumInteractiveComponentSize()"))

        // 8. AnalyticsBreakdownToggle.kt: Mode toggle has minimumInteractiveComponentSize
        val analyticsToggle = File("$basePath/com/example/expensetracker/ui/analytics/AnalyticsBreakdownToggle.kt").readText()
        assertTrue("AnalyticsBreakdownToggle must enforce minimumInteractiveComponentSize()", analyticsToggle.contains("minimumInteractiveComponentSize()"))

        // 9. InPlaceCalculator.kt: Keypad buttons are at least 48dp
        val calculatorContent = File("$basePath/com/example/expensetracker/ui/components/InPlaceCalculator.kt").readText()
        assertFalse("InPlaceCalculator must not have 44dp height buttons", calculatorContent.contains(".height(44.dp)"))
        assertTrue("InPlaceCalculator must have 48dp height buttons", calculatorContent.contains(".height(48.dp)"))
    }
}
