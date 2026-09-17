package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class GroupJoinRequest(
    @SerializedName("code")
    val code: String
)