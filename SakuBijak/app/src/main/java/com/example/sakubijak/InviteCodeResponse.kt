package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class InviteCodeResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: InviteCodeData
)

data class InviteCodeData(
    @SerializedName("code")
    val code: String, // Contoh kode unik: "SB-8F3A2K"

    @SerializedName("expired_at")
    val expiredAt: String?,

    @SerializedName("max_usage")
    val maxUsage: Int?
)