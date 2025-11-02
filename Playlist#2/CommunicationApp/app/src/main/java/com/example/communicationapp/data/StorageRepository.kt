package com.example.communicationapp.data

import android.net.Uri
import com.example.communicationapp.App
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import java.io.File

object StorageRepository {
    suspend fun uploadImage(path: String, uri: Uri): String {
        val file = File(uri.path ?: return "")
        val bucket = App.supabase.storage["media"]
        bucket.upload(path, file)
        return bucket.publicUrl(path)
    }
}
