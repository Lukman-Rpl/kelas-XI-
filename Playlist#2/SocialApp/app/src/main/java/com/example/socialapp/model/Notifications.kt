package com.example.socialapp.model

data class Notification(
    val senderId: String? = null,
    val receiverId: String? = null,
    val type: String? = null,
    val postId: String? = null,
    val storyId: String? = null,
    val timestamp: Long? = null
)
