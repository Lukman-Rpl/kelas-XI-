package com.example.communicationapp.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.communicationapp.databinding.ItemUserBinding
import com.example.communicationapp.data.models.User

class UserAdapter(private val users: List<User>, private val onClick: (User) -> Unit) :
    RecyclerView.Adapter<UserAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val u = users[position]
        holder.bind(u)
        holder.itemView.setOnClickListener { onClick(u) }
    }

    override fun getItemCount(): Int = users.size

    class VH(private val b: ItemUserBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(u: User) {
            b.tvUsername.text = u.username ?: u.email ?: "Unknown"
            b.tvLastMessage.text = "" // could show last message if available
            // load avatar with Glide/Picasso (omitted)
        }
    }
}
