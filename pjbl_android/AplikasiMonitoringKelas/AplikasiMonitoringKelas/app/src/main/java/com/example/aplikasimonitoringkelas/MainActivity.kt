package com.example.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.aplikasimonitoringkelas.model.UserData
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AplikasiMonitoringKelasTheme {
                LoginScreen { user ->
                    saveUserToPreferences(user)
                    navigateToRoleActivity(user.role)
                }
            }
        }
    }

    private fun saveUserToPreferences(user: UserData) {
        val shared = getSharedPreferences("user", MODE_PRIVATE)
        shared.edit()
            .putInt("id", user.id ?: 0)
            .putString("nama", user.nama)
            .putString("email", user.email)
            .putString("role", user.role)
            .putInt("kelas_id", user.kelas_id ?: 0)
            .apply()
    }

    private fun navigateToRoleActivity(role: String?) {
        if (role == null) return
        val intent = when (role.lowercase()) {
            "siswa" -> Intent(this, SiswaActivity::class.java)
            "kurikulum" -> Intent(this, KurikulumActivity::class.java)
            "kepala-sekolah" -> Intent(this, KepalaSekolahActivity::class.java)
            else -> null
        }
        intent?.let {
            startActivity(it)
            finish()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginScreen(onLoginSuccess: (UserData) -> Unit) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("siswa") }
        var expanded by remember { mutableStateOf(false) }

        val roles = listOf("admin", "kurikulum", "siswa", "kepala-sekolah")
        val snackbar = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor() // PENTING agar dropdown muncul
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roles.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r) },
                                onClick = {
                                    role = r
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = {
                        scope.launch {
                            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                snackbar.showSnackbar("Email tidak valid")
                                return@launch
                            }
                            if (password.isBlank()) {
                                snackbar.showSnackbar("Password tidak boleh kosong")
                                return@launch
                            }

                            try {
                                val res = ApiClient.apiService.login(
                                    LoginRequest(
                                        email.trim(),
                                        password.trim(),
                                        role
                                    )
                                )
                                res.body()?.let { body ->
                                    if (body.success && body.user != null) {
                                        onLoginSuccess(body.user)
                                    } else {
                                        snackbar.showSnackbar(body.message ?: "Login gagal")
                                    }
                                } ?: run {
                                    snackbar.showSnackbar("Gagal mendapatkan respons dari server")
                                }
                            } catch (e: Exception) {
                                snackbar.showSnackbar("Error: ${e.message ?: "Terjadi kesalahan"}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
            }
        }
    }
}
