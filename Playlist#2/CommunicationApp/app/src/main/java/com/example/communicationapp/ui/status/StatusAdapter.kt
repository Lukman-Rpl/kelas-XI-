package com.example.communicationapp.ui.status

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communicationapp.data.models.Status
import com.example.communicationapp.databinding.ItemStatusBinding

class StatusAdapter(
    private val statuses: List<Status>
) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {

    inner class StatusViewHolder(val binding: ItemStatusBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val binding = ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val status = statuses[position]
        with(holder.binding) {
            tvUserName.text = status.userId   // nanti bisa diganti ambil nama user dari repo
            tvTime.text = android.text.format.DateFormat.format("HH:mm", status.timestamp.toLong())

            // foto profil (dummy dulu, nanti bisa diambil dari user profile)
            Glide.with(root.context)
                .load("https://via.placeholder.com/150")
                .circleCrop()
                .into(ivUserImage)

            // media status
            Glide.with(root.context)
                .load(status.mediaUrl)
                .centerCrop()
                .into(ivMediaThumb)
        }
    }

    override fun getItemCount(): Int = statuses.size
}
