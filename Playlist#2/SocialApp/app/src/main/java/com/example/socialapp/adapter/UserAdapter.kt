package com.example.socialapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.example.socialapp.model.User

class UserAdapter(private val userList: List<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.ivProfile)
        val username: TextView = itemView.findViewById(R.id.tvUsername)
        // Hapus: val fullName: TextView = itemView.findViewById(R.id.tvFullName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.username.text = user.username
        // Hapus: holder.fullName.text = user.fullName

        Glide.with(holder.itemView.context)
            .load(user.profileImageUrl)
            .into(holder.profileImage)
    }

    override fun getItemCount(): Int = userList.size
}
