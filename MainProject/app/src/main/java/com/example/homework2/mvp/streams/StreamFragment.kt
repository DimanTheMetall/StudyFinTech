package com.example.homework2.mvp.streams

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.homework2.PagerChannelsAdapter
import com.example.homework2.R
import com.example.homework2.databinding.FragmentChannelBinding
import com.example.homework2.mvp.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

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

    val searchObservable: Observable<String> get() = searchSubject
    private val searchSubject = BehaviorSubject.create<String>()

    private lateinit var adapter: PagerChannelsAdapter
    private lateinit var viewPager: ViewPager2

    override fun initPresenter(): StreamsPresenter =
        StreamPresenterImpl(this, StreamsModelImpl())

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChannelBinding {
        return FragmentChannelBinding.inflate(inflater, container, false)
    }

    override fun initViewPager() {
        adapter = PagerChannelsAdapter(this)
        viewPager = binding.channelPager
        viewPager.adapter = adapter

        TabLayoutMediator(binding.tabChannels, viewPager) { tab, pos ->
            tab.text = Tabs.values()[pos].getText(requireContext())
        }.attach()
    }

    companion object {
        fun newInstance(): StreamFragment {
            return StreamFragment()
        }
    }

}
