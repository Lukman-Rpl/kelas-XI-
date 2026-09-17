package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("wallet_id") val walletId: Int?,
    @SerializedName("categories") val categories: String?, // Sesuaikan nama key JSON ("categories")
    @SerializedName("limit_amount") val limitAmount: String?
)