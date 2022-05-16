package com.example.homework2.mvp.streams

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.homework2.R
import com.example.homework2.databinding.FragmentChannelBinding
import com.example.homework2.mvp.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator

enum class Tabs {
    SUBSCRIBED {
        override fun getText(context: Context): String {
            return context.getString(R.string.tab_item_subscribe)
        }
    },
    ALL {
        override fun getText(context: Context): String {
            return context.getString(R.string.tab_item_all)
        }
    };

    abstract fun getText(context: Context): String
}

class StreamFragment :
    BaseFragment<StreamsPresenter, FragmentChannelBinding>(),
    StreamsView {

    private lateinit var adapter: PagerChannelsAdapter
    private lateinit var viewPager: ViewPager2
    override var presenter: StreamsPresenter = StreamPresenterImpl(StreamsModelImpl())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChannelBinding {
        return FragmentChannelBinding.inflate(inflater, container, false)
    }

    private fun initViewPager() {
        adapter = PagerChannelsAdapter(this)
        viewPager = binding.channelPager
        viewPager.adapter = adapter

        TabLayoutMediator(binding.tabChannels, viewPager) { tab, pos ->
            tab.text = Tabs.values()[pos].getText(requireContext())
        }.attach()
    }

    companion object {
        fun newInstance(): StreamFragment = StreamFragment()
    }
}
