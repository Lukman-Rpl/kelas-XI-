package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class ApiResponseGeneric(
    @SerializedName("status")
    val status: String? = null, // "success" atau "error"

    @SerializedName("message")
    val message: String? = null
) {
    // Helper function agar kamu tetap bisa mengecek boolean dengan mudah di kode Android
    fun isSuccess(): Boolean = status == "success"
}