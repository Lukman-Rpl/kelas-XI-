package com.example.sakubijak

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREF_NAME = "SakuBijakPrefs"
        private const val KEY_ACTIVE_WALLET_ID = "active_wallet_id"

        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHOTO_URI = "user_photo_uri"
    }

    // ==========================================
    // 1. FITUR AUTH TOKEN
    // ==========================================
    fun saveAuthToken(token: String) {
        val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        prefs.edit { putString(KEY_AUTH_TOKEN, formattedToken) }
    }

    /**
     * Mengembalikan token lengkap dengan prefix "Bearer "
     * Cocok digunakan jika Interceptor Retrofit langsung memasang nilainya.
     */
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Alias dari getAuthToken() agar kompatibel dengan pemanggilan di MainActivity.kt
     */
    fun getToken(): String? = getAuthToken()

    /**
     * Mengembalikan token MURNI tanpa kata "Bearer "
     * Sangat berguna jika butuh string token murni atau pengecekan JWT/payload
     */
    fun getRawToken(): String? {
        val token = getAuthToken() ?: return null
        return if (token.startsWith("Bearer ")) token.removePrefix("Bearer ").trim() else token
    }

    // ==========================================
    // 2. FITUR PROFIL USER
    // ==========================================
    fun saveUserProfile(name: String, email: String, photoUri: String? = null) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            photoUri?.let { putString(KEY_USER_PHOTO_URI, it) }
            apply()
        }
    }

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""
    fun getUserEmail(): String = prefs.getString(KEY_USER_EMAIL, "") ?: ""
    fun getUserPhotoUri(): String? = prefs.getString(KEY_USER_PHOTO_URI, null)

    // ==========================================
    // 3. FITUR WALLET
    // ==========================================
    fun saveActiveWalletId(walletId: Long) {
        prefs.edit().putLong(KEY_ACTIVE_WALLET_ID, walletId).apply()
    }

    fun getActiveWalletId(): Long {
        return try {
            prefs.getLong(KEY_ACTIVE_WALLET_ID, -1L)
        } catch (e: ClassCastException) {
            val oldIntId = prefs.getInt(KEY_ACTIVE_WALLET_ID, -1)
            val newLongId = oldIntId.toLong()
            saveActiveWalletId(newLongId)
            newLongId
        }
    }

    // ==========================================
    // 4. CLEAR SESSION / LOGOUT
    // ==========================================
    fun clear() {
        prefs.edit().clear().apply()
    }

    fun clearSession() {
        clear()
    }
}