package com.example.sakubijak

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.sakubijak.databinding.ActivityChangePasswordBinding
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Ambil password user dari SharedPreferences & otomatis isi ke etOldPassword
        loadSavedPassword()

        // 2. Setup Listener Tombol
        setupListeners()

        // 3. Menghapus error secara otomatis saat user mulai mengetik ulang
        setupTextWatchers()
    }

    private fun loadSavedPassword() {
        val sharedPref = getSharedPreferences("sakubijak_pref", Context.MODE_PRIVATE)
        val savedPassword = sharedPref.getString("saved_password", "")

        if (!savedPassword.isNullOrEmpty()) {
            binding.etOldPassword.setText(savedPassword)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnChangePassword.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun setupTextWatchers() {
        // Otomatis bersihkan error saat user mengetik
        binding.etOldPassword.doOnTextChanged { _, _, _, _ -> binding.etOldPassword.error = null }
        binding.etNewPassword.doOnTextChanged { _, _, _, _ -> binding.etNewPassword.error = null }
        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ -> binding.etConfirmPassword.error = null }
    }

    private fun validateAndSubmit() {
        val oldPassword = binding.etOldPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // 1. Validasi Input Tidak Boleh Kosong
        if (oldPassword.isEmpty()) {
            binding.etOldPassword.error = "Password lama wajib diisi"
            binding.etOldPassword.requestFocus()
            return
        }

        if (newPassword.isEmpty()) {
            binding.etNewPassword.error = "Password baru wajib diisi"
            binding.etNewPassword.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Konfirmasi password wajib diisi"
            binding.etConfirmPassword.requestFocus()
            return
        }

        // 2. Validasi: Password Baru Tidak Boleh Sama dengan Password Lama
        if (oldPassword == newPassword) {
            binding.etNewPassword.error = "Password baru tidak boleh sama dengan password lama"
            binding.etNewPassword.requestFocus()
            return
        }

        // 3. Validasi Ketentuan Password Baru
        if (newPassword.length < 8) {
            binding.etNewPassword.error = "Password minimal 8 karakter"
            binding.etNewPassword.requestFocus()
            return
        }

        if (!newPassword.any { it.isUpperCase() }) {
            binding.etNewPassword.error = "Password harus mengandung minimal 1 huruf besar"
            binding.etNewPassword.requestFocus()
            return
        }

        if (!newPassword.any { it.isDigit() }) {
            binding.etNewPassword.error = "Password harus mengandung minimal 1 angka"
            binding.etNewPassword.requestFocus()
            return
        }

        // 4. Validasi Kesesuaian Konfirmasi Password
        if (newPassword != confirmPassword) {
            binding.etConfirmPassword.error = "Konfirmasi password tidak cocok"
            binding.etConfirmPassword.requestFocus()
            return
        }

        // 5. Kirim Permintaan Ubah Password ke Server
        processChangePassword(oldPassword, newPassword)
    }

    private fun processChangePassword(oldPass: String, newPass: String) {
        binding.btnChangePassword.isEnabled = false
        binding.btnChangePassword.text = "Memproses..."

        lifecycleScope.launch {
            try {
                // Catatan: Pastikan endpoint ubah password tersedia di ApiService Anda jika ingin dihubungkan ke server.

                // Update password baru di SharedPreferences agar tetap sinkron jika user ganti password lagi
                val sharedPref = getSharedPreferences("sakubijak_pref", Context.MODE_PRIVATE)
                sharedPref.edit().putString("saved_password", newPass).apply()

                Toast.makeText(
                    this@ChangePasswordActivity,
                    "Password berhasil diperbarui!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } catch (e: Exception) {
                e.printStackTrace()
                binding.btnChangePassword.isEnabled = true
                binding.btnChangePassword.text = "Ubah Password"

                Toast.makeText(
                    this@ChangePasswordActivity,
                    "Gagal mengubah password: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}