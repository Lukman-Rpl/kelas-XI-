package com.example.aplikasimonitoringkelas.model
data class Schedule(
    val id: Long,
    val hari: String,
    val tanggal: String,
    val kelas_id: Long?,
    val jam_ke: String,             // format "HH:mm:ss"
    val sampai_jam: String,         // format "HH:mm:ss"
    val mapel_id: Long?,            // ID mapel, optional
    val mapel: String,              // nama mapel
    val guru_id: Long?,
    val guru_pengganti_id: Long?,
    val status: String,             // 'hadir', 'tidak_hadir', 'terlambat'
    val keterangan: String?,
    val created_at: String?,
    val updated_at: String?
)
