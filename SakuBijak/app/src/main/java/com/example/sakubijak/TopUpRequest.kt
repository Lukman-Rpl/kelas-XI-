package com.example.sakubijak

import com.google.gson.annotations.SerializedName

// 1. Data yang dikirim ke Laravel API
data class TopUpRequest(
    @SerializedName("wallet_id")
    val walletId: Long,

    @SerializedName("amount")
    val amount: Double,

    // Tambahkan deskripsi opsional (tidak wajib diisi)
    @SerializedName("description")
    val description: String? = null
)

// 2. Data wrapper terluar dari Laravel Response (Tetap Sama)
data class TopUpResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: TopUpData?
)

// 3. Objek data bagian dalam yang berisi token
data class TopUpData(
    @SerializedName("snap_token")
    val snapToken: String? = null,

    @SerializedName("order_id")
    val orderId: String? = null,

    // Tambahkan transaction_id yang dikembalikan oleh backend
    @SerializedName("transaction_id")
    val transactionId: Long? = null
)