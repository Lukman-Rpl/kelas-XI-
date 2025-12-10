package com.example.communicationapp.data

import com.example.communicationapp.App
import com.example.communicationapp.data.models.Status
import io.github.jan.supabase.postgrest.postgrest

object StatusRepository {
    suspend fun addStatus(status: Status) {
        App.supabase.postgrest["statuses"].insert(status)
    }

    suspend fun getStatuses(): List<Status> =
        App.supabase.postgrest["statuses"].select().decodeList<Status>()
}
