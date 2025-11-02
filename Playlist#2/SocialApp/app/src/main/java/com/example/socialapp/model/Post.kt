package com.example.socialapp.model
data class Post(
    var postId: String? = null,
    var userId: String? = null,
    var username: String? = null,   // biar bisa tampil di feed
    var mediaUrl: String? = null,
    var mediaType: String? = null,  // "image" / "video"
    var caption: String? = null,
    var timestamp: Long? = null
)
