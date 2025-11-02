package com.example.blogging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.model.Blog
import com.example.blogging.adapter.BlogAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private lateinit var blogRecycler: RecyclerView
    private val blogs = mutableListOf<Blog>()
    private lateinit var adapter: BlogAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        blogRecycler = view.findViewById(R.id.blogRecyclerView)
        blogRecycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = BlogAdapter(blogs, requireContext())
        blogRecycler.adapter = adapter
        loadBlogs()
        return view
    }

    private fun loadBlogs() {
        val blogsRef = FirebaseDatabase.getInstance().getReference("blogs")
        blogsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                blogs.clear()
                for (child in snapshot.children) {
                    val blog = child.getValue(Blog::class.java)
                    if (blog != null) {
                        // Set id key dari child key
                        blog.id = child.key ?: ""
                        blogs.add(blog)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal muat blog: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
