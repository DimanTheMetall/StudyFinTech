package com.example.homework2.mvp.streams

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.ZulipApp
import com.example.homework2.adapters.StreamRecycleViewAdapter
import com.example.homework2.customviews.dpToPx
import com.example.homework2.databinding.FragmentRecycleChannelsBinding
import com.example.homework2.dataclasses.ResultStream
import com.example.homework2.fragments.ChatFragment
import com.example.homework2.viewmodels.StreamViewModel
import com.example.homework2.zulipApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class RecycleStreamsFragment : Fragment() {

    private var _binding: FragmentRecycleChannelsBinding? = null
    private val binding get() = _binding!!

    private var recycleAdapter = StreamRecycleViewAdapter { topic, stream ->
        openFrag(ChatFragment.newInstance(topic, stream), ChatFragment.TAG)
    }

    private val compositeDisposable = CompositeDisposable()

    private var itemDivider = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.apply {
                top += requireContext().dpToPx(2)
                bottom += requireContext().dpToPx(2)
            }
        }
    }

    private val viewModel: StreamViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.initDataBase(requireContext())

        _binding = FragmentRecycleChannelsBinding.inflate(inflater)
        binding.recycleChannel.adapter = recycleAdapter
        binding.recycleChannel.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.recycleChannel.addItemDecoration(itemDivider)

        val shimmer = binding.channelShimmer

        val searchDisposable = (parentFragment as StreamFragment).searchObservable
            .debounce(1, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .subscribe {
                when (getIsSubscribedBoolean()) {
                    true -> {
                        viewModel.onSearchChangedSubscribedChannel(
                            it,
                            (requireActivity().application as ZulipApp).retrofitService
                        )
                    }
                    false -> {
                        viewModel.onSearchChangedAllChannel(
                            it,
                            (requireActivity().application as ZulipApp).retrofitService
                        )
                    }
                }
            }

        compositeDisposable.add(searchDisposable)

        fun updateChannelResult(result: ResultStream) {
            when (result) {
                is ResultStream.Success -> {
                    recycleAdapter.updateList(result.streamList)
                    viewModel.insertStreamsAndTopics(result.streamList, getIsSubscribedBoolean())
                    shimmer.hideShimmer()
                }
                is ResultStream.Error -> {
                    shimmer.hideShimmer()
                }
                is ResultStream.Progress -> {
                    shimmer.showShimmer(true)
                }
            }
        }

        when (getIsSubscribedBoolean()) {
            true -> {
                val subscribedChannelsDisposable = viewModel.subscribedChannelsObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        updateChannelResult(it)
                    }
                compositeDisposable.add(subscribedChannelsDisposable)
            }
            false -> {
                val allChannelsDisposable = viewModel.allChannelsObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        updateChannelResult(it)
                    }
                compositeDisposable.add(allChannelsDisposable)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (getIsSubscribedBoolean()) {
            true -> {
                viewModel.loadSubscribedStreams(requireActivity().zulipApp().retrofitService)
            }
            false -> {
                viewModel.loadAllStreams(requireActivity().zulipApp().retrofitService)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }

    private fun openFrag(fragment: Fragment, tag: String? = null) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    private fun getIsSubscribedBoolean(): Boolean {
        return requireArguments().getBoolean(Constance.ALL_OR_SUBSCRIBED_KEY)
    }

    companion object {

        fun newInstance(isSubscribed: Boolean): RecycleStreamsFragment {
            val fragment = RecycleStreamsFragment()
            val arguments = Bundle()
            arguments.putBoolean(Constance.ALL_OR_SUBSCRIBED_KEY, isSubscribed)
            fragment.arguments = arguments

            return fragment
        }
    }
}
