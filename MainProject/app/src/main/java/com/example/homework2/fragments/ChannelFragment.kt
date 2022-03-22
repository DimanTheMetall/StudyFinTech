package com.example.homework2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.homework2.R
import com.example.homework2.PagerChannelsAdapter
import com.example.homework2.databinding.FragmentChannelBinding
import com.google.android.material.tabs.TabLayoutMediator

class ChannelFragment : Fragment() {

    lateinit var adapter: PagerChannelsAdapter
    lateinit var viewPager: ViewPager2
    lateinit var binding: FragmentChannelBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChannelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PagerChannelsAdapter(this)
        viewPager = binding.channelPager
        viewPager.adapter = adapter

        TabLayoutMediator(binding.tabChannels, viewPager) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = getString(R.string.tab_item_subscribe)
                }
                1 -> {
                    tab.text = getString(R.string.tab_item_all)
                }
            }
        }.attach()
    }

    companion object {

        fun newInstance(): ChannelFragment {
            return ChannelFragment()
        }
    }
}
