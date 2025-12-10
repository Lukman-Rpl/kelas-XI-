package com.example.communicationapp.data

import com.example.communicationapp.App
import com.example.communicationapp.data.models.User
import io.github.jan.supabase.postgrest.from



object UserRepository {
    suspend fun updateUser(
        id: String,
        username: String?,
        status: String?,
        profileImageUrl: String?
    ) {
        App.supabase.from("users").update({
            username?.let { set("username", it) }
            status?.let { set("status", it) }
            profileImageUrl?.let { set("profileImageUrl", it) }
        }) {
            filter {
                eq("id", id) // ✅ filter di sini, bukan langsung di update
            }
        }
    }

    suspend fun getUsers(): List<User> =
        App.supabase.from("users").select().decodeList<User>()
}
