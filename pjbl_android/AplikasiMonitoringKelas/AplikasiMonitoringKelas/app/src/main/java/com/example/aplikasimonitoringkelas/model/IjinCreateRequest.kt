package com.example.aplikasimonitoringkelas.model

data class IjinCreateRequest(
    val guru_id: Long,               // Menggantikan user_id
    val tanggal_mulai: String,       // Contoh: "2025-11-26"
    val tanggal_selesai: String,     // Contoh: "2025-11-28"
    val hari: Int,                   // Total hari ijin (boleh dihitung di Android atau backend)
    val status: String,              // "sakit" atau "ijin"
    val keterangan: String? = null   // Opsional
)
