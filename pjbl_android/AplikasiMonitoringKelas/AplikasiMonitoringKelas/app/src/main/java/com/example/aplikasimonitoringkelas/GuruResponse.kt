package com.example.aplikasimonitoringkelas

import com.example.aplikasimonitoringkelas.model.GuruData

data class GuruResponse(
    val success: Boolean,
    val message: String,
    val data: GuruData
)