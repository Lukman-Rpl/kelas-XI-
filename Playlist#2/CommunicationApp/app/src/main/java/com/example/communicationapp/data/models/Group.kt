package com.example.communicationapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String,
    val name: String,
    val members: List<String> = emptyList() // default supaya tidak null saat decode
)
