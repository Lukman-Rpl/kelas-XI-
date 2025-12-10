package com.example.aplikasimonitoringkelas.model

import com.google.gson.annotations.SerializedName

data class JadwalCreateRequest(
    @SerializedName("hari") val hari: String,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("kelas_id") val kelasId: Long,
    @SerializedName("jam_ke") val jamKe: String,
    @SerializedName("sampai_jam") val sampaiJam: String,
    @SerializedName("mapel_id") val mapelId: Long,
    @SerializedName("guru_id") val guruId: Long,
    @SerializedName("guru_pengganti_id") val guruPenggantiId: Long?,
    @SerializedName("status") val status: String,
    @SerializedName("keterangan") val keterangan: String?
)
