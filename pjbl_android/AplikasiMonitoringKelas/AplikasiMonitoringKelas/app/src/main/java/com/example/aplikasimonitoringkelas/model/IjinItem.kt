package com.example.aplikasimonitoringkelas.model

import com.google.gson.annotations.SerializedName

data class IjinItem(
    val id: Long,
    @SerializedName("guru_id") val guruId: Long,
    @SerializedName("nama_guru") val namaGuru: String,
    @SerializedName("tanggal_mulai") val tanggalMulai: String,
    @SerializedName("tanggal_selesai") val tanggalSelesai: String,
    val hari: String?,        // Optional
    val status: String,
    val keterangan: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
