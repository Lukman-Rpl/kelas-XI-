package com.example.socialapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.example.socialapp.activities.StoryActivity
import com.example.socialapp.model.Story

class StoryAdapter(
    private val context: Context,
    private val storyList: List<Story>
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgStoryProfile: ImageView = itemView.findViewById(R.id.imgStoryProfile)
        val tvStoryUsername: TextView = itemView.findViewById(R.id.tvStoryUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]

        holder.tvStoryUsername.text = story.username
        Glide.with(context).load(story.mediaUrl).into(holder.imgStoryProfile)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, StoryActivity::class.java)
            intent.putExtra("userId", story.userId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = storyList.size
}
