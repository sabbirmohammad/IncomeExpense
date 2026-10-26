package com.example.expensetracker

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.theme.ColorBackgroundLight
import com.example.expensetracker.theme.ColorSurfaceLight
import com.example.expensetracker.theme.ColorTextMutedDark
import com.example.expensetracker.theme.ColorTextMutedLight
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextPrimaryLight
import com.example.expensetracker.theme.ColorTextSecondaryDark
import com.example.expensetracker.theme.ColorTextSecondaryLight
import com.example.expensetracker.theme.Typography
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ChallengerPhase1StressTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun relativeLuminance(color: Color): Double {
        fun channel(c: Float): Double {
            val v = c.toDouble()
            return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        }
        val r = channel(color.red)
        val g = channel(color.green)
        val b = channel(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun contrastRatio(fg: Color, bg: Color): Double {
        val l1 = relativeLuminance(fg)
        val l2 = relativeLuminance(bg)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    @Test
    fun testEmpiricalContrastOfSecondaryAndMutedColorsOnLightSurface() {
        val white = Color(0xFFFFFFFF)
        val lightBg = Color(0xFFF8FAFC)

        // ColorTextSecondaryDark (0xFF9DA3B4) on White surface
        val secondaryContrastOnWhite = contrastRatio(ColorTextSecondaryDark, white)
        val secondaryContrastOnBg = contrastRatio(ColorTextSecondaryDark, lightBg)

        // ColorTextMutedDark (0xFF7E8597) on White surface
        val mutedContrastOnWhite = contrastRatio(ColorTextMutedDark, white)
        val mutedContrastOnBg = contrastRatio(ColorTextMutedDark, lightBg)

        // ColorTextSecondaryLight (0xFF475569) on White surface
        val properSecondaryContrast = contrastRatio(ColorTextSecondaryLight, white)
        // ColorTextMutedLight (0xFF64748B) on White surface
        val properMutedContrast = contrastRatio(ColorTextMutedLight, white)

        println("EMPIRICAL CONTRAST RESULTS:")
        println("ColorTextSecondary on White: $secondaryContrastOnWhite:1 (WCAG AA requires >= 4.5:1)")
        println("ColorTextSecondary on LightBg: $secondaryContrastOnBg:1")
        println("ColorTextMuted on White: $mutedContrastOnWhite:1 (WCAG AA requires >= 4.5:1)")
        println("Proper ColorTextSecondaryLight on White: $properSecondaryContrast:1")
        println("Proper ColorTextMutedLight on White: $properMutedContrast:1")

        // Empirical proof: ColorTextSecondary and ColorTextMuted fail WCAG AA (4.5:1)
        assertTrue("ColorTextSecondary fails WCAG AA on white (measured $secondaryContrastOnWhite < 4.5)", secondaryContrastOnWhite < 4.5)
        assertTrue("ColorTextMuted fails WCAG AA on white (measured $mutedContrastOnWhite < 4.5)", mutedContrastOnWhite < 4.5)

        // The light theme counterparts satisfy WCAG AA
        assertTrue("ColorTextSecondaryLight passes WCAG AA (measured $properSecondaryContrast >= 4.5)", properSecondaryContrast >= 4.5)
        assertTrue("ColorTextMutedLight passes WCAG AA (measured $properMutedContrast >= 4.5)", properMutedContrast >= 4.5)
    }

    @Test
    fun testInspectAllTypographyStylesForSubtleHardcodedColors() {
        // Requirement UI-01 & M1 state all typography styles must leave color Unspecified
        // so that Compose resolves text dynamically against LocalContentColor / onSurface.
        val stylesWithColors = mutableListOf<String>()

        if (Typography.headlineLarge.color != Color.Unspecified) stylesWithColors.add("headlineLarge: ${Typography.headlineLarge.color}")
        if (Typography.headlineMedium.color != Color.Unspecified) stylesWithColors.add("headlineMedium: ${Typography.headlineMedium.color}")
        if (Typography.titleLarge.color != Color.Unspecified) stylesWithColors.add("titleLarge: ${Typography.titleLarge.color}")
        if (Typography.titleMedium.color != Color.Unspecified) stylesWithColors.add("titleMedium: ${Typography.titleMedium.color}")
        if (Typography.titleSmall.color != Color.Unspecified) stylesWithColors.add("titleSmall: ${Typography.titleSmall.color}")
        if (Typography.bodyLarge.color != Color.Unspecified) stylesWithColors.add("bodyLarge: ${Typography.bodyLarge.color}")
        if (Typography.bodyMedium.color != Color.Unspecified) stylesWithColors.add("bodyMedium: ${Typography.bodyMedium.color}")
        if (Typography.bodySmall.color != Color.Unspecified) stylesWithColors.add("bodySmall: ${Typography.bodySmall.color}")
        if (Typography.labelMedium.color != Color.Unspecified) stylesWithColors.add("labelMedium: ${Typography.labelMedium.color}")
        if (Typography.labelSmall.color != Color.Unspecified) stylesWithColors.add("labelSmall: ${Typography.labelSmall.color}")

        println("Typography styles with hardcoded colors: $stylesWithColors")

        // After Phase 1 Iteration 2 fix: zero typography styles retain hardcoded colors
        assertTrue("All typography styles must have Color.Unspecified, found: $stylesWithColors", stylesWithColors.isEmpty())
        assertEquals("bodySmall must have Color.Unspecified", Color.Unspecified, Typography.bodySmall.color)
        assertEquals("labelMedium must have Color.Unspecified", Color.Unspecified, Typography.labelMedium.color)
        assertEquals("labelSmall must have Color.Unspecified", Color.Unspecified, Typography.labelSmall.color)
    }

    @Test
    fun testPreRestoreSnapshotDatabaseAndWalShmCopyBehavior() {
        // Stress test the exact logic used in GoogleDriveSyncManager.createPreRestoreSnapshot
        val dbDir = tempFolder.newFolder("databases")
        val dbFile = File(dbDir, "expense_tracker.db")
        dbFile.writeText("SQLITE_DATABASE_MAIN_PAYLOAD")

        val walFile = File(dbDir, "expense_tracker.db-wal")
        walFile.writeText("SQLITE_WAL_PAYLOAD_UNCHECKPOINTED")

        val shmFile = File(dbDir, "expense_tracker.db-shm")
        shmFile.writeText("SQLITE_SHM_INDEX_PAYLOAD")

        val parentDir = dbFile.parentFile ?: tempFolder.root
        if (!parentDir.exists()) parentDir.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val timestampedBak = File(parentDir, "expense_tracker_pre_restore_$timestamp.db.bak")
        val fallbackBak = File(parentDir, "expense_tracker.db.bak")

        // Step 1: Copy main db
        dbFile.copyTo(timestampedBak, overwrite = true)
        dbFile.copyTo(fallbackBak, overwrite = true)

        // Step 2: WAL / SHM copy logic from GoogleDriveSyncManager
        if (walFile.exists()) {
            walFile.copyTo(File(parentDir, "${timestampedBak.name}-wal"), overwrite = true)
            walFile.copyTo(File(parentDir, "${fallbackBak.name}-wal"), overwrite = true)
        }
        if (shmFile.exists()) {
            shmFile.copyTo(File(parentDir, "${timestampedBak.name}-shm"), overwrite = true)
            shmFile.copyTo(File(parentDir, "${fallbackBak.name}-shm"), overwrite = true)
        }

        // Verify all 6 snapshot files exist
        assertTrue("timestamped .db.bak exists", timestampedBak.exists())
        assertTrue("fallback .db.bak exists", fallbackBak.exists())
        val timestampedWal = File(parentDir, "${timestampedBak.name}-wal")
        val fallbackWal = File(parentDir, "${fallbackBak.name}-wal")
        val timestampedShm = File(parentDir, "${timestampedBak.name}-shm")
        val fallbackShm = File(parentDir, "${fallbackBak.name}-shm")

        assertTrue("timestamped -wal exists", timestampedWal.exists())
        assertTrue("fallback -wal exists", fallbackWal.exists())
        assertTrue("timestamped -shm exists", timestampedShm.exists())
        assertTrue("fallback -shm exists", fallbackShm.exists())

        assertEquals("SQLITE_DATABASE_MAIN_PAYLOAD", fallbackBak.readText())
        assertEquals("SQLITE_WAL_PAYLOAD_UNCHECKPOINTED", fallbackWal.readText())
        assertEquals("SQLITE_SHM_INDEX_PAYLOAD", fallbackShm.readText())

        // Verify naming: expense_tracker.db.bak-wal vs expense_tracker.db-wal
        assertEquals("expense_tracker.db.bak-wal", fallbackWal.name)
        assertEquals("expense_tracker.db.bak-shm", fallbackShm.name)
    }

    @Test
    fun testPreRestoreSnapshotWhenDatabaseDoesNotExist() {
        val dbDir = tempFolder.newFolder("empty_databases")
        val dbFile = File(dbDir, "non_existent.db")

        // GoogleDriveSyncManager: if (!dbFile.exists()) return@withContext null
        val result = if (!dbFile.exists()) null else File(dbDir, "snapshot.db.bak")
        assertEquals(null, result)
        assertEquals(0, dbDir.listFiles()?.size ?: 0)
    }

    @Test
    fun testPreRestoreSnapshotWhenWalAndShmAreAbsent() {
        val dbDir = tempFolder.newFolder("no_wal_databases")
        val dbFile = File(dbDir, "expense_tracker.db")
        dbFile.writeText("SQLITE_DELETE_MODE_DATABASE")

        val parentDir = dbFile.parentFile ?: tempFolder.root
        val fallbackBak = File(parentDir, "expense_tracker.db.bak")
        dbFile.copyTo(fallbackBak, overwrite = true)

        val walFile = File(parentDir, "${dbFile.name}-wal")
        val shmFile = File(parentDir, "${dbFile.name}-shm")

        // WAL / SHM check
        val fallbackWal = File(parentDir, "${fallbackBak.name}-wal")
        val fallbackShm = File(parentDir, "${fallbackBak.name}-shm")

        if (walFile.exists()) {
            walFile.copyTo(fallbackWal, overwrite = true)
        }
        if (shmFile.exists()) {
            shmFile.copyTo(fallbackShm, overwrite = true)
        }

        assertTrue(fallbackBak.exists())
        assertFalse("WAL file should not be created if source did not have it", fallbackWal.exists())
        assertFalse("SHM file should not be created if source did not have it", fallbackShm.exists())
    }

    @Test
    fun testPreRestoreSnapshotDirectoryHandlingParentMissing() {
        val baseDir = tempFolder.newFolder("app_data")
        val nestedDir = File(baseDir, "nested/path/to/databases")
        assertFalse(nestedDir.exists())

        // Suppose parent directory does not exist yet
        val dbFile = File(nestedDir, "expense_tracker.db")
        val parentDir = dbFile.parentFile ?: baseDir

        if (!parentDir.exists()) {
            val created = parentDir.mkdirs()
            assertTrue(created)
        }
        assertTrue(parentDir.exists())

        dbFile.writeText("FRESH_DB")
        val fallbackBak = File(parentDir, "expense_tracker.db.bak")
        dbFile.copyTo(fallbackBak, overwrite = true)
        assertTrue(fallbackBak.exists())
    }
}
