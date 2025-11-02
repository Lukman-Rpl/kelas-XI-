package com.example.communicationapp

import android.app.Application
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage


class App : Application() {

    companion object {
        lateinit var supabase: SupabaseClient
    }

    override fun onCreate() {
        super.onCreate()

        // ✅ Modern Supabase Kotlin DSL setup
        supabase = createSupabaseClient(
            supabaseUrl = "https://znsphqgqttgpisgdjtvq.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        ) {
            // This is the new way to "install" features
            install(Auth)
            install(Postgrest)
            install(Realtime)
            install(Storage)
        }
    }
}
