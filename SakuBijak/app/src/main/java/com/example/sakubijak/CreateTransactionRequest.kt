package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class CreateTransactionRequest(
    @SerializedName("wallet_id") val walletId: Long,
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("type") val type: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("date") val date: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("recipient_name") val recipientName: String? = null,
    @SerializedName("recipient_account") val recipientAccount: String? = null
)