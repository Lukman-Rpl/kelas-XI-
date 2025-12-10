package com.example.socialapp.model

data class Story(
    var storyId: String? = null,
    var userId: String? = null,
    var username: String? = null,
    var mediaUrl: String? = null,
    var mediaType: String? = null, // "image" atau "video"
    var timestampStart: Long? = null,
    var timestampEnd: Long? = null
)
