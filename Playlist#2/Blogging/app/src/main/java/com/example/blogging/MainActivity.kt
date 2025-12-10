package com.example.blogging

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.model.Blog
import com.example.blogging.activities.CreateBlogActivity
import com.example.blogging.adapter.BlogAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var fabAddBlog: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAddBlog = findViewById(R.id.fabAddBlog)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        loadFragment(HomeFragment()) // default

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    fabAddBlog.visibility = View.GONE
                    loadFragment(HomeFragment())
                    true
                }
                R.id.menu_profile -> {
                    fabAddBlog.visibility = View.VISIBLE
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        fabAddBlog.setOnClickListener {
            startActivity(Intent(this, CreateBlogActivity::class.java))
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
