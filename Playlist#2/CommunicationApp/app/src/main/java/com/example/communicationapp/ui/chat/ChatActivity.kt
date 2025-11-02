package com.example.communicationapp.ui.chat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communicationapp.data.AuthRepository
import com.example.communicationapp.data.ChatRepository
import com.example.communicationapp.data.models.Message
import com.example.communicationapp.databinding.ActivityChatBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class ChatActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityChatBinding

    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private var receiverId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil receiverId dari intent
        receiverId = intent.getStringExtra("receiverId")

        // Setup RecyclerView
        adapter = MessageAdapter(messages)
        binding.recyclerMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerMessages.adapter = adapter

        // Tombol kirim
        binding.btnSend.setOnClickListener { sendMessage() }

        // Mulai dengarkan pesan
        listenMessages()
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = AuthRepository.currentUser()?.id ?: "",
            receiverId = receiverId,
            groupId = null,
            messageText = text,
            messageType = "text",
            mediaUrl = null,
            timestamp = System.currentTimeMillis().toString()
        )

        lifecycleScope.launch {
            try {
                ChatRepository.sendMessage(message)
                binding.etMessage.text.clear()
            } catch (e: Exception) {
                Toast.makeText(
                    this@ChatActivity,
                    "Gagal mengirim pesan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun listenMessages() {
        val currentUserId = AuthRepository.currentUser()?.id ?: return
        val otherId = receiverId ?: return

        lifecycleScope.launch {
            ChatRepository.observeDirectMessages(currentUserId, otherId)
                .collectLatest { list ->
                    messages.clear()
                    messages.addAll(list)
                    adapter.notifyDataSetChanged()
                    // Scroll ke bawah jika ada pesan baru
                    if (messages.isNotEmpty()) {
                        binding.recyclerMessages.scrollToPosition(messages.size - 1)
                    }
                }
        }
    }
}
