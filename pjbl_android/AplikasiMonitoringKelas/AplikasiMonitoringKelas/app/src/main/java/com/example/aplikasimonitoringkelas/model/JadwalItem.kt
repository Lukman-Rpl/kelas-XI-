package com.example.aplikasimonitoringkelas.model

import com.google.gson.annotations.SerializedName

data class JadwalItem(
    val id: Long,
    val hari: String,
    val tanggal: String,
    @SerializedName("kelas_id") val kelasId: Long?,
    @SerializedName("jam_ke") val jamKe: String,
    @SerializedName("sampai_jam") val sampaiJam: String,
    @SerializedName("mapel_id") val mapelId: Long?,
    val mapel: String,
    @SerializedName("guru_id") val guruId: Long?,
    val guru: String,
    @SerializedName("guru_pengganti_id") val guruPenggantiId: Long?,
    @SerializedName("guru_pengganti") val guruPengganti: String?,
    val status: String,
    val keterangan: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
