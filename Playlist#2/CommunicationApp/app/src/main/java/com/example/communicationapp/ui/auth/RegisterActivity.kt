package com.example.communicationapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.communicationapp.databinding.ActivityRegisterBinding
import com.example.communicationapp.ui.main.MainActivity
import com.example.communicationapp.data.AuthRepository
import com.example.communicationapp.data.UserRepository
import com.example.communicationapp.data.models.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            doRegister()
        }
    }

    private fun doRegister() {
        val email = binding.inputEmail.text.toString().trim()
        val password = binding.inputPassword.text.toString().trim()
        val username = binding.inputUsername.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // panggil Supabase signUp
                AuthRepository.register(email, password)

                // Ambil current user dari session
                val currentUser = AuthRepository.currentUser()
                if (currentUser != null) {
                    // Simpan/update user ke tabel users (PostgREST)
                    UserRepository.updateUser(
                        id = currentUser.id,
                        username = username,
                        status = "Hey there! I am using ChatApp",
                        profileImageUrl = null
                    )
                }

                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Register gagal: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
