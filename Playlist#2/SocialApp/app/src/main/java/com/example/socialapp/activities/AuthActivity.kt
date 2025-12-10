package com.example.socialapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.socialapp.MainActivity
import com.example.socialapp.SupabaseClientProvider
import com.example.socialapp.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        setupUI()
    }

    private fun setupUI() {
        binding.btnAction.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (!isLoginMode) {
                val username = binding.etUsername.text.toString().trim()
                if (username.isEmpty()) {
                    binding.etUsername.error = "Username harus diisi"
                    return@setOnClickListener
                }
            }

            if (email.isEmpty()) {
                binding.etEmail.error = "Email harus diisi"
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 6) {
                binding.etPassword.error = "Password minimal 6 karakter"
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            if (isLoginMode) {
                loginUser(email, password)
            } else {
                val username = binding.etUsername.text.toString().trim()
                registerUser(username, email, password)
            }
        }

        binding.tvSwitchAuth.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
        }
    }

    private fun updateUI() {
        if (isLoginMode) {
            binding.tvTitle.text = "Login"
            binding.btnAction.text = "Login"
            binding.tvSwitchAuth.text = "Belum punya akun? Daftar"
            binding.etUsername.visibility = View.GONE
        } else {
            binding.tvTitle.text = "Register"
            binding.btnAction.text = "Daftar"
            binding.tvSwitchAuth.text = "Sudah punya akun? Login"
            binding.etUsername.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
        binding.etUsername.text?.clear()
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    // 🔑 Login ke Supabase juga
                    lifecycleScope.launch {
                        try {
                            SupabaseClientProvider.client.auth.signInWith(Email) {
                                this.email = email
                                this.password = password
                            }
                            Toast.makeText(this@AuthActivity, "Login berhasil (Firebase + Supabase)", Toast.LENGTH_SHORT).show()
                            goToMain()
                        } catch (e: Exception) {
                            Toast.makeText(this@AuthActivity, "Login Supabase gagal: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Login Firebase gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun registerUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    // simpan user di Firebase
                    val userMap = mapOf(
                        "uid" to uid,
                        "username" to username,
                        "email" to email,
                        "createdAt" to System.currentTimeMillis()
                    )
                    FirebaseDatabase.getInstance().getReference("users")
                        .child(uid)
                        .setValue(userMap)

                    // 🔑 Register di Supabase juga
                    lifecycleScope.launch {
                        try {
                            SupabaseClientProvider.client.auth.signUpWith(Email) {
                                this.email = email
                                this.password = password
                                this.data = buildJsonObject {
                                    put("username", username)
                                }
                            }
                            Toast.makeText(this@AuthActivity, "Registrasi berhasil (Firebase + Supabase)", Toast.LENGTH_SHORT).show()
                            goToMain()
                        } catch (e: Exception) {
                            Toast.makeText(this@AuthActivity, "Registrasi Supabase gagal: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Registrasi Firebase gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
