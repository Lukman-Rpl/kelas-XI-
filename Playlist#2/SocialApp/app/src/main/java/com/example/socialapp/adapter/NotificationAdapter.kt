package com.example.socialapp.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.example.socialapp.model.Notification
import com.google.firebase.database.FirebaseDatabase

class NotificationAdapter(
    private val context: Context,
    private val notifList: List<Notification>
) : RecyclerView.Adapter<NotificationAdapter.NotifViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        return NotifViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        val notif = notifList[position]
        holder.bind(notif)
    }

    override fun getItemCount() = notifList.size

    inner class NotifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgSender: ImageView = itemView.findViewById(R.id.imgSender)
        private val tvNotificationText: TextView = itemView.findViewById(R.id.tvNotificationText)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)

        fun bind(notif: Notification) {
            // ✅ Tampilkan teks berdasarkan type
            val text = when (notif.type) {
                "like" -> "menyukai postingan Anda"
                "comment" -> "mengomentari postingan Anda"
                "like_story" -> "menyukai story Anda"
                "comment_story" -> "mengomentari story Anda"
                else -> "melakukan sesuatu"
            }
            tvNotificationText.text = text

            // ✅ Tampilkan waktu relatif
            notif.timestamp?.let {
                tvTimestamp.text = DateUtils.getRelativeTimeSpanString(it)
            }

            // ✅ Ambil foto profil pengirim
            notif.senderId?.let { senderId ->
                val ref = FirebaseDatabase.getInstance().getReference("users").child(senderId)
                ref.get().addOnSuccessListener { snapshot ->
                    val profileUrl = snapshot.child("profileImageUrl").getValue(String::class.java)
                    Glide.with(context)
                        .load(profileUrl)
                        .placeholder(R.drawable.ic_profile)
                        .circleCrop()
                        .into(imgSender)
                }
            }
        }
    }
}
