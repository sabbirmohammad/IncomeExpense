package com.example.expensetracker.data.local

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Provides hardware-backed AES-256-GCM encryption for sensitive preferences such as
 * the Gemini API Key using the Android KeyStore provider.
 *
 * Automatically migrates existing plaintext keys on first read.
 */
class SecureStorageHelper(private val context: Context) {

    companion object {
        private const val TAG = "SecureStorageHelper"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "ExpenseTracker_Master_Key_v1"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH_BITS = 128

        const val PREFS_NAME = "expense_tracker_secure_prefs"
        const val PREF_KEY_GEMINI_API_KEY = "gemini_api_key"
        private const val PREF_KEY_GEMINI_API_KEY_ENCRYPTED = "gemini_api_key_enc"
        private const val PREF_KEY_GEMINI_API_KEY_IV = "gemini_api_key_iv"
    }

    private val prefs: SharedPreferences by lazy {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()

            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    /**
     * Encrypts the given plaintext string using AES-256-GCM.
     * Returns a Pair of (Base64 Encrypted Data, Base64 IV).
     */
    private fun encrypt(plainText: String): Pair<String, String>? {
        return try {
            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            val encBase64 = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
            val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            Pair(encBase64, ivBase64)
        } catch (e: Exception) {
            Log.e(TAG, "Encryption failed: ${e.message}", e)
            null
        }
    }

    /**
     * Decrypts the given Base64 ciphertext and IV using AES-256-GCM.
     */
    private fun decrypt(encBase64: String, ivBase64: String): String? {
        return try {
            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val iv = Base64.decode(ivBase64, Base64.NO_WRAP)
            val cipherBytes = Base64.decode(encBase64, Base64.NO_WRAP)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val plainBytes = cipher.doFinal(cipherBytes)
            String(plainBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Decryption failed: ${e.message}", e)
            null
        }
    }

    @Volatile
    private var cachedApiKey: String? = null
    @Volatile
    private var isCacheLoaded: Boolean = false

    /**
     * Securely saves the Gemini API Key.
     * If key is null or blank, deletes the stored key.
     */
    fun saveGeminiApiKey(apiKey: String?) {
        val clean = apiKey?.trim()?.ifBlank { null }
        if (isCacheLoaded && clean == cachedApiKey) {
            return // No change, avoid unnecessary encryption & disk write
        }
        cachedApiKey = clean
        isCacheLoaded = true

        if (clean == null) {
            prefs.edit()
                .remove(PREF_KEY_GEMINI_API_KEY)
                .remove(PREF_KEY_GEMINI_API_KEY_ENCRYPTED)
                .remove(PREF_KEY_GEMINI_API_KEY_IV)
                .apply()
            return
        }

        val encrypted = encrypt(clean)
        if (encrypted != null) {
            prefs.edit()
                .putString(PREF_KEY_GEMINI_API_KEY_ENCRYPTED, encrypted.first)
                .putString(PREF_KEY_GEMINI_API_KEY_IV, encrypted.second)
                // Remove legacy plaintext key
                .remove(PREF_KEY_GEMINI_API_KEY)
                .apply()
        } else {
            // Fallback: If hardware keystore fails on rare custom ROMs, keep plaintext with warning
            Log.w(TAG, "Keystore encryption unavailable, falling back to private SharedPreferences.")
            prefs.edit().putString(PREF_KEY_GEMINI_API_KEY, clean).apply()
        }
    }

    /**
     * Securely retrieves the Gemini API Key.
     * Automatically migrates legacy plaintext keys if found.
     */
    fun getGeminiApiKey(): String? {
        if (isCacheLoaded) {
            return cachedApiKey
        }

        // 1. Check encrypted storage first
        val enc = prefs.getString(PREF_KEY_GEMINI_API_KEY_ENCRYPTED, null)
        val iv = prefs.getString(PREF_KEY_GEMINI_API_KEY_IV, null)

        if (!enc.isNullOrBlank() && !iv.isNullOrBlank()) {
            val decrypted = decrypt(enc, iv)
            if (!decrypted.isNullOrBlank()) {
                val result = decrypted.trim()
                cachedApiKey = result
                isCacheLoaded = true
                return result
            }
        }

        // 2. Fallback / Migration: Check legacy plaintext key
        val legacy = prefs.getString(PREF_KEY_GEMINI_API_KEY, null)?.trim()?.ifBlank { null }
        if (legacy != null) {
            // Automatically migrate legacy plaintext key to encrypted format
            saveGeminiApiKey(legacy)
            return legacy
        }

        cachedApiKey = null
        isCacheLoaded = true
        return null
    }
}
