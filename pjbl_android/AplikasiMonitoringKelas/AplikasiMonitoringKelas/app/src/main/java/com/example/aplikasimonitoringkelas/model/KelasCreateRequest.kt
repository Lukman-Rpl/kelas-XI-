package com.example.aplikasimonitoringkelas.model

data class KelasCreateRequest(
    val nama_kelas: String,
    val jurusan_id: Long,
    val tahun_ajaran_id: Long
)
