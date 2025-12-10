package com.example.communicationapp.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communicationapp.data.AuthRepository
import com.example.communicationapp.data.ChatRepository
import com.example.communicationapp.data.models.Message
import com.example.communicationapp.databinding.FragmentChatBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MessageAdapter(messages)
        binding.recyclerMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMessages.adapter = adapter

        binding.btnSend.setOnClickListener { sendMessage() }

        listenMessages()
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = AuthRepository.currentUser()?.id ?: "",
            receiverId = null, // gunakan null jika chat umum, bisa disesuaikan
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
                    requireContext(),
                    "Gagal mengirim pesan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun listenMessages() {
        lifecycleScope.launch {
            ChatRepository.observeDirectMessages(
                currentUserId = AuthRepository.currentUser()?.id ?: "",
                otherUserId = "receiverId" // sesuaikan jika ada receiver tertentu
            ).collectLatest { newMessages ->
                messages.clear()
                messages.addAll(newMessages)
                adapter.notifyDataSetChanged()
                binding.recyclerMessages.scrollToPosition(messages.size - 1)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
