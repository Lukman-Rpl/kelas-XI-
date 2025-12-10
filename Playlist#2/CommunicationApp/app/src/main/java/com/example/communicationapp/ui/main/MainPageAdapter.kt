package com.example.communicationapp.ui.main


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.communicationapp.ui.calls.CallsFragment
import com.example.communicationapp.ui.chat.ChatFragment
import com.example.communicationapp.ui.status.StatusFragment


class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatFragment()
            1 -> StatusFragment()
            2 -> CallsFragment()
            else -> ChatFragment()
        }
    }
}
