package com.example.communicationapp.ui.chat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communicationapp.data.AuthRepository
import com.example.communicationapp.data.ChatRepository
import com.example.communicationapp.data.models.Message
import com.example.communicationapp.databinding.ActivityGroupChatBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class GroupChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getStringExtra("groupId")

        // 🔧 Setup RecyclerView
        adapter = MessageAdapter(messages)
        binding.rvGroupMessages.layoutManager = LinearLayoutManager(this)
        binding.rvGroupMessages.adapter = adapter

        // 🔧 Tombol kirim
        binding.btnSendGroup.setOnClickListener { sendMessage() }

        // 🔧 Mulai mendengarkan pesan realtime
        listenMessages()
    }

    private fun sendMessage() {
        val text = binding.etGroupMessage.text.toString().trim()
        if (text.isEmpty()) return

        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = AuthRepository.currentUser()?.id ?: "",
            receiverId = null,
            groupId = groupId,
            messageText = text,
            messageType = "text",
            mediaUrl = null,
            timestamp = java.time.Instant.now().toString() // gunakan ISO format
        )

        lifecycleScope.launch {
            try {
                ChatRepository.sendMessage(message)
                binding.etGroupMessage.text.clear()
            } catch (e: Exception) {
                Toast.makeText(
                    this@GroupChatActivity,
                    "Gagal mengirim pesan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun listenMessages() {
        val id = groupId ?: return

        lifecycleScope.launch {
            // 🔥 Kumpulkan flow realtime dari Supabase
            ChatRepository.observeGroupMessagesRealtime(id).collectLatest { newMessages ->
                messages.clear()
                messages.addAll(newMessages)
                adapter.notifyDataSetChanged()
                binding.rvGroupMessages.scrollToPosition(messages.size - 1)
            }
        }
    }

}
