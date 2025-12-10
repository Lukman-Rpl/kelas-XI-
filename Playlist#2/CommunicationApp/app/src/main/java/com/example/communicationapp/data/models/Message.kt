package com.example.communicationapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("receiver_id") val receiverId: String? = null,
    @SerialName("group_id") val groupId: String? = null,
    @SerialName("message") val messageText: String,
    @SerialName("message_type") val messageType: String = "text", // text, image, video
    @SerialName("media_url") val mediaUrl: String? = null,
    @SerialName("created_at") val timestamp: String
)
