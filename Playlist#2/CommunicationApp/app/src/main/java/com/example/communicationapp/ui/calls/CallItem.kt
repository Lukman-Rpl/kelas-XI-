package com.example.communicationapp.ui.calls


data class CallItem(
    val id: String,
    val callerName: String,
    val callerImageUrl: String?,
    val callType: CallType, // VOICE atau VIDEO
    val callTime: String,   // Bisa pakai timestamp atau format string
    val isMissed: Boolean
)

enum class CallType {
    VOICE,
    VIDEO
}
