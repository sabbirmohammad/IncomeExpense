package com.example.expensetracker

import com.example.expensetracker.ui.components.Strings
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CleanSubtitleAndClutterReductionTest {

    private fun findSourceFile(subpath: String): File {
        val normalized = subpath.removePrefix("app/")
        val candidates = listOf(
            File(normalized),
            File("app", normalized),
            File(System.getProperty("user.dir"), normalized),
            File(System.getProperty("user.dir"), "app/$normalized"),
            File("../$normalized")
        )
        return candidates.firstOrNull { it.exists() } ?: File(normalized)
    }

    @Test
    fun testBillsDueAlertSubtitleDoesNotContainRedundantTapInstructions() {
        val bnStrings = Strings(isBangla = true)
        val enStrings = Strings(isBangla = false)

        // Verify concise subtitle text
        assertFalse(
            "Bangla billsDueAlertSubtitle should not contain 'ট্যাপ করুন'",
            bnStrings.billsDueAlertSubtitle.contains("ট্যাপ করুন")
        )
        assertFalse(
            "Bangla billsDueAlertSubtitle should not contain bullet separator",
            bnStrings.billsDueAlertSubtitle.contains("•")
        )
        assertTrue(
            "Bangla billsDueAlertSubtitle should contain 'টি বিল পরিশোধের সময় হয়েছে'",
            bnStrings.billsDueAlertSubtitle.contains("টি বিল পরিশোধের সময় হয়েছে")
        )

        assertFalse(
            "English billsDueAlertSubtitle should not contain 'Tap to pay'",
            enStrings.billsDueAlertSubtitle.contains("Tap to pay")
        )
        assertFalse(
            "English billsDueAlertSubtitle should not contain bullet separator",
            enStrings.billsDueAlertSubtitle.contains("•")
        )
        assertTrue(
            "English billsDueAlertSubtitle should be 'bill(s) due soon'",
            enStrings.billsDueAlertSubtitle.contains("bill(s) due soon")
        )
    }

    @Test
    fun testSettingsTopBarHasAccessibilityTouchTargetAndNoSubtitle() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/settings/SettingsTopBar.kt")
        assertTrue("SettingsTopBar.kt must exist at ${file.absolutePath}", file.exists())
        val content = file.readText()

        assertTrue(
            "SettingsTopBar back button must include minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )
        assertFalse(
            "SettingsTopBar must not contain settingsSubtitle clutter",
            content.contains("settingsSubtitle")
        )
    }

    @Test
    fun testSettingsLanguageCardHasAccessibilityTouchTargetsAndNoRedundantCurrentText() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/settings/SettingsLanguageCard.kt")
        assertTrue("SettingsLanguageCard.kt must exist at ${file.absolutePath}", file.exists())
        val content = file.readText()

        assertTrue(
            "SettingsLanguageCard options must include minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )
        assertFalse(
            "SettingsLanguageCard must not contain redundant 'বর্তমান: ' microcopy",
            content.contains("বর্তমান: ")
        )
    }

    @Test
    fun testSettingsThemeAndBengaliNumeralsCardsHaveNoSubtitles() {
        val themeFile = findSourceFile("src/main/java/com/example/expensetracker/ui/settings/SettingsThemeCard.kt")
        assertTrue("SettingsThemeCard.kt must exist at ${themeFile.absolutePath}", themeFile.exists())
        val themeContent = themeFile.readText()
        assertFalse(
            "SettingsThemeCard must not reference themeSettingSubtitle",
            themeContent.contains("themeSettingSubtitle")
        )

        val numeralsFile = findSourceFile("src/main/java/com/example/expensetracker/ui/settings/SettingsBengaliNumeralsCard.kt")
        assertTrue("SettingsBengaliNumeralsCard.kt must exist at ${numeralsFile.absolutePath}", numeralsFile.exists())
        val numeralsContent = numeralsFile.readText()
        assertFalse(
            "SettingsBengaliNumeralsCard must not reference bengaliNumeralsSubtitle",
            numeralsContent.contains("bengaliNumeralsSubtitle")
        )
    }

    @Test
    fun testSettingsDataBackupCardAndExportTilesCleanliness() {
        val backupCardFile = findSourceFile("src/main/java/com/example/expensetracker/ui/settings/SettingsDataBackupCard.kt")
        assertTrue("SettingsDataBackupCard.kt must exist at ${backupCardFile.absolutePath}", backupCardFile.exists())
        val backupCardContent = backupCardFile.readText()
        assertFalse(
            "SettingsDataBackupCard must not contain redundant statement subheader inside accordion",
            backupCardContent.contains("অফিসিয়াল স্টেটমেন্ট (PDF ও এক্সেল)")
        )

        val exportTilesFile = findSourceFile("src/main/java/com/example/expensetracker/ui/settings/SettingsExportTiles.kt")
        assertTrue("SettingsExportTiles.kt must exist at ${exportTilesFile.absolutePath}", exportTilesFile.exists())
        val exportTilesContent = exportTilesFile.readText()
        assertFalse(
            "SettingsExportTiles must not have 'প্রিন্টযোগ্য A4' subtitle clutter",
            exportTilesContent.contains("প্রিন্টযোগ্য A4")
        )
        assertFalse(
            "SettingsExportTiles must not have 'শেয়ারযোগ্য' subtitle clutter",
            exportTilesContent.contains("শেয়ারযোগ্য")
        )
        assertFalse(
            "SettingsExportTiles must not have 'স্প্রেডশীট' subtitle clutter",
            exportTilesContent.contains("স্প্রেডশীট")
        )
        assertTrue(
            "SettingsExportTileItem should have optional subtitle parameter defaulting to null",
            exportTilesContent.contains("subtitle: String? = null")
        )
    }

    @Test
    fun testTransactionClutterEliminatedInReceiptAndDateTime() {
        val receiptCardFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionReceiptCard.kt")
        assertTrue("TransactionReceiptCard.kt must exist at ${receiptCardFile.absolutePath}", receiptCardFile.exists())
        val receiptContent = receiptCardFile.readText()
        assertFalse(
            "TransactionReceiptCard must not display redundant tapToViewReceipt subtitle",
            receiptContent.contains("tapToViewReceipt")
        )

        val dateTimeFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionDateTimeSection.kt")
        assertTrue("TransactionDateTimeSection.kt must exist at ${dateTimeFile.absolutePath}", dateTimeFile.exists())
        val dateTimeContent = dateTimeFile.readText()
        assertFalse(
            "TransactionDateTimeSection must not display redundant tapToChange subtitle",
            dateTimeContent.contains("tapToChange")
        )

        val headerFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionSheetHeader.kt")
        assertTrue("TransactionSheetHeader.kt must exist at ${headerFile.absolutePath}", headerFile.exists())
        val headerContent = headerFile.readText()
        assertFalse(
            "TransactionSheetHeader must not contain editModeActiveBanner",
            headerContent.contains("editModeActiveBanner")
        )
    }
}
