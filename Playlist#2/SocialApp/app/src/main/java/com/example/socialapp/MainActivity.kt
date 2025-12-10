package com.example.socialapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.socialapp.activities.CreatePostActivity
import com.example.socialapp.HomeFragment
import com.example.socialapp.ProfileFragment
import com.example.socialapp.SearchFragment
import com.example.socialapp.NotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var lastSelectedItemId: Int = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNavigationView)

        // Default fragment saat pertama kali dibuka → Home
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            bottomNav.selectedItemId = R.id.nav_home
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    lastSelectedItemId = item.itemId
                    true
                }
                R.id.nav_explore -> {
                    replaceFragment(SearchFragment())
                    lastSelectedItemId = item.itemId
                    true
                }
                R.id.nav_post -> {
                    startActivity(Intent(this, CreatePostActivity::class.java))
                    // Kembali ke item sebelumnya
                    bottomNav.selectedItemId = lastSelectedItemId
                    true
                }
                R.id.nav_notifications -> {
                    replaceFragment(NotificationsFragment())
                    lastSelectedItemId = item.itemId
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    lastSelectedItemId = item.itemId
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
