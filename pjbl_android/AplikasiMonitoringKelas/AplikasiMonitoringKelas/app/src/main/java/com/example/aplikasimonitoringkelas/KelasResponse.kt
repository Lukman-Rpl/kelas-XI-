package com.example.aplikasimonitoringkelas

import com.example.aplikasimonitoringkelas.model.KelasItem

data class KelasResponse(
    val status: String,
    val data: List<KelasItem>
)
