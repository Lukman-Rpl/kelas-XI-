package com.example.communicationapp.ui.status

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communicationapp.data.models.Status
import com.example.communicationapp.data.AuthRepository
import com.example.communicationapp.data.StatusRepository
import com.example.communicationapp.data.StorageRepository
import com.example.communicationapp.databinding.FragmentStatusBinding
import kotlinx.coroutines.launch
import java.util.*

class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StatusAdapter
    private val statuses = mutableListOf<Status>()
    private val PICK_MEDIA = 200
    private var mediaUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StatusAdapter(statuses)
        binding.rvStatus.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStatus.adapter = adapter

        binding.btnAddStatus.setOnClickListener {
            pickMedia()
        }

        loadStatuses()
    }

    private fun pickMedia() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        }
        startActivityForResult(intent, PICK_MEDIA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_MEDIA && resultCode == Activity.RESULT_OK) {
            mediaUri = data?.data
            mediaUri?.let { uploadStatus(it) }
        }
    }

    private fun uploadStatus(uri: Uri) {
        val user = AuthRepository.currentUser() ?: return

        lifecycleScope.launch {
            try {
                val mediaUrl = StorageRepository.uploadImage("status/${UUID.randomUUID()}", uri)
                StatusRepository.addStatus(
                    Status(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        mediaUrl = mediaUrl,
                        mediaType = if (mediaUrl.endsWith(".mp4")) "video" else "image",
                        timestamp = System.currentTimeMillis().toString()
                    )
                )
                Toast.makeText(requireContext(), "Status berhasil diupload", Toast.LENGTH_SHORT).show()
                loadStatuses()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal upload status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadStatuses() {
        lifecycleScope.launch {
            val result = StatusRepository.getStatuses()
            statuses.clear()
            statuses.addAll(result)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
