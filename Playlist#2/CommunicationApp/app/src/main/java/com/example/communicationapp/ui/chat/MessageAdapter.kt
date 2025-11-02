package com.example.communicationapp.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communicationapp.R
import com.example.communicationapp.data.AuthRepository
import com.example.communicationapp.data.models.Message

class MessageAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2

    override fun getItemViewType(position: Int): Int {
        val currentUserId = AuthRepository.currentUser()?.id
        return if (messages[position].senderId == currentUserId) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENDER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sender, parent, false)
            SenderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_receiver, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SenderViewHolder) holder.bind(message)
        else if (holder is ReceiverViewHolder) holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessageSender)
        private val ivMedia: ImageView = itemView.findViewById(R.id.ivMediaSender)

        fun bind(message: Message) {
            if (message.messageType == "text") {
                tvMessage.visibility = View.VISIBLE
                tvMessage.text = message.messageText
                ivMedia.visibility = View.GONE
            } else {
                tvMessage.visibility = View.GONE
                ivMedia.visibility = View.VISIBLE
                Glide.with(itemView.context).load(message.mediaUrl).into(ivMedia)
            }
        }
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessageReceiver)
        private val ivMedia: ImageView = itemView.findViewById(R.id.ivMediaReceiver)

        fun bind(message: Message) {
            if (message.messageType == "text") {
                tvMessage.visibility = View.VISIBLE
                tvMessage.text = message.messageText
                ivMedia.visibility = View.GONE
            } else {
                tvMessage.visibility = View.GONE
                ivMedia.visibility = View.VISIBLE
                Glide.with(itemView.context).load(message.mediaUrl).into(ivMedia)
            }
        }
    }
}