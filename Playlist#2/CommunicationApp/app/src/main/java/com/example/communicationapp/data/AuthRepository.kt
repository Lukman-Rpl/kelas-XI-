package com.example.communicationapp.data

import com.example.communicationapp.App
import com.example.communicationapp.data.models.User
import io.github.jan.supabase.auth.auth

object AuthRepository {
    fun currentUser(): User? {
        val session = App.supabase.auth.currentSessionOrNull() ?: return null
        val user = session.user
        return User(
            id = user?.id ?: "",
            email = user?.email,
            username = user?.userMetadata?.get("username") as? String,
            status = user?.userMetadata?.get("status") as? String,
            profileImageUrl = user?.userMetadata?.get("profileImageUrl") as? String
        )
    }

    suspend fun login(email: String, password: String) =
        App.supabase.auth.signInWith(io.github.jan.supabase.auth.providers.builtin.Email) {
            this.email = email
            this.password = password
        }

    suspend fun register(email: String, password: String) =
        App.supabase.auth.signUpWith(io.github.jan.supabase.auth.providers.builtin.Email) {
            this.email = email
            this.password = password
        }

    suspend fun logout() = App.supabase.auth.signOut()
}
