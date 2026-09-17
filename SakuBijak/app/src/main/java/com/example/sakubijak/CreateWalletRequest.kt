package com.example.sakubijak

import com.google.gson.annotations.SerializedName

// Model Body Request untuk membuat Dompet Baru
data class CreateWalletRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String = "personal", // Default "personal"

    @SerializedName("balance")
    val balance: Double = 0.0
)