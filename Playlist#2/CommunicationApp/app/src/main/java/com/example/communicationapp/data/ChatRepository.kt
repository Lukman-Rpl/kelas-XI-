package com.example.communicationapp.data

import android.util.Log
import com.example.communicationapp.App
import com.example.communicationapp.data.models.Message
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresListDataFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter

object ChatRepository {

    private const val TAG = "ChatRepository"
    private val supabase = App.supabase
    private val ioScope = CoroutineScope(Dispatchers.IO)

    // ✅ Kirim pesan (INSERT)
    suspend fun sendMessage(message: Message) {
        try {
            val result = supabase.postgrest
                .from("messages")
                .insert(listOf(message))

            val inserted: List<Message> = result.decodeList()
            Log.d(TAG, "Inserted ${inserted.size} message(s)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message", e)
        }
    }

    // ✅ Observasi pesan langsung (DM)
    fun observeDirectMessages(currentUserId: String, otherUserId: String): StateFlow<List<Message>> {
        val flow = MutableStateFlow<List<Message>>(emptyList())

        ioScope.launch {
            try {
                val result = supabase.postgrest
                    .from("messages")
                    .select(Columns.raw("*"))

                val allMessages: List<Message> = result.decodeList()

                val filtered = allMessages.filter {
                    (it.senderId == currentUserId && it.receiverId == otherUserId) ||
                            (it.senderId == otherUserId && it.receiverId == currentUserId)
                }.sortedBy { parseTimestamp(it.timestamp) }

                flow.value = filtered
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching direct messages", e)
            }
        }

        return flow
    }

    // ✅ Observasi pesan grup (Realtime)
    fun observeGroupMessagesRealtime(groupId: String): Flow<List<Message>> {
        val channel = supabase.channel("group_messages_$groupId")

        // Dengarkan seluruh tabel messages secara realtime
        val messagesFlow: Flow<List<Message>> = channel.postgresListDataFlow(
            schema = "public",
            table = "messages",
            primaryKey = Message::id
        )

        // Subscribe ke channel agar mulai menerima update
        ioScope.launch {
            try {
                channel.subscribe(blockUntilSubscribed = true)
                Log.d(TAG, "Subscribed to realtime channel for group $groupId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to subscribe to realtime channel", e)
            }
        }

        // Filter di sisi klien
        return messagesFlow.map {allMessages ->
            allMessages.filter { it.groupId == groupId }
                .sortedBy { parseTimestamp(it.timestamp) }
        }
    }

    // ✅ Helper konversi waktu ISO -> Instant
    private fun parseTimestamp(ts: String): Instant {
        return try {
            Instant.from(DateTimeFormatter.ISO_INSTANT.parse(ts))
        } catch (e: Exception) {
            Instant.EPOCH
        }
    }
}
