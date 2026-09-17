package com.example.sakubijak

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sakubijak_session", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
    }

    /**
     * Menyimpan token autentikasi
     */
    fun saveAuthToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }

    /**
     * Mengambil token mentah (digunakan oleh ApiClient & komponen lain)
     */
    fun getAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Alias dari getAuthToken() agar kompatibel jika kode lain memanggil fetchAuthToken()
     */
    fun fetchAuthToken(): String? {
        return getAuthToken()
    }

    /**
     * Ambil langsung format Header ("Bearer <token>")
     */
    fun fetchAuthTokenHeader(): String? {
        val token = getAuthToken()
        return if (!token.isNullOrEmpty()) {
            if (token.startsWith("Bearer ")) token else "Bearer $token"
        } else null
    }

    /**
     * Cek status login
     */
    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrEmpty()
    }

    /**
     * Menghapus sesi / Logout
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}