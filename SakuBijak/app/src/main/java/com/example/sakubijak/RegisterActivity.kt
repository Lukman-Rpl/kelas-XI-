package com.example.sakubijak

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sakubijak.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var prefManager: PreferenceManager
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        prefManager = PreferenceManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getApiService(this)

        binding.btnRegister.setOnClickListener {
            handleRegister()
        }

        binding.tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun handleRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // 1. Validasi Input Sisi Android
        if (name.isEmpty()) {
            binding.etName.error = "Nama lengkap wajib diisi!"
            binding.etName.requestFocus()
            return
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Masukkan email yang valid!"
            binding.etEmail.requestFocus()
            return
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password minimal 6 karakter!"
            binding.etPassword.requestFocus()
            return
        }

        // 2. Loading State
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Mendaftarkan..."

        // 3. Kirim ke Server
        lifecycleScope.launch {
            try {
                val registerData = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password
                )

                val response = apiService.register(registerData)

                // Cek HTTP Response Code 200/201 (Sukses)
                if (response.isSuccessful && response.body() != null) {
                    val apiBody = response.body()!!
                    val authData = apiBody.data // Objek AuthResponse

                    val rawToken = authData?.token
                    if (!rawToken.isNullOrEmpty()) {
                        // Simpan Token & Active Wallet ID
                        sessionManager.saveAuthToken(rawToken)

                        authData.wallet?.id?.let { walletId ->
                            if (walletId != -1L) {
                                prefManager.saveActiveWalletId(walletId)
                            }
                        }

                        val message = apiBody.message ?: "Pendaftaran berhasil!"
                        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()

                        navigateToMain()
                    } else {
                        resetRegisterButton("Token autentikasi tidak ditemukan.")
                    }
                } else {
                    // Ambil pesan error dari errorBody jika status HTTP 4xx / 5xx
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorJson) ?: "Pendaftaran gagal"
                    resetRegisterButton(errorMessage)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                resetRegisterButton("Tidak dapat terhubung ke server. Periksa koneksi internet.")
            }
        }
    }

    /**
     * Helper untuk membaca pesan error JSON dari Laravel saat response gagal (HTTP 422/400/500)
     */
    private fun parseErrorMessage(errorJson: String?): String? {
        if (errorJson.isNullOrEmpty()) return null
        return try {
            val jsonObject = JSONObject(errorJson)
            if (jsonObject.has("message")) {
                jsonObject.getString("message")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun resetRegisterButton(message: String) {
        binding.btnRegister.isEnabled = true
        binding.btnRegister.text = "Daftar Sekarang"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}