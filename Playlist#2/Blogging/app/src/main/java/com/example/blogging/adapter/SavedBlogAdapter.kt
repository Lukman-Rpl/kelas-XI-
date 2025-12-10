package com.example.blogging.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.model.Blog
import com.example.blogging.R

class SavedBlogAdapter (private val blogs: List<Blog>) :
    RecyclerView.Adapter<SavedBlogAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvBlogTitle)
        val content: TextView = view.findViewById(R.id.tvBlogContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_blog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blog = blogs[position]
        holder.title.text = blog.title
        holder.content.text = blog.content
    }

    override fun getItemCount(): Int = blogs.size
}
