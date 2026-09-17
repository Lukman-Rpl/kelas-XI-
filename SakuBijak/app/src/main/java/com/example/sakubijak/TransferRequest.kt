package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class TransferRequest(
    @SerializedName("from_wallet_id")
    val fromWalletId: Long,

    // Disesuaikan dengan backend baru (menggunakan nomor unik wallet)
    @SerializedName("to_no_wallet")
    val toNoWallet: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("date")
    val date: String, // Format: YYYY-MM-DD (e.g., "2026-08-26")

    @SerializedName("description")
    val description: String? = null
)