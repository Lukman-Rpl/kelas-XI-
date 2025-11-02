package com.example.socialapp

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

object SupabaseClientProvider {

    private var _client: SupabaseClient? = null

    val client: SupabaseClient
        get() {
            if (_client == null) {
                _client = createSupabaseClient(
                    supabaseUrl = "https://bajvkldmxwbnrzzflvmj.supabase.co", // 🔗 ganti
                    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJhanZrbGRteHdibnJ6emZsdm1qIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTcwNzQwODEsImV4cCI6MjA3MjY1MDA4MX0.2zpI5XaSSIIsr1d7MD4L0g4sQ08HmZCll8NUebBczis" // 🔑 ganti
                ) {
                    install(Auth)      // ✅ auth module
                    install(Postgrest) // ✅ database
                    install(Storage)   // ✅ storage
                }
            }
            return _client!!
        }
}
