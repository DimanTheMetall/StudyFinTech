package com.example.homework2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.viewpager2.widget.ViewPager2
import com.example.homework2.R
import com.example.homework2.PagerChannelsAdapter
import com.example.homework2.databinding.FragmentChannelBinding
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class ChannelFragment : Fragment() {

    val searchObservable: Observable<String> get() = searchSubject
    private val searchSubject = PublishSubject.create<String>()

    private lateinit var adapter: PagerChannelsAdapter
    private lateinit var viewPager: ViewPager2
    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchStreamsEditText.addTextChangedListener { text ->
            searchSubject.onNext(text.toString())
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): ChannelFragment {
            return ChannelFragment()
        }
    }
}
