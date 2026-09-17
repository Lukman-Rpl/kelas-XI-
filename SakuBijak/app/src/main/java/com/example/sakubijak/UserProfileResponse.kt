package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserData?
)