package com.example.sakubijak

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sakubijak.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var apiService: ApiService
    private lateinit var prefManager: PreferenceManager
    private lateinit var googleAuthManager: GoogleAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Render UI langsung tanpa pengecekan session di sini
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PreferenceManager(this)
        apiService = ApiClient.getApiService(this)
        googleAuthManager = GoogleAuthManager(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        binding.btnGoogleLogin.setOnClickListener {
            handleGoogleLogin()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        setLoadingState(true, isGoogle = false)

        lifecycleScope.launch {
            try {
                val loginData = hashMapOf(
                    "email" to email,
                    "password" to password
                )

                val response = apiService.login(loginData)
                val body = response.body()

                if (response.isSuccessful && body?.status == true) {

                    // =========================================================
                    // TAMBAHKAN DI SINI: Simpan password yang berhasil digunakan
                    // =========================================================
                    val sharedPref = getSharedPreferences("sakubijak_pref", MODE_PRIVATE)
                    sharedPref.edit().putString("saved_password", password).apply()

                    processAuthSuccess(body)
                } else {
                    val errorMessage = body?.message ?: "Email atau password salah!"
                    resetButtons(errorMessage)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                resetButtons("Gagal terhubung ke server: ${e.localizedMessage ?: "Cek koneksi internet Anda"}")
            }
        }
    }

    private fun handleGoogleLogin() {
        setLoadingState(true, isGoogle = true)

        lifecycleScope.launch {
            try {
                val idToken = googleAuthManager.signInWithGoogle()

                if (!idToken.isNullOrEmpty()) {
                    val request = GoogleLoginRequest(idToken = idToken)
                    val response = apiService.googleLogin(request)
                    val body = response.body()

                    if (response.isSuccessful && body?.status == true) {
                        processAuthSuccess(body)
                    } else {
                        val errorMessage = body?.message ?: "Gagal memproses autentikasi Google di server"
                        resetButtons(errorMessage)
                    }
                } else {
                    resetButtons("Batal atau gagal mendapatkan autentikasi Google.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                resetButtons("Error Google Login: ${e.localizedMessage ?: "Terjadi kesalahan"}")
            }
        }
    }

    private fun processAuthSuccess(authResponse: AuthResponse) {
        val rawToken = authResponse.token

        if (!rawToken.isNullOrEmpty()) {
            // Simpan token ke PreferenceManager
            prefManager.saveAuthToken(rawToken)

            authResponse.wallet?.id?.let { walletId ->
                if (walletId != -1L) {
                    prefManager.saveActiveWalletId(walletId)
                }
            }

            val message = authResponse.message.ifEmpty { "Login berhasil!" }
            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()

            navigateToMain()
        } else {
            resetButtons("Token autentikasi tidak ditemukan.")
        }
    }

    private fun setLoadingState(isLoading: Boolean, isGoogle: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        binding.btnGoogleLogin.isEnabled = !isLoading

        if (isLoading) {
            binding.btnLogin.text = if (!isGoogle) "Mohon tunggu..." else "Login"
            binding.btnGoogleLogin.text = if (isGoogle) "Mohon tunggu..." else "Login dengan Google"
        } else {
            binding.btnLogin.text = "Login"
            binding.btnGoogleLogin.text = "Login dengan Google"
        }
    }

    private fun resetButtons(errorMessage: String) {
        setLoadingState(false, isGoogle = false)
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        // Hilangkan animasi transisi bawaan agar tidak berkedip saat berpindah halaman
        overridePendingTransition(0, 0)
        finish()
    }
}