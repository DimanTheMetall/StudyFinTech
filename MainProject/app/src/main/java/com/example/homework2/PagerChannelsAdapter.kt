package com.example.homework2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.homework2.fragments.RecycleStreamsFragment

class PagerChannelsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val isSubscribed: Boolean = when (position) {
            0 -> true
            else -> false
        }
        return RecycleStreamsFragment.newInstance(isSubscribed)
    }
}
