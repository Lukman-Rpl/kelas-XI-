package com.example.sakubijak

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthManager(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    // WAJIB GANTI DENGAN WEB CLIENT ID ASLI DARI google-services.json (client_type: 3)
    private val webClientId = "930728813096-dbg5uika9akjqgajvui6rq1oatn1iq65.apps.googleusercontent.com"

    suspend fun signInWithGoogle(): String? {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // Menampilkan semua akun Google di HP
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                googleIdTokenCredential.idToken
            } else {
                null
            }
        } catch (e: GetCredentialCancellationException) {
            // User sengaja menutup dialog Google (Back button / Tap di luar dialog)
            Log.d("GoogleAuthManager", "User membatalkan proses Google Sign-In")
            null
        } catch (e: NoCredentialException) {
            // SHA-1 laptop belum terdaftar / Web Client ID salah / HP tidak ada akun Google
            Log.e("GoogleAuthManager", "Kredensial tidak ditemukan (Cek SHA-1 / Client ID): ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("GoogleAuthManager", "Gagal melakukan Google Sign-In: ${e.message}", e)
            null
        }
    }

    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("GoogleAuthManager", "Gagal sign out: ${e.message}")
        }
    }
}