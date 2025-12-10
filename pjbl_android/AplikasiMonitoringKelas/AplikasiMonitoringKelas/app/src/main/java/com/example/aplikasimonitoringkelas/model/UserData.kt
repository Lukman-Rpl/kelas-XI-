package com.example.aplikasimonitoringkelas.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("nama")
    val nama: String?, // WAJIB nullable

    @SerializedName("email")
    val email: String?, // WAJIB nullable

    @SerializedName("role")
    val role: String?, // WAJIB nullable

    @SerializedName("kelas_id")
    val kelas_id: Int?,

    val password: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)
