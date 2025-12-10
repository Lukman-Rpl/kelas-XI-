package com.example.aplikasimonitoringkelas

import com.example.aplikasimonitoringkelas.model.UserData

data class UserResponse(
    val message: String,
    val data: UserData // <-- PERBAIKAN: Ubah dari 'User' menjadi 'UserData'
)
