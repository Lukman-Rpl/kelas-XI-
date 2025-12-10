package com.example.communicationapp.data

import com.example.communicationapp.data.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object DatabaseManager {

    private const val SUPABASE_URL = "https://znsphqgqttgpisgdjtvq.supabase.co/rest/v1/messages"
    private const val SUPABASE_KEY = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // isi sesuai key kamu

    // insert message (pakai REST Supabase)
    suspend fun sendMessage(message: Message): Result<Message> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("id", message.id)
                put("sender_id", message.senderId)
                message.receiverId?.let { put("receiver_id", it) }
                message.groupId?.let { put("group_id", it) }
                put("message_text", message.messageText)
                put("message_type", message.messageType)
                put("media_url", message.mediaUrl)
                put("timestamp", message.timestamp)
            }

            val url = URL(SUPABASE_URL)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", SUPABASE_KEY)
                doOutput = true
            }

            conn.outputStream.use { os ->
                os.write(json.toString().toByteArray())
            }

            val response = conn.inputStream.bufferedReader().use { it.readText() }

            if (conn.responseCode !in 200..299) {
                return@withContext Result.failure(Exception("Send failed: $response"))
            }

            val arr = JSONArray(response)
            if (arr.length() == 0) {
                return@withContext Result.failure(Exception("Empty response from Supabase"))
            }

            val obj = arr.getJSONObject(0)
            val returned = message.copy(
                timestamp = obj.optString("timestamp")
            )

            Result.success(returned)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
