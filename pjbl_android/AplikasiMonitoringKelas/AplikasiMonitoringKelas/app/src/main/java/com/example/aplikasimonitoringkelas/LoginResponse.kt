package com.example.aplikasimonitoringkelas

import com.example.aplikasimonitoringkelas.model.UserData
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,      // dibuat nullable untuk keamanan

    @SerializedName("token")
    val token: String?,        // token juga bisa null jika gagal login

    @SerializedName("user")
    val user: UserData?        // HARUS nullable karena jika login gagal, user = null
)
