package com.example.aplikasimonitoringkelas.model

import com.google.gson.annotations.SerializedName

data class KelasItem(
    val id: Long,
    @SerializedName("nama_kelas") val namaKelas: String,
    @SerializedName("jurusan_id") val jurusanId: Long,
    @SerializedName("tahun_ajaran_id") val tahunAjaranId: Long,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val jurusan: JurusanItem?,          // object jurusan
    @SerializedName("tahun_ajaran") val tahunAjaran: TahunAjaranItem? // object tahun ajaran
)

data class JurusanItem(
    val id: Long,
    @SerializedName("nama_jurusan") val namaJurusan: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class TahunAjaranItem(
    val id: Long,
    val tahun: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
