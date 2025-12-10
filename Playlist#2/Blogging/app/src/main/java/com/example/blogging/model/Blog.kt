package com.example.blogapp.model

data class Blog(
    var id: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String = "", // base64 disimpan di sini
    val authorId: String = "",
    val timestamp: Long = 0L
)
