package com.example.socialapp

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Tidak perlu init manual karena SupabaseClientProvider sudah lazy
        // Supabase akan otomatis siap saat dipanggil
    }
}
