package com.example.aplikasimonitoringkelas.model

data class GuruPenggantiItem(
    val id: Long,
    val guru_id: Long,
    val guru_pengganti_id: Long,
    val nama: String?,
    val mapel_id: Long?,
    val kelas_id: Long?,
    val tanggal: String,
    val jam: String?,
    val keterangan: String?,
    val created_at: String?,
    val updated_at: String?
)
