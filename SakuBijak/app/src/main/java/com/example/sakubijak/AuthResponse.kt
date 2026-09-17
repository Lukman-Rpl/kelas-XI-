package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: String?,

    @SerializedName("user")
    val user: UserData?,

    @SerializedName("wallet")
    val wallet: WalletResponse? // <--- Ditambahkan agar bisa membaca wallet bawaan
)

data class UserData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    // GANTI 'profile_photo_url' MENJADI 'profile_photo'
    @SerializedName("profile_photo")
    val profilePhoto: String?,

    @SerializedName("google_id")
    val googleId: String?
)

 