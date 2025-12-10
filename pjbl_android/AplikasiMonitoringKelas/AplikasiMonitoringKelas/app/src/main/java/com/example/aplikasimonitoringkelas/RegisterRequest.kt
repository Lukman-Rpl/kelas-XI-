package com.example.aplikasimonitoringkelas

data class RegisterRequest(
    val nama: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val role: String? = "siswa",
    val kelas_id: Int? = null
)
