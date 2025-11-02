package com.example.socialapp.model

data class Comment(
    var commentId: String? = null,
    var userId: String? = null,
    var content: String? = null,
    var timestamp: Long? = null
)
